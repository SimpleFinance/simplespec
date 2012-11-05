package com.simple.simplespec

import org.hamcrest.Matcher
import org.junit.Assert
import org.scalacheck.{Prop, Test, Pretty}

trait Matchables {
  implicit def any2LiteralMatchable[A](value: A) = new LiteralMatchable[A](value)

  /**
   * Match the result of evaluating the given closure.
   */
  def evaluating[A](f: => A) = new OutcomeMatchable[A](() => f)

  /**
   * Match the eventual result of evaluating the given closure.
   */
  def eventually[A](f: => A) = new EventuallyMatchable[A](() => f, 20)

  /**
   * Match the eventual result of evaluating the given closure a maximum number
   * of times.
   */
  def eventually[A](maxAttempts: Int)(f: => A) = new EventuallyMatchable[A](() => f, maxAttempts)

  implicit def propToPropMatchable(p: Prop): PropMatchable = {
    new PropMatchable(p)
  }
}

class LiteralMatchable[A](actual: A) {
  def must(condition: Matcher[_ <: A]): Any = {
    must(condition, "")
  }

  def must(condition: Matcher[_ <: A], reason: String): Any = {
    Assert.assertThat(reason, actual, condition.asInstanceOf[Matcher[A]])
  }
}

class OutcomeMatchable[A](context: () => A) {
  def must(condition: Matcher[Outcome[Any]]): Any = {
    must(condition, "")
  }

  def must(condition: Matcher[Outcome[Any]], reason: String): Any = {
    val actual = try {
      Success(context())
    } catch {
      case e: Throwable => Failure(e)
    }
    Assert.assertThat(
      reason, actual, condition.asInstanceOf[Matcher[Outcome[A]]])
  }
}

class EventuallyMatchable[A](context: () => A, maxAttempts: Int) {
  def must(condition: Matcher[_ <: A]): Any = {
    must(condition, "")
  }

  def must(condition: Matcher[_ <: A], reason: String): Any = {
    for (i <- 1 to maxAttempts) {
      try {
        Assert.assertThat(
          reason, context(), condition.asInstanceOf[Matcher[A]])
        return ()
      } catch {
        case e: AssertionError if i < maxAttempts => // bury it
      }
    }
  }
}

/**
 * A matchable that matches a ScalaCheck property.
 */
class PropMatchable(p: Prop) {
  def must(condition: Matcher[Test.Result]) {
    val actual = Test.check(Test.Parameters.default, p)
    Assert.assertThat(Pretty.prettyTestRes(actual)(Pretty.defaultParams),
                      actual, condition)
  }
}
