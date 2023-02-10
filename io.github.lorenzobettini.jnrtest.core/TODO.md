- index descriptions to run a single test

V listeners: store also the exception?
- how to distinguish before each, before all, etc. from standard
tests?

- better notification:
	- V starting and finishing a test case
	- V result of a test

- adapter for the listener

V test runnable lifecycle event
V differentiate between actual test event and before/after events?

V store

V each element of the store should contain also a description
(i.e., before, after, should also provide a description)

- functional interface version of test case so that the test case can be specified as a lambda
(with a parameter it of type store?)

- parameterized test?

- extension for Mockito and Guice as examples
