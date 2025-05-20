package io.github.lorenzobettini.jnrtest.core;

import java.util.function.Predicate;

/**
 * Manages filters for test classes and test specifications.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestFilters {

	private Predicate<JnrTest> classFilter;
	private Predicate<JnrTestRunnableSpecification> specificationFilter;

	/**
	 * Creates a new instance with null filters.
	 */
	public JnrTestFilters() {
		this.classFilter = null;
		this.specificationFilter = null;
	}

	/**
	 * Creates a filter that accepts test classes whose description matches 
	 * the specified pattern.
	 * 
	 * @param pattern the regex pattern to match against the test class description
	 * @return this instance for method chaining
	 */
	public JnrTestFilters byClassDescription(String pattern) {
		return classFilter(testClass -> testClass.getDescription().matches(pattern));
	}

	/**
	 * Creates a filter that accepts test specifications whose description matches 
	 * the specified pattern.
	 * 
	 * @param pattern the regex pattern to match against the test specification description
	 * @return this instance for method chaining
	 */
	public JnrTestFilters bySpecificationDescription(String pattern) {
		return specificationFilter(
			runnableSpecification -> runnableSpecification.description().matches(pattern)
		);
	}

	/**
	 * Adds a class filter and combines it with any existing class filter using logical AND.
	 * 
	 * @param filter the filter to add
	 * @return this instance for method chaining
	 */
	public JnrTestFilters classFilter(Predicate<JnrTest> filter) {
		if (this.classFilter != null) {
			this.classFilter = this.classFilter.and(filter);
		} else {
			this.classFilter = filter;
		}
		return this;
	}

	/**
	 * Adds a specification filter and combines it with any existing specification filter using logical AND.
	 * 
	 * @param filter the filter to add
	 * @return this instance for method chaining
	 */
	public JnrTestFilters specificationFilter(Predicate<JnrTestRunnableSpecification> filter) {
		if (this.specificationFilter != null) {
			this.specificationFilter = this.specificationFilter.and(filter);
		} else {
			this.specificationFilter = filter;
		}
		return this;
	}

	/**
	 * Gets the current class filter.
	 * 
	 * @return the current class filter
	 */
	public Predicate<JnrTest> getClassFilter() {
		return classFilter;
	}

	/**
	 * Gets the current specification filter.
	 * 
	 * @return the current specification filter
	 */
	public Predicate<JnrTestRunnableSpecification> getSpecificationFilter() {
		return specificationFilter;
	}
	
	/**
	 * Create a filter predicate that tests if a class description matches a pattern.
	 * 
	 * @param pattern the regex pattern to match
	 * @return a predicate that tests if a class description matches the pattern
	 */
	public Predicate<JnrTest> createClassDescriptionFilter(String pattern) {
		return testClass -> testClass.getDescription().matches(pattern);
	}
	
	/**
	 * Create a filter predicate that tests if a specification description matches a pattern.
	 * 
	 * @param pattern the regex pattern to match
	 * @return a predicate that tests if a specification description matches the pattern
	 */
	public Predicate<JnrTestRunnableSpecification> createSpecificationDescriptionFilter(String pattern) {
		return spec -> spec.description().matches(pattern);
	}
}
