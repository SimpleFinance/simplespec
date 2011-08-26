package com.codahale.simplespec.matchers

import scala.util.matching.Regex
import com.codahale.simplespec.{Failure, Outcome}
import org.hamcrest.{Description, BaseMatcher}

class ThrownExceptionPatternMatcher(expectedException: Class[_],
                                    expectedPattern: Regex) extends BaseMatcher[Outcome[Any]] {
  def describeTo(description: Description) {
    description.appendText("throws an exception of type <" + expectedException.getName + ">")
      .appendText(" with a message which matches <")
      .appendText(expectedPattern.toString())
      .appendText(">")
  }

  def matches(item: AnyRef) = item match {
    case Failure(e) if expectedException.isAssignableFrom(e.asInstanceOf[AnyRef].getClass) =>
      expectedPattern.findFirstIn(e.getMessage).isDefined
    case _ => false
  }
}
