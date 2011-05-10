package com.codahale.simplespec

import org.specs.Specification

trait Spec extends Specification with Discovery {
  def beforeAll() {}

  def afterAll() {}

  {
    val klass = Class.forName(this.getClass.getName.replace("$", ""))
    for ((path, requirements) <- discover(klass).groupBy {_.names}) {
      val sus = specify(path.mkString(" "))
      sus.verb = ""
      sus.should {
        beforeAll.before
        afterAll.after
        requirements.foreach { req =>
          req.name >> {
            req.evaluate()
          }
        }
      }
    }
  }
}

trait Before {
  def beforeEach() {}
}

trait After {
  def afterEach() {}
}

trait Around extends Before with After
