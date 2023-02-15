package io.github.lorenzobettini.jnrtest.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.inject.Guice;
import com.google.inject.Inject;

class JnrTestRunnerTest {

	static interface Callable {
		void firstMethod();

		void secondMethod();

		void beforeAllMethod1();

		void beforeAllMethod2();

		void beforeEachMethod1();

		void beforeEachMethod2();

		void afterAllMethod1();

		void afterAllMethod2();

		void afterEachMethod1();

		void afterEachMethod2();
	}

	@Test
	@DisplayName("should run all the tests")
	void shouldRunAllTheTests() {
		var callable = mock(Callable.class);
		JnrTestRunner runner = new JnrTestRunner()
			.testCase(new JnrTestCase("a test case") {
				@Override
				protected void specify() {
					test("first test", () -> {
						callable.firstMethod();
					});
					test("test throwing exception", () -> {
						throw new RuntimeException("exception");
					});
				}
			}).testCase(new JnrTestCase("anoter test case") {
				@Override
				protected void specify() {
					test("test failing assertion", () -> {
						assertTrue(false);
					});
					test("second test", () -> {
						callable.secondMethod();
					});
				}
			});
		runner.execute();
		var inOrder = inOrder(callable);
		inOrder.verify(callable).firstMethod();
		inOrder.verify(callable).secondMethod();
	}

	@Test
	@DisplayName("should notify listeners")
	void shouldNotifyListeners() {
		var listener = new JnrTestListener() {
			private StringBuilder results = new StringBuilder();

			@Override
			public void notify(JnrTestResult result) {
				this.results.append(result.toString() + "\n");
			}

			@Override
			public void notify(JnrTestCaseLifecycleEvent event) {
				this.results.append(event.toString() + "\n");
			}

			@Override
			public void notify(JnrTestRunnableLifecycleEvent event) {
				this.results.append(event.toString() + "\n");
			}
		};
		var listenerAdapter = new JnrTestListenerAdapter() {
		};
		JnrTestRunner runner = new JnrTestRunner()
			.testCase(new JnrTestCase("a test case") {
				@Override
				protected void specify() {
					beforeAll("before all", () -> {});
					afterAll("after all", () -> {});
					test("first test", () -> {
						// success
					});
					test("test throwing exception", () -> {
						throw new RuntimeException("exception");
					});
				}
			}).testCase(new JnrTestCase("another test case") {
				@Override
				protected void specify() {
					beforeEach("before each", () -> {});
					afterEach("after each", () -> {});
					test("test failing assertion", () -> {
						assertTrue(false);
					});
					test("second test", () -> {
						// success
					});
				}
			});
		runner.testListener(listener);
		runner.testListener(listenerAdapter);
		runner.execute();
		assertEquals("""
				[  START] a test case
				[  START] BEFORE_ALL before all
				[    END] BEFORE_ALL before all
				[  START] TEST first test
				[SUCCESS] first test
				[    END] TEST first test
				[  START] TEST test throwing exception
				[  ERROR] test throwing exception
				[    END] TEST test throwing exception
				[  START] AFTER_ALL after all
				[    END] AFTER_ALL after all
				[    END] a test case
				[  START] another test case
				[  START] BEFORE_EACH before each
				[    END] BEFORE_EACH before each
				[  START] TEST test failing assertion
				[ FAILED] test failing assertion
				[    END] TEST test failing assertion
				[  START] AFTER_EACH after each
				[    END] AFTER_EACH after each
				[  START] BEFORE_EACH before each
				[    END] BEFORE_EACH before each
				[  START] TEST second test
				[SUCCESS] second test
				[    END] TEST second test
				[  START] AFTER_EACH after each
				[    END] AFTER_EACH after each
				[    END] another test case
				""", listener.results.toString());
	}

	@Test
	@DisplayName("should runs all tests with parameters")
	void shouldRunsAllTestsWithParameters() {
		var listener = new JnrTestListenerAdapter() {
			private StringBuilder results = new StringBuilder();

			@Override
			public void notify(JnrTestResult result) {
				this.results.append(result.toString() + "\n");
			}

			@Override
			public void notify(JnrTestCaseLifecycleEvent event) {
				this.results.append(event.toString() + "\n");
			}
		};
		JnrTestRunner runner = new JnrTestRunner()
			.testCase(new JnrTestCase("a test case with parameterized test (single)") {
				@Override
				protected void specify() {
					testWithParameters("parameter should be positive ",
						() -> List.of(0, 1, 2, 3),
						i -> assertThat(i).isPositive()
					);
				}
			})
			.testCase(new JnrTestCase("a test case with parameterized test (pair)") {
				@Override
				protected void specify() {
					testWithParameters("strings should be equal ",
						() -> List.of(new Pair<>("foo", "foo"), Pair.pair("foo", "bar")),
						p -> assertEquals(p.first(), p.second())
					);
				}
			})
			.testCase(new JnrTestCase("a test case with parameterized test and description") {
				@Override
				protected void specify() {
					testWithParameters("strings should be equal: ",
						() -> List.of(new Pair<>("foo", "foo"), Pair.pair("foo", "bar")),
						p -> String.format("is \"%s\".equals(\"%s\")?", p.first(), p.second()),
						p -> assertEquals(p.first(), p.second())
					);
				}
			});
		runner.testListener(listener);
		runner.execute();
		assertEquals("""
				[  START] a test case with parameterized test (single)
				[ FAILED] parameter should be positive 0
				[SUCCESS] parameter should be positive 1
				[SUCCESS] parameter should be positive 2
				[SUCCESS] parameter should be positive 3
				[    END] a test case with parameterized test (single)
				[  START] a test case with parameterized test (pair)
				[SUCCESS] strings should be equal (foo,foo)
				[ FAILED] strings should be equal (foo,bar)
				[    END] a test case with parameterized test (pair)
				[  START] a test case with parameterized test and description
				[SUCCESS] strings should be equal: is "foo".equals("foo")?
				[ FAILED] strings should be equal: is "foo".equals("bar")?
				[    END] a test case with parameterized test and description
				""", listener.results.toString());
	}

	@Test
	@DisplayName("should record results")
	void shouldRecordResults() {
		var testRecorder = new JnrTestRecorder();
		var testRecorderWithElapsed = new JnrTestRecorder().withElapsedTime();
		JnrTestRunner runner = new JnrTestRunner()
			.testCase(new JnrTestCase("a test case with success") {
				@Override
				protected void specify() {
					beforeAll("before all", () -> {});
					afterAll("after all", () -> {});
					test("success test", () -> {
						// success
						// since we record time, let's make sure to have some
						// delay, otherwise the elapsed time might be 0 on fast machines
						Thread.sleep(10);
					});
				}
			});
		runner.testListener(testRecorder);
		runner.testListener(testRecorderWithElapsed);
		runner.execute();
		assertTrue(testRecorder.isSuccess());
		runner.testCase(new JnrTestCase("a test case with failure") {
			@Override
			protected void specify() {
				beforeAll("before all", () -> {});
				afterAll("after all", () -> {});
				test("failed test", () -> {
					assertTrue(false);
				});
			}
		});
		runner.execute();
		assertFalse(testRecorder.isSuccess());
		// the first test case is executed twice
		assertEquals("{a test case with success="
			+ "[[SUCCESS] success test, [SUCCESS] success test],"
			+ " a test case with failure="
			+ "[[ FAILED] failed test]}",
			testRecorder.getResults().toString());
		assertThat(testRecorderWithElapsed.getTotalTime())
			.isPositive();

		var aggregator = new JnrTestResultAggregator().aggregate(testRecorder);
		assertEquals(2, aggregator.getSucceeded());
		assertEquals(1, aggregator.getFailed());
		assertEquals(0, aggregator.getErrors());
		assertEquals("Tests run: 3, Succeeded: 2, Failures: 1, Errors: 0", aggregator.toString());
		aggregator = new JnrTestResultAggregator().aggregate(testRecorderWithElapsed);
		assertEquals(2, aggregator.getSucceeded());
		assertEquals(1, aggregator.getFailed());
		assertEquals(0, aggregator.getErrors());
		assertThat(aggregator.getTotalTime())
			.isPositive();
		assertThat(aggregator.toString())
			.contains("Tests run: 3, Succeeded: 2, Failures: 1, Errors: 0 - Time elapsed: ");
	}

	@Test
	@DisplayName("should specify the tests only once")
	void shouldSpecifyTestsOnlyOnce() {
		var callable = mock(Callable.class);
		JnrTestRunner runner = new JnrTestRunner()
				.testCase(new JnrTestCase("a test case") {
			@Override
			protected void specify() {
				test("first test", () -> {
					callable.firstMethod();
				});
			}
		});
		runner.execute();
		// second execution should not specify tests again
		runner.execute();
		verify(callable, times(2)).firstMethod();
	}

	@Test
	@DisplayName("should execute lifecycle")
	void shouldExecuteLifecycle() {
		var callable = mock(Callable.class);
		JnrTestRunner runner = new JnrTestRunner()
				.testCase(new JnrTestCase("a test case") {
			@Override
			protected void specify() {
				beforeEach("", () -> {
					callable.beforeEachMethod1();
				});
				beforeEach("", () -> {
					throw new RuntimeException("exception");
				});
				beforeEach("", () -> {
					callable.beforeEachMethod2();
				});
				beforeAll("", () -> {
					callable.beforeAllMethod1();
				});
				beforeAll("", () -> {
					throw new RuntimeException("exception");
				});
				beforeAll("", () -> {
					callable.beforeAllMethod2();
				});
				afterEach("", () -> {
					callable.afterEachMethod1();
				});
				afterEach("", () -> {
					throw new RuntimeException("exception");
				});
				afterEach("", () -> {
					callable.afterEachMethod2();
				});
				afterAll("", () -> {
					callable.afterAllMethod1();
				});
				afterAll("", () -> {
					throw new RuntimeException("exception");
				});
				afterAll("", () -> {
					callable.afterAllMethod2();
				});
				test("first test", () -> {
					callable.firstMethod();
				});
				test("second test", () -> {
					callable.secondMethod();
				});
			}
		});
		runner.execute();
		var inOrder = inOrder(callable);
		// before all
		inOrder.verify(callable).beforeAllMethod1();
		inOrder.verify(callable).beforeAllMethod2();
		// first test
		inOrder.verify(callable).beforeEachMethod1();
		inOrder.verify(callable).beforeEachMethod2();
		inOrder.verify(callable).firstMethod();
		inOrder.verify(callable).afterEachMethod1();
		inOrder.verify(callable).afterEachMethod2();
		// second test
		inOrder.verify(callable).beforeEachMethod1();
		inOrder.verify(callable).beforeEachMethod2();
		inOrder.verify(callable).secondMethod();
		inOrder.verify(callable).afterEachMethod1();
		inOrder.verify(callable).afterEachMethod2();
		// after all
		inOrder.verify(callable).afterAllMethod1();
		inOrder.verify(callable).afterAllMethod2();
	}

	@Test
	@DisplayName("should run extensions")
	void shouldRunExtensions() {
		var callable = mock(Callable.class);
		var runner = new JnrTestRunner()
				.testCase(new JnrTestCase("a test case") {
			// this must be mocked by the first test extension
			@Mock
			Object sut = null;

			// this must be injected by the second test extension
			@Inject
			String stringSut;

			@Override
			protected void specify() {
				test("first test", () -> {
					// the assertion fails if the extension
					// does not inject it, see below
					assertEquals("first string", stringSut);
					// this will not be called if the assertion fails
					callable.firstMethod();
				});
				test("second test", () -> {
					// the assertion fails if the extension
					// does not mock it, see below
					assertNotNull(sut);
					// this will not be called if the assertion fails
					callable.firstMethod();
				});
				test("third test", () -> {
					// the assertion fails if the extension's
					// afterTest has not been called
					assertEquals("after first string", stringSut);
					// this will not be called if the assertion fails
					callable.firstMethod();
				});
			}
		});
		runner.extendWith(new JnrTestExtension() {
			@Override
			public void beforeTest(JnrTestCase t) {
				MockitoAnnotations.openMocks(t);
			}
		});
		runner.extendWith(new JnrTestExtension() {
			// this will be modified in afterTest
			// so this value is used only by the first test
			String stringToInject = "first string";

			@Override
			public void beforeTest(JnrTestCase t) {
				Guice.createInjector(
					binder -> {
						binder.bind(String.class)
							.toInstance(stringToInject);
				}).injectMembers(t);
			}

			@Override
			public void afterTest(JnrTestCase t) {
				stringToInject = "after first string";
			}
		});
		runner.execute();
		// this fails if the assertions above fail
		// that is, if the extensions have not been used
		verify(callable, times(3)).firstMethod();
	}
}
