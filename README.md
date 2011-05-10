SimpleSpec
==========

*I already have compositional techniques, tanks.*

SimpleSpec is a tiny extension to the [specs](http://code.google.com/p/specs/)
BDD library which allows you to write your specifications as simple classes and
methods.


Requirements
------------

* Scala 2.8.1


How To Use
----------

**First**, specify SimpleSpec as a dependency:
    
    val scalaToolsReleases = "scala-tools.org Releases" at "http://scala-tools.org/repo-releases"
    val codaRepo = "Coda Hale's Repository" at "http://repo.codahale.com/"
    val simplespec = "com.codahale" %% "simplespec" % "0.1.0" withSources()

**Second**, write a spec:
    
    import com.example.Stack
    import com.codahale.simplespec.Spec
    
    object StackSpec extends Spec {
      class `An empty stack` {
        val stack = Stack()
        
        def `should have a size of zero` {
          stack.size must be(0)
        }
        
        def `should be empty` {
          stack.isEmpty must be(true)
        }
      }
    }

That's it.


How It Works
------------

`Spec` is a simple abstract class which extends spec's `Specification`. When
it's instantiated, it reflects on the instance's declared inner classes and for
each inner class, declares a spec system-under-specification. For each method of
the inner class which starts with `should`, it declares an example for the SUS
which instantiates the inner class and calls the particular method.

Because Scala can use arbitrary strings for class and method names using the
backtick characters, it's easy to use the literate style in naming
systems-under-specification and examples.


License
-------

Copyright (c) 2010 Coda Hale

Published under The MIT License, see LICENSE
