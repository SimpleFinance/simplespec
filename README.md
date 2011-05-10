SimpleSpec
==========

*I already have compositional techniques, tanks.*

SimpleSpec is a tiny extension to the
[specs2](http://etorreborre.github.com/specs2/) BDD library which allows you to
write your specifications as simple classes and methods.


Requirements
------------

* Scala 2.8.1 or 2.9.0.RC1 or 2.9.0.RC2
* Specs2 1.2


How To Use
----------

**First**, specify SimpleSpec as a dependency:

```scala
    val codaRepo = "Coda Hale's Repository" at "http://repo.codahale.com/"
    val simplespec = "com.codahale" %% "simplespec" % "0.3.0" % "test"
```

**Second**, write a spec:

```scala
    import com.example.Stack
    import com.codahale.simplespec.Spec
    
    object StackSpec extends Spec {
      class `An empty stack` {
        val stack = Stack()
        
        def `has a size of zero` = {
          stack.size must beEqualTo(0)
        }
        
        def `is empty` = {
          stack.isEmpty must beTrue
        }

        class `with an item added to it` = {
          stack += "woo"

          def `might have an item in it` = {
            stack.isEmpty must beFalse
          }
        }
      }
    }
```

This will produce the following output:

```
StackSpec
An empty stack
+ has a size of zero
+ is empty

An empty stack with an item added to it
+ might have an item in it
```


How It Works
------------

`Spec` is a simple trait which extends spec's `Specification`. When it's
instantiated, it recursively reflects on the instance's inner classes, creating
a set of Spec2 examples for all of their public methods. Each example method
(e.g., ``should blah blah``) is invoked on its own instance of the enclosing
classes, making it play nicely with mutable data structures and mocks.

Because Scala can use arbitrary strings for class and method names using the
backtick characters, it's easy to use the literate style in naming
systems-under-specification and examples.

`Spec` has two methods -- `beforeAll` and `afterAll` which are called before and
after the entire set of requirements are evaluated. This is useful for fixture
setup and teardown.

Context classes can extend three traits -- `BeforeEach`, `AfterEach`, and
`BeforeAndAfterEach` -- which provide `beforeEach` and `afterEach` methods which
are called before and after each example method.

An example method which does not result a Specs2 `Result` (i.e., the result of
a matcher or one of the explicit `success` or `failure` functions) is considered
pending.


License
-------

Copyright (c) 2010-2011 Coda Hale

Published under The MIT License, see LICENSE
