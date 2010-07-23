package com.codahale.simplespec

import org.specs.Specification
import reflect.NameTransformer
import java.lang.reflect.InvocationTargetException

abstract class Spec extends Specification {
  {
    val klass = Class.forName(this.getClass.getName.replace("$", ""))
    for (susKlass <- klass.getDeclaredClasses) {
      NameTransformer.decode(susKlass.getSimpleName) should { beforeAll.before
        afterAll.after
        val beforeEachMethod = try {
          Some(susKlass.getDeclaredMethod("beforeEach"))
        } catch {
          case e: Exception => None
        }
        val afterEachMethod = try {
          Some(susKlass.getDeclaredMethod("afterEach"))
        } catch {
          case e: Exception => None
        }
        
        for (exampleMethod <- susKlass.getDeclaredMethods if exampleMethod.getName.startsWith("should")) {
          val name = NameTransformer.decode(exampleMethod.getName)
          name.substring("should".length) >> {
            val instance = susKlass.getConstructor().newInstance()
            try {
              beforeEachMethod.map { _.invoke(instance) }
              try {
                exampleMethod.invoke(instance)
              } finally {
                afterEachMethod.map { _.invoke(instance) }
              }
            } catch {
              case e: InvocationTargetException =>
                throw e.getCause
            }
          }
        }
      }
    }
  }
  
  def beforeAll {
    // override me for awesome fun
  }
  
  def afterAll {
    // override me for awesome fun
  }
}