package io.github.lorenzobettini.jnrtest.core;

/**
 * A runnable test with parameters.
 * 
 * @author Lorenzo Bettini
 */
@FunctionalInterface
public interface JnrTestRunnableWithParameters<T> {

	void runTest(T parameter) throws Exception; // NOSONAR
}
