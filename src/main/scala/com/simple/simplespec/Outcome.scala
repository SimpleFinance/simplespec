package com.simple.simplespec

sealed abstract class Outcome[+A]

final case class Failure[+A](e: Throwable) extends Outcome[A] {
  override def toString = "an exception of type <" + e.getClass.getName + "> with a message of <" + e.getMessage + ">"
}

final case class Success[+A](value: A) extends Outcome[A] {
  override def toString = "no exception thrown; <" + value + "> returned"
}
