Simplespec
==========

*No seriously, keep it simple.*

Simplespec is a thin layer of convenience over [JUnit](http://www.junit.org/),
the most commonly-used test framework on the JVM.


Requirements
------------

* Scala 2.8.1 or 2.9.0-1
* JUnit 4.8.x
* Mockito 1.8.x


Getting Started
---------------

**First**, specify SimpleSpec as a dependency:

```xml
<repositories>
    <repository>
        <id>repo.codahale.com</id>
        <url>http://repo.codahale.com</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.codahale</groupId>
        <artifactId>simplespec_${scala.version}</artifactId>
        <version>0.5.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

**Second**, write a spec:

```scala
import com.example.Stack
import org.junit.Test
import com.codahale.simplespec.Spec

class StackSpec extends Spec {
  class `An empty stack` {
    val stack = Stack()

    @Test def `has a size of zero` = {
      stack.size.must(be(0))
    }

    @Test def `is empty` = {
      stack.isEmpty.must(be(true))
    }

    class `with an item added to it` = {
      stack += "woo"

      @Test def `might have an item in it` = {
        stack.must(be(empty))
      }
    }
  }
}
```


Execution Model
---------------

The execution model for a `Spec` is just a logical extension of how JUnit itself
works -- a `Spec` class contains one or more regular classes, each of which can
contain zero or more `@Test`-annotated methods or further nested classes.

When JUnit runs the `Spec` class, it creates new instances of each class for
each test method run, allowing for full test isolation. In the above example,
first an instance of `StackSpec` would be created, then an instance of
`` StackSpec#`An empty stack` ``, then an instance of
`` StackSpec#`An empty stack`#`with an item added to it` ``, and finally its
`` `might have an item in it` `` method is run as a test.

The tradeoff of this execution model (vs. one which shares state between test
invocation) is that tests which create a substantial amount of shared state
(e.g., data-intensive tests) spend a lot of time setting up or tearing down
state.

Unlike JUnit, Simplespec doesn't require your test methods to return void.


Matchers
--------

Simplespec provides a thin layer over
[Hamcrest matchers](http://code.google.com/p/hamcrest/) to allow for declarative
assertions in your tests:

```scala
stack.must(be(empty))
```

Simplespec includes the following matchers by default, but you're encouraged to
write your own:

* `x.must(equal(y))`: Asserts `x == y`.
* `x.must(be(y))`: A synonym for `equal`.
* `x.must(beA(klass))`: Asserts that `x` is assignable as an instance of `klass`.
* `x.must(be(matcher))`: Asserts that `matcher` applies to `x`.
* `x.must(not(be(matcher)))`: Asserts that `matcher` does *not* apply to `x`.
* `x.must(be(empty))`: Asserts that `x` is a `TraversableLike` which is empty.
* `x.must(haveSize(n))`: Asserts that `x` is a `TraversableLike` which has `n`
  elements.
* `x.must(contain(y))`: Asserts that `x` is a `SeqLike` which contains the
  element `y`.
* `x.must(be(notNull))`: Asserts that `x` is not `null`.
* `x.must(be(approximately(y, delta)))`: Asserts that `x` is within `delta` of
  `y`. Useful for floating-point math.

Matchers like `be` and `not` take matchers as their arguments, which means you
can write domain-specific matchers for your tests:

```scala
class IsSufficientlyCromulentMatcher extends BaseMatcher[Fromulator] {
  def describeTo(description: Description) {
    description.appendText("a cromulemnt fromulator")
  }

  def matches(item: AnyRef) = item match {
    case fromulator: Fromulator => fromulator.isCromulent
    case _ => false
  }
}

trait CromulentMatcher {
  def cromulent = new IsSufficientlyCromulentMatcher
}

class BlahBlahSpec extends Spec with CromulentMatcher {
  class `A Fromulator` {
    val fromulator = new Fromulator

    def `is cromulent` = {
      fromulator.must(be(cromulent)
    }
  }
}
```

Simplespec also includes two helper methods: `evaluating` and `eventually`.

`evaluating` captures a closure and allows you to make assertions about what
happens when it's evaluated:

```scala
@Test def `throws an exception` = {
  evaluating {
    dooHicky.stop()
  }.must(throwAn[UnsupportedOperationException])
}
```

`eventually` also captures a closure, but allows you to assert things about
what happens when the closure is evaluated which might not be true the first
few times:

```scala
@Test def `decay to zero` = {
  eventually {
    thingy.rate
  }.must(be(approximately(0.0, 0.001)))
}
```

See `Matchers.scala` for the full run-down.


Mocks
-----

Also, yeah, mocks. Simplespec uses [Mockito](http://mockito.org/) for its
mocking stuff:

```scala
class PublisherSpec extends Spec {
  class `A publisher` {
    val message = mock[Message]

    val queue = mock[Queue]
    queue.enqueue(any).returns(0, 1, 2, 3)

    val publisher = new Publisher(queue)

    @Test def `sends a message to the queue` = {
      publisher.receive(message)

      verify.one(queue).enqueue(message)
    }
  }
}
```

See `Mocks.scala` for the full run-down.


License
-------

Copyright (c) 2010-2011 Coda Hale

Published under The MIT License, see LICENSE
