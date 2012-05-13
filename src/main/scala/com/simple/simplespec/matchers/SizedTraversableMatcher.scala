package com.simple.simplespec.matchers

import scala.collection.TraversableLike
import org.hamcrest.{Description, BaseMatcher}

class SizedTraversableMatcher[A <: TraversableLike[_, _]](expectedSize: Int) extends BaseMatcher[A] {
  def describeTo(description: Description) {
    description.appendText("a collection with a size of ").appendValue(expectedSize)
  }

  def matches(item: AnyRef) = item match {
    case coll: TraversableLike[_, _] => coll.hasDefiniteSize && coll.size == expectedSize
    case _ => false
  }
}
