package io.github.lorenzobettini.jnrtest.core;

/**
 * Represents code to be executed before and after each test of the
 * {@link JnrTestCase} passed as parameters to the methods.
 * 
 * The method {@link #beforeTest(JnrTestCase)} must be implemented, while
 * {@link #afterTest(JnrTestCase)} defaults to an empty implementation.
 * 
 * @author Lorenzo Bettini
 *
 */
@FunctionalInterface
public interface JnrTestExtension {

	/**
	 * Executed before running each single test of the passed {@link JnrTestCase}.
	 * 
	 * @param runner
	 */
	void beforeTest(JnrTestCase testCase);

	/**
	 * Executed after each single test of the passed {@link JnrTestCase}. By
	 * default, it does not perform anything.
	 * 
	 * @param runner
	 */
	default void afterTest(JnrTestCase testCase) {

	}
}
