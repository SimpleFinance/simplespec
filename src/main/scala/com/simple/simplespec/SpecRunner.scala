package com.simple.simplespec

import java.lang.reflect.Modifier._
import java.util.ArrayList
import scala.reflect.NameTransformer
import org.junit.internal.runners.statements.{RunAfters, RunBefores}
import org.junit.runner.{Description, Runner}
import org.junit.runner.notification.RunNotifier
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.FrameworkMethod
import org.junit.{Ignore, Test}

case class RunnerScope(klass: Class[_], runner: Runner, children: Seq[RunnerScope])

class SpecRunner(topKlass: Class[_]) extends Runner {
  private val runners = discover(topKlass, Nil)

  def getDescription = {
    val top = Description.createSuiteDescription(topKlass)
    runners.foreach { s => describe(s, top) }
    top
  }

  private def describe(scope: RunnerScope, parent: Description): Unit = {
    val d = scope.runner.getDescription
    scope.children.foreach { s => describe(s, d) }
    parent.addChild(d)
  }

  def run(notifier: RunNotifier) {
    runners.foreach { run(notifier, _) }
  }

  private def run(notifier: RunNotifier, scope: RunnerScope) {
    scope.runner.run(notifier)
    scope.children.foreach { run(notifier, _) }
  }

  private def couldHaveTests(klass: Class[_]) = {
    !isInterface(klass.getModifiers) &&
      isPublic(klass.getModifiers) &&
      !klass.isAnonymousClass &&
      !klass.isAnnotationPresent(classOf[Ignore]) &&
      !klass.getName.contains("$$anon$") // excludes anonymous classes written in Scala
  }


  private def discover(klass: Class[_], path: List[Class[_]]): Seq[RunnerScope] = {
    for (inner <- klass.getDeclaredClasses if couldHaveTests(inner)) yield {
      val runner = new InnerClassRunner(klass :: path, inner)
      RunnerScope(klass, runner, discover(inner, klass :: path))
    }
  }
}

class InnerClassRunner(scope: List[Class[_]], klass: Class[_]) extends BlockJUnit4ClassRunner(klass) {
  private val path = (klass :: scope).reverse

  override def testName(method: FrameworkMethod) = NameTransformer.decode(method.getName)

  override def getName = NameTransformer.decode(klass.getSimpleName)

  override def collectInitializationErrors(errors: java.util.List[Throwable]) {
    import scala.collection.JavaConverters._
    val allErrors = new ArrayList[Throwable]
    super.collectInitializationErrors(allErrors)
    for (e <- allErrors.asScala) {
      if (!ignoredError(e.getMessage)) {
        errors.add(e)
      }
    }

    if (!klass.getDeclaredClasses.exists { k => !k.isAnonymousClass } &&
          !klass.getMethods.exists { _.getAnnotation(classOf[Test]) != null }) {
      errors.add(new Exception("No runnable methods"))
    }
  }

  override def methodInvoker(method: FrameworkMethod, test: AnyRef) = {
    import scala.collection.JavaConverters._

    def traverseInstances(obj: Object): List[Object] = {
      if (obj.getClass.getEnclosingClass == null) {
        Nil
      } else {
        val field = obj.getClass.getDeclaredField("$outer")
        val instance = field.get(obj)
        instance :: traverseInstances(instance)
      }
    }

    val statement = super.methodInvoker(method, test)
    val instances = test :: traverseInstances(test)
    val withBefores = instances.foldLeft(statement) { (stmt, obj) =>
      if (classOf[BeforeEach].isAssignableFrom(obj.getClass)) {
        val method = obj.getClass.getMethod("beforeEach")
        new RunBefores(stmt, List(new FrameworkMethod(method)).asJava, obj)
      } else stmt
    }

    val withAfters = instances.foldLeft(withBefores) { (stmt, obj) =>
      if (classOf[AfterEach].isAssignableFrom(obj.getClass)) {
        val method = obj.getClass.getMethod("afterEach")
        new RunAfters(stmt, List(new FrameworkMethod(method)).asJava, obj)
      } else stmt
    }
    
    withAfters
  }

  private def ignoredError(msg: String) =
    msg.contains("should be void") ||
      msg.contains("No runnable methods") ||
      msg.contains("exactly one public zero-argument constructor") ||
      msg.contains("is not static")

  override def createTest() = {
    val root = path.head.newInstance().asInstanceOf[Object]
    val instances = path.tail.foldLeft(root :: Nil) { (parents, k) =>
      val parent = parents.head
      k.getConstructor(parent.getClass).newInstance(parent).asInstanceOf[Object] :: parents
    }
    instances.head
  }
}
