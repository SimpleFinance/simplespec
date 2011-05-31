package com.codahale.simplespec

import java.lang.reflect.Modifier._
import reflect.NameTransformer
import java.lang.reflect.{InvocationTargetException, Method}
import org.specs2.execute.{FailureException, Failure}

case class Requirement(klass: Class[_], method: Method) {
  lazy val path = (klass :: parents(klass)).reverse

  lazy val names = path.map { k => NameTransformer.decode(k.getSimpleName) }

  lazy val name = NameTransformer.decode(method.getName)

  def evaluate() = {
    try {
      val root = path.head.newInstance().asInstanceOf[Object]
      val instance = path.tail.foldLeft(root) {(parent, k) =>
        k.getConstructor(parent.getClass).newInstance(parent).asInstanceOf[Object]
      }

      if (classOf[BeforeEach].isAssignableFrom(instance.getClass)) {
        instance.asInstanceOf[BeforeEach].beforeEach()
      }
      try {
        method.invoke(instance)
      } finally {
        if (classOf[AfterEach].isAssignableFrom(instance.getClass)) {
          instance.asInstanceOf[AfterEach].afterEach()
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
      !klass.isAnnotationPresent(classOf[ignore]) &&
      isPublic(klass.getModifiers)
  }


  private def isRequirement(method: Method) = {
    method.getParameterTypes.length == 0 &&
    !isSynthetic(method.getModifiers) &&
      !isPrivate(method.getModifiers) &&
      !isFinal(method.getModifiers) &&
      !(method.getName == "beforeEach" && classOf[BeforeEach].isAssignableFrom(method.getDeclaringClass)) &&
      !(method.getName == "afterEach" && classOf[AfterEach].isAssignableFrom(method.getDeclaringClass)) &&
      !method.isAnnotationPresent(classOf[ignore]) &&
      !method.getName.endsWith("$outer")
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
