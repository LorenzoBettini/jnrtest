package io.github.lorenzobettini.jnrtest.examples;

import io.github.lorenzobettini.jnrtest.core.JnrTestRecorder;
import io.github.lorenzobettini.jnrtest.core.JnrTestResultAggregator;
import io.github.lorenzobettini.jnrtest.core.JnrTestRunner;
import io.github.lorenzobettini.jnrtest.core.JnrTestStandardReporter;

public class JnrExamplesTestMain {

	public static void main(String[] args) {
		var recorder = new JnrTestRecorder().withElapsedTime();
		var runner = new JnrTestRunner()
				.testCase(new FactorialJnrTestCase())
				.testCase(new FactorialJnrParameterizedTestCase())
				.testCase(new MyStringUtilsJnrTestCase())
				.testCase(new MyStringUtilsJnrParameterizedTestCase())
				.testCase(new JnrTestTemporaryFolderExampleTestCase())
				.testCase(new JnrTestTemporaryFolderExampleBeforeAllTestCase())
				.testCase(new JnrTestTemporaryFolderAnotherExampleTestCase())
				.testListener(recorder)
				.testListener(new JnrTestStandardReporter().withElapsedTime());
		runner.execute();
		System.out.println("\nResults:\n\n" + new JnrTestResultAggregator().aggregate(recorder));
		if (!recorder.isSuccess())
			throw new RuntimeException("There are test failures");
	}
}
