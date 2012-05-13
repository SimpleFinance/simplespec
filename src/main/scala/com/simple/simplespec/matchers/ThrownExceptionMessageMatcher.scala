package com.simple.simplespec.matchers

import org.hamcrest.{Description, BaseMatcher}
import com.simple.simplespec.{Outcome, Failure}

class ThrownExceptionMessageMatcher(expectedException: Class[_],
                                    expectedMessage: String) extends BaseMatcher[Outcome[Any]] {
  def describeTo(description: Description) {
    description.appendText("throws an exception of type <" + expectedException.getName + ">")
    description.appendText(" with a message of ")
    description.appendValue(expectedMessage)
  }

  def matches(item: AnyRef) = item match {
    case Failure(e) if expectedException.isAssignableFrom(e.asInstanceOf[AnyRef].getClass) =>
      e.getMessage == expectedMessage
    case _ => false
  }
}
