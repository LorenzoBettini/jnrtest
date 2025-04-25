package io.github.lorenzobettini.jnrtest.core;

import java.io.PrintStream;

/**
 * Standard reporter for JnrTest.
 * 
 * This class implements the JnrTestListener interface and provides a simple
 * console output for test results.
 * It defaults to using System.out for output, but can be configured to use any
 * {@link PrintStream}.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestStandardReporter implements JnrTestListener {

	private final PrintStream printStream;
	private JnrTestStatistics testStatistics = new JnrTestStatistics();

	public JnrTestStandardReporter() {
		this(System.out);
	}

	public JnrTestStandardReporter(PrintStream printStream) {
		this.printStream = printStream;
	}

	public JnrTestStandardReporter withElapsedTime() {
		testStatistics.setWithElapsedTime(true);
		return this;
	}

	private void reset() {
		testStatistics.reset();
	}

	@Override
	public void notify(JnrTestCaseLifecycleEvent event) {
		if (event.status() == JnrTestCaseStatus.START) {
			reset();
			show(event.toString());
		}
		if (event.status() == JnrTestCaseStatus.END) {
			show(String.format("Tests run: %d, Succeeded: %d, Failures: %d, Errors: %d",
					testStatistics.getTotalTests(),
					testStatistics.getSucceeded(),
					testStatistics.getFailed(),
					testStatistics.getErrors())
					+ (testStatistics.isWithElapsedTime() ? String.format(" - Time elapsed: %f s", (float) testStatistics.getTotalTime() / 3600) : ""));
		}
	}

	@Override
	public void notify(JnrTestRunnableLifecycleEvent event) {
		if (!testStatistics.isWithElapsedTime() || event.kind() != JnrTestRunnableKind.TEST) {
			return;
		}
		if (event.status() == JnrTestRunnableStatus.START) {
			testStatistics.startTimer();
		} else {
			testStatistics.stopTimer();
		}
	}

	@Override
	public void notify(JnrTestResult result) {
		switch (result.status()) {
			case SUCCESS: {
				testStatistics.incrementSucceeded();
				break;
			}
			case FAILED: {
				testStatistics.incrementFailed();
				result.throwable().printStackTrace();
				break;
			}
			case ERROR: {
				testStatistics.incrementErrors();
				result.throwable().printStackTrace();
			}
		}
		show(result.toString()
				+ (testStatistics.isWithElapsedTime() ? String.format(" - Time elapsed: %f s", (float) testStatistics.getElapsedTime() / 3600) : ""));
	}

	public void show(String message) {
		printStream.println(message);
	}

}
