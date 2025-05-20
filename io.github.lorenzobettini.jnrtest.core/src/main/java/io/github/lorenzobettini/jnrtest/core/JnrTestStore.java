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
	 * Specify a test to run (in the shape of a {@link JnrTestRunnable}, with the
	 * given description.
	 * 
	 * @param description
	 * @param testRunnable
	 */
	public void test(String description, JnrTestRunnable testRunnable) {
		runnableSpecifications.add(new JnrTestRunnableSpecification(description, testRunnable));
	}

	/**
	 * Specifies a code to run before all tests.
	 * 
	 * @param description
	 * @param beforeAllRunnable
	 */
	public void beforeAll(String description, JnrTestRunnable beforeAllRunnable) {
		beforeAllRunnables.add(new JnrTestRunnableSpecification(description, beforeAllRunnable));
	}

	/**
	 * Specifies a code to run before each test.
	 * 
	 * @param description
	 * @param beforeEachRunnable
	 */
	public void beforeEach(String description, JnrTestRunnable beforeEachRunnable) {
		beforeEachRunnables.add(new JnrTestRunnableSpecification(description, beforeEachRunnable));
	}

	/**
	 * Specifies a code to run after all tests.
	 * 
	 * @param description
	 * @param afterAllRunnable
	 */
	public void afterAll(String description, JnrTestRunnable afterAllRunnable) {
		afterAllRunnables.add(new JnrTestRunnableSpecification(description, afterAllRunnable));
	}

	/**
	 * Specifies a code to run after each test.
	 * 
	 * @param description
	 * @param afterEachRunnable
	 */
	public void afterEach(String description, JnrTestRunnable afterEachRunnable) {
		afterEachRunnables.add(new JnrTestRunnableSpecification(description, afterEachRunnable));
	}

	public List<JnrTestRunnableSpecification> getRunnableSpecifications() {
		return runnableSpecifications;
	}

	public List<JnrTestRunnableSpecification> getBeforeAllRunnables() {
		return beforeAllRunnables;
	}

	public List<JnrTestRunnableSpecification> getBeforeEachRunnables() {
		return beforeEachRunnables;
	}

	public List<JnrTestRunnableSpecification> getAfterAllRunnables() {
		return afterAllRunnables;
	}

	public List<JnrTestRunnableSpecification> getAfterEachRunnables() {
		return afterEachRunnables;
	}
}
