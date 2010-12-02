package com.codahale.simplespec.specs

import com.codahale.simplespec.Spec

// TODO: figure out how to test this monster

object SpecSpec extends Spec {
  
  override def beforeAll() {
    println("before all")
  }
  
  override def afterAll() {
    println("after all")
  }
  
  trait IAmATrait {
    val v: Boolean
    def `should work out but only when included` {
      true must be(v)
    }
  }
  
  abstract class `Abstract classes` {
    def `should never run` {
      true must be(false)
    }
  }
  
  class `The simplest thing ever` {
    def `should work` {
      true must be(true)
    }
  }
  
  class `Classes with mixins` extends IAmATrait {
    override val v = true
  }
  
  class `A crazy object-oriented spec` {
    def beforeEach() {
      println("before each")
    }
    
    def `should be awesome` {
      println("during awesome")
      true must be(true)
    }
    
    def `should be kind to others` {
      println("during kind")
      true must be(true)
    }
    
    def afterEach() {
      println("after each")
    }
  }

  class `A class which throws an exception when it's created` {
    throw new IllegalArgumentException("oh noes")

    def `should not be horribly ugly` {
      true must be(true)
    }

    def `should do something cool` {
      true must be(true)
    }
  }
}
