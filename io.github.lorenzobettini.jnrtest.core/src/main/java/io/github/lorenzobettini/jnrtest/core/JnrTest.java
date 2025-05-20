package io.github.lorenzobettini.jnrtest.core;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Specifies the tests represented by {@link JnrTestRunnableSpecification}, by
 * overriding {@link #specify()}.
 * 
 * @author Lorenzo Bettini
 *
 */
public abstract class JnrTest {

	private String description;

	private JnrTestStore store = null;

	/**
	 * A pair for parameterized tests, when two parameters would be needed.
	 * 
	 * @author Lorenzo Bettini
	 *
	 * @param <T1> The type of the first element
	 * @param <T2> The type of the second element
	 */
	public static class Pair<T1, T2> {
		private T1 first;
		private T2 second;

		/**
		 * Creates a new pair with the given elements.
		 * 
		 * @param first The first element
		 * @param second The second element
		 */
		public Pair(T1 first, T2 second) {
			this.first = first;
			this.second = second;
		}

		/**
		 * Static factory method to create a new pair.
		 * 
		 * @param <U> The type of the first element
		 * @param <V> The type of the second element
		 * @param first The first element
		 * @param second The second element
		 * @return A new pair containing the given elements
		 */
		public static <U, V> Pair<U, V> pair(U first, V second) {
			return new Pair<>(first, second);
		}

		/**
		 * Gets the first element of the pair.
		 * 
		 * @return The first element
		 */
		public T1 first() {
			return first;
		}

		/**
		 * Gets the second element of the pair.
		 * 
		 * @return The second element
		 */
		public T2 second() {
			return second;
		}

		@Override
		public String toString() {
			return "(" + first + "," + second + ")";
		}
	}

	/**
	 * Creates a new test with the given description.
	 * 
	 * @param description The description of the test
	 */
	protected JnrTest(String description) {
		this.description = description;
	}

	/**
	 * Returns the {@link JnrTestStore}, created, the first time,
	 * by calling {@link #specify()}.
	 * 
	 * @return The test store containing all test specifications
	 */
	public JnrTestStore getStore() {
		if (store == null) {
			store = new JnrTestStore();
			specify();
		}
		return store;
	}

	/**
	 * Responsible of specifying the tests by calling the method
	 * {@link #test(String, JnrTestRunnable)}.
	 */
	protected abstract void specify();

	/**
	 * Specify a test to run (in the shape of a {@link JnrTestRunnable}, with the
	 * given description.
	 * 
	 * @param description The description of the test to be executed
	 * @param testRunnable The runnable implementation containing the test code to execute
	 */
	protected void test(String description, JnrTestRunnable testRunnable) {
		store.test(description, testRunnable);
	}

	/**
	 * Specify a test to run with parameters; parameters are provided by
	 * parameterProvider, and the description is formatted with the parameters
	 * provided for each single test.
	 * 
	 * @param <T> The type of the parameter
	 * @param description The description template for the test
	 * @param parameterProvider A supplier that provides a collection of parameters
	 * @param testRunnable The runnable implementation containing the test code to execute with each parameter
	 */
	protected <T> void testWithParameters(String description, Supplier<Collection<T>> parameterProvider,
			JnrTestRunnableWithParameters<T> testRunnable) {
		testWithParameters(description, parameterProvider,
			Object::toString,
			testRunnable);
	}

	/**
	 * Specify a test to run with parameters; parameters are provided by
	 * parameterProvider, and the description for parameters is provided by the
	 * descriptionProvider (that has to provide a description for each passed
	 * parameter).
	 * 
	 * @param <T> The type of the parameter
	 * @param description The description template for the test
	 * @param parameterProvider A supplier that provides a collection of parameters
	 * @param descriptionProvider A function that converts each parameter to a string representation
	 * @param testRunnable The runnable implementation containing the test code to execute with each parameter
	 */
	protected <T> void testWithParameters(String description, Supplier<Collection<T>> parameterProvider,
			Function<T, String> descriptionProvider,
			JnrTestRunnableWithParameters<T> testRunnable) {
		var parameters = parameterProvider.get();
		for (T parameter : parameters) {
			test(description + descriptionProvider.apply(parameter), () -> testRunnable.runTest(parameter));
		}
	}

	/**
	 * Specifies a code to run before all tests.
	 * 
	 * @param description The description of the before-all hook
	 * @param beforeAllRunnable The runnable to execute before all tests
	 */
	protected void beforeAll(String description, JnrTestRunnable beforeAllRunnable) {
		store.beforeAll(description, beforeAllRunnable);
	}

	/**
	 * Specifies a code to run before each test.
	 * 
	 * @param description The description of the before-each hook
	 * @param beforeEachRunnable The runnable to execute before each test
	 */
	protected void beforeEach(String description, JnrTestRunnable beforeEachRunnable) {
		store.beforeEach(description, beforeEachRunnable);
	}

	/**
	 * Specifies a code to run after all tests.
	 * 
	 * @param description The description of the after-all hook
	 * @param afterAllRunnable The runnable to execute after all tests
	 */
	protected void afterAll(String description, JnrTestRunnable afterAllRunnable) {
		store.afterAll(description, afterAllRunnable);
	}

	/**
	 * Specifies a code to run after each test.
	 * 
	 * @param description The description of the after-each hook
	 * @param afterEachRunnable The runnable to execute after each test
	 */
	protected void afterEach(String description, JnrTestRunnable afterEachRunnable) {
		store.afterEach(description, afterEachRunnable);
	}

	/**
	 * Gets the description of this test.
	 * 
	 * @return The description of this test
	 */
	public String getDescription() {
		return description;
	}

}
