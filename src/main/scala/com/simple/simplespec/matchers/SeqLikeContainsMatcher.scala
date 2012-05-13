package com.simple.simplespec.matchers

import scala.collection.SeqLike
import org.hamcrest.{Description, BaseMatcher}

class SeqLikeContainsMatcher[A <: SeqLike[_, _]](element: Any) extends BaseMatcher[A] {
  def describeTo(description: Description) {
    description.appendText("a collection containing ").appendValue(element)
  }

  def matches(item: AnyRef) = item match {
    case coll: SeqLike[_, _] => coll.contains(element)
    case _ => false
  }
}
