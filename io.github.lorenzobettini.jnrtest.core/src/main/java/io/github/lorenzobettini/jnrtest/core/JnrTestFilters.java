package io.github.lorenzobettini.jnrtest.core;

/**
 * Standard implementations of {@link JnrTestFilter}.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestFilters {
	
	/**
	 * Filter that accepts all tests.
	 */
	public static final JnrTestFilter ACCEPT_ALL = (testClass, runnableSpecification) -> true;
	
	/**
	 * Creates a filter that accepts tests whose test class description matches 
	 * the specified pattern.
	 * 
	 * @param pattern the regex pattern to match against the test class description
	 * @return a filter that accepts tests whose test class description matches the pattern
	 */
	public static JnrTestFilter byTestClassDescription(String pattern) {
		return (testClass, runnableSpecification) -> 
			testClass.getDescription().matches(pattern);
	}
	
	/**
	 * Creates a filter that accepts tests whose test specification description matches 
	 * the specified pattern.
	 * 
	 * @param pattern the regex pattern to match against the test specification description
	 * @return a filter that accepts tests whose test specification description matches the pattern
	 */
	public static JnrTestFilter byTestSpecificationDescription(String pattern) {
		return (testClass, runnableSpecification) -> 
			runnableSpecification.description().matches(pattern);
	}
	
	/**
	 * Combines multiple filters with a logical AND.
	 * 
	 * @param filters the filters to combine
	 * @return a filter that accepts a test only if all the specified filters accept it
	 */
	public static JnrTestFilter all(JnrTestFilter... filters) {
		return (testClass, runnableSpecification) -> {
			for (JnrTestFilter filter : filters) {
				if (!filter.include(testClass, runnableSpecification)) {
					return false;
				}
			}
			return true;
		};
	}
	
	/**
	 * Combines multiple filters with a logical OR.
	 * 
	 * @param filters the filters to combine
	 * @return a filter that accepts a test if any of the specified filters accept it
	 */
	public static JnrTestFilter any(JnrTestFilter... filters) {
		return (testClass, runnableSpecification) -> {
			for (JnrTestFilter filter : filters) {
				if (filter.include(testClass, runnableSpecification)) {
					return true;
				}
			}
			return filters.length == 0;
		};
	}
	
	/**
	 * Creates a filter that negates the result of the specified filter.
	 * 
	 * @param filter the filter to negate
	 * @return a filter that accepts a test if the specified filter rejects it
	 */
	public static JnrTestFilter not(JnrTestFilter filter) {
		return (testClass, runnableSpecification) -> 
			!filter.include(testClass, runnableSpecification);
	}
}
