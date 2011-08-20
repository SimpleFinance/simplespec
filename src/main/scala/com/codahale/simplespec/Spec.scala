package com.codahale.simplespec

import org.junit.runner.RunWith

@RunWith(classOf[SpecRunner])
abstract class Spec extends Assertions with BeforeAndAfterEach {
  
}

trait BeforeEach {
  def beforeEach() {}
}

trait AfterEach {
  def afterEach() {}
}

trait BeforeAndAfterEach extends BeforeEach with AfterEach
