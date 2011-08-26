package com.codahale.simplespec.matchers

import org.hamcrest.{Description, BaseMatcher}

class ApproximateNumericMatcher[A](expected: A,
                                   delta: A,
                                   num: Numeric[A]) extends BaseMatcher[A] {
  def describeTo(description: Description) {
    description.appendValue(expected).appendText(" (+/- ").appendValue(delta).appendText(")")
  }

  def matches(item: AnyRef) = if (item.getClass.isAssignableFrom(expected.asInstanceOf[AnyRef].getClass)) {
    val actual = item.asInstanceOf[A]
    val lowerBound = num.minus(expected, delta)
    val upperBound = num.plus(expected, delta)
    !(num.lt(actual, lowerBound) || num.gt(actual, upperBound))
  } else false
}
