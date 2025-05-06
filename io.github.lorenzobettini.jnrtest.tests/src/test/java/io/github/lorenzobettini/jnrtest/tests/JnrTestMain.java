package io.github.lorenzobettini.jnrtest.tests;

import io.github.lorenzobettini.jnrtest.core.JnrTestRecorder;
import io.github.lorenzobettini.jnrtest.core.JnrTestResultAggregator;
import io.github.lorenzobettini.jnrtest.core.JnrTestRunner;
import io.github.lorenzobettini.jnrtest.core.JnrTestConsoleReporter;

public class JnrTestMain {

	public static void main(String[] args) {
		var recorder = new JnrTestRecorder().withElapsedTime();
		var runner = new JnrTestRunner()
				.testCase(new JnrTestRunnerTestCase())
				.testCase(new JnrTestStandardReporterTestCase())
			.testListener(recorder)
			.testListener(new JnrTestConsoleReporter().withElapsedTime());
		runner.execute();
		System.out.println("\nResults:\n\n" + new JnrTestResultAggregator().aggregate(recorder));
		if (!recorder.isSuccess())
			throw new RuntimeException("There are test failures");
	}
}
