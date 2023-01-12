package io.github.lorenzobettini.jnrtest.core;

/**
 * Runs the tests represented by {@link JnrTestSpecification}, specified by
 * calling {@link #withSpecifications(JnrTestSpecification...)}; once
 * initialized, the actual test execution is performed by {@link #execute()}.
 * 
 * @author Lorenzo Bettini
 *
 */
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
