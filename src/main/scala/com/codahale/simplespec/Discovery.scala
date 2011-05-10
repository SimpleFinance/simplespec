package com.codahale.simplespec

import java.lang.reflect.Modifier._
import reflect.NameTransformer
import java.lang.reflect.{InvocationTargetException, Method}

case class Requirement(klass: Class[_], method: Method) {
  lazy val path = (klass :: parents(klass)).reverse

  lazy val names = path.map { k => NameTransformer.decode(k.getSimpleName) }

  lazy val name = NameTransformer.decode(method.getName)

  def evaluate() = {
    val root = path.head.newInstance().asInstanceOf[Object]
    val instance = path.tail.foldLeft(root) { (parent, k) =>
      k.getConstructor(parent.getClass).newInstance(parent).asInstanceOf[Object]
    }
    try {
      if (classOf[Before].isAssignableFrom(instance.getClass)) {
        instance.asInstanceOf[Before].beforeEach()
      }
      try {
        method.invoke(instance)
      } finally {
        if (classOf[After].isAssignableFrom(instance.getClass)) {
          instance.asInstanceOf[After].afterEach()
        }
      }
    } catch {
      case e: InvocationTargetException => {
        throw e.getCause
//        val entry = e.getCause.getStackTrace.dropWhile { e =>
//          val n = e.getClassName
//          n.startsWith("com.codahale.simplespec") || n.startsWith("scala")
//        }.head
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
  private def couldHaveRequirements(klass: Class[_]) = try {
    isPublic(klass.getModifiers) &&
    !isAbstract(klass.getModifiers) &&
    !isInterface(klass.getModifiers) &&
    !(isStatic(klass.getModifiers) &&
      isFinal(klass.getModifiers)) &&
      !klass.getSimpleName.contains("$anonfun$")
  } catch {
    case e => false
  }

  private def isRequirement(method: Method) =
    isPublic(method.getModifiers) &&
      !method.getName.contains("$$") &&
      method.getParameterTypes.length == 0 &&
      !(method.getName == "beforeEach" && classOf[Before].isAssignableFrom(method.getDeclaringClass)) &&
      !(method.getName == "afterEach" && classOf[After].isAssignableFrom(method.getDeclaringClass))

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
