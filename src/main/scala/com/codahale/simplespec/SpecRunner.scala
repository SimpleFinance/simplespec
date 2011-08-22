package com.codahale.simplespec

import org.junit.runner.{Description, Runner}
import java.lang.reflect.Modifier._
import java.lang.reflect.Method
import com.codahale.simplespec.annotation.test
import scala.reflect.NameTransformer
import org.junit.runner.notification.{Failure => TestFailure, RunNotifier}

class SpecRunner(topKlass: Class[_]) extends Runner {
  require(classOf[Spec].isAssignableFrom(topKlass))
  private val klass = Class.forName(topKlass.getName.replace("$", ""))
  private val (descriptions, requirements) = describe(klass)
  
  def getDescription = {
    val top = Description.createSuiteDescription(klass)
    descriptions.foreach(top.addChild)
    top
  }

  def run(notifier: RunNotifier) {
    // sort for stability
    for ((req, desc) <- requirements.toSeq.sortBy { case (r, d) => (r.klass.getName, r.method.getName) }) {
      notifier.fireTestStarted(desc)
      try {
        req.evaluate()
        notifier.fireTestFinished(desc)
      } catch {
        case e: IgnoredTestException => notifier.fireTestIgnored(desc)
        case e: Throwable => {
          notifier.fireTestFailure(new TestFailure(desc, e))
          notifier.fireTestFinished(desc)
        }
      }
    }
  }

  private def couldHaveRequirements(klass: Class[_]) =
    klass.getInterfaces.contains(classOf[ScalaObject]) &&
      !isInterface(klass.getModifiers) &&
      isPublic(klass.getModifiers)

  private def isRequirement(method: Method) =
    method.getParameterTypes.length == 0 &&
      method.isAnnotationPresent(classOf[test])

  protected def describe(klass: Class[_]): (List[Description], Map[Requirement, Description]) = {
    var descriptions = List.empty[Description]
    var requirements = Map.empty[Requirement, Description]
    for (inner <- klass.getClasses if couldHaveRequirements(inner)) {
      val classDescription = Description.createSuiteDescription(NameTransformer.decode(inner.getSimpleName))
      for (method <- inner.getDeclaredMethods if isRequirement(method)) {
        val testDescription = Description.createTestDescription(inner, NameTransformer.decode(method.getName))
        requirements += Requirement(inner, method) -> testDescription
        classDescription.addChild(testDescription)
      }

      val (innerDesc, innerReq) = describe(inner)
      innerDesc.foreach(classDescription.addChild)
      requirements ++= innerReq
      descriptions = classDescription :: descriptions
    }
    (descriptions, requirements)
  }
}
