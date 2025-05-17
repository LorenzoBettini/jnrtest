package io.github.lorenzobettini.jnrtest.core;

import java.util.Arrays;

/**
 * Standard implementations of filters for test classes and test specifications.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestFilters {

	private JnrTestFilters() {
		// Utility class should not be instantiated
	}

	/**
	 * Creates a filter that accepts test classes whose description matches 
	 * the specified pattern.
	 * 
	 * @param pattern the regex pattern to match against the test class description
	 * @return a filter that accepts test classes whose description matches the pattern
	 */
	public static JnrTestClassFilter byClassDescription(String pattern) {
		return (testClass) -> testClass.getDescription().matches(pattern);
	}

	/**
	 * Creates a filter that accepts test specifications whose description matches 
	 * the specified pattern.
	 * 
	 * @param pattern the regex pattern to match against the test specification description
	 * @return a filter that accepts test specifications whose description matches the pattern
	 */
	public static JnrTestSpecificationFilter bySpecificationDescription(String pattern) {
		return (runnableSpecification) -> 
			runnableSpecification.description().matches(pattern);
	}

	/**
	 * Combines multiple class filters with a logical AND.
	 * 
	 * @param filters the filters to combine
	 * @return a filter that accepts a test class only if all the specified filters accept it.
	 *         When the array of filters is empty, this returns a filter that always accepts
	 *         (returns true for) every test class.
	 */
	public static JnrTestClassFilter allClasses(JnrTestClassFilter... filters) {
		return (testClass) -> {
			if (filters.length == 0) {
				return true;
			}
			return Arrays.stream(filters)
					.allMatch(filter -> filter.include(testClass));
		};
	}

	/**
	 * Combines multiple specification filters with a logical AND.
	 * 
	 * @param filters the filters to combine
	 * @return a filter that accepts a test specification only if all the specified filters accept it.
	 *         When the array of filters is empty, this returns a filter that always accepts
	 *         (returns true for) every specification.
	 */
	public static JnrTestSpecificationFilter allSpecifications(JnrTestSpecificationFilter... filters) {
		return (runnableSpecification) -> {
			if (filters.length == 0) {
				return true;
			}
			return Arrays.stream(filters)
					.allMatch(filter -> filter.include(runnableSpecification));
		};
	}

	/**
	 * Combines multiple class filters with a logical OR.
	 * 
	 * @param filters the filters to combine
	 * @return a filter that accepts a test class if any of the specified filters accept it.
	 *         When the array of filters is empty, this returns a filter that always accepts
	 *         (returns true for) every test class.
	 */
	public static JnrTestClassFilter anyClass(JnrTestClassFilter... filters) {
		return (testClass) -> {
			if (filters.length == 0) {
				return true;
			}
			return Arrays.stream(filters)
					.anyMatch(filter -> filter.include(testClass));
		};
	}

	/**
	 * Combines multiple specification filters with a logical OR.
	 * 
	 * @param filters the filters to combine
	 * @return a filter that accepts a test specification if any of the specified filters accept it.
	 *         When the array of filters is empty, this returns a filter that always accepts
	 *         (returns true for) every specification.
	 */
	public static JnrTestSpecificationFilter anySpecification(JnrTestSpecificationFilter... filters) {
		return (runnableSpecification) -> {
			if (filters.length == 0) {
				return true;
			}
			return Arrays.stream(filters)
					.anyMatch(filter -> filter.include(runnableSpecification));
		};
	}

	/**
	 * Creates a filter that negates the result of the specified class filter.
	 * 
	 * @param filter the filter to negate
	 * @return a filter that accepts a test class if the specified filter rejects it
	 */
	public static JnrTestClassFilter notClass(JnrTestClassFilter filter) {
		return (testClass) -> !filter.include(testClass);
	}

	/**
	 * Creates a filter that negates the result of the specified specification filter.
	 * 
	 * @param filter the filter to negate
	 * @return a filter that accepts a test specification if the specified filter rejects it
	 */
	public static JnrTestSpecificationFilter notSpecification(JnrTestSpecificationFilter filter) {
		return (runnableSpecification) -> 
			!filter.include(runnableSpecification);
	}
}
