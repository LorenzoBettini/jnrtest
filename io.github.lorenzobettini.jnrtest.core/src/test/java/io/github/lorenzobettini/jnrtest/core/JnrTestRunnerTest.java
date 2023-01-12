package io.github.lorenzobettini.jnrtest.core;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
		JnrTestRunner runner = new JnrTestRunner() {
			@Override
			protected void withSpecifications() {
				test("first test", () -> {
					callable.firstMethod();
				});
				test("test throwing exception", () -> {
					throw new RuntimeException("exception");
				});
				test("test failing assertion", () -> {
					assertTrue(false);
				});
				test("second test", () -> {
					callable.secondMethod();
				});
			}
		};
		runner.execute();
		var inOrder = inOrder(callable);
		inOrder.verify(callable).firstMethod();
		inOrder.verify(callable).secondMethod();
	}

	@Test
	@DisplayName("should execute lifecycle")
	void shouldExecuteLifecycle() {
		var callable = mock(Callable.class);
		JnrTestRunner runner = new JnrTestRunner() {
			@Override
			protected void withSpecifications() {
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
		};
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

}
