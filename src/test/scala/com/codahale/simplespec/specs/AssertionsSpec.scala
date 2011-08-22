package com.codahale.simplespec.specs

import org.junit.Test
import org.junit.Assert._
import com.codahale.simplespec.{IgnoredTestException, Assertions}

class ExceptionAssertionSpec extends Assertions {
  def boom: Any = throw new RuntimeException("EFFF")
  def fizz: Any = ()

  @Test
  def mustPassIfTheExceptionIsThrown() {
    boom.mustThrowA[RuntimeException]
  }

  @Test
  def mustFailIfTheExceptionIsNotThrown() {
    var ok = true
    try {
      fizz.mustThrowA[RuntimeException]
      ok = false
    } catch {
      case e: AssertionError => {
        assertEquals("expected a java.lang.RuntimeException to be thrown, but nothing happened", e.getMessage)
      }
    }

    if (!ok) {
      fail("should have thrown an AssertionError but didn't")
    }
  }

  @Test
  def mustFailIfTheExceptionIsNotOfTheExpectedType() {
    var ok = true
    try {
      boom.mustThrowAn[UnsupportedOperationException]
      ok = false
    } catch {
      case e: AssertionError => {
        assertEquals("expected a java.lang.UnsupportedOperationException to be thrown, but a java.lang.RuntimeException was thrown instead", e.getMessage)
      }
    }

    if (!ok) {
      fail("should have thrown an AssertionError but didn't")
    }
  }
}

class BooleanAssertionSpec extends Assertions {
  @Test
  def trueMustBeTrue() {
    true.mustBeTrue()
  }

  @Test
  def trueMustNotBeFalse() {
    true.mustBeFalse().mustThrowAn[AssertionError]("expected:<false> but was:<true>")
  }

  @Test
  def falseMustBeFalse() {
    false.mustBeFalse()
  }

  @Test
  def falseMustNotBeTrue() {
    false.mustBeTrue().mustThrowAn[AssertionError]("expected:<true> but was:<false>")
  }
}

class OptionAssertionSpec extends Assertions {
  @Test
  def noneMustBeNone() {
    None.mustBeNone()
  }

  @Test
  def noneMustNotBeSome() {
    None.mustBeSome(12).mustThrowAn[AssertionError]("expected:<Some(12)> but was:<None>")
  }

  @Test
  def someMustNotBeNone() {
    Some(12).mustBeNone().mustThrowAn[AssertionError]("expected:<None> but was:<Some(12)>")
  }

  @Test
  def someMustBeAnEqualSome() {
    Some(12).mustBeSome(12)
  }

  @Test
  def someMustNotBeSomeOtherValue() {
    Some(12).mustBeSome(13).mustThrowA[AssertionError]("expected:<Some(13)> but was:<Some(12)>")
  }
}

class EqualityAssertionSpec extends Assertions {
  @Test
  def somethingMustEqualItself() {
    1.mustEqual(1)
  }

  @Test
  def somethingMustEqualItselfIfConvertible() {
    1.mustEqual(1L)
  }

  @Test
  def somethingNotMustEqualSomethingElse() {
    1.mustEqual(2).mustThrowAn[AssertionError]("expected:<2> but was:<1>")
  }
}

class PendingAssertionSpec extends Assertions {
  @Test
  def pendingMustThrowAnIgnoredTestException() {
    pending().mustThrowAn[IgnoredTestException]
  }
}

class EitherAssertionSpec extends Assertions {
  val left: Either[String, Int] = Left("woo")
  val right: Either[String, Int] = Right(1)

  @Test
  def leftMustBeLeft() {
    left.mustBeLeft("woo")
  }

  @Test
  def rightMustBeRight() {
    right.mustBeRight(1)
  }

  @Test
  def rightMustNotBeLeft() {
    right.mustBeLeft("woo").mustThrowAn[AssertionError]("expected:<Left(woo)> but was:<Right(1)>")
  }

  @Test
  def leftMustNotBeSomeOtherLeft() {
    right.mustBeRight(2).mustThrowAn[AssertionError]("expected:<Right(2)> but was:<Right(1)>")
  }
}

class TraversableSpec extends Assertions {
  val empty: List[String] = Nil
  val full = "woo" :: Nil

  @Test
  def emptyCollectionsMustBeEmpty() {
    empty.mustBeEmpty()
  }

  @Test
  def emptyCollectionsMustNotBeNotEmpty() {
    empty.mustNotBeEmpty().mustThrowAn[AssertionError]("expected: <non-empty> but was: <empty>")
  }

  @Test
  def fullCollectionsMustNotBeEmpty() {
    full.mustBeEmpty().mustThrowAn[AssertionError]("expected: <empty> but was: <non-empty>")
  }

  @Test
  def fullCollectionsMustBeNotEmpty() {
    full.mustNotBeEmpty()
  }

  @Test
  def collectionsMustHaveSizes() {
    empty.mustHaveSize(0)
    empty.mustHaveSize(1).mustThrowAn[AssertionError]("expected: <1> elements but had: <0> elements")

    full.mustHaveSize(1)
    full.mustHaveSize(0).mustThrowAn[AssertionError]("expected: <0> elements but had: <1> elements")
  }
}

class NumericSpec extends Assertions {
  @Test
  def numbersMustBeApproximate() {
    100.mustBeApproximately(101, 2)
  }

  @Test
  def numbersMustNotBeTooApproximate() {
    100.mustBeApproximately(102, 1).mustThrowAn[AssertionError]("expected: <102+/-1> but was: <100>")
    100.mustBeApproximately(98, 1).mustThrowAn[AssertionError]("expected: <98+/-1> but was: <100>")
  }
}

class EventuallySpec extends Assertions {
  var elements = 1 :: 2 :: 3 :: 4 :: Nil

  @Test
  def aConditionMustEventuallyPass() {
    pop().eventually { _.mustBeNone() }
  }

  @Test
  def aConditionMustEventuallyNotPass() {
    pop().eventually(2) { _.mustBeNone() }.mustThrowAn[AssertionError]
    pop().eventually(2) { _.mustBeSome(40) }.mustThrowAn[AssertionError]
  }

  def pop() = {
    elements match {
      case x :: xs => {
        elements = xs
        Some(x)
      }
      case Nil => None
    }
  }
}
