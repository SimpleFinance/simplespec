package com.codahale.simplespec.specs

import com.codahale.simplespec.Spec

// TODO: figure out how to test this monster

object SpecSpec extends Spec {
  class `A crazy object-oriented spec` {
    def `should be awesome` {
      true must be(true)
    }
  }
}