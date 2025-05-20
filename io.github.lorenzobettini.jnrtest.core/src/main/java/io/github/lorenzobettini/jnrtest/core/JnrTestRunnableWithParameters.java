package io.github.lorenzobettini.jnrtest.core;

/**
 * A runnable test with parameters.
 * 
 * @author Lorenzo Bettini
 * @param <T> The type of parameter that will be passed to the test
 */
@FunctionalInterface
public interface JnrTestRunnableWithParameters<T> {

	/**
	 * Runs the test with the specified parameter.
	 * 
	 * @param parameter The parameter to pass to the test
	 * @throws Exception If the test execution fails
	 */
	void runTest(T parameter) throws Exception; // NOSONAR
}
