package org.example.specs

import collection.mutable
import com.codahale.simplespec.{BeforeAndAfterEach, Spec}
import org.specs2.mock.Mockito

object ExampleSpec extends Spec with Mockito {
  override def beforeAll() {
    println("beforeAll")
  }

  override def afterAll() {
    println("afterAll")
  }

  class `A set with two numbers` {
    private val numbers = new mutable.HashSet[Int]
    numbers += 1
    numbers += 2

    def `has a size of two` = {
      numbers must haveSize(2)
    }

    class `plus another number` {
      numbers += 3

      def `probably has a size of three` = {
        numbers must haveSize(3)
      }

      class `and a fourth number` {
        numbers += 4

        def `really should have a size of four` = {
          numbers must haveSize(4)
        }

        def `should be awesome` = {
          numbers must contain(2)
        }
      }

      class `minus a number which wasn't there in the first place` {
        numbers -= 109

        def `probably doesn't have a size of three hundred and four` = {
//          numbers must haveSize(304)
        }
      }
    }
  }

  class `A class with some setup` extends BeforeAndAfterEach {
    private var items: List[String] = Nil

    override def beforeEach() {
      println("before")
      items = "whoah" :: items
    }

    override def afterEach() {
      println("after")
    }

    def `should run the setup` = {
      items must haveSize(1)
    }

    def `should do another thing` = {
      done
    }

    def `should really blah blee bloo` = {
      done
    }
  }

  private class Dingo {
    def poop = "woo"
  }

  class `A class with a mock` {
    private val dingo = mock[Dingo]

    def `shouldn't need to return a Result, really` = {
      dingo.poop

      there was one(dingo).poop
    }
  }

//  class `A class that explodes on creation` {
//    throw new IllegalArgumentException("what the hell dude")
//
//    def `should blah blee bloo` = {
////      skip("poop")
//    }
//  }
}
