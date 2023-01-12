package io.github.lorenzobettini.jnrtest.core;

public class JnrTestRunner {

	private JnrTestSpecification[] jnrTestSpecifications = {};

	public void withSpecifications(JnrTestSpecification... jnrTestSpecifications) {
		this.jnrTestSpecifications = jnrTestSpecifications;
	}

	public void execute() {
		for (JnrTestSpecification jnrTestSpecification : jnrTestSpecifications) {
			jnrTestSpecification.specify();
			var runnableSpecifications = jnrTestSpecification.getRunnableSpecifications();
			for (JnrTestRunnableSpecification jnrTestRunnableSpecification : runnableSpecifications) {
				try {
					jnrTestRunnableSpecification.testRunnable().runTest();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
