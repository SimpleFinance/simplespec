package com.codahale.simplespec

import org.hamcrest._
import org.junit.internal.matchers.CombinableMatcher
import com.codahale.simplespec.matchers._
import scala.util.matching.Regex
import scala.collection.{SeqLike, TraversableLike}

trait Matchers extends Matchables with Mocks {
  /**
   * Allows for the natural combination of matchers:
   *
   * {@code must(be(1).or(be(2))}
   */
  implicit def matcher2CombinableMatcher[A](matcher: Matcher[A]) = new CombinableMatcher(matcher)

  /**
   * Fail the test immediately.
   */
  def fail(): Any = org.junit.Assert.fail()

  /**
   * Fail the test immediately with the given message.
   */
  def fail(message: String): Any = org.junit.Assert.fail(message)

  /**
   * Does the context throw an exception of the given type?
   */
  def throwA[E <: Throwable](implicit mf: Manifest[E]) = new ThrownExceptionMatcher(mf.erasure)

  /**
   * Does the context throw an exception of the given type?
   */
  def throwAn[E <: Throwable](implicit mf: Manifest[E]) = throwA[E](mf)

  /**
   * Does the context throw an exception of the given type and with the given
   * message?
   */
  def throwA[E <: Throwable](message: String)(implicit mf: Manifest[E]) = new ThrownExceptionMessageMatcher(mf.erasure, message)

  /**
   * Does the context throw an exception of the given type and with the given
   * message?
   */
  def throwAn[E <: Throwable](message: String)(implicit mf: Manifest[E]) = throwA[E](message)(mf)

  /**
   * Does the context throw an exception of the given type and with a message
   * which matches the given pattern?
   */
  def throwA[E <: Throwable](pattern: Regex)(implicit mf: Manifest[E]) = new ThrownExceptionPatternMatcher(mf.erasure, pattern)

  /**
   * Does the context throw an exception of the given type and with a message
   * which matches the given pattern?
   */
  def throwAn[E <: Throwable](pattern: Regex)(implicit mf: Manifest[E]) = throwA[E](pattern)(mf)

  /**
   * Does the context throw an exception of the given type and which matches the
   * given partial function?
   */
  def throwAnExceptionLike(pf: PartialFunction[Throwable, Any]) = new ThrownExceptionFunctionMatcher(pf)

  /**
   * Is the value equal to the expected value? (e.g., {@code ==})
   */
  def equal[A](expected: A) = CoreMatchers.equalTo(expected)

  /**
   * Inverts the given condition.
   */
  def not[A](condition: Matcher[A]) = CoreMatchers.not(condition)

  /**
   * Syntactic sugar for the given condition.
   */
  def be[A](condition: Matcher[_ <: A]) = CoreMatchers.is(condition)

  /**
   * A shortcut for equal().
   */
  def be[A](value: A) = CoreMatchers.is(value)

  /**
   * Is the value an instance of the given type?
   */
  def beA[A <: AnyRef](implicit mf: Manifest[A]) = CoreMatchers.is(CoreMatchers.instanceOf(mf.erasure)).asInstanceOf[Matcher[_ <: A]]

  /**
   * Is the value an empty traversable?
   */
  def empty[A <: TraversableLike[_, _]] = new EmptyTraversableMatcher[A]

  /**
   * Is the value a traversable with the given size?
   */
  def haveSize[A <: TraversableLike[_, _]](expectedSize: Int) = new SizedTraversableMatcher[A](expectedSize)

  /**
   * Is the value a seq which contains the given element?
   */
  def contain[A <: SeqLike[_, _]](element: Any) = new SeqLikeContainsMatcher[A](element)

  /**
   * Is the value not null?
   */
  // FWIW, this is the only expressed negative in the matchers for a reason.
  // I'd rather express nullity as a double-negative in tests to express how
  // ungainly it is to deal with in an API.
  def notNull[A] = CoreMatchers.notNullValue[A]()
}
