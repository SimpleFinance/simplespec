package com.codahale.simplespec.specs

import org.junit.Assert._
import com.codahale.simplespec.Matchers
import org.junit.{Ignore, Test}

class MatchersSpec extends Matchers {
  @Test
  def mustConsiderTwoLiteralsEqual() {
    1 must beEqualTo(1)
  }

  @Test
  def mustConsiderTwoLiteralsOfDifferentTypesEqual() {
    1L must beEqualTo(1)
  }

  @Test
  def mustNotConsiderTwoDifferentValuesEqual() {
    var ok = true
    try {
      1L must beEqualTo(2)
      ok = false
    } catch {
      case e: AssertionError => {
        assertEquals("expected:<2> but was:<1>", e.getMessage)
      }
    }

    if (!ok) {
      fail("should have thrown an AssertionError but didn't")
    }
  }

  @Test
  def mustConsiderTwoDifferentCollectionTypesEqual() {
    List(1, 2, 3) must beEqualTo(Vector(1, 2, 3))
  }

  @Test
  @Ignore
  def mustConsiderTwoArraysEqual() {
    // TODO: 8/19/11 <coda> -- figure out how to support arrays
    Array(1, 2, 3) must beEqualTo(Array(1, 2, 3))
  }
}
