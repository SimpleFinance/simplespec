package com.codahale.simplespec.matchers

import org.hamcrest.{Description, BaseMatcher}

class EmptyTraversableMatcher[A <: Traversable[_]] extends BaseMatcher[A] {
  def describeTo(description: Description) {
    description.appendText("an empty collection")
  }

  def matches(item: AnyRef) = item match {
    case coll: Traversable[_] => coll.isEmpty
    case _ => false
  }
}
