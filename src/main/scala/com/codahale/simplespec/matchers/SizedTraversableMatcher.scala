package com.codahale.simplespec.matchers

import org.hamcrest.{Description, BaseMatcher}

class SizedTraversableMatcher[A <: Traversable[_]](expectedSize: Int) extends BaseMatcher[A] {
  def describeTo(description: Description) {
    description.appendText("a collection with a size of ").appendValue(expectedSize)
  }

  def matches(item: AnyRef) = item match {
    case coll: Traversable[_] => coll.hasDefiniteSize && coll.size == expectedSize
    case _ => false
  }
}
