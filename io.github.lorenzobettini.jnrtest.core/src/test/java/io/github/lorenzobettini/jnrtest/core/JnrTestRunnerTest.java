package io.github.lorenzobettini.jnrtest.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.inject.Guice;
import com.google.inject.Inject;

/**
 * Unit test for simple App.
 */
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
			public void notify(JnrTestCaseResult result) {
				this.results.append(result.toString() + "\n");
			}
		};
		JnrTestRunner runner = new JnrTestRunner()
			.testCase(new JnrTestCase("a test case") {
				@Override
				protected void specify() {
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
					test("test failing assertion", () -> {
						assertTrue(false);
					});
					test("second test", () -> {
						// success
					});
				}
			});
		runner.testListener(listener);
		runner.execute();
		assertEquals("""
				[  START] a test case
				[SUCCESS] first test
				[  ERROR] test throwing exception
				[    END] a test case
				[  START] another test case
				[ FAILED] test failing assertion
				[SUCCESS] second test
				[    END] another test case
				""", listener.results.toString());
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
				beforeEach(() -> {
					callable.beforeEachMethod1();
				});
				beforeEach(() -> {
					throw new RuntimeException("exception");
				});
				beforeEach(() -> {
					callable.beforeEachMethod2();
				});
				beforeAll(() -> {
					callable.beforeAllMethod1();
				});
				beforeAll(() -> {
					throw new RuntimeException("exception");
				});
				beforeAll(() -> {
					callable.beforeAllMethod2();
				});
				afterEach(() -> {
					callable.afterEachMethod1();
				});
				afterEach(() -> {
					throw new RuntimeException("exception");
				});
				afterEach(() -> {
					callable.afterEachMethod2();
				});
				afterAll(() -> {
					callable.afterAllMethod1();
				});
				afterAll(() -> {
					throw new RuntimeException("exception");
				});
				afterAll(() -> {
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
