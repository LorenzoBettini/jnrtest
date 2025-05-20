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
	 * <pre>
	 * // Create a filter that matches test classes whose description contains "Calculator"
	 * Predicate&lt;JnrTest&gt; calculatorFilter = testClass -&gt; 
	 *     testClass.getDescription().contains("Calculator");
	 * 
	 * // Apply the filter to a test runner
	 * runner.classFilter(calculatorFilter);
	 * </pre>
	 */
	public static void filterByClassDescription() {
		// Example method - not meant to be executed
	}

	/**
	 * How to filter test specifications by their description.
	 * 
	 * <pre>
	 * // Create a filter that matches specifications whose description contains "important"
	 * Predicate&lt;JnrTestRunnableSpecification&gt; importantFilter = spec -&gt; 
	 *     spec.description().contains("important");
	 * 
	 * // Apply the filter to a test runner
	 * runner.specificationFilter(importantFilter);
	 * </pre>
	 */
	public static void filterBySpecificationDescription() {
		// Example method - not meant to be executed
	}

	/**
	 * How to combine multiple filters using AND logic.
	 * 
	 * <pre>
	 * // Create two filters
	 * Predicate&lt;JnrTest&gt; calculatorFilter = testClass -&gt; 
	 *     testClass.getDescription().contains("Calculator");
	 * Predicate&lt;JnrTest&gt; utilsFilter = testClass -&gt; 
	 *     testClass.getDescription().contains("Utils");
	 * 
	 * // Combine filters with AND
	 * Predicate&lt;JnrTest&gt; combinedFilter = calculatorFilter.and(utilsFilter);
	 * 
	 * // Apply the combined filter
	 * runner.classFilter(combinedFilter);
	 * </pre>
	 */
	public static void combineFiltersWithAnd() {
		// Example method - not meant to be executed
	}

	/**
	 * How to combine multiple filters using OR logic.
	 * 
	 * <pre>
	 * // Create two filters
	 * Predicate&lt;JnrTest&gt; calculatorFilter = testClass -&gt; 
	 *     testClass.getDescription().contains("Calculator");
	 * Predicate&lt;JnrTest&gt; stringFilter = testClass -&gt; 
	 *     testClass.getDescription().contains("String");
	 * 
	 * // Combine filters with OR
	 * Predicate&lt;JnrTest&gt; combinedFilter = calculatorFilter.or(stringFilter);
	 * 
	 * // Apply the combined filter
	 * runner.classFilter(combinedFilter);
	 * </pre>
	 */
	public static void combineFiltersWithOr() {
		// Example method - not meant to be executed
	}

	/**
	 * How to negate a filter.
	 * 
	 * <pre>
	 * // Create a filter
	 * Predicate&lt;JnrTest&gt; calculatorFilter = testClass -&gt; 
	 *     testClass.getDescription().contains("Calculator");
	 * 
	 * // Negate the filter
	 * Predicate&lt;JnrTest&gt; notCalculatorFilter = calculatorFilter.negate();
	 * 
	 * // Apply the negated filter
	 * runner.classFilter(notCalculatorFilter);
	 * </pre>
	 */
	public static void negateFilter() {
		// Example method - not meant to be executed
	}

	/**
	 * Using convenience methods from JnrTestFilters.
	 * 
	 * <pre>
	 * // Create a filter directly
	 * Predicate&lt;JnrTest&gt; classFilter = testClass -&gt; 
	 *     testClass.getDescription().matches("Calculator.*");
	 * 
	 * // Apply the filter
	 * runner.classFilter(classFilter);
	 * 
	 * // Or use the convenience methods directly on the runner
	 * runner.filterByClassDescription("Calculator.*");
	 * </pre>
	 */
	public static void usingConvenienceMethods() {
		// Example method - not meant to be executed
	}
}