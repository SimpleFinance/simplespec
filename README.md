simplespec
==========

*No seriously, keep it simple.*

**simplespec** is a thin Scala wrapper over [JUnit](http://www.junit.org/),
the most commonly-used test framework on the JVM. simplespec was originally written
by Coda Hale and is now maintained and developed by Simple. The library features
extensible Hamcrest matchers, easy mocks, and other niceties.


Requirements
------------

* Scala 2.10.1
* JUnit 4.11
* Mockito 1.9.5

(Scala 2.9.1 and 2.9.2 are supported in simplespec 0.6.0 and 0.7.0, respectively.)


Getting Started
---------------

**First**, specify simplespec as a dependency.

```xml
<dependencies>
    <dependency>
        <groupId>com.simple</groupId>
        <artifactId>simplespec_2.10.1</artifactId>
        <version>0.8.0</version>
    </dependency>
</dependencies>
```


If you are on Scala 2.9.2, you should use:

```xml
<dependencies>
    <dependency>
        <groupId>com.simple</groupId>
        <artifactId>simplespec_2.9.2</artifactId>
        <version>0.7.0</version>
    </dependency>
</dependencies>
```

And for 2.9.1:

```xml
<dependencies>
    <dependency>
        <groupId>com.simple</groupId>
        <artifactId>simplespec_2.9.1</artifactId>
        <version>0.6.0</version>
    </dependency>
</dependencies>
```


**Second**, write a spec:

```scala
import com.example.Stack
import org.junit.Test
import com.simple.simplespec.Spec

class StackSpec extends Spec {
  class `An empty stack` {
    val stack = Stack()

    @Test def `has a size of zero` = {
      stack.size.must(be(0))
    }

    @Test def `is empty` = {
      stack.isEmpty.must(be(true))
    }

    class `with an item added to it` {
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

Unlike JUnit, simplespec doesn't require your test methods to return void.

The outer `Spec` instance has `beforeEach` and `afterEach` methods which can be
overridden to perform setup and teardown tasks for each test contained in the
context. simplespec also provides `BeforeEach`, `AfterEach`, and
`BeforeAndAfterEach` traits which inner classes can extend to perform more
tightly-scoped setup and teardown tasks.


Matchers
--------

simplespec provides a thin layer over
[Hamcrest matchers](http://code.google.com/p/hamcrest/) to allow for declarative
assertions in your tests:

```scala
stack.must(be(empty))
```

simplespec includes the following matchers by default, but you're encouraged to
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
* `x.must(be(lessThan(2))`: Asserts that `x` is less than `2`.
* `x.must(be(greaterThan(2))`: Asserts that `x` is greater than `2`.
* `x.must(be(lessThanOrEqualTo(2))`: Asserts that `x` is less than or equal to
  `2`.
* `x.must(be(greaterThanOrEqualTo(2))`: Asserts that `x` is greater than or
  equal to `2`.
* `x.must(startWith("woo"))`: Asserts that string `x` starts with `"woo"`.
* `x.must(endWith("woo"))`: Asserts that string `x` ends with `"woo"`.
* `x.must(contain("woo"))`: Asserts that string `x` contains with `"woo"`.
* ``x.must(`match`(".*oo".r))``: Asserts that string `x` matches the regular
  expression `.*oo`.

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

simplespec also includes two helper methods: `evaluating` and `eventually`.

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

Also, yeah, mocks. simplespec uses [Mockito](http://mockito.org/) for its
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

Copyright (c) 2010-2012 Coda Hale

Copyright (c) 2012 Simple Finance Technology

Published under The MIT License, see LICENSE
