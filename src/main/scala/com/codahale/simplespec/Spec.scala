package com.codahale.simplespec

import org.specs2.mutable.Specification
import org.specs2.execute.Result
import org.specs2.specification.Step

trait Spec extends Specification with Discovery {
  def beforeAll() {}
  def afterAll() {}

  override def is = Step(beforeAll) ^ super.is ^ Step(afterAll)

  {
    val klass = Class.forName(this.getClass.getName.replace("$", ""))
    for ((path, requirements) <- discover(klass).groupBy { _.names }) {
      path.mkString(" ") >> {
        requirements.map { req =>
          req.name in {
            req.evaluate() match {
              case r: Result => r
              case r: { def toResult: Result } => r.toResult
              case _ => pending
            }
          }
        }.head
      }
    }
  }
}

trait BeforeEach {
  def beforeEach() {}
}

trait AfterEach {
  def afterEach() {}
}

trait BeforeAndAfterEach extends BeforeEach with AfterEach
