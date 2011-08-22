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


How To Use
----------

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
    import com.codahale.simplespec.Spec
    import com.codahale.simplespec.annotation.test
    
    class StackSpec extends Spec {
      class `An empty stack` {
        val stack = Stack()
        
        @test def `has a size of zero` = {
          stack.size.must(be(0))
        }
        
        @test def `is empty` = {
          stack.isEmpty.must(be(true))
        }

        class `with an item added to it` = {
          stack += "woo"

          @test def `might have an item in it` = {
            stack.must(be(empty))
          }
        }
      }
    }
```


License
-------

Copyright (c) 2010-2011 Coda Hale

Published under The MIT License, see LICENSE
