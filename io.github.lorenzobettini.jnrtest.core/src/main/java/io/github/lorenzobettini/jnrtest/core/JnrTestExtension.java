package io.github.lorenzobettini.jnrtest.core;

import java.util.List;

/**
 * Represents code to be executed before and after a {@link JnrTest}
 * lifecyle.
 * 
 * The implementation must implement the method {@link #extend(JnrTest, List, List)} that
 * receives the lists of runnables to be executed "before" and "after". Whether
 * they are the lists of runnables before and after all or each tests depends on
 * the public method invoked: {@link #extendAll(JnrTest)} or
 * {@link #extendEach(JnrTest)}, respectively.
 * 
 * @author Lorenzo Bettini
 *
 */
public abstract class JnrTestExtension {

	/**
	 * Creates a new test extension.
	 */
	protected JnrTestExtension() {
		// Default constructor
	}

	/**
	 * Extends a test class with before-all and after-all hooks.
	 * 
	 * @param <T> The type of the test class
	 * @param testClass The test class to extend
	 * @return The extended test class
	 */
	public <T extends JnrTest> T extendAll(T testClass) {
		var store = testClass.getStore();
		extend(testClass, store.getBeforeAllRunnables(), store.getAfterAllRunnables());
		return testClass;
	}

	/**
	 * Extends a test class with before-each and after-each hooks.
	 * 
	 * @param <T> The type of the test class
	 * @param testClass The test class to extend
	 * @return The extended test class
	 */
	public <T extends JnrTest> T extendEach(T testClass) {
		var store = testClass.getStore();
		extend(testClass, store.getBeforeEachRunnables(), store.getAfterEachRunnables());
		return testClass;
	}

	/**
	 * Extends a test class by modifying the before and after runnables.
	 * 
	 * @param <T> The type of the test class
	 * @param testClass The test class to extend
	 * @param before The list of runnables to execute before tests
	 * @param after The list of runnables to execute after tests
	 */
	protected abstract <T extends JnrTest> void extend(T testClass, List<JnrTestRunnableSpecification> before,
			List<JnrTestRunnableSpecification> after);

}
