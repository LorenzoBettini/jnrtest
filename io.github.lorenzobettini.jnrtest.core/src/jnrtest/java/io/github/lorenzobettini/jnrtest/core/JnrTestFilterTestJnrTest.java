package io.github.lorenzobettini.jnrtest.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.function.Predicate;

public class JnrTestFilterTestJnrTest extends JnrTest {

	public JnrTestFilterTestJnrTest() {
		super("JnrTestFilterTest in JnrTest");
	}

	protected @Override void specify() {
		test("should filter using class filter methods", () -> {
			var callable = mock(Callable.class);
			JnrTestRunner runner = new JnrTestRunner().add(new JnrTest("FirstTestClass") {
				@Override
				protected void specify() {
					test("test 1", callable::testMethod1);
					test("test 2", callable::testMethod2);
				}
			}).add(new JnrTest("SecondTestClass") {
				@Override
				protected void specify() {
					test("test 3", callable::testMethod3);
					test("test 4", callable::testMethod4);
				}
			});

			// Use convenience method instead of creating a Predicate directly
			runner.filterByClassDescription("First.*");

			runner.execute();

			// Only methods from the first test class should be executed
			verify(callable).testMethod1();
			verify(callable).testMethod2();
			verify(callable, never()).testMethod3();
			verify(callable, never()).testMethod4();
		});
		test("should filter by test specification description", () -> {
			var callable = mock(Callable.class);
			JnrTestRunner runner = new JnrTestRunner().add(new JnrTest("FirstTestClass") {
				@Override
				protected void specify() {
					test("test 1", callable::testMethod1);
					test("important test", callable::testMethod2);
				}
			}).add(new JnrTest("SecondTestClass") {
				@Override
				protected void specify() {
					test("test 3", callable::testMethod3);
					test("important test 2", callable::testMethod4);
				}
			});

			// Use convenience method instead of creating a Predicate directly
			runner.filterBySpecificationDescription(".*important.*");

			runner.execute();

			// Only methods with "important" in their description should be executed
			verify(callable, never()).testMethod1();
			verify(callable).testMethod2();
			verify(callable, never()).testMethod3();
			verify(callable).testMethod4();
		});
		test("should combine multiple filters with AND logic", () -> {
			var callable = mock(Callable.class);
			JnrTestRunner runner = new JnrTestRunner().add(new JnrTest("FirstTestClass") {
				@Override
				protected void specify() {
					test("test 1", callable::testMethod1);
					test("important test", callable::testMethod2);
				}
			}).add(new JnrTest("SecondTestClass") {
				@Override
				protected void specify() {
					test("test 3", callable::testMethod3);
					test("important test 2", callable::testMethod4);
				}
			});

			// Apply both filters using convenience methods
			runner.filterByClassDescription("First.*");
			runner.filterBySpecificationDescription(".*important.*");

			runner.execute();

			// Only method2 should be executed (from FirstTestClass with "important" in
			// description)
			verify(callable, never()).testMethod1();
			verify(callable).testMethod2();
			verify(callable, never()).testMethod3();
			verify(callable, never()).testMethod4();
		});
		test("should negate filter", () -> {
			var callable = mock(Callable.class);
			JnrTestRunner runner = new JnrTestRunner().add(new JnrTest("FirstTestClass") {
				@Override
				protected void specify() {
					test("test 1", callable::testMethod1);
					test("important test", callable::testMethod2);
				}
			}).add(new JnrTest("SecondTestClass") {
				@Override
				protected void specify() {
					test("test 3", callable::testMethod3);
					test("important test 2", callable::testMethod4);
				}
			});

			// Apply negation to the specification filter
			Predicate<JnrTestRunnableSpecification> importantFilter = spec -> spec.description()
					.matches(".*important.*");
			Predicate<JnrTestRunnableSpecification> notImportantFilter = importantFilter.negate();

			runner.specificationFilter(notImportantFilter);

			runner.execute();

			// Only methods without "important" in their description should be executed
			verify(callable).testMethod1();
			verify(callable, never()).testMethod2();
			verify(callable).testMethod3();
			verify(callable, never()).testMethod4();
		});
		test("should combine class filters with AND logic", () -> {
			var callable = mock(Callable.class);
			JnrTestRunner runner = new JnrTestRunner().add(new JnrTest("FirstTestClass") {
				@Override
				protected void specify() {
					test("test 1", callable::testMethod1);
				}
			}).add(new JnrTest("FirstImportantTestClass") {
				@Override
				protected void specify() {
					test("test 2", callable::testMethod2);
				}
			}).add(new JnrTest("SecondTestClass") {
				@Override
				protected void specify() {
					test("test 3", callable::testMethod3);
				}
			});

			// Apply AND logic to multiple class filters
			Predicate<JnrTest> firstFilter = testClass -> testClass.getDescription().matches("First.*");
			Predicate<JnrTest> importantFilter = testClass -> testClass.getDescription().matches(".*Important.*");

			// Combine filters with AND logic
			Predicate<JnrTest> combinedFilter = firstFilter.and(importantFilter);

			runner.classFilter(combinedFilter);

			runner.execute();

			// Only methods from classes matching both patterns should be executed
			verify(callable, never()).testMethod1();
			verify(callable).testMethod2();
			verify(callable, never()).testMethod3();
		});
		test("should combine specification filters with OR logic", () -> {
			var callable = mock(Callable.class);
			JnrTestRunner runner = new JnrTestRunner().add(new JnrTest("TestClass") {
				@Override
				protected void specify() {
					test("simple test", callable::testMethod1);
					test("important test", callable::testMethod2);
					test("critical test", callable::testMethod3);
					test("normal test", callable::testMethod4);
				}
			});

			// Apply OR logic to multiple specification filters
			Predicate<JnrTestRunnableSpecification> importantFilter = spec -> spec.description()
					.matches(".*important.*");
			Predicate<JnrTestRunnableSpecification> criticalFilter = spec -> spec.description().matches(".*critical.*");

			// Combine filters with OR logic
			Predicate<JnrTestRunnableSpecification> combinedFilter = importantFilter.or(criticalFilter);

			runner.specificationFilter(combinedFilter);

			runner.execute();

			// Only methods with "important" or "critical" in their description should be
			// executed
			verify(callable, never()).testMethod1();
			verify(callable).testMethod2();
			verify(callable).testMethod3();
			verify(callable, never()).testMethod4();
		});
	}

	static interface Callable {
		void testMethod1();

		void testMethod2();

		void testMethod3();

		void testMethod4();
	}
}
