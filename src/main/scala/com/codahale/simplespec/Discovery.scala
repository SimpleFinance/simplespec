package com.codahale.simplespec

import java.lang.reflect.Modifier._
import reflect.NameTransformer
import java.lang.reflect.{InvocationTargetException, Method}
import org.specs2.execute.{FailureException, Failure}
import com.codahale.simplespec.annotation.test

case class Requirement(klass: Class[_], method: Method) {
  lazy val path = (klass :: parents(klass)).reverse

  lazy val names = path.map { k => NameTransformer.decode(k.getSimpleName) }

  lazy val name = NameTransformer.decode(method.getName)

  def evaluate() = {
    try {
      val root = path.head.newInstance().asInstanceOf[Object]
      val instances = path.tail.foldLeft(root :: Nil) { (parents, k) =>
        val parent = parents.head
        k.getConstructor(parent.getClass).newInstance(parent).asInstanceOf[Object] :: parents
      }

      val instance = instances.head

      for (o <- instances.reverse if classOf[BeforeEach].isAssignableFrom(o.getClass)) {
        o.asInstanceOf[BeforeEach].beforeEach()
      }


      try {
        method.invoke(instance)
      } finally {
        for (o <- instances if classOf[AfterEach].isAssignableFrom(o.getClass)) {
          o.asInstanceOf[AfterEach].afterEach()
        }
      }
    } catch {
      case e: InvocationTargetException => {
        e.getCause match {
          case failure: FailureException => {
            val f = failure.f
            throw new FailureException(
              Failure(f.m, f.e,
                // seriously this is what it takes to fake out specs2's
                // location code
                new StackTraceElement("dummy", "dummy", "dummy", -1) ::
                  f.stackTrace.filterNot {el =>
                    el.getClassName.startsWith("org.specs2")
                  },
                f.details)
            )
          }
          case e2 if e2 != null => throw e2
          case _ => throw e
        }
      }
    }
  }

  private def parents(k: Class[_]): List[Class[_]] = {
    val parent = k.getEnclosingClass
    if (parent == null || isStatic(k.getModifiers)) {
      Nil
    } else {
      parent :: parents(parent)
    }
  }
}

trait Discovery {
  private def isSynthetic(modifiers: Int) = (modifiers & 0x00001000) != 0

  private def couldHaveRequirements(klass: Class[_]) = {
    klass.getInterfaces.contains(classOf[ScalaObject]) &&
      !isInterface(klass.getModifiers) &&
      isPublic(klass.getModifiers)
  }


  private def isRequirement(method: Method) = {
    method.getParameterTypes.length == 0 &&
      method.isAnnotationPresent(classOf[test])
  }


  protected def discover(klass: Class[_]): List[Requirement] = {
    var requirements = List.empty[Requirement]
    for (inner <- klass.getClasses if couldHaveRequirements(inner)) {
      for (method <- inner.getDeclaredMethods if isRequirement(method)) {
        requirements = Requirement(inner, method) :: requirements
      }
      requirements = requirements ::: discover(inner)
    }
    requirements
  }
}
