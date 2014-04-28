/**
 * © 2013 Simple Finance Technology Corp. All rights reserved.
 * Author: Ian Eure <ieure@simple.com>
 */
package com.simple.simplespec.matchers

import org.scalacheck.Test
import org.scalacheck.util.Pretty
import org.hamcrest.{Description, BaseMatcher}

trait PropErrorMatcher {
  private def pretty(r: Test.Result): String = {
    Pretty.prettyTestRes(r)(Pretty.defaultParams)
  }

  def throwIfError(r: Test.Result) {
    r.status match {
      case Test.PropException(_, e, _) =>
        throw new RuntimeException(pretty(r), e)

      case Test.GenException(e) =>
        throw new RuntimeException(pretty(r), e)
      case _ =>
    }
  }
}

/**
 * Matcher indicating that a property holds.
 *
 * This is a looser check than ProvedPropertyMatcher.
 */
class HeldPropertyMatcher[T <: Test.Result] extends BaseMatcher[T]
with PropErrorMatcher {

  def matches(rr: Any) = {
    val r = rr.asInstanceOf[T]
    throwIfError(r)
    r.status match {
      case Test.Passed => true
      case Test.Proved(_) => true
      case _ => false
    }
  }

  def describeTo(desc: Description) = {
    desc.appendText("property to hold")
  }
}

/**
 * Matcher indicating that a property proves.
 *
 * This is a stricter check than HeldPropertyMatcher.
 */
class ProvedPropertyMatcher[T <: Test.Result] extends BaseMatcher[T]
with PropErrorMatcher {

  def matches(rr: Any) = {
    val r = rr.asInstanceOf[T]
    throwIfError(r)
    r.status match {
      case Test.Proved(_) => true
      case _ => false
    }
  }

  def describeTo(desc: Description) = {
    desc.appendText("property to prove")
  }
}
