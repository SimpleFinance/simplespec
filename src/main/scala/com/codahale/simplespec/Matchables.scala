package com.codahale.simplespec

import org.hamcrest.Matcher
import org.junit.Assert

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
}

class LiteralMatchable[A](actual: A) {
  def must(condition: Matcher[_ <: A]): Any = {
    Assert.assertThat(actual, condition.asInstanceOf[Matcher[A]])
  }
}

class OutcomeMatchable[A](context: () => A) {
  def must(condition: Matcher[Outcome[Any]]): Any = {
    val actual = try {
      Success(context())
    } catch {
      case e: Throwable => Failure(e)
    }
    Assert.assertThat(actual, condition.asInstanceOf[Matcher[Outcome[A]]])
  }
}

class EventuallyMatchable[A](context: () => A, maxAttempts: Int) {
  def must(condition: Matcher[_ <: A]): Any = {
    for (i <- 1 to maxAttempts) {
      try {
        Assert.assertThat(context(), condition.asInstanceOf[Matcher[A]])
        return ()
      } catch {
        case e: AssertionError if i < maxAttempts => // bury it
      }
    }
  }
}
