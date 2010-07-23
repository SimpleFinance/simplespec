package com.codahale.simplespec

import org.specs.Specification
import reflect.NameTransformer
import java.lang.reflect.InvocationTargetException

abstract class Spec extends Specification {
  {
    val klass = Class.forName(this.getClass.getName.replace("$", ""))
    for (susKlass <- klass.getDeclaredClasses) {
      NameTransformer.decode(susKlass.getSimpleName) should {
        for (exampleMethod <- susKlass.getDeclaredMethods if exampleMethod.getName.startsWith("should")) {
          val name = NameTransformer.decode(exampleMethod.getName)
          name.substring("should".length) >> {
            val instance = susKlass.getConstructor().newInstance()
            try {
              exampleMethod.invoke(instance)
            } catch {
              case e: InvocationTargetException =>
                throw e.getCause
            }
          }
        }
      }
    }
  }
}