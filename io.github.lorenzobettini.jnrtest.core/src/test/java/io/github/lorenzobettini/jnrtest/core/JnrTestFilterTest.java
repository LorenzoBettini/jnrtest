package io.github.lorenzobettini.jnrtest.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JnrTestFilterTest {

	static interface Callable {
		void testMethod1();
		void testMethod2();
		void testMethod3();
		void testMethod4();
	}

	@Test
	@DisplayName("should filter by test class description")
	void shouldFilterByTestClassDescription() {
		var callable = mock(Callable.class);
		JnrTestRunner runner = new JnrTestRunner()
				.add(new JnrTest("FirstTestClass") {
					@Override
					protected void specify() {
						test("test 1", () -> {
							callable.testMethod1();
						});
						test("test 2", () -> {
							callable.testMethod2();
						});
					}
				})
				.add(new JnrTest("SecondTestClass") {
					@Override
					protected void specify() {
						test("test 3", () -> {
							callable.testMethod3();
						});
						test("test 4", () -> {
							callable.testMethod4();
						});
					}
				})
				.filterByTestClassDescription("First.*");
		
		runner.execute();
		
		// Only methods from the first test class should be executed
		verify(callable).testMethod1();
		verify(callable).testMethod2();
		verify(callable, never()).testMethod3();
		verify(callable, never()).testMethod4();
	}
	
	@Test
	@DisplayName("should filter by test specification description")
	void shouldFilterByTestSpecificationDescription() {
		var callable = mock(Callable.class);
		JnrTestRunner runner = new JnrTestRunner()
				.add(new JnrTest("FirstTestClass") {
					@Override
					protected void specify() {
						test("test 1", () -> {
							callable.testMethod1();
						});
						test("important test", () -> {
							callable.testMethod2();
						});
					}
				})
				.add(new JnrTest("SecondTestClass") {
					@Override
					protected void specify() {
						test("test 3", () -> {
							callable.testMethod3();
						});
						test("important test 2", () -> {
							callable.testMethod4();
						});
					}
				})
				.filterByTestSpecificationDescription(".*important.*");
		
		runner.execute();
		
		// Only methods with "important" in their description should be executed
		verify(callable, never()).testMethod1();
		verify(callable).testMethod2();
		verify(callable, never()).testMethod3();
		verify(callable).testMethod4();
	}
	
	@Test
	@DisplayName("should combine multiple filters with AND logic")
	void shouldCombineFiltersWithAnd() {
		var callable = mock(Callable.class);
		JnrTestRunner runner = new JnrTestRunner()
				.add(new JnrTest("FirstTestClass") {
					@Override
					protected void specify() {
						test("test 1", () -> {
							callable.testMethod1();
						});
						test("important test", () -> {
							callable.testMethod2();
						});
					}
				})
				.add(new JnrTest("SecondTestClass") {
					@Override
					protected void specify() {
						test("test 3", () -> {
							callable.testMethod3();
						});
						test("important test 2", () -> {
							callable.testMethod4();
						});
					}
				})
				.filter(JnrTestFilters.all(
						JnrTestFilters.byTestClassDescription("First.*"),
						JnrTestFilters.byTestSpecificationDescription(".*important.*")
				));
		
		runner.execute();
		
		// Only method2 should be executed (from FirstTestClass with "important" in description)
		verify(callable, never()).testMethod1();
		verify(callable).testMethod2();
		verify(callable, never()).testMethod3();
		verify(callable, never()).testMethod4();
	}
	
	@Test
	@DisplayName("should combine multiple filters with OR logic")
	void shouldCombineFiltersWithOr() {
		var callable = mock(Callable.class);
		JnrTestRunner runner = new JnrTestRunner()
				.add(new JnrTest("FirstTestClass") {
					@Override
					protected void specify() {
						test("test 1", () -> {
							callable.testMethod1();
						});
						test("special test", () -> {
							callable.testMethod2();
						});
					}
				})
				.add(new JnrTest("SecondTestClass") {
					@Override
					protected void specify() {
						test("test 3", () -> {
							callable.testMethod3();
						});
						test("important test", () -> {
							callable.testMethod4();
						});
					}
				})
				.filter(JnrTestFilters.any(
						JnrTestFilters.byTestClassDescription("First.*"),
						JnrTestFilters.byTestSpecificationDescription(".*important.*")
				));
		
		runner.execute();
		
		// Methods from FirstTestClass and those with "important" in description should be executed
		verify(callable).testMethod1();
		verify(callable).testMethod2();
		verify(callable, never()).testMethod3();
		verify(callable).testMethod4();
	}
	
	@Test
	@DisplayName("should negate filter")
	void shouldNegateFilter() {
		var callable = mock(Callable.class);
		JnrTestRunner runner = new JnrTestRunner()
				.add(new JnrTest("FirstTestClass") {
					@Override
					protected void specify() {
						test("test 1", () -> {
							callable.testMethod1();
						});
						test("important test", () -> {
							callable.testMethod2();
						});
					}
				})
				.add(new JnrTest("SecondTestClass") {
					@Override
					protected void specify() {
						test("test 3", () -> {
							callable.testMethod3();
						});
						test("important test 2", () -> {
							callable.testMethod4();
						});
					}
				})
				.filter(JnrTestFilters.not(
						JnrTestFilters.byTestSpecificationDescription(".*important.*")
				));
		
		runner.execute();
		
		// Only methods without "important" in their description should be executed
		verify(callable).testMethod1();
		verify(callable, never()).testMethod2();
		verify(callable).testMethod3();
		verify(callable, never()).testMethod4();
	}
}
