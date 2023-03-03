package io.github.lorenzobettini.jnrtest.core;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Specifies the tests represented by {@link JnrTestSpecification}, by
 * overriding {@link #specify()}.
 * 
 * @author Lorenzo Bettini
 *
 */
public abstract class JnrTestCase {

	private String description;

	private JnrTestStore store = null;

	/**
	 * A pair for parameterized tests, when two parameters would be needed.
	 * 
	 * @author Lorenzo Bettini
	 *
	 * @param <T1>
	 * @param <T2>
	 */
	public static class Pair<T1, T2> {
		private T1 first;
		private T2 second;

		public Pair(T1 first, T2 second) {
			this.first = first;
			this.second = second;
		}

		public static <U, V> Pair<U, V> pair(U first, V second) {
			return new Pair<>(first, second);
		}

		public T1 first() {
			return first;
		}

		public T2 second() {
			return second;
		}

		@Override
		public String toString() {
			return "(" + first + "," + second + ")";
		}
	}

	protected JnrTestCase(String description) {
		this.description = description;
	}

	/**
	 * Returns the {@link JnrTestStore}, created, the first time,
	 * by callying {@link #specify()}.
	 * 
	 * @return
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
	 * @param description
	 * @param testRunnable
	 */
	protected void test(String description, JnrTestRunnable testRunnable) {
		store.test(description, testRunnable);
	}

	/**
	 * Specify a test to run with parameters; parameters are provided by
	 * parameterProvider, and the description is formatted with the parameters
	 * provided for each single test.
	 * 
	 * @param <T>
	 * @param description
	 * @param parameterProvider
	 * @param testRunnable
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
	 * @param <T>
	 * @param description
	 * @param parameterProvider
	 * @param descriptionProvider
	 * @param testRunnable
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
	 * @param description
	 * @param beforeAllRunnable
	 */
	protected void beforeAll(String description, JnrTestRunnable beforeAllRunnable) {
		store.beforeAll(description, beforeAllRunnable);
	}

	/**
	 * Specifies a code to run before each test.
	 * 
	 * @param description
	 * @param beforeEachRunnable
	 */
	protected void beforeEach(String description, JnrTestRunnable beforeEachRunnable) {
		store.beforeEach(description, beforeEachRunnable);
	}

	/**
	 * Specifies a code to run after all tests.
	 * 
	 * @param description
	 * @param afterAllRunnable
	 */
	protected void afterAll(String description, JnrTestRunnable afterAllRunnable) {
		store.afterAll(description, afterAllRunnable);
	}

	/**
	 * Specifies a code to run after each test.
	 * 
	 * @param description
	 * @param afterEachRunnable
	 */
	protected void afterEach(String description, JnrTestRunnable afterEachRunnable) {
		store.afterEach(description, afterEachRunnable);
	}

	public String getDescription() {
		return description;
	}

}
