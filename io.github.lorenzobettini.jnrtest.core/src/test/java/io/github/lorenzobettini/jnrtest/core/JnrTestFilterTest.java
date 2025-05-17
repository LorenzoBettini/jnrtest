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
	@DisplayName("should filter using class filter methods")
	void shouldFilterUsingClassFilterMethods() {
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
				});
				
		// Use filterByClassDescription which is a convenience method
		runner.filterByClassDescription("First.*");
		
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
				});
				
		// Use convenience method for specification filtering
		runner.filterBySpecificationDescription(".*important.*");
		
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
				});
		
		// Apply both filters - class filter first, then specification filter
		runner.classFilter(JnrTestFilters.byClassDescription("First.*"));
		runner.specificationFilter(JnrTestFilters.bySpecificationDescription(".*important.*"));
		
		runner.execute();
		
		// Only method2 should be executed (from FirstTestClass with "important" in description)
		verify(callable, never()).testMethod1();
		verify(callable).testMethod2();
		verify(callable, never()).testMethod3();
		verify(callable, never()).testMethod4();
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
				});
		
		// Apply negation to the specification filter
		runner.specificationFilter(JnrTestFilters.notSpecification(
				JnrTestFilters.bySpecificationDescription(".*important.*")
		));
		
		runner.execute();
		
		// Only methods without "important" in their description should be executed
		verify(callable).testMethod1();
		verify(callable, never()).testMethod2();
		verify(callable).testMethod3();
		verify(callable, never()).testMethod4();
	}
	
	@Test
	@DisplayName("should combine class filters with AND logic")
	void shouldCombineClassFiltersWithAnd() {
		var callable = mock(Callable.class);
		JnrTestRunner runner = new JnrTestRunner()
				.add(new JnrTest("FirstTestClass") {
					@Override
					protected void specify() {
						test("test 1", () -> {
							callable.testMethod1();
						});
					}
				})
				.add(new JnrTest("FirstImportantTestClass") {
					@Override
					protected void specify() {
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
					}
				});
		
		// Apply AND logic to multiple class filters
		runner.classFilter(JnrTestFilters.allClasses(
				JnrTestFilters.byClassDescription("First.*"),
				JnrTestFilters.byClassDescription(".*Important.*")
		));
		
		runner.execute();
		
		// Only methods from classes matching both patterns should be executed
		verify(callable, never()).testMethod1();
		verify(callable).testMethod2();
		verify(callable, never()).testMethod3();
	}
	
	@Test
	@DisplayName("should combine specification filters with OR logic")
	void shouldCombineSpecificationFiltersWithOr() {
		var callable = mock(Callable.class);
		JnrTestRunner runner = new JnrTestRunner()
				.add(new JnrTest("TestClass") {
					@Override
					protected void specify() {
						test("simple test", () -> {
							callable.testMethod1();
						});
						test("important test", () -> {
							callable.testMethod2();
						});
						test("critical test", () -> {
							callable.testMethod3();
						});
						test("normal test", () -> {
							callable.testMethod4();
						});
					}
				});
		
		// Apply OR logic to multiple specification filters
		runner.specificationFilter(JnrTestFilters.anySpecification(
				JnrTestFilters.bySpecificationDescription(".*important.*"),
				JnrTestFilters.bySpecificationDescription(".*critical.*")
		));
		
		runner.execute();
		
		// Only methods with "important" or "critical" in their description should be executed
		verify(callable, never()).testMethod1();
		verify(callable).testMethod2();
		verify(callable).testMethod3();
		verify(callable, never()).testMethod4();
	}
}
