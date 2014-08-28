simplespec
==========

*No seriously, keep it simple.*

**simplespec** is a thin Scala wrapper over [JUnit](http://www.junit.org/),
the most commonly-used test framework on the JVM. simplespec was originally written
by Coda Hale and is now maintained and developed by Simple. The library features
extensible Hamcrest matchers, easy mocks, and other niceties.

<img src="https://travis-ci.org/SimpleFinance/simplespec.png" />


Requirements
------------

* Scala 2.11.0
* JUnit 4.11
* Mockito 1.9.5

(Scala 2.10.2, 2.9.1, and 2.9.2 are supported in simplespec 0.8.4, 0.6.0, and 0.7.0, respectively.)


Getting Started
---------------

**First**, specify simplespec as a dependency.

```xml
<dependencies>
    <dependency>
        <groupId>com.simple</groupId>
        <artifactId>simplespec_2.11.0</artifactId>
        <version>0.8.4</version>
    </dependency>
</dependencies>
```

If you are on Scala 2.10.2, you should use:

```xml
<dependencies>
    <dependency>
        <groupId>com.simple</groupId>
        <artifactId>simplespec_2.10.2</artifactId>
        <version>0.8.4</version>
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

SimpleSpec uses [Mockito](http://mockito.org/) for mocking stuff. It has its
own wrappers around Mockito to make things a bit easier.

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

### Mock Stubbing

By default, when you mock something and call a method on it, the call will
return `null` or a basic value like `0` or `false` for primitives.

If you want to control what the mocked object returns for a given method call,
you can use `returns`, `throws`, or `answersWith`:

```scala
val foo = mock[FooService]

// .returns() can be used when you just want to return a static value
foo.getNumber("one").returns(1)
foo.getNumber("two").returns(2)

// .throws() will make the call throw the given exception.
// Note: if Mockito complains about a checked exception being invalid, you'll
// need to use .answersWith() to throw the exception instead.
foo.getNumber("dogs").throws(new NumberFormatException)

// .answersWith() will call the function you pass it and use its result
// as the mocked return value.
foo.getNumber("three").answersWith(_ => 3)
foo.getNumber("dogs").answersWith(_ => throw new NumberFormatException)
```

These stubbing functions are sensitive to order. So this:

```scala
foo.get(1).returns("cats")
foo.get(1).returns("dogs")
```

Will return `"dogs"` every time you call `foo.get(1)`.

You can also dynamically match arguments in method calls. The simplest way is
to use `any` to match any argument of a given type:

```scala
foo.get(any[Int]).returns(None)
foo.get(1).returns(Some("dogs"))
```

This example uses the fact that stubs are sensitive to ordering to its
advantage.

Note that if you match *any* of the method's arguments with a dymanic matcher
like `any`, you'll need to match them *all* dynamically. For example, this does
**not** work:

```scala
foo.get(any[Int], "Hello").returns(...)
```

You can use `equalTo` to get around this:

```scala
foo.get(any[Int], equalTo[String]("Hello")).returns(...)
```

Available dynamic matchers:

* `any[A](implicit mf: Manifest[A])`: A matcher which will accept any instance.
* `isA[A](implicit mf: Manifest[A])`: A matcher which will accept any instance of the given type.
* `equalTo[A](value: A)`: A matcher which will accept any instance of the given type which is equal to the given value.
* `same[A](value: A)`: A matcher which will accept only the same instance as the given value.
* `isNull[A]`: A matcher which will accept only null values.
* `isNotNull[A]`: A matcher which will accept only non-null values.
* `contains(substring: String)`: A matcher which will accept only strings which contain the given substring.
* `matches(pattern: Regex)`: A matcher which will accept only strings which match the given pattern.
* `endsWith(suffix: String)`: A matcher which will accept only strings which end with the given suffix.
* `startsWith(prefix: String)`: A matcher which will accept only strings which start with the given prefix.

**WARNING**: Since the matchers are really Java under the hood, they do not
understand Scala default arguments. If you are matching against a method with
default arguments, you *must* specify the default arguments as well
(Scala calls the method with `null` if the default is used.)

### Responding to invocations with answersWith

If you have a mock, you can invoke arbitrary behavior when it is called
by using `answersWith`. This calls a function whenever the mock is used.

This can let you use a fake implementation for the mocked object. It's
useful for implementing enough of the functionality to make your code work,
or for doing some more advanced checks than normal matchers allow.

```scala
myMock.get(any[String]).answersWith { f =>
  val stringArg = f.getArguments.toSeq.head.asInstanceOf[String]
  println("I was called with " + stringArg)
  false // your return value
}
```

### Mock Verification

TODO: Document this.

### Argument Capture

Simplespec supports Mockito's [ArgumentCaptor](http://docs.mockito.googlecode.com/hg/org/mockito/ArgumentCaptor.html) to capture
arguments:

```scala
class FooClass {
  def concatMethod(x: String, y: Int): String = x + y.toString
}
val arg3 = captor[String]
val arg4 = captor[Int]
val fooMock = mock[FooClass]
fooMock.concatMethod("foo", 1)

verify.one(fooMock).concatMethod(arg3.capture(), arg4.capture())
arg3.getValue().must(be("foo"))
arg4.getValue().must(be(1))
```

ScalaCheck
----------

SimpleSpec includes helpers for integrating ScalaCheck properties into
your tests, with the `hold` and `prove` matchers.

```scala
class StringPropertySpec extends Spec {
  import org.scalacheck.Prop._

  class `String operations` {
    @Test def startsWith {
      forAll((a: String, b: String) => (a+b).startsWith(a)).must(hold)
    }

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
```

This is very convenient, since you may mix property and non-property
tests freely, and produce test reports & code coverage for your
ScalaCheck properties.

License
-------

Copyright (c) 2010-2012 Coda Hale

Copyright (c) 2012-2014 Simple Finance Technology

Published under The MIT License, see LICENSE
