package io.github.lorenzobettini.jnrtest.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs the tests represented by {@link JnrTestSpecification}, specified by
 * overriding {@link #withSpecifications()}; the actual test execution is
 * performed by {@link #execute()}.
 * 
 * @author Lorenzo Bettini
 *
 */
public abstract class JnrTestRunner {

	private List<JnrTestRunnableSpecification> runnableSpecifications = new ArrayList<>();

	private List<JnrTestRunnable> beforeAllRunnables = new ArrayList<>();
	private List<JnrTestRunnable> beforeEachRunnables = new ArrayList<>();
	private List<JnrTestRunnable> afterAllRunnables = new ArrayList<>();
	private List<JnrTestRunnable> afterEachRunnables = new ArrayList<>();

	protected JnrTestRunner() {
		withSpecifications();
	}

	/**
	 * Responsible of specifying the tests by calling the method
	 * {@link #test(String, JnrTestRunnable)}.
	 */
	protected abstract void withSpecifications();

	/**
	 * Specify a test to run (in the shape of a {@link JnrTestRunnable}, with the
	 * given description.
	 * 
	 * @param description
	 * @param testRunnable
	 */
	protected void test(String description, JnrTestRunnable testRunnable) {
		runnableSpecifications.add(new JnrTestRunnableSpecification(description, testRunnable));
	}

	/**
	 * Specifies a code to run before all tests.
	 * 
	 * @param beforeAllRunnable
	 */
	protected void beforeAll(JnrTestRunnable beforeAllRunnable) {
		beforeAllRunnables.add(beforeAllRunnable);
	}

	/**
	 * Specifies a code to run before each test.
	 * 
	 * @param beforeEachRunnable
	 */
	protected void beforeEach(JnrTestRunnable beforeEachRunnable) {
		beforeEachRunnables.add(beforeEachRunnable);
	}

	/**
	 * Specifies a code to run after all tests.
	 * 
	 * @param afterAllRunnable
	 */
	protected void afterAll(JnrTestRunnable afterAllRunnable) {
		afterAllRunnables.add(afterAllRunnable);
	}

	/**
	 * Specifies a code to run after each test.
	 * 
	 * @param afterEachRunnable
	 */
	protected void afterEach(JnrTestRunnable afterEachRunnable) {
		afterEachRunnables.add(afterEachRunnable);
	}

	public void execute() {
		for (var beforeAll : beforeAllRunnables) {
			executeSafely(beforeAll);
		}
		for (var runnableSpecification : runnableSpecifications) {
			for (var beforeEach : beforeEachRunnables) {
				executeSafely(beforeEach);
			}
			var testRunnable = runnableSpecification.testRunnable();
			executeSafely(testRunnable);
			for (var afterEach : afterEachRunnables) {
				executeSafely(afterEach);
			}
		}
		for (var afterAll : afterAllRunnables) {
			executeSafely(afterAll);
		}
	}

	private void executeSafely(JnrTestRunnable testRunnable) {
		try {
			testRunnable.runTest();
		} catch (Exception e) {
			// TODO report it
		} catch (AssertionError error) {
			// TODO report it
		}
	}

}
