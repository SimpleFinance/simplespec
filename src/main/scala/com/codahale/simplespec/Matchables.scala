package com.codahale.simplespec

import org.hamcrest.Matcher
import org.junit.Assert
import com.codahale.simplespec.matchers.ApproximateNumericMatcher

trait Matchables {
  implicit def any2LiteralMatchable[A](value: A) = new LiteralMatchable[A](value)

  def evaluating[A](f: => A) = new OutcomeMatchable[A](() => f)

  def eventually[A](f: => A) = new EventuallyMatchable[A](() => f, 20)

  def eventually[A](maxAttempts: Int)(f: => A) = new EventuallyMatchable[A](() => f, maxAttempts)

  def approximately[A](expected: A, delta: A)(implicit num: Numeric[A]) = new ApproximateNumericMatcher[A](expected, delta, num)
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
