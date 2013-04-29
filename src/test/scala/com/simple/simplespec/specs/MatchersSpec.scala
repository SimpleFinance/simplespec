package com.simple.simplespec.specs

import org.junit.Test
import org.junit.Assert._
import com.simple.simplespec.{Spec, Matchables, Matchers}

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
      case e: AssertionError => {}
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
      case e: AssertionError => {}
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
      case e: AssertionError => {}
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
    }.must(throwAn[AssertionError])
  }

  @Test
  def somethingNotMustEqualSomethingElse() {
    evaluating {
      1.must(equal(2))
    }.must(throwAn[AssertionError])
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
    }.must(throwAn[AssertionError])
  }
}

class NumericSpec extends Matchables with Matchers {
  @Test
  def numbersMustBeApproximate() {
    100.must(be(approximately(101, 2)))
    
    evaluating {
      100.must(be(approximately(102, 1)))
    }.must(throwAn[AssertionError])

    evaluating {
      100.must(be(approximately(98, 1)))
    }.must(throwAn[AssertionError])
  }

  @Test
  def numbersMustBeComparable() {
    100.must(be(lessThan(101)))
    100.must(be(lessThanOrEqualTo(100)))
    100.must(be(greaterThan(99)))
    100.must(be(greaterThanOrEqualTo(100)))

    evaluating {
      100.must(be(lessThan(99)))
    }.must(throwAn[AssertionError])

    evaluating {
      100.must(be(lessThanOrEqualTo(99)))
    }.must(throwAn[AssertionError])

    evaluating {
      100.must(be(greaterThan(101)))
    }.must(throwAn[AssertionError])

    evaluating {
      100.must(be(greaterThanOrEqualTo(101)))
    }.must(throwAn[AssertionError])
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
    }.must(throwAn[AssertionError])
  }

  @Test
  def sizedCollectionsHaveSizes() {
    a.must(haveSize(3))

    evaluating {
      a.must(haveSize(40))
    }.must(throwA[AssertionError])
  }

  @Test
  def containsStuff() {
    a.must(contain(2))

    evaluating {
      b.must(contain(40))
    }.must(throwA[AssertionError])
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
    }.must(throwAn[AssertionError])

    evaluating {
      eventually(2) {
        pop()
      }.must(be(Some(40)))
    }.must(throwAn[AssertionError])
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

class StringSpec extends Spec {
  class `Matching a string` {
    val string = "this is a string"

    class `by its prefix` {
      @Test def `passes if the string has that prefix` = {
        string.must(startWith("this "))
      }

      @Test def `fails if the string doesn't have that prefix` = {
        evaluating {
          string.must(startWith("poop"))
        }.must(throwAn[AssertionError])
      }
    }

    class `by its suffix` {
      @Test def `passes if the string has that suffix` = {
        string.must(endWith("string"))
      }

      @Test def `fails if the string doesn't have that suffix` = {
        evaluating {
          string.must(endWith("poop"))
        }.must(throwAn[AssertionError])
      }
    }

    class `by a substring` {
      @Test def `passes if the string has that substring` = {
        string.must(contain("is a"))
      }

      @Test def `fails if the string doesn't have that substring` = {
        evaluating {
          string.must(contain("poop"))
        }.must(throwAn[AssertionError])
      }
    }

    class `by a regular expression` {
      @Test def `passes if the string matches the expression` = {
        string.must(`match`(""".*ring""".r))
      }

      @Test def `fails if the string doesn't match the expression` = {
        evaluating {
          string.must(`match`(""".*oop""".r))
        }.must(throwAn[AssertionError])
      }
    }
  }
}
