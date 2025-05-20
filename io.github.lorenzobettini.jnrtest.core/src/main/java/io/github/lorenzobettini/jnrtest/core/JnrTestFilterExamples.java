package io.github.lorenzobettini.jnrtest.core;

/**
 * Examples of how to use filters with JnrTest.
 * 
 * <p>
 * This class provides documentation and code examples showing how to use
 * Java's Predicate functional interface to filter JnrTest classes and specifications.
 * </p>
 * 
 * @author Lorenzo Bettini
 */
public final class JnrTestFilterExamples {

	private JnrTestFilterExamples() {
		// Utility class should not be instantiated
	}

	/**
	 * How to filter test classes by their description.
	 * 
	 * <pre>{@code
	 * // Use the convenience method on the runner
	 * runner.filterByClassDescription("Calculator.*");
	 * }</pre>
	 */
	public static void filterByClassDescription() {
		// Example method - not meant to be executed
	}

	/**
	 * How to filter test specifications by their description.
	 * 
	 * <pre>{@code
	 * // Use the convenience method on the runner
	 * runner.filterBySpecificationDescription(".*important.*");
	 * }</pre>
	 */
	public static void filterBySpecificationDescription() {
		// Example method - not meant to be executed
	}

	/**
	 * How to combine multiple filters using AND logic.
	 * 
	 * <pre>{@code
	 * // Create two filters
	 * Predicate<JnrTest> calculatorFilter = testClass -> 
	 *     testClass.getDescription().contains("Calculator");
	 * Predicate<JnrTest> utilsFilter = testClass -> 
	 *     testClass.getDescription().contains("Utils");
	 * 
	 * // Combine filters with AND
	 * Predicate<JnrTest> combinedFilter = calculatorFilter.and(utilsFilter);
	 * 
	 * // Apply the combined filter
	 * runner.classFilter(combinedFilter);
	 * }</pre>
	 */
	public static void combineFiltersWithAnd() {
		// Example method - not meant to be executed
	}

	/**
	 * How to combine multiple filters using OR logic.
	 * 
	 * <pre>{@code
	 * // Create two filters
	 * Predicate<JnrTest> calculatorFilter = testClass -> 
	 *     testClass.getDescription().contains("Calculator");
	 * Predicate<JnrTest> stringFilter = testClass -> 
	 *     testClass.getDescription().contains("String");
	 * 
	 * // Combine filters with OR
	 * Predicate<JnrTest> combinedFilter = calculatorFilter.or(stringFilter);
	 * 
	 * // Apply the combined filter
	 * runner.classFilter(combinedFilter);
	 * }</pre>
	 */
	public static void combineFiltersWithOr() {
		// Example method - not meant to be executed
	}

	/**
	 * How to negate a filter.
	 * 
	 * <pre>{@code
	 * // Create a filter
	 * Predicate<JnrTest> calculatorFilter = testClass -> 
	 *     testClass.getDescription().contains("Calculator");
	 * 
	 * // Negate the filter
	 * Predicate<JnrTest> notCalculatorFilter = calculatorFilter.negate();
	 * 
	 * // Apply the negated filter
	 * runner.classFilter(notCalculatorFilter);
	 * }</pre>
	 */
	public static void negateFilter() {
		// Example method - not meant to be executed
	}

	/**
	 * Using convenience methods.
	 * 
	 * <pre>{@code
	 * // Using the convenience method directly on the runner
	 * runner.filterByClassDescription("Calculator.*");
	 * 
	 * // And for specifications
	 * runner.filterBySpecificationDescription(".*important.*");
	 * }</pre>
	 */
	public static void usingConvenienceMethods() {
		// Example method - not meant to be executed
	}
}