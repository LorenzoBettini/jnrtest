package io.github.lorenzobettini.jnrtest.examples;

import io.github.lorenzobettini.jnrtest.core.JnrTestConsoleParallelExecutor;
import io.github.lorenzobettini.jnrtest.examples.extensions.JnrTestCaseGuiceExtension;
import io.github.lorenzobettini.jnrtest.examples.extensions.JnrTestCaseMockitoExtension;

public class JnrExamplesParallelTestMain {

	public static void main(String[] args) {
		new JnrTestConsoleParallelExecutor()
				.add(new FactorialJnrTestCase())
				.add(new FactorialJnrParameterizedTestCase())
				.add(new FactorialJnrParameterizedWithDescriptionTestCase())
				.add(new MyStringUtilsJnrTestCase())
				.add(new MyStringUtilsJnrParameterizedTestCase())
				.add(new JnrTestTemporaryFolderExampleTestCase())
				.add(new JnrTestTemporaryFolderExampleBeforeAllTestCase())
				.add(new JnrTestTemporaryFolderAnotherExampleTestCase())
				.add(new JnrTestCaseMockitoExtension()
					.extendEach(new StringServiceWithMockTestCase()))
				.add(new JnrTestCaseGuiceExtension(
						new StringRepositoryInMemoryGuiceModule())
					.extendAll(new StringServiceWithGuiceTestCase()))
				.execute();
	}
}
