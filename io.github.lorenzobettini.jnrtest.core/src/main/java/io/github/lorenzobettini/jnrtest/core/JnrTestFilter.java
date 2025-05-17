package io.github.lorenzobettini.jnrtest.core;

/**
 * Interface for filtering tests to be executed.
 * 
 * @author Lorenzo Bettini
 */
@FunctionalInterface
public interface JnrTestFilter {
	/**
	 * Determines whether a test should be included in the execution.
	 * 
	 * @param testClass the test class to check
	 * @param runnableSpecification the test specification to check
	 * @return true if the test should be included, false otherwise
	 */
	boolean include(JnrTest testClass, JnrTestRunnableSpecification runnableSpecification);
}
