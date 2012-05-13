package com.simple.simplespec

import org.junit.runner.RunWith
import org.junit.Rule

@RunWith(classOf[SpecRunner])
abstract class Spec extends Matchers with BeforeAndAfterEach {
  
}

trait BeforeEach {
  def beforeEach() {}
}

trait AfterEach {
  def afterEach() {}
}

trait BeforeAndAfterEach extends BeforeEach with AfterEach
