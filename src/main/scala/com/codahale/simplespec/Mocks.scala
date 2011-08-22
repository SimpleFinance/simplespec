package com.codahale.simplespec

import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock
import org.hamcrest.Matcher
import org.mockito.{InOrder, Matchers => MockitoMatchers, Mockito}
import scala.util.matching.Regex

trait Mocks {
  /**
   * Returns a mock object of the given type.
   */
  def mock[A](implicit mf: Manifest[A]): A = Mockito.mock(mf.erasure.asInstanceOf[Class[A]])

  /**
   * Returns a spy for the given object.
   */
  def spy[A](o: A) = Mockito.spy(o)

  /**
   * Returns a matcher which will accept any instance.
   */
  def any[A](implicit mf: Manifest[A]) = MockitoMatchers.any(mf.erasure.asInstanceOf[Class[A]])

  /**
   * Returns a matcher which will accept any instance of the given type.
   */
  def isA[A](implicit mf: Manifest[A]) = MockitoMatchers.isA(mf.erasure.asInstanceOf[Class[A]])

  /**
   * Returns a matcher which will accept any instance of the given type which is
   * equal to the given value.
   */
  def equalTo[A](value: A): A = MockitoMatchers.eq(value)

  /**
   * Returns a matcher which will accept only the same instance as the given
   * value.
   */
  def same[A](value: A): A = MockitoMatchers.same(value)

  /**
   * Returns a matcher which will accept only null values.
   */
  def isNull[A]: A = MockitoMatchers.isNull.asInstanceOf[A]

  /**
   * Returns a matcher which will accept only non-null values.
   */
  def isNotNull[A]: A = MockitoMatchers.isNotNull.asInstanceOf[A]

  /**
   * Returns a matcher which will accept only strings which contain the given
   * substring.
   */
  def contains(substring: String) = MockitoMatchers.contains(substring)

  /**
   * Returns a matcher which will accept only strings which match the given
   * pattern.
   */
  def matches(pattern: Regex) = MockitoMatchers.matches(pattern.toString())

  /**
   * Returns a matcher which will accept only strings which end with the given
   * suffix.
   */
  def endsWith(suffix: String) = MockitoMatchers.endsWith(suffix)

  /**
   * Returns a matcher which will accept only strings which start with the given
   * prefix.
   */
  def startsWith(prefix: String) = MockitoMatchers.startsWith(prefix)

  /**
   * Returns a matcher which will accept values which match the given matcher.
   */
  def somethingThat[A](condition: Matcher[A]) = MockitoMatchers.argThat(condition)

  /**
   * Resets the given mock or stub's previous behavior.
   */
  def reset[A](m: A) {
    Mockito.reset(m)
  }

  /**
   * Start a new verification context.
   */
  def verify = new VerificationContext

  implicit def any2Stubbable[A](method: A) = new Stubbable[A](method)
}

class Stubbable[A](method: A) {
  /**
   * Make the given mock invocation return the given value(s).
   */
  def returns(returnValue: A, optionalOthers: A*) {
    Mockito.when(method).thenReturn(returnValue, optionalOthers:_*)
  }

  /**
   * Make the given mock invocation throw the given exception(s).
   */
  def throws(e: Throwable*) {
    Mockito.when(method).thenThrow(e:_*)
  }

  /**
   * Make the given mock invocation evaluate the given function.
   */
  def answersWith(f: InvocationOnMock => A) {
    Mockito.when(method).thenAnswer(new LambdaAnswer[A](f))
  }
}

class LambdaAnswer[A](f: InvocationOnMock => A) extends Answer[A] {
  def answer(invocation: InvocationOnMock) = f(invocation)
}

class VerificationContext {
  /**
   * Verifies that a method was called with the given arguments exactly once.
   */
  def one[A](mock: A) = Mockito.verify(mock, Mockito.times(1))

  /**
   * Verifies that a given set of interactions with the given set of mocks
   * happened in the given order.
   */
  def inOrder(mocks: AnyRef*)(f: OrderedVerificationContext => Any) {
    val order = Mockito.inOrder(mocks: _*)
    val context = new OrderedVerificationContext(order)
    f(context)
  }

  /**
   * Verifies that a method was called with the given arguments at least the
   * given number of times.
   */
  def atLeast[A](invocationCount: Int)(mock: A) = Mockito.verify(mock, Mockito.atLeast(invocationCount))

  /**
   * Verifies that a method was called with the given arguments at most the
   * given number of times.
   */
  def atMost[A](invocationCount: Int)(mock: A) = Mockito.verify(mock, Mockito.atMost(invocationCount))

  /**
   * Verifies that a method was called with the given arguments at least once.
   */
  def atLeastOne[A](mock: A) = Mockito.verify(mock, Mockito.atLeastOnce())

  /**
   * Verifies that a method was called with the given arguments exactly the
   * given number of times.
   */
  def exactly[A](invocationCount: Int)(mock: A) = Mockito.verify(mock, Mockito.times(invocationCount))

  /**
   * Verifies that the method called with the given arguments was the only
   * invocation of the mock.
   */
  def only[A](mock: A) = Mockito.verify(mock, Mockito.only())

  /**
   * Verifies that the given mocks had no further interactions.
   */
  def noMoreInteractionsWith[A <: AnyRef](mocks: A*) {
    Mockito.verifyNoMoreInteractions(mocks: _*)
  }

  /**
   * Verifies that the given mocks had no interactions at all.
   */
  def noInteractionsWith[A <: AnyRef](mocks: A*) {
    Mockito.verifyZeroInteractions(mocks: _*)
  }
}

class OrderedVerificationContext(inOrder: InOrder) {
  /**
   * Verifies that a method was called with the given arguments exactly once.
   */
  def one[A](mock: A) = inOrder.verify(mock, Mockito.times(1))

  /**
   * Verifies that a method was called with the given arguments at least the
   * given number of times.
   */
  def atLeast[A](invocationCount: Int)(mock: A) = inOrder.verify(mock, Mockito.atLeast(invocationCount))

  /**
   * Verifies that a method was called with the given arguments at most the
   * given number of times.
   */
  def atMost[A](invocationCount: Int)(mock: A) = inOrder.verify(mock, Mockito.atMost(invocationCount))

  /**
   * Verifies that a method was called with the given arguments at least once.
   */
  def atLeastOne[A](mock: A) = inOrder.verify(mock, Mockito.atLeastOnce())

  /**
   * Verifies that a method was called with the given arguments exactly the
   * given number of times.
   */
  def exactly[A](invocationCount: Int)(mock: A) = inOrder.verify(mock, Mockito.times(invocationCount))

  /**
   * Verifies that the method called with the given arguments was the only
   * invocation of the mock.
   */
  def only[A](mock: A) = inOrder.verify(mock, Mockito.only())

  /**
   * Verifies that the given mocks had no further interactions.
   */
  def noMoreInteractions() {
    inOrder.verifyNoMoreInteractions()
  }
}
