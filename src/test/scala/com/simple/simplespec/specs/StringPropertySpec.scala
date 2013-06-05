/**
 * © 2013 Simple Finance Technology Corp. All rights reserved.
 * Author: Ian Eure <ieure@simple.com>
 */
package com.simple.simplespec.specs

import org.junit.{Test, Ignore}
import com.simple.simplespec.Spec

class StringPropertySpec extends Spec {
  import org.scalacheck.Prop._

  class `String operations` {
    @Test def startsWith {
      forAll((a: String, b: String) => (a+b).startsWith(a)).must(hold)
    }

    @Ignore
    @Test def concatenate {
      forAll((a: String, b: String) =>
        (a+b).length > a.length && (a+b).length > b.length
      ).must(hold)
    }

    @Test def substring {
      forAll((a: String, b: String, c: String) =>
        (a+b+c).substring(a.length, a.length+b.length) == b
      ).must(hold)
    }
  }
}
