package io.github.lorenzobettini.jnrtest.core;

/**
 * Interface for filtering test specifications to be executed.
 * 
 * @author Lorenzo Bettini
 */
@FunctionalInterface
public interface JnrTestSpecificationFilter {
	/**
	 * Determines whether a test specification should be included in the execution.
	 * 
	 * @param runnableSpecification the test specification to check
	 * @return true if the test specification should be included, false otherwise
	 */
	boolean include(JnrTestRunnableSpecification runnableSpecification);
}
