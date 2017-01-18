package com.simple.simplespec.specs

import java.util.concurrent.CopyOnWriteArrayList
import scala.collection.JavaConverters._
import org.junit.Test
import org.junit.runner.notification.RunNotifier
import com.simple.simplespec._

class BeforeAfterEachSpec extends Matchers {
  @Test
  def mustRunAllBeforeAndAfterBlocks() {
    BeforeAfterEachExample.clear()
    val runner = new SpecRunner(classOf[BeforeAfterEachExample])
    val notifier = new RunNotifier
    runner.run(notifier)

    BeforeAfterEachExample.befores.asScala.toList.must(be(List("BeforeAfterEachExample", "Doing a thing", "with another thing", "<test>")))
    BeforeAfterEachExample.afters.asScala.toList.must(be(List("<test>", "with another thing", "Doing a thing", "BeforeAfterEachExample")))
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

  override def beforeEach() {
    befores.add("BeforeAfterEachExample")
  }

  override def afterEach() {
    afters.add("BeforeAfterEachExample")
  }

  class `Doing a thing` extends BeforeAndAfterEach {
    override def beforeEach() {
      befores.add("Doing a thing")
    }

    override def afterEach() {
      afters.add("Doing a thing")
    }

    class `with another thing` extends BeforeAndAfterEach {
      override def beforeEach() {
        befores.add("with another thing")
      }

      override def afterEach() {
        afters.add("with another thing")
      }

      @Test def `poops` = {
        befores.add("<test>")
        afters.add("<test>")
      }
    }
  }
}
