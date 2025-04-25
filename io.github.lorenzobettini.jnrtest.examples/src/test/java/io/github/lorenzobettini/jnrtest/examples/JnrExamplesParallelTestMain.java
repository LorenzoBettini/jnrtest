package io.github.lorenzobettini.jnrtest.examples;

import io.github.lorenzobettini.jnrtest.core.JnrTestParallelRunner;
import io.github.lorenzobettini.jnrtest.core.JnrTestResultAggregator;
import io.github.lorenzobettini.jnrtest.core.JnrTestThreadSafeRecorder;
import io.github.lorenzobettini.jnrtest.core.JnrTestThreadSafeStandardReporter;
import io.github.lorenzobettini.jnrtest.examples.extensions.JnrTestCaseGuiceExtension;
import io.github.lorenzobettini.jnrtest.examples.extensions.JnrTestCaseMockitoExtension;

public class JnrExamplesParallelTestMain {

	public static void main(String[] args) {
		var recorder = new JnrTestThreadSafeRecorder().withElapsedTime();
		var runner = new JnrTestParallelRunner()
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
			.testListener(recorder)
			.testListener(new JnrTestThreadSafeStandardReporter().withElapsedTime());
		runner.execute();
		System.out.println("\nResults:\n\n" + new JnrTestResultAggregator().aggregate(recorder));
		if (!recorder.isSuccess())
			throw new RuntimeException("There are test failures");
	}
}
