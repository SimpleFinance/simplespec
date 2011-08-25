package com.codahale.simplespec.specs

import java.util.concurrent.CopyOnWriteArrayList
import scala.collection.JavaConversions._
import org.junit.runner.notification.RunNotifier
import org.junit.Test
import com.codahale.simplespec._

class BeforeAfterEachSpec extends Matchers {
  @Test
  def mustRunAllBeforeAndAfterBlocks() {
    BeforeAfterEachExample.clear()
    val runner = new SpecRunner(classOf[BeforeAfterEachExample])
    val notifier = new RunNotifier
    runner.run(notifier)

    BeforeAfterEachExample.befores.toList.must(be(List("BeforeAfterEachExample", "Doing a thing", "with another thing", "<test>")))
    BeforeAfterEachExample.afters.toList.must(be(List("<test>", "with another thing", "Doing a thing", "BeforeAfterEachExample")))
  }
}

object BeforeAfterEachExample {
  val befores = new CopyOnWriteArrayList[String]
  val afters = new CopyOnWriteArrayList[String]

  def clear() {
    befores.clear()
    afters.clear()
  }
}

class BeforeAfterEachExample extends Spec {
  import BeforeAfterEachExample._

  override def beforeEach() = {
    befores += "BeforeAfterEachExample"
  }

  override def afterEach() = {
    afters += "BeforeAfterEachExample"
  }

  class `Doing a thing` extends BeforeAndAfterEach {
    override def beforeEach() = {
      befores += "Doing a thing"
    }

    override def afterEach() = {
      afters += "Doing a thing"
    }

    class `with another thing` extends BeforeAndAfterEach {
      override def beforeEach() = {
        befores += "with another thing"
      }

      override def afterEach() = {
        afters += "with another thing"
      }

      @Test def `poops` = {
        befores += "<test>"
        afters += "<test>"
      }
    }
  }
}
