package com.simple.simplespec.matchers

import org.hamcrest.{Description, BaseMatcher}

class GreaterThanNumericMatcher[A](floor: A,
                                   num: Numeric[A]) extends BaseMatcher[A] {
  def describeTo(description: Description) {
    description.appendText("greater than ").appendValue(floor)
  }

  def matches(item: AnyRef) = if (item.getClass.isAssignableFrom(floor.asInstanceOf[AnyRef].getClass)) {
    val actual = item.asInstanceOf[A]
    num.gt(actual, floor)
  } else false
}

class GreaterThanOrEqualToNumericMatcher[A](floor: A,
                                            num: Numeric[A]) extends BaseMatcher[A] {
  def describeTo(description: Description) {
    description.appendText("greater than or equal to ").appendValue(floor)
  }

  def matches(item: AnyRef) = if (item.getClass.isAssignableFrom(floor.asInstanceOf[AnyRef].getClass)) {
    val actual = item.asInstanceOf[A]
    num.gteq(actual, floor)
  } else false
}
