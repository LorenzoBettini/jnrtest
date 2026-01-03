package io.github.lorenzobettini.jnrtest.tools;

import java.io.IOException;

/**
 * We use this main class to generate the JnrTest versions of our own JUnit test classes.
 */
public class GenerateJnrTestsFromOurJUnitTestClasses {

	private static final String CORE_SRC_TEST_JAVA = "../io.github.lorenzobettini.jnrtest.core/src/test/java";
	private static final String CORE_SRC_JNRTEST_JAVA = "../io.github.lorenzobettini.jnrtest.core/src/jnrtest/java";

	public static void main(String[] args) throws IOException {
		new JUnit5ToJnrTestGenerator().generate(
			CORE_SRC_TEST_JAVA,
			CORE_SRC_JNRTEST_JAVA
		);
		new JnrTestMainGenerator().generateMain(
			CORE_SRC_JNRTEST_JAVA,
			CORE_SRC_JNRTEST_JAVA,
			"io.github.lorenzobettini.jnrtest.core.JnrTestMainGenerated"
		);
	}
}
