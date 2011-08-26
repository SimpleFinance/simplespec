package com.codahale.simplespec.matchers

import org.hamcrest.{Description, BaseMatcher}
import com.codahale.simplespec.{Outcome, Failure}

class ThrownExceptionMatcher(expectedException: Class[_]) extends BaseMatcher[Outcome[Any]] {
  def describeTo(description: Description) {
    description.appendText("throws an exception of type <" + expectedException.getName + ">")
  }

  def matches(item: AnyRef) = item match {
    case Failure(e) if expectedException.isAssignableFrom(e.asInstanceOf[AnyRef].getClass) => true
    case _ => false
  }
}
