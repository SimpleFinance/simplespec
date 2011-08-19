package com.codahale.simplespec

import scala.reflect.NameTransformer
import java.lang.reflect.Modifier._
import java.lang.reflect.{InvocationTargetException, Method}

case class Requirement(klass: Class[_], method: Method) {
  lazy val path = (klass :: parents(klass)).reverse

  lazy val names = path.map { k => NameTransformer.decode(k.getSimpleName) }

  lazy val name = NameTransformer.decode(method.getName)

  def evaluate(): Any = {
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
        throw if (e.getCause == null) e else e.getCause
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
