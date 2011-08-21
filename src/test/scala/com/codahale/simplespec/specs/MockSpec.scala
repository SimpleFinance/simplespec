package com.codahale.simplespec.specs

import org.junit.Test
import com.codahale.simplespec.{Assertions, Mocks}

trait MockableThing {
  def aString: String
  def number(i: Int): Int
  def poop(input: String)
}

class MockSpec extends Assertions with Mocks {
  @Test
  def mustReturnStubbedMethods() {
    val thing = mock[MockableThing]
    thing.aString.returns("yay")

    thing.aString.mustEqual("yay")
  }

  @Test
  def mustReturnAnsweredMethods() {
    val thing = mock[MockableThing]
    thing.number(any).answersWith { i =>
      i.getArguments.apply(0).asInstanceOf[Int] % 2
    }

    thing.number(1).mustEqual(1)
    thing.number(2).mustEqual(0)
    thing.number(3).mustEqual(1)
    thing.number(4).mustEqual(0)
    thing.number(5).mustEqual(1)
    thing.number(6).mustEqual(0)
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

    verify.exactly(12)(thing).poop("never happened").mustThrowAn[AssertionError]

    verify.exactly(2)(thing).poop(isNotNull)
    verify.one(thing).poop(isNull).mustThrowAn[AssertionError]
  }

  @Test
  def mustPlayNicelyWithVoidMethods() {
    val thing = mock[MockableThing]
    thing.poop("boom").throws(new RuntimeException("boom"))

    thing.poop("boom").mustThrowA[RuntimeException]("boom")
  }
}
