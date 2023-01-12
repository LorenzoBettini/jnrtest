package io.github.lorenzobettini.jnrtest.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible of specifying the tests by redefining the abstract method
 * {@link #specify()} and calling the method
 * {@link #test(String, JnrTestRunnable)}.
 * 
 * Given an object of this class, first the method {@link #specify()} should be
 * called and then the specifications can be retrieved by calling
 * {@link #getRunnableSpecifications()}.
 * 
 * @author Lorenzo Bettini
 *
 */
public abstract class JnrTestSpecification {
	private List<JnrTestRunnableSpecification> runnableSpecifications = new ArrayList<>();

	protected abstract void specify();

	protected void test(String description, JnrTestRunnable testRunnable) {
		runnableSpecifications.add(new JnrTestRunnableSpecification(description, testRunnable));
	}

	public List<JnrTestRunnableSpecification> getRunnableSpecifications() {
		return runnableSpecifications;
	}
}
