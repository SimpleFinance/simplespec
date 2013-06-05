/**
 * © 2013 Simple Finance Technology Corp. All rights reserved.
 * Author: Ian Eure <ieure@simple.com>
 */
package com.simple.simplespec.matchers

import org.scalacheck.Test
import org.hamcrest.{Description, BaseMatcher}

/**
 * Matcher indicating that a property holds.
 *
 * This is a looser check than ProvedPropertyMatcher.
 */
class HeldPropertyMatcher[T <: Test.Result] extends BaseMatcher[T] {

  def matches(r: Any) = r.asInstanceOf[T].passed

  def describeTo(desc: Description) = {
    desc.appendText("property to hold")
  }
}

/**
 * Matcher indicating that a property proves.
 *
 * This is a stricter check than HeldPropertyMatcher.
 */
class ProvedPropertyMatcher[T <: Test.Result] extends BaseMatcher[T] {

  def matches(r: Any) = r.asInstanceOf[T].status match {
    case Test.Proved(_) => true
    case _ => false
  }

  def describeTo(desc: Description) = {
    desc.appendText("property to prove")
  }
}
