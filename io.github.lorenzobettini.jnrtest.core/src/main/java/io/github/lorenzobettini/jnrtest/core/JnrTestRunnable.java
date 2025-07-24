package io.github.lorenzobettini.jnrtest.core;

/**
 * A runnable specification.
 * 
 * This can represent a test case or a lifecycle code.
 * 
 * Differently from Java {@link Runnable}, this interface allows throwing
 * exceptions, which is useful for test execution where exceptions may
 * occur during the test run.
 * 
 * @author Lorenzo Bettini
 */
@FunctionalInterface
public interface JnrTestRunnable {
	/**
	 * Runs the code.
	 * 
	 * @throws Exception any exception that may occur during the run
	 */
	void run() throws Exception; // NOSONAR
}
