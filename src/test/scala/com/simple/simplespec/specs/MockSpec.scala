package com.simple.simplespec.specs

import org.junit.Test
import com.simple.simplespec.{Matchables, Matchers, Mocks}

trait MockableThing {
  def aString: String
  def number(i: Int): Int
  def poop(input: String)
}

class MockSpec extends Matchables with Matchers with Mocks {
  @Test
  def mustReturnStubbedMethods() {
    val thing = mock[MockableThing]
    thing.aString.returns("yay")

    thing.aString.must(be("yay"))
  }

  @Test
  def mustReturnAnsweredMethods() {
    val thing = mock[MockableThing]
    thing.number(any[Int]).answersWith { i =>
      i.getArguments.apply(0).asInstanceOf[Int] % 2
    }

    thing.number(1).must(be(1))
    thing.number(2).must(be(0))
    thing.number(3).must(be(1))
    thing.number(4).must(be(0))
    thing.number(5).must(be(1))
    thing.number(6).must(be(0))
  }

  @Test
  def mustVerifyCalledMethods() {
    val thing = mock[MockableThing]

    thing.aString

    verify.one(thing).aString
  }

  @Test
  def mustVerifyCalledMethodsInOrder() {
    val thing = mock[MockableThing]

    thing.number(1)
    thing.number(2)
    thing.number(3)

    verify.inOrder(thing) { o =>
      o.one(thing).number(1)
      o.one(thing).number(2)
      o.one(thing).number(3)
    }
  }

  @Test
  def mustVerifyCalledMethodsWithMatchers() {
    val thing = mock[MockableThing]

    val input = "what the hey"

    thing.poop("yes sir")
    thing.poop(input)

    verify.one(thing).poop(startsWith("yes"))
    verify.one(thing).poop(endsWith("hey"))
    verify.one(thing).poop(equalTo("yes sir"))
    verify.one(thing).poop(same(input))

    evaluating {
      verify.exactly(12)(thing).poop("never happened")
    }.must(throwAn[AssertionError])

    verify.exactly(2)(thing).poop(isNotNull[String])

    evaluating {
      verify.one(thing).poop(isNull[String])
    }.must(throwAn[AssertionError])
  }

  @Test
  def mustPlayNicelyWithVoidMethods() {
    val thing = mock[MockableThing]
    thing.poop("boom").throws(new RuntimeException("boom"))

    evaluating {
      thing.poop("boom")
    }.must(throwAn[RuntimeException]("boom"))
  }

  @Test
  def mustHaveArgumentCaptorsOrRyanWillBeSad() {
    val thing = mock[MockableThing]
    val s = captor[String]

    thing.poop("one")
    thing.poop("two")

    verify.exactly(2)(thing).poop(s.capture())

    s.value.must(be(Some("two")))
    s.allValues.must(be(Seq("one", "two")))
  }
}
