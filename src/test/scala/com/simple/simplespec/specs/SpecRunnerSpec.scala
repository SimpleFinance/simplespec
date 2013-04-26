package com.simple.simplespec.specs

import scala.collection.mutable
import org.junit.Assert._
import org.junit.runner.{Result, Description}
import com.simple.simplespec.{SpecRunner, Spec}
import org.junit.{Ignore, Test}
import org.junit.runner.notification.{RunNotifier, Failure, RunListener}
import org.junit.runners.model.InitializationError
import scala.collection.JavaConversions._

class SpecRunnerSpec {
  @Test
  def mustGenerateDescriptionsCorrectly() {
    val runner = new SpecRunner(classOf[SpecExample])
    val desc = runner.getDescription

    assertEquals("com.simple.simplespec.specs.SpecExample", desc.getDisplayName)
    assertEquals("com.simple.simplespec.specs.SpecExample", desc.getClassName)
    assertNull(desc.getMethodName)
    assertEquals(classOf[SpecExample], desc.getTestClass)
    assertEquals(3, desc.getChildren.size())

    // Child ordering changes between versions of Scala, so we sort them.
    {
      val emptySet = desc.getChildren.sortBy(_.getDisplayName).apply(2)
      assertEquals("An empty set", emptySet.getDisplayName)
      assertEquals("An empty set", emptySet.getClassName)
      assertNull(emptySet.getMethodName)
      assertNull(emptySet.getTestClass)
      assertEquals(2, emptySet.getChildren.size())

      {
        val hasZero = emptySet.getChildren.sortBy(_.getDisplayName).apply(0)
        assertEquals("has a size of zero(com.simple.simplespec.specs.SpecExample$An$u0020empty$u0020set)", hasZero.getDisplayName)
        assertEquals("com.simple.simplespec.specs.SpecExample$An$u0020empty$u0020set", hasZero.getClassName)
        assertEquals("has a size of zero", hasZero.getMethodName)
        assertEquals(classOf[SpecExample#`An empty set`], hasZero.getTestClass)
      }

      {
        val isEmpty = emptySet.getChildren.sortBy(_.getDisplayName).apply(1)
        assertEquals("is empty(com.simple.simplespec.specs.SpecExample$An$u0020empty$u0020set)", isEmpty.getDisplayName)
        assertEquals("com.simple.simplespec.specs.SpecExample$An$u0020empty$u0020set", isEmpty.getClassName)
        assertEquals("is empty", isEmpty.getMethodName)
        assertEquals(classOf[SpecExample#`An empty set`], isEmpty.getTestClass)
      }
    }

    {
      val oneNumber = desc.getChildren.sortBy(_.getDisplayName).apply(0)
      assertEquals("A set with one number", oneNumber.getDisplayName)
      assertEquals("A set with one number", oneNumber.getClassName)
      assertNull(oneNumber.getMethodName)
      assertNull(oneNumber.getTestClass)
      assertEquals(3, oneNumber.getChildren.size())

      {
        val hasOne = oneNumber.getChildren.sortBy(_.getDisplayName).apply(0)
        assertEquals("has a size of one(com.simple.simplespec.specs.SpecExample$A$u0020set$u0020with$u0020one$u0020number)", hasOne.getDisplayName)
        assertEquals("com.simple.simplespec.specs.SpecExample$A$u0020set$u0020with$u0020one$u0020number", hasOne.getClassName)
        assertEquals("has a size of one", hasOne.getMethodName)
        assertEquals(classOf[SpecExample#`A set with one number`], hasOne.getTestClass)
      }

      {
        val notEmpty = oneNumber.getChildren.sortBy(_.getDisplayName).apply(2)
        assertEquals("is not empty(com.simple.simplespec.specs.SpecExample$A$u0020set$u0020with$u0020one$u0020number)", notEmpty.getDisplayName)
        assertEquals("com.simple.simplespec.specs.SpecExample$A$u0020set$u0020with$u0020one$u0020number", notEmpty.getClassName)
        assertEquals("is not empty", notEmpty.getMethodName)
        assertEquals(classOf[SpecExample#`A set with one number`], notEmpty.getTestClass)
      }

      {
        val withRemoved = oneNumber.getChildren.sortBy(_.getDisplayName).apply(1)
        assertEquals("having had that number removed", withRemoved.getDisplayName)
        assertEquals("having had that number removed", withRemoved.getClassName)
        assertNull(withRemoved.getMethodName)
        assertNull(withRemoved.getTestClass)
        assertEquals(2, withRemoved.getChildren.size())

        {
          val hasZero = withRemoved.getChildren.sortBy(_.getDisplayName).apply(0)
          assertEquals("has a size of zero(com.simple.simplespec.specs.SpecExample$A$u0020set$u0020with$u0020one$u0020number$having$u0020had$u0020that$u0020number$u0020removed)", hasZero.getDisplayName)
          assertEquals("com.simple.simplespec.specs.SpecExample$A$u0020set$u0020with$u0020one$u0020number$having$u0020had$u0020that$u0020number$u0020removed", hasZero.getClassName)
          assertEquals("has a size of zero", hasZero.getMethodName)
          assertEquals(classOf[SpecExample#`A set with one number`#`having had that number removed`], hasZero.getTestClass)
        }

        {
          val isEmpty = withRemoved.getChildren.sortBy(_.getDisplayName).apply(1)
          assertEquals("is empty(com.simple.simplespec.specs.SpecExample$A$u0020set$u0020with$u0020one$u0020number$having$u0020had$u0020that$u0020number$u0020removed)", isEmpty.getDisplayName)
          assertEquals("com.simple.simplespec.specs.SpecExample$A$u0020set$u0020with$u0020one$u0020number$having$u0020had$u0020that$u0020number$u0020removed", isEmpty.getClassName)
          assertEquals("is empty", isEmpty.getMethodName)
          assertEquals(classOf[SpecExample#`A set with one number`#`having had that number removed`], isEmpty.getTestClass)
        }
      }

      // TODO: 8/25/11 <coda> -- add test for super-nested description
    }
  }

  @Test
  def mustRunTests() {
    val runner = new SpecRunner(classOf[SpecExample])
    val notifier = new RunNotifier
    val listener = new RecordingRunListener
    notifier.addListener(listener)
    runner.run(notifier)

    // The order in which they run is dependent on the Scala compiler, so we only check the size.
    assertEquals(16, listener.events.size)
  }

  @Test
  def mustHandleTestFailures() {
    val runner = new SpecRunner(classOf[BustedSpecExample])
    val notifier = new RunNotifier
    val listener = new RecordingRunListener
    notifier.addListener(listener)
    runner.run(notifier)

    assertEquals(
      Vector(
        ('started, "should fail"),
        ('failure, "java.lang.AssertionError:should fail"),
        ('finished, "should fail"),
        ('ignored, "should be ignored"),
        ('started, "should explode"),
        ('failure, "java.lang.RuntimeException:should explode"),
        ('finished, "should explode")
      ),
      listener.events
    )
  }

  @Test
  def mustExplodeWhenNoTests() {
    import scala.collection.JavaConversions._
    
    try {
      new SpecRunner(classOf[InvalidExample])
      fail("expected an InitializationError but didn't see one")
    } catch {
      case e: InitializationError => {
        assertTrue(e.getCauses.exists { _.getMessage == "No runnable methods" })
      }
    }
  }

  @Test
  def mustNotExplodeWithAnonymousClasses() {
    try {
      new SpecRunner(classOf[AnonymousClassExample])
      assertTrue(true)
    } catch {
      case e: Exception => {
        println(e.getMessage)
        println(e)
        throw e
      }
    }
  }
}

class RecordingRunListener extends RunListener {
  var events = Vector.empty[(Symbol, String)]

  override def testAssumptionFailure(failure: Failure) {
    events = events :+ (('assumptionFailure, failure.toString))
  }

  override def testFailure(failure: Failure) {
    events = events :+ (('failure, failure.getException.getClass.getName + ":" + failure.getDescription.getMethodName))
  }

  override def testFinished(description: Description) {
    events = events :+ (('finished, description.getMethodName))
  }

  override def testIgnored(description: Description) {
    events = events :+ (('ignored, description.getMethodName))
  }

  override def testRunFinished(result: Result) {
    events = events :+ (('runFinished, result.toString))
  }

  override def testRunStarted(description: Description) {
    events = events :+ (('runStarted, description.toString))
  }

  override def testStarted(description: Description) {
    events = events :+ (('started, description.getMethodName))
  }
}

class SpecExample extends Spec {
  val numbers = new mutable.HashSet[Int]()

  class `An empty set` {
    @Test def `has a size of zero` = {
      numbers.size.must(be(0))
    }

    @Test def `is empty` = {
      numbers.isEmpty.must(be(true))
    }
  }

  class `A set with one number` {
    numbers += 1

    @Test def `has a size of one` = {
      numbers.size.must(be(1))
    }

    @Test def `is not empty` = {
      numbers.isEmpty.must(be(false))
    }

    class `having had that number removed` {
      numbers -= 1

      @Test def `has a size of zero` = {
        numbers.size.must(be(0))
      }

      @Test def `is empty` = {
        numbers.isEmpty.must(be(true))
      }
    }
  }

  class `A set with two numbers` {
    numbers ++= Set(1, 2)

    class `with one removed` {
      numbers -= 1

      @Test def `has a size of one` = {
        numbers.size.must(be(1))
      }

      @Test def `is not empty` = {
        numbers.isEmpty.must(be(false))
      }
    }
  }

  @Ignore
  class `An ignored set of tests` {

  }
}

class BustedSpecExample extends Spec {
  class `An ignored test` {
    @Ignore @Test def `should be ignored` = {
      throw new RuntimeException("should have ignored me!")
    }
  }

  class `A failing test` {
    @Test def `should fail` = {
      assertEquals(1, 2)
    }
  }

  class `An exploding test` {
    @Test def `should explode` = {
      throw new RuntimeException("EFFFFF")
    }
  }
}

class InvalidExample extends Spec {
  class `A class with children doesn't need tests` {
    class `but a class without children does` {

    }
  }
}

class AnonymousClassExample extends Spec {
  class `A thing` {
    val thing = new Runnable {
      def run() {}
    }

    @Test def `do a thing` = {
      1
    }
  }
}
