package io.github.lorenzobettini.jnrtest.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the tests represented by {@link JnrTestRunnableSpecification}.
 * 
 * @author Lorenzo Bettini
 *
 */
public class JnrTestStore {

	private List<JnrTestRunnableSpecification> runnableSpecifications = new ArrayList<>();

	private List<JnrTestRunnableSpecification> beforeAllRunnables = new ArrayList<>();
	private List<JnrTestRunnableSpecification> beforeEachRunnables = new ArrayList<>();
	private List<JnrTestRunnableSpecification> afterAllRunnables = new ArrayList<>();
	private List<JnrTestRunnableSpecification> afterEachRunnables = new ArrayList<>();

	/**
	 * Creates a new empty test store.
	 */
	public JnrTestStore() {
		// Default constructor
	}

	/**
	 * Specify a test to run (in the shape of a {@link JnrTestRunnable}, with the
	 * given description.
	 * 
	 * @param description The description of the test to be executed
	 * @param testRunnable The runnable implementation containing the test code to execute
	 */
	public void test(String description, JnrTestRunnable testRunnable) {
		runnableSpecifications.add(new JnrTestRunnableSpecification(description, testRunnable));
	}

	/**
	 * Specifies a code to run before all tests.
	 * 
	 * @param description The description of the before-all hook
	 * @param beforeAllRunnable The runnable to execute before all tests
	 */
	public void beforeAll(String description, JnrTestRunnable beforeAllRunnable) {
		beforeAllRunnables.add(new JnrTestRunnableSpecification(description, beforeAllRunnable));
	}

	/**
	 * Specifies a code to run before each test.
	 * 
	 * @param description The description of the before-each hook
	 * @param beforeEachRunnable The runnable to execute before each test
	 */
	public void beforeEach(String description, JnrTestRunnable beforeEachRunnable) {
		beforeEachRunnables.add(new JnrTestRunnableSpecification(description, beforeEachRunnable));
	}

	/**
	 * Specifies a code to run after all tests.
	 * 
	 * @param description The description of the after-all hook
	 * @param afterAllRunnable The runnable to execute after all tests
	 */
	public void afterAll(String description, JnrTestRunnable afterAllRunnable) {
		afterAllRunnables.add(new JnrTestRunnableSpecification(description, afterAllRunnable));
	}

	/**
	 * Specifies a code to run after each test.
	 * 
	 * @param description The description of the after-each hook
	 * @param afterEachRunnable The runnable to execute after each test
	 */
	public void afterEach(String description, JnrTestRunnable afterEachRunnable) {
		afterEachRunnables.add(new JnrTestRunnableSpecification(description, afterEachRunnable));
	}

	/**
	 * Gets the list of all runnable specifications (tests).
	 * 
	 * @return the list of test specifications
	 */
	public List<JnrTestRunnableSpecification> getRunnableSpecifications() {
		return runnableSpecifications;
	}

	/**
	 * Gets the list of before-all runnable specifications.
	 * 
	 * @return the list of before-all runnable specifications
	 */
	public List<JnrTestRunnableSpecification> getBeforeAllRunnables() {
		return beforeAllRunnables;
	}

	/**
	 * Gets the list of before-each runnable specifications.
	 * 
	 * @return the list of before-each runnable specifications
	 */
	public List<JnrTestRunnableSpecification> getBeforeEachRunnables() {
		return beforeEachRunnables;
	}

	/**
	 * Gets the list of after-all runnable specifications.
	 * 
	 * @return the list of after-all runnable specifications
	 */
	public List<JnrTestRunnableSpecification> getAfterAllRunnables() {
		return afterAllRunnables;
	}

	/**
	 * Gets the list of after-each runnable specifications.
	 * 
	 * @return the list of after-each runnable specifications
	 */
	public List<JnrTestRunnableSpecification> getAfterEachRunnables() {
		return afterEachRunnables;
	}
}
