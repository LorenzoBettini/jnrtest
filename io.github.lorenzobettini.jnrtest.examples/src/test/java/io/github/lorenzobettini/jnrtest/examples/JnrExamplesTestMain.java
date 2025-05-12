package io.github.lorenzobettini.jnrtest.examples;

import io.github.lorenzobettini.jnrtest.core.JnrTestConsoleExecutor;
import io.github.lorenzobettini.jnrtest.examples.extensions.JnrTestCaseGuiceExtension;
import io.github.lorenzobettini.jnrtest.examples.extensions.JnrTestCaseMockitoExtension;

public class JnrExamplesTestMain {

	public static void main(String[] args) {
		new JnrTestConsoleExecutor()
				.add(new FactorialJnrTest())
				.add(new FactorialJnrParameterizedTest())
				.add(new FactorialJnrParameterizedWithDescriptionTest())
				.add(new MyStringUtilsJnrTest())
				.add(new MyStringUtilsJnrParameterizedTest())
				.add(new JnrTestTemporaryFolderExampleTest())
				.add(new JnrTestTemporaryFolderExampleBeforeAllTest())
				.add(new JnrTestTemporaryFolderAnotherExampleTest())
				.add(new JnrTestCaseMockitoExtension()
					.extendEach(new StringServiceWithMockTest()))
				.add(new JnrTestCaseGuiceExtension(
						new StringRepositoryInMemoryGuiceModule())
					.extendAll(new StringServiceWithGuiceTest()))
				.execute();
	}
}
