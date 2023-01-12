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
}
