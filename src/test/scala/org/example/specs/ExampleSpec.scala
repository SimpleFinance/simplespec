package org.example.specs

import collection.mutable
import org.specs2.mock.Mockito
import com.codahale.simplespec.annotation.test
import com.codahale.simplespec.{BeforeAndAfterEach, Spec}

class ExampleSpec extends Spec with Mockito {
  override def beforeAll() {
    println("beforeAll")
  }

  override def afterAll() {
    println("afterAll")
  }

  override def arguments = sequential

  class `A set with two numbers` {
    private val numbers = new mutable.HashSet[Int]
    numbers += 1
    numbers += 2
    
    @test def `has a size of two` = {
      numbers must haveSize(2)
    }

    class `plus another number` {
      numbers += 3

      @test def `probably has a size of three` = {
        numbers must haveSize(3)
      }

      class `and a fourth number` {
        numbers += 4

        @test def `really should have a size of four` = {
          numbers must haveSize(4)
        }

        @test def `should be awesome` = {
          numbers must contain(2)
        }
      }

      class `minus a number which wasn't there in the first place` {
        numbers -= 109

        @test def `probably doesn't have a size of three hundred and four` = {
//          numbers must haveSize(304)
        }

        @test def `is also a giraffe` = {
          "moo"
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

    @test def `should run the setup` = {
      items must haveSize(1)
    }

    @test def `should do another thing` = {
      done
    }

    @test def `should really blah blee bloo` = {
      done
    }

    @test def `should do a thing`() {
      1 must beEqualTo(1)
    }
  }

  class Dingo {
    def poop = "woo"
  }

  class `A class with a mock` {
    private val dingo = mock[Dingo]

    @test def `shouldn't need to return a Result, really` = {
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
