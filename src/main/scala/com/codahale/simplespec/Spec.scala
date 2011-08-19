package com.codahale.simplespec

import org.junit.runner.RunWith

class IgnoredTestException extends Exception

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
