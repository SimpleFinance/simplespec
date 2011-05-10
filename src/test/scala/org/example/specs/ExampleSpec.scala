package org.example.specs

import collection.mutable
import com.codahale.simplespec.{Before, Spec}

object ExampleSpec extends Spec {
  private val numbers = new mutable.HashSet[Int]

  override def beforeAll() {
    numbers += 1
  }

  class `A set with two numbers` {
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
          skip("poop")
          numbers must haveSize(304)
        }
      }
    }
  }

  class `A class with some setup` extends Before {
    private var items: List[String] = Nil

    override def beforeEach() {
      items = "whoah" :: items
    }

    def `should run the setup` = {
      items must haveSize(1)
    }
  }

//  class `A class that explodes on creation` {
//    throw new IllegalArgumentException("what the hell dude")
//
//    def `should blah blee bloo` = {
//      skip("poop")
//    }
//  }
}
