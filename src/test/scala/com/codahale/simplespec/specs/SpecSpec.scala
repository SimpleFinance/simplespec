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
  
  class `The simplest thing ever` {
    def `should work` {
      
    }
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
}