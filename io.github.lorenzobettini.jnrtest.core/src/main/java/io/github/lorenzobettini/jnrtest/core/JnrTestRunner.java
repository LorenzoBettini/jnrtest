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

	public void execute() {
		for (var runnableSpecification : runnableSpecifications) {
			try {
				runnableSpecification.testRunnable().runTest();
			} catch (Exception e) {
				// TODO report it
			}
		}
	}

}
