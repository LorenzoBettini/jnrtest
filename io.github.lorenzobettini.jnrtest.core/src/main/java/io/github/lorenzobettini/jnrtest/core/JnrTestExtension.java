package io.github.lorenzobettini.jnrtest.core;

/**
 * Represents code to be executed before and after each test of the
 * {@link JnrTestRunner} passed as parameters to the methods.
 * 
 * The method {@link #beforeTest(JnrTestRunner)} must be implemented, while
 * {@link #afterTest(JnrTestRunner)} defaults to an empty implementation.
 * 
 * @author Lorenzo Bettini
 *
 */
@FunctionalInterface
public interface JnrTestExtension {

	/**
	 * Executed before running each single test of the passed runner.
	 * 
	 * @param runner
	 */
	void beforeTest(JnrTestRunner runner);

	/**
	 * Executed after each single test of the passed runner. By default, it does not
	 * perform anything.
	 * 
	 * @param runner
	 */
	default void afterTest(JnrTestRunner runner) {

	}
}
