package com.codahale.simplespec.matchers

import org.hamcrest.{Description, BaseMatcher}

class LessThanNumericMatcher[A](ceiling: A,
                                num: Numeric[A]) extends BaseMatcher[A] {
  def describeTo(description: Description) {
    description.appendText("less than ").appendValue(ceiling)
  }

  def matches(item: AnyRef) = if (item.getClass.isAssignableFrom(ceiling.asInstanceOf[AnyRef].getClass)) {
    val actual = item.asInstanceOf[A]
    num.lt(actual, ceiling)
  } else false
}

class LessThanOrEqualToNumericMatcher[A](ceiling: A,
                                         num: Numeric[A]) extends BaseMatcher[A] {
  def describeTo(description: Description) {
    description.appendText("less than or equal to ").appendValue(ceiling)
  }

  def matches(item: AnyRef) = if (item.getClass.isAssignableFrom(ceiling.asInstanceOf[AnyRef].getClass)) {
    val actual = item.asInstanceOf[A]
    num.lteq(actual, ceiling)
  } else false
}
