package io.github.lorenzobettini.jnrtest.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the tests represented by {@link JnrTestSpecification}.
 * 
 * @author Lorenzo Bettini
 *
 */
public class JnrTestStore {

	private List<JnrTestRunnableSpecification> runnableSpecifications = new ArrayList<>();

	private List<JnrTestRunnable> beforeAllRunnables = new ArrayList<>();
	private List<JnrTestRunnable> beforeEachRunnables = new ArrayList<>();
	private List<JnrTestRunnable> afterAllRunnables = new ArrayList<>();
	private List<JnrTestRunnable> afterEachRunnables = new ArrayList<>();

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
	 * @param beforeAllRunnable
	 */
	public void beforeAll(JnrTestRunnable beforeAllRunnable) {
		beforeAllRunnables.add(beforeAllRunnable);
	}

	/**
	 * Specifies a code to run before each test.
	 * 
	 * @param beforeEachRunnable
	 */
	public void beforeEach(JnrTestRunnable beforeEachRunnable) {
		beforeEachRunnables.add(beforeEachRunnable);
	}

	/**
	 * Specifies a code to run after all tests.
	 * 
	 * @param afterAllRunnable
	 */
	public void afterAll(JnrTestRunnable afterAllRunnable) {
		afterAllRunnables.add(afterAllRunnable);
	}

	/**
	 * Specifies a code to run after each test.
	 * 
	 * @param afterEachRunnable
	 */
	public void afterEach(JnrTestRunnable afterEachRunnable) {
		afterEachRunnables.add(afterEachRunnable);
	}

	public List<JnrTestRunnableSpecification> getRunnableSpecifications() {
		return runnableSpecifications;
	}

	public List<JnrTestRunnable> getBeforeAllRunnables() {
		return beforeAllRunnables;
	}

	public List<JnrTestRunnable> getBeforeEachRunnables() {
		return beforeEachRunnables;
	}

	public List<JnrTestRunnable> getAfterAllRunnables() {
		return afterAllRunnables;
	}

	public List<JnrTestRunnable> getAfterEachRunnables() {
		return afterEachRunnables;
	}
}
