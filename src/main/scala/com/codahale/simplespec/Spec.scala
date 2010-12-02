package com.codahale.simplespec

import org.specs.Specification
import reflect.NameTransformer
import java.lang.reflect.{InvocationTargetException}
import java.lang.reflect.Modifier.{isAbstract, isFinal, isInterface, isPublic, isStatic}

abstract class Spec extends Specification {
  {
    val klass = Class.forName(this.getClass.getName.replace("$", ""))
    for (susKlass <- klass.getClasses if isPublic(susKlass.getModifiers) &&
                                         !isAbstract(susKlass.getModifiers) &&
                                         !isInterface(susKlass.getModifiers) && 
                                         !(isStatic(susKlass.getModifiers) &&
                                           isPublic(susKlass.getModifiers) &&
                                           isFinal(susKlass.getModifiers))) {
      NameTransformer.decode(susKlass.getSimpleName) should {
        // println(susKlass)
        // println(susKlass.getModifiers)
        beforeAll.before
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
            try {
              val instance = susKlass.getConstructor().newInstance()
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
