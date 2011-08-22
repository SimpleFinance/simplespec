package com.codahale.simplespec

import org.junit.Assert._
import scala.util.matching.Regex
import org.mockito.internal.matchers.Matches

private[simplespec] class IgnoredTestException extends Exception

trait Assertions {
  /**
   * Fail the test immediately.
   */
  def fail(): Any = org.junit.Assert.fail()

  /**
   * Fail the test immediately with the given message.
   */
  def fail(message: String): Any = org.junit.Assert.fail(message)

  /**
   * Ignore all following assertions in the test.
   */
  def pending(): Any = throw new IgnoredTestException
  
  implicit def any2Assertable[A](value: A) = new AssertableAny[A](value)
  implicit def anyRef[A <: AnyRef](value: A) = new AssertableAnyRef[A](value)
  implicit def bool2Assertable(value: Boolean) = new AssertableBoolean(value)
  implicit def opt2Assertable[A](value: Option[A]) = new AssertableOption[A](value)
  implicit def either2Assertable[A, B](value: Either[A, B]) = new AssertableEither[A, B](value)
  implicit def traversableLike2Assertable[A](value: Traversable[A]) = new AssertableTraversable[A](value)
  implicit def lambda2Assertable[A](value: => A) = new AssertableLambda[A](() => value)
  implicit def numeric2Assertable[A](value: A) = new AssertableNumeric[A](value)
}

class AssertableAny[A](actual: A) {
  /**
   * Assert that the value is equal to the given value.
   */
  def mustEqual(expected: Any): Any = {
    if (actual != expected) {
      assertEquals(expected, actual)
    }
  }

  /**
   * Assert that the value is null.
   */
  def mustBeNull(): Any = {
    assertNull(actual)
  }

  /**
   * Assert that the value is not null.
   */
  def mustBeNotNull(): Any = {
    assertNotNull(actual)
  }
}

class AssertableAnyRef[A <: AnyRef](actual: A) {
  /**
   * Assert that the value is an instance of the given type.
   */
  def mustBeAnInstanceOf[B](implicit mf: Manifest[B]): Any = {
    if (!mf.erasure.isAssignableFrom(actual.getClass)) {
      fail("expected: an instance of <" + mf.erasure.getName +
        "> but was: an instance of <" + actual.getClass.getName + ">")
    }
  }
}

class AssertableBoolean(actual: Boolean) {
  /**
   * Assert that the value is true.
   */
  def mustBeTrue(): Any = {
    assertEquals(true, actual)
  }

  /**
   * Assert that the value is false.
   */
  def mustBeFalse(): Any = {
    assertEquals(false, actual)
  }
}

class AssertableOption[A](actual: Option[A]) {
  /**
   * Assert that the value is None.
   */
  def mustBeNone(): Any = {
    assertEquals(None, actual)
  }

  /**
   * Assert that the value is Some(x).
   */
  def mustBeSome(expected: Any): Any = {
    val realExpected = Some(expected)
    if (actual.isDefined) {
      if (actual != realExpected) {
        assertEquals(realExpected, actual)
      }
    } else assertEquals(realExpected, actual)
  }
}

class AssertableLambda[A](lambda: () => A) {
  /**
   * Assert that the left-hand lambda throws an exception of the given type.
   */
  def mustThrowA[E <: Throwable]()(implicit mf: Manifest[E]): Any = {
    val klass = mf.erasure
    var ok = true
    try {
      lambda()
      ok = false
    } catch {
      case e: Throwable => {
        assertTrue("expected a " + klass.getName + " to be thrown, but a " + e.getClass.getName + " was thrown instead",
          klass.isAssignableFrom(e.getClass))
      }
    }

    if (!ok) {
      fail("expected a " + klass.getName + " to be thrown, but nothing happened")
    }
  }

  /**
   * Assert that the left-hand lambda throws an exception of the given type.
   */
  def mustThrowAn[E <: Throwable]()(implicit mf: Manifest[E]): Any =
    mustThrowA[E]()(mf)

  /**
   * Assert that the left-hand lambda throws an exception of the given type and
   * with the given message.
   */
  def mustThrowA[E <: Throwable](message: String)(implicit mf: Manifest[E]): Any = {
    val klass = mf.erasure
    var ok = true
    try {
      lambda()
      ok = false
    } catch {
      case e: Throwable => {
        assertTrue("expected a " + klass.getName + " to be thrown, but a " + e.getClass.getName + " was thrown instead",
          klass.isAssignableFrom(e.getClass))
        assertEquals(message, e.getMessage)
      }
    }

    if (!ok) {
      fail("expected a " + klass.getName + " to be thrown, but nothing happened")
    }
  }

  /**
   * Assert that the left-hand lambda throws an exception of the given type and
   * with the given message.
   */
  def mustThrowAn[E <: Throwable](message: String)(implicit mf: Manifest[E]): Any =
    mustThrowA[E](message)(mf)

  /**
   * Assert that the left-hand lambda throws an exception of the given type and
   * with a message which matches the given regular expression.
   */
  def mustThrowA[E <: Throwable](pattern: Regex)(implicit mf: Manifest[E]): Any = {
    val klass = mf.erasure
    var ok = true
    try {
      lambda()
      ok = false
    } catch {
      case e: Throwable => {
        assertTrue("expected a " + klass.getName + " to be thrown, but a " + e.getClass.getName + " was thrown instead",
          klass.isAssignableFrom(e.getClass))
        assertThat(e.getMessage, new Matches(pattern.toString()))
      }
    }

    if (!ok) {
      fail("expected a " + klass.getName + " to be thrown, but nothing happened")
    }
  }

  /**
   * Assert that the left-hand lambda throws an exception of the given type and
   * with a message which matches the given regular expression.
   */
  def mustThrowAn[E <: Throwable](pattern: Regex)(implicit mf: Manifest[E]): Any =
    mustThrowA[E](pattern)(mf)

  /**
   * Assert that the left-hand lambda throws an exception which matches the
   * given partial function.
   */
  def mustThrowA(pf: PartialFunction[Throwable, Any]): Any = {
    var ok = true
    try {
      lambda()
      ok = false
    } catch {
      case e: Throwable => {
        if (pf.isDefinedAt(e)) {
          pf(e)
        } else {
          fail("expected something besides a " + e.getClass.getName + " to be thrown")
        }
      }
    }

    if (!ok) {
      fail("expected an exception to be thrown, but nothing happened")
    }
  }

  /**
   * Assert that the left-hand lambda will eventually pass the provided
   * assertions given 20 attempts.
   */
  def eventually(condition: A => Any): Any =
    eventually(20)(condition)

  /**
   * Assert that the left-hand lambda will eventually pass the provided
   * assertions given sufficient attempts.
   */
  def eventually(attempts: Int)(condition: A => Any): Any = {
    for (i <- 1 to attempts) {
      try {
        condition(lambda())
        return ()
      } catch {
        case e: AssertionError if i < attempts => // bury it
      }
    }
  }
}

class AssertableEither[L, R](actual: Either[L, R]) {
  /**
   * Assert that the value is Right(x).
   */
  def mustBeRight(expected: R): Any = {
    assertEquals(Right(expected), actual)
  }

  /**
   * Assert that the value is Left(x).
   */
  def mustBeLeft(expected: L): Any = {
    assertEquals(Left(expected), actual)
  }
}

class AssertableTraversable[A](actual: Traversable[A]) {
  /**
   * Assert that the value is empty.
   */
  def mustBeEmpty(): Any = {
    if (!actual.isEmpty) {
      fail("expected: <empty> but was: <non-empty>")
    }
  }

  /**
   * Assert that the value is not empty.
   */
  def mustNotBeEmpty(): Any = {
    if (actual.isEmpty) {
      fail("expected: <non-empty> but was: <empty>")
    }
  }

  /**
   * Assert that the value has the expected size.
   */
  def mustHaveSize(expected: Int): Any = {
    val size = actual.size
    if (size != expected) {
      fail("expected: <" + expected + "> elements but had: <" + size + "> elements")
    }
  }
}

class AssertableNumeric[A](actual: A) {
  /**
   * Assert that the value is approximately equal to another value with a given
   * bound.
   */
  def mustBeApproximately(expected: A, delta: A)(implicit num: Numeric[A]): Any = {
    val lowerBound = num.minus(expected, delta)
    val upperBound = num.plus(expected, delta)
    if (num.lt(actual, lowerBound) || num.gt(actual, upperBound)) {
      fail("expected: <" + expected + "+/-" + delta + "> but was: <" + actual + ">")
    }
  }
}
