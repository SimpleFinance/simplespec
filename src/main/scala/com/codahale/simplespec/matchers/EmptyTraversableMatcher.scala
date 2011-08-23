package com.codahale.simplespec.matchers

import scala.collection.TraversableLike
import org.hamcrest.{Description, BaseMatcher}

class EmptyTraversableMatcher[A <: TraversableLike[_, _]] extends BaseMatcher[A] {
  def describeTo(description: Description) {
    description.appendText("an empty collection")
  }

  def matches(item: AnyRef) = item match {
    case coll: TraversableLike[_, _] => coll.isEmpty
    case _ => false
  }
}
