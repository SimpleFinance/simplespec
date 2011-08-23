package com.codahale.simplespec.specs

import org.junit.Test
import org.junit.Assert._
import com.codahale.simplespec.{Matchables, Matchers, IgnoredTestException}

class ExceptionAssertionSpec extends Matchables with Matchers {
  def boom(): Any = throw new RuntimeException("EFFF")
  def fizz(): Any = ()

  @Test
  def mustPassIfTheExceptionIsThrown() {
    evaluating {
      boom()
    }.must(throwA[RuntimeException])
  }

  @Test
  def mustFailIfTheExceptionIsNotThrown() {
    var ok = true
    try {
      evaluating {
        fizz()
      }.must(throwA[RuntimeException])
      ok = false
    } catch {
      case e: AssertionError => {
        assertEquals(
"""
Expected: throws an exception of type <java.lang.RuntimeException>
     got: <no exception thrown; <()> returned>
""", e.getMessage)
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
      evaluating {
        boom()
      }.must(throwA[UnsupportedOperationException])
      ok = false
    } catch {
      case e: AssertionError => {
        assertEquals(
"""
Expected: throws an exception of type <java.lang.UnsupportedOperationException>
     got: <an exception of type <java.lang.RuntimeException> with a message of <EFFF>>
""", e.getMessage)
      }
    }

    if (!ok) {
      fail("should have thrown an AssertionError but didn't")
    }
  }

  @Test
  def mustFailIfTheExceptionDoesNotMatchThePartialFunction() {
    var ok = true
    try {
      evaluating {
        boom()
      }.must(throwAnExceptionLike {
        case e: UnsupportedOperationException => {
          
        }
      })
      ok = false
    } catch {
      case e: AssertionError => {
        assertEquals(
"""
Expected: throws an exception which matches a partial function
     got: <an exception of type <java.lang.RuntimeException> with a message of <EFFF>>
""", e.getMessage)
      }
    }

    if (!ok) {
      fail("should have thrown an AssertionError but didn't")
    }
  }
}

class EqualityAssertionSpec extends Matchables with Matchers {
  @Test
  def somethingMustEqualItself() {
    1.must(equal(1))
  }

  @Test
  def somethingMustEqualItselfIfConvertible() {
    1.must(equal(1))
  }

  @Test
  def arraysMustEqualOtherInstancesWithTheSameElements() {
    Array(1, 2, 3).must(equal(Array(1, 2, 3)))
  }

  @Test
  def arraysMustNotEqualArraysWithOtherElements() {
    evaluating {
      Array(1, 2, 3).must(equal(Array(1, 2, 4)))
    }.must(throwAn[AssertionError](
"""
Expected: [<1>, <2>, <4>]
     got: [<1>, <2>, <3>]
"""))
  }

  @Test
  def somethingNotMustEqualSomethingElse() {
    evaluating {
      1.must(equal(2))
    }.must(throwAn[AssertionError](
"""
Expected: <2>
     got: <1>
"""))
  }
}

class PendingAssertionSpec extends Matchables with Matchers {
  @Test
  def pendingMustThrowAnIgnoredTestException() {
    evaluating {
      pending()
    }.must(throwAn[IgnoredTestException])
  }
}

class ClassSpec extends Matchables with Matchers {
  @Test
  def checkSubclasses() {
    val list: Seq[Int] = Nil
    list.must(beA[List[Int]])
  }

  @Test
  def checkInstances() {
    val obj: Object = "yay"
    obj.must(beA[String])

    evaluating {
      val list: Object = Vector.empty
      list.must(beA[List[Int]])
    }.must(throwAn[AssertionError](
"""
Expected: is an instance of scala.collection.immutable.List
     got: <Vector()>
"""))
  }
}

class NumericSpec extends Matchables with Matchers {
  @Test
  def numbersMustBeApproximate() {
    100.must(be(approximately(101, 2)))
  }

  @Test
  def numbersMustNotBeTooApproximate() {
    evaluating {
      100.must(be(approximately(102, 1)))
    }.must(throwAn[AssertionError](
"""
Expected: is <102> (+/- <1>)
     got: <100>
"""))

    evaluating {
      100.must(be(approximately(98, 1)))
    }.must(throwAn[AssertionError](
      """
Expected: is <98> (+/- <1>)
     got: <100>
"""))
  }
}

class CollectionSpec extends Matchables with Matchers {
  val a = List(1, 2, 3)
  val b: List[Int] = Nil

  @Test
  def emptyCollectionsAreEmpty() {
    b.must(be(empty))
  }

  @Test
  def nonEmptyCollectionsAreNotEmpty() {
    evaluating {
      a.must(be(empty))
    }.must(throwAn[AssertionError](
"""
Expected: is an empty collection
     got: <List(1, 2, 3)>
"""))
  }

  @Test
  def sizedCollectionsHaveSizes() {
    a.must(haveSize(3))

    evaluating {
      a.must(haveSize(40))
    }.must(throwA[AssertionError](
"""
Expected: a collection with a size of <40>
     got: <List(1, 2, 3)>
"""))
  }

  @Test
  def containsStuff() {
    a.must(contain(2))

    evaluating {
      b.must(contain(40))
    }.must(throwA[AssertionError]("""
Expected: a collection containing <40>
     got: <List()>
"""))
  }
}

class EventuallySpec extends Matchables with Matchers {
  var elements = 1 :: 2 :: 3 :: 4 :: Nil

  @Test
  def aConditionMustEventuallyPass() {
    eventually {
      pop()
    }.must(be(None))
  }

  @Test
  def aConditionMustEventuallyNotPass() {
    evaluating {
      eventually(2) {
        pop()
      }.must(be(None))
    }.must(throwAn[AssertionError]("""
Expected: is <None>
     got: <Some(2)>
"""))

    evaluating {
      eventually(2) {
        pop()
      }.must(be(Some(40)))
    }.must(throwAn[AssertionError]("""
Expected: is <Some(40)>
     got: <Some(4)>
"""))
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
