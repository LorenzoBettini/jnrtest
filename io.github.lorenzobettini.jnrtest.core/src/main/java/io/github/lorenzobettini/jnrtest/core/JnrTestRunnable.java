package io.github.lorenzobettini.jnrtest.core;

/**
 * A runnable test.
 * 
 * @author Lorenzo Bettini
 */
@FunctionalInterface
public interface JnrTestRunnable {
	void runTest() throws Exception; // NOSONAR
}
