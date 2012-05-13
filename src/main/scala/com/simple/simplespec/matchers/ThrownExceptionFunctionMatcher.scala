package com.simple.simplespec.matchers

import org.hamcrest.{Description, BaseMatcher}
import com.simple.simplespec.{Failure, Outcome}

class ThrownExceptionFunctionMatcher(pf: PartialFunction[Throwable, Any])
  extends BaseMatcher[Outcome[Any]] {

  def describeTo(description: Description) {
    description.appendText("throws an exception which matches a partial function")
  }

  def matches(item: AnyRef) = item match {
    case Failure(e) if pf.isDefinedAt(e) => {
      pf(e)
      true
    }
    case _ => false
  }
}
