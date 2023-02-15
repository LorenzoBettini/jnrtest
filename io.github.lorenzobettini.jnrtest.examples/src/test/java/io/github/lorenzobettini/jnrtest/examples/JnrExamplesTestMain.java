package io.github.lorenzobettini.jnrtest.examples;

import io.github.lorenzobettini.jnrtest.core.JnrTestRecorder;
import io.github.lorenzobettini.jnrtest.core.JnrTestRunner;
import io.github.lorenzobettini.jnrtest.core.JnrTestStandardReporter;

public class JnrExamplesTestMain {

	public static void main(String[] args) {
		var recorder = new JnrTestRecorder();
		var runner = new JnrTestRunner()
				.testCase(new FactorialJnrTestCase())
				.testCase(new FactorialJnrParameterizedTestCase())
				.testListener(recorder)
				.testListener(new JnrTestStandardReporter());
		runner.execute();
		if (!recorder.isSuccess())
			throw new RuntimeException("There are test failures");
	}
}
