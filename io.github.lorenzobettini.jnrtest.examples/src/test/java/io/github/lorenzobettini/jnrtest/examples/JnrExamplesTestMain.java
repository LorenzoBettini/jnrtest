package io.github.lorenzobettini.jnrtest.examples;

import io.github.lorenzobettini.jnrtest.core.JnrTestConsoleExecutor;
import io.github.lorenzobettini.jnrtest.examples.extensions.JnrTestCaseGuiceExtension;
import io.github.lorenzobettini.jnrtest.examples.extensions.JnrTestCaseMockitoExtension;

public class JnrExamplesTestMain {

	public static void main(String[] args) {
		new JnrTestConsoleExecutor()
				.testCase(new FactorialJnrTestCase())
				.testCase(new FactorialJnrParameterizedTestCase())
				.testCase(new FactorialJnrParameterizedWithDescriptionTestCase())
				.testCase(new MyStringUtilsJnrTestCase())
				.testCase(new MyStringUtilsJnrParameterizedTestCase())
				.testCase(new JnrTestTemporaryFolderExampleTestCase())
				.testCase(new JnrTestTemporaryFolderExampleBeforeAllTestCase())
				.testCase(new JnrTestTemporaryFolderAnotherExampleTestCase())
				.testCase(new JnrTestCaseMockitoExtension()
					.extendEach(new StringServiceWithMockTestCase()))
				.testCase(new JnrTestCaseGuiceExtension(
						new StringRepositoryInMemoryGuiceModule())
					.extendAll(new StringServiceWithGuiceTestCase()))
				.execute();
	}
}
