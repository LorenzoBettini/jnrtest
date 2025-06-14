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
public class JnrTestConsoleReporter implements JnrTestReporterInterface {

	private final PrintStream printStream;
	private boolean onlySummaries = false;
	private JnrTestStatistics testStatistics = new JnrTestStatistics();

	/**
	 * Creates a new console reporter with the default output stream (System.out).
	 */
	public JnrTestConsoleReporter() {
		this(System.out); // NOSONAR
	}

	/**
	 * Creates a new console reporter with the specified output stream.
	 * 
	 * @param printStream the stream to use for output
	 */
	public JnrTestConsoleReporter(PrintStream printStream) {
		this.printStream = printStream;
	}

	@Override
	public JnrTestConsoleReporter withOnlySummaries(boolean onlySummaries) {
		this.onlySummaries = onlySummaries;
		return this;
	}

	@Override
	public JnrTestConsoleReporter withElapsedTime(boolean withElapsedTime) {
		testStatistics.setWithElapsedTime(withElapsedTime);
		return this;
	}

	/**
	 * Resets the test statistics.
	 */
	private void reset() {
		testStatistics.reset();
	}

	/**
	 * Handles test lifecycle events.
	 * 
	 * @param event the test lifecycle event
	 */
	@Override
	public void notify(JnrTestLifecycleEvent event) {
		if (event.status() == JnrTestStatus.START) {
			reset();
			show(event.toString());
		}
		if (event.status() == JnrTestStatus.END) {
			show(String.format("Tests run: %d, Succeeded: %d, Failures: %d, Errors: %d",
				testStatistics.getTotalTests(),
				testStatistics.getSucceeded(),
				testStatistics.getFailed(),
				testStatistics.getErrors())
				+ (testStatistics.isWithElapsedTime() ?
					String.format(" - Time elapsed: %f s",
						(float) testStatistics.getTotalTime() / 1000) :
					""));
		}
	}

	/**
	 * Handles test runnable lifecycle events, specifically to track elapsed time.
	 * 
	 * @param event the test runnable lifecycle event
	 */
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

	/**
	 * Handles test result events and updates statistics accordingly.
	 * 
	 * @param result the test result
	 */
	@Override
	public void notify(JnrTestResult result) {
		switch (result.status()) {
			case FAILED: {
				testStatistics.incrementFailed();
				result.throwable().printStackTrace();
				break;
			}
			case ERROR: {
				testStatistics.incrementErrors();
				result.throwable().printStackTrace();
				break;
			}
			default: { // SUCCESS
				testStatistics.incrementSucceeded();
				break;
			}
		}
		if (!onlySummaries) {
			show(result.toString()
				+ (testStatistics.isWithElapsedTime() ?
						String.format(" - Time elapsed: %f s", (float) testStatistics.getElapsedTime() / 1000) :
						""));
		}
	}

	/**
	 * Displays a message to the configured output stream.
	 * 
	 * @param message the message to display
	 */
	private void show(String message) {
		printStream.println(message);
	}

}
