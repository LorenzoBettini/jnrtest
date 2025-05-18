package io.github.lorenzobettini.jnrtest.core;

/**
 * Interface for filtering test classes to be executed.
 * 
 * @author Lorenzo Bettini
 */
@FunctionalInterface
public interface JnrTestClassFilter {
	/**
	 * Determines whether a test class should be included in the execution.
	 * 
	 * @param testClass the test class to check
	 * @return true if the test class should be included, false otherwise
	 */
	boolean include(JnrTest testClass);
}
