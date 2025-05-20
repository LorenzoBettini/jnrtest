package io.github.lorenzobettini.jnrtest.core;

/**
 * A runnable test.
 * 
 * @author Lorenzo Bettini
 */
@FunctionalInterface
public interface JnrTestRunnable {
	/**
	 * Runs the test.
	 * 
	 * @throws Exception If the test execution fails
	 */
	void runTest() throws Exception; // NOSONAR
}
