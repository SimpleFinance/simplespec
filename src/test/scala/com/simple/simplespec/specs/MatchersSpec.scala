
package com.simple.simplespec.specs

import org.junit.Test
import org.junit.Assert._
import com.simple.simplespec.{Spec, Matchables, Matchers}
import com.simple.simplespec.matchers.{PropErrorMatcher, HeldPropertyMatcher,
  ProvedPropertyMatcher}
import org.scalacheck.{Prop, Arg}
import org.scalacheck.Test.{Result, Passed, Proved, Failed, Exhausted, 
  PropException, GenException}
import org.scalacheck.util.FreqMap

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

  @Test
  def notNullMustWork() {
    evaluating {
      val x: String = null
      x.must(be(notNull))
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

class PropertyMatchersSpec extends Spec {
  val fm = FreqMap.empty[Set[Any]]
  val args = List[Arg[Any]]()
  val passed = Result(Passed, 0, 0, fm, 0L)
  val proved = Result(Proved(args), 0, 0, fm, 0L)
  val failed = Result(Failed(args, Set()), 0, 0, fm, 0L)
  val exhausted = Result(Exhausted, 0, 0, fm, 0L)
  val propEx = Result(
    PropException(args, new RuntimeException("whoops"), Set()),
    0, 0, fm, 0L)
  val genEx = Result(GenException(new ArithmeticException),
                     0, 0, fm, 0L)

  class `Property Error Matcher` extends PropErrorMatcher {
    @Test def `Exceptions generating data are re-thrown` {
      evaluating {
        throwIfError(genEx)
      }.must(throwA[RuntimeException])
    }

    @Test def `Exceptions evaluating properties data are re-thrown` {
      evaluating {
        throwIfError(propEx)
      }.must(throwA[RuntimeException])
    }
  }

  class `Held property matcher` {
    val m = new HeldPropertyMatcher

    @Test def `Passing properties pass` {
      m.matches(passed).must(be(true))
    }

    @Test def `Proved properties pass` {
      m.matches(proved).must(be(true))
    }

    @Test def `Failed properties don't pass` {
      m.matches(failed).must(be(false))
    }

    @Test def `Exhausted properties don't pass` {
      m.matches(exhausted).must(be(false))
    }
  }

  class `Proved property matcher` {
    val m = new ProvedPropertyMatcher

    @Test def `Passing properties don't pass` {
      m.matches(passed).must(be(false))
    }

    @Test def `Proved properties pass` {
      m.matches(proved).must(be(true))
    }

    @Test def `Failed properties don't pass` {
      m.matches(failed).must(be(false))
    }

    @Test def `Exhausted properties don't pass` {
      m.matches(exhausted).must(be(false))
    }
  }
}

class AssertionMessageSpec extends Spec {
  class `Literal matchables` {
    @Test def `Can have a failure message` {
      evaluating {
        1.must(not(be(1)), "I am what I am")
      }.must(throwAn[AssertionError]("""I am what I am
Expected: not is <1>
     got: <1>
"""))
    }
  }

  class `Evaluating matchables` {
    @Test def `Can have a failure message` {
      evaluating {
        evaluating {
          fail("sup")
        }.must(not(throwAn[AssertionError]), "whops")
      }.must(throwAn[AssertionError]("""whops
Expected: not throws an exception of type <java.lang.AssertionError>
     got: <an exception of type <java.lang.AssertionError> with a message of <sup>>
"""))
    }
  }

  class `Eventual matchables` {
    @Test def `Can have a failure message` {
      evaluating {
        eventually(2) {
          None
        }.must(not(be(None)), "whops")
      }.must(throwAn[AssertionError]("""whops
Expected: not is <None>
     got: <None>
"""))
    }
  }
}
