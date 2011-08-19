package com.codahale.simplespec

import org.junit.Assert._

trait Matchers extends AnyMatchers {
  def pending = throw new IgnoredTestException

  implicit def any2ActualValue[T](t: => T) = new ActualValue(() => t)
}

class ActualValue[+T](val value: () => T) {
  def must(m: => Matcher[T]): Any = m(this)
}

trait Matcher[-T] {
  def apply[S <: T](t: ActualValue[S])
}

trait AnyMatchers {
  def beTrue = new BeEqualTo(true)

  def beFalse = new BeEqualTo(false)

  def beSome[T](expected: T) = new BeEqualTo(Some(expected))

  def beNone = new BeEqualTo(None)

  def beEqualTo(expected: Any) = new BeEqualTo(expected)

  def throwA[E <: Throwable](implicit m: ClassManifest[E]): ExceptionClassMatcher = new ExceptionClassMatcher(m.erasure)

  // def beNull
}

class ExceptionClassMatcher(klass: Class[_]) extends Matcher[Any] {
  def apply[S <: Any](t: ActualValue[S]) {
    try {
      t.value()
      fail("expected a " + klass.getName + " to be thrown, but nothing happened")
    } catch {
      case e: Throwable if !e.isInstanceOf[AssertionError] => {
        assertTrue("expected a " + klass.getName + " to be thrown, but a " + e.getClass + " was thrown instead",
          klass.isAssignableFrom(e.getClass))
      }
    }
  }
}

class BeEqualTo[T](expected: => T) extends Matcher[T] {
  def apply[S <: T](variable: ActualValue[S]) {
    val v = variable.value()
    val e = expected
    
    if (e != v) {
      assertEquals(e, v)
    }
  }
}
