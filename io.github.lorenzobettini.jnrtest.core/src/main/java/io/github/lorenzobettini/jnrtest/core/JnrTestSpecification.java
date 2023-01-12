package io.github.lorenzobettini.jnrtest.core;

import java.util.ArrayList;
import java.util.List;

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
