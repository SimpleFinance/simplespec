package com.codahale.simplespec.specs

import scala.collection.mutable
import org.junit.Assert._
import com.codahale.simplespec.annotation.test
import com.codahale.simplespec.{SpecRunner, Spec}
import org.junit.Test
import org.junit.runner.{Result, Description}
import org.junit.runner.notification.{Failure, RunListener, RunNotifier}

class SpecRunnerSpec {
  @Test
  def mustGenerateDescriptionsCorrectly() {
    val runner = new SpecRunner(classOf[SpecExample])
    val desc = runner.getDescription

    assertEquals("com.codahale.simplespec.specs.SpecExample", desc.getDisplayName)
    assertEquals("com.codahale.simplespec.specs.SpecExample", desc.getClassName)
    assertNull(desc.getMethodName)
    assertEquals(classOf[SpecExample], desc.getTestClass)
    assertEquals(2, desc.getChildren.size())

    {
      val oneNumber = desc.getChildren.get(0)
      assertEquals("A set with one number", oneNumber.getDisplayName)
      assertEquals("A set with one number", oneNumber.getClassName)
      assertNull(oneNumber.getMethodName)
      assertNull(oneNumber.getTestClass)
      assertEquals(3, oneNumber.getChildren.size())

      {
        val hasOne = oneNumber.getChildren.get(0)
        assertEquals("has a size of one(com.codahale.simplespec.specs.SpecExample$A$u0020set$u0020with$u0020one$u0020number)", hasOne.getDisplayName)
        assertEquals("com.codahale.simplespec.specs.SpecExample$A$u0020set$u0020with$u0020one$u0020number", hasOne.getClassName)
        assertEquals("has a size of one", hasOne.getMethodName)
        assertEquals(classOf[SpecExample#`A set with one number`], hasOne.getTestClass)
      }

      {
        val notEmpty = oneNumber.getChildren.get(1)
        assertEquals("is not empty(com.codahale.simplespec.specs.SpecExample$A$u0020set$u0020with$u0020one$u0020number)", notEmpty.getDisplayName)
        assertEquals("com.codahale.simplespec.specs.SpecExample$A$u0020set$u0020with$u0020one$u0020number", notEmpty.getClassName)
        assertEquals("is not empty", notEmpty.getMethodName)
        assertEquals(classOf[SpecExample#`A set with one number`], notEmpty.getTestClass)
      }

      {
        val withRemoved = oneNumber.getChildren.get(2)
        assertEquals("having had that number removed", withRemoved.getDisplayName)
        assertEquals("having had that number removed", withRemoved.getClassName)
        assertNull(withRemoved.getMethodName)
        assertNull(withRemoved.getTestClass)
        assertEquals(2, withRemoved.getChildren.size())

        {
          val hasZero = withRemoved.getChildren.get(0)
          assertEquals("has a size of zero(com.codahale.simplespec.specs.SpecExample$A$u0020set$u0020with$u0020one$u0020number$having$u0020had$u0020that$u0020number$u0020removed)", hasZero.getDisplayName)
          assertEquals("com.codahale.simplespec.specs.SpecExample$A$u0020set$u0020with$u0020one$u0020number$having$u0020had$u0020that$u0020number$u0020removed", hasZero.getClassName)
          assertEquals("has a size of zero", hasZero.getMethodName)
          assertEquals(classOf[SpecExample#`A set with one number`#`having had that number removed`], hasZero.getTestClass)
        }

        {
          val isEmpty = withRemoved.getChildren.get(1)
          assertEquals("is empty(com.codahale.simplespec.specs.SpecExample$A$u0020set$u0020with$u0020one$u0020number$having$u0020had$u0020that$u0020number$u0020removed)", isEmpty.getDisplayName)
          assertEquals("com.codahale.simplespec.specs.SpecExample$A$u0020set$u0020with$u0020one$u0020number$having$u0020had$u0020that$u0020number$u0020removed", isEmpty.getClassName)
          assertEquals("is empty", isEmpty.getMethodName)
          assertEquals(classOf[SpecExample#`A set with one number`#`having had that number removed`], isEmpty.getTestClass)
        }
      }
    }

    {
      val emptySet = desc.getChildren.get(1)
      assertEquals("An empty set", emptySet.getDisplayName)
      assertEquals("An empty set", emptySet.getClassName)
      assertNull(emptySet.getMethodName)
      assertNull(emptySet.getTestClass)
      assertEquals(2, emptySet.getChildren.size())

      {
        val hasZero = emptySet.getChildren.get(0)
        assertEquals("has a size of zero(com.codahale.simplespec.specs.SpecExample$An$u0020empty$u0020set)", hasZero.getDisplayName)
        assertEquals("com.codahale.simplespec.specs.SpecExample$An$u0020empty$u0020set", hasZero.getClassName)
        assertEquals("has a size of zero", hasZero.getMethodName)
        assertEquals(classOf[SpecExample#`An empty set`], hasZero.getTestClass)
      }

      {
        val isEmpty = emptySet.getChildren.get(1)
        assertEquals("is empty(com.codahale.simplespec.specs.SpecExample$An$u0020empty$u0020set)", isEmpty.getDisplayName)
        assertEquals("com.codahale.simplespec.specs.SpecExample$An$u0020empty$u0020set", isEmpty.getClassName)
        assertEquals("is empty", isEmpty.getMethodName)
        assertEquals(classOf[SpecExample#`An empty set`], isEmpty.getTestClass)
      }
    }
  }

  @Test
  def mustRunTests() {
    val runner = new SpecRunner(classOf[SpecExample])
    val notifier = new RunNotifier
    val listener = new RecordingRunListener
    notifier.addListener(listener)
    runner.run(notifier)

    assertEquals(
      Vector(
        ('started, "has a size of one"),
        ('finished, "has a size of one"),
        ('started, "is not empty"),
        ('finished, "is not empty"),
        ('started, "has a size of zero"),
        ('finished, "has a size of zero"),
        ('started, "is empty"),
        ('finished, "is empty"),
        ('started, "has a size of zero"),
        ('finished, "has a size of zero"),
        ('started, "is empty"),
        ('finished, "is empty")
      ),
      listener.events
    )
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
        ('started, "should explode"),
        ('failure, "java.lang.RuntimeException:should explode"),
        ('finished, "should explode"),
        ('started, "should be ignored"),
        ('ignored, "should be ignored")
      ),
      listener.events
    )
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
    @test def `has a size of zero` = {
      numbers.size must beEqualTo(0)
    }

    @test def `is empty` = {
      numbers.isEmpty must beTrue
    }
  }

  class `A set with one number` {
    numbers += 1

    @test def `has a size of one` = {
      numbers.size must beEqualTo(1)
    }

    @test def `is not empty` = {
      numbers.isEmpty must beFalse
    }

    class `having had that number removed` {
      numbers -= 1

      @test def `has a size of zero` = {
        numbers.size must beEqualTo(0)
      }

      @test def `is empty` = {
        numbers.isEmpty must beTrue
      }
    }
  }
}

class BustedSpecExample extends Spec {
  class `An ignored test` {
    @test def `should be ignored` = {
      pending
    }
  }

  class `A failing test` {
    @test def `should fail` = {
      assertEquals(1, 2)
    }
  }

  class `An exploding test` {
    @test def `should explode` = {
      throw new RuntimeException("EFFFFF")
    }
  }
}
