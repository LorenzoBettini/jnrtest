package io.github.lorenzobettini.jnrtest.examples;

import io.github.lorenzobettini.jnrtest.core.JnrTestConsoleExecutor;
import io.github.lorenzobettini.jnrtest.examples.extensions.JnrTestGuiceExtension;
import io.github.lorenzobettini.jnrtest.examples.extensions.JnrTestMockitoExtension;

public class JnrExamplesTestMain {

	public static void main(String[] args) {
		new JnrTestConsoleExecutor()
				.add(new FactorialJnrTest())
				.add(new FactorialJnrParameterizedTest())
				.add(new FactorialJnrParameterizedWithDescriptionTest())
				.add(new MyStringUtilsJnrTest())
				.add(new MyStringUtilsJnrParameterizedTest())
				.add(new MathOperationsJnrTripleParameterizedTest())
				.add(new JnrTestTemporaryFolderExampleTest())
				.add(new JnrTestTemporaryFolderExampleBeforeAllTest())
				.add(new JnrTestTemporaryFolderAnotherExampleTest())
				.add(new JnrTestMockitoExtension()
					.extendEach(new StringServiceWithMockTest()))
				.add(new JnrTestGuiceExtension(
						new StringRepositoryInMemoryGuiceModule())
					.extendAll(new StringServiceWithGuiceTest()))
				.execute();
	}
}
