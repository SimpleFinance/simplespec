v0.5.2: Sep 13 2011
===================

* Added support for `@Ignore` on classes as well as methods.
* Added `startWith`, `endWith`, `match`, and `contain` matchers for strings.

v0.5.1: Sep 08 2011
===================

* Added support for Scala 2.9.1.
* Added `lessThan`, `greaterThan`, `lessThanOrEqualTo`, and
  `greaterThanOrEqualTo` matchers.

v0.5.0: Aug 26 2011
===================

* Completely rewrote all the code to work entirely on JUnit and use Hamcrest
  matchers. No longer uses Specs in any form.

v0.4.1: Aug 16 2011
===================

* Now calling `beforeEach` and `afterEach` for all enclosing classes of an
  example.

v0.4.0: Jul 25 2011
===================

* Upgraded to Specs2 1.5.
* Modified discovery to **only** run methods annotated with `@test`.
* Added `Spec#arguments` method for specifying Specs2 arguments.

v0.3.4: Jun 04 2011
===================

* Added JUnit `@RunWith` annotation so `Spec` instances can run in Maven.

v0.3.3: May 12 2011
===================

* Upgraded to Spec2 1.3.

v0.3.2: May 12 2011
===================

* Fixed a reflection bug on Scala 2.9.0.

v0.3.1: May 12 2011
===================

* Now only cross-building for Scala 2.8.1 and 2.9.0.
* Fixed a bug handling non-`Result` return types.
* No longer considers example methods returning `Unit` to be pending.s
* Added a new `@ignore` annotation.
* Improved example discovery reflection.

v0.3.0: May 10 2011
===================

* Upgraded to Spec2 1.2 and Scala 2.9 RCs.
* Big rewrite to handle aribtrarily-nested context classes and example methods
  without the `should` prefix.

v0.2.0: Dec 02 2010
===================

* Upgraded to Specs 1.6.6 and Scala 2.8.1.
* Bugfixes.

v0.1.0: Jul 23 2010
===================

* Initial release.
