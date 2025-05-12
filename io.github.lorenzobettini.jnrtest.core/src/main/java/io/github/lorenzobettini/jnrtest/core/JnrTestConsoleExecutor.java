package io.github.lorenzobettini.jnrtest.core;

/**
 * A high-level class that provides a simplified API for setting up and executing test classes
 * in a sequential environment.
 * It handles the creation of recorders and reporters, execution of tests, and reporting of results.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestConsoleExecutor {
	private final JnrTestRunner runner;
	private final JnrTestRecorder recorder;
	private final JnrTestConsoleReporter reporter;

	/**
	 * Creates a new JnrTestConsoleExecutor with default recorder and reporter configured with elapsed time.
	 */
	public JnrTestConsoleExecutor() {
		this.recorder = new JnrTestRecorder().withElapsedTime();
		this.reporter = new JnrTestConsoleReporter().withElapsedTime();
		this.runner = new JnrTestRunner();
		this.runner.testListener(recorder);
		this.runner.testListener(reporter);
	}

	/**
	 * Adds a test class to be executed.
	 *
	 * @param testCase the test class to add
	 * @return this instance for method chaining
	 */
	public JnrTestConsoleExecutor testCase(JnrTest testCase) {
		runner.add(testCase);
		return this;
	}

	/**
	 * Adds a listener to the test execution.
	 *
	 * @param listener the listener to add
	 * @return this instance for method chaining
	 */
	public JnrTestConsoleExecutor testListener(JnrTestListener listener) {
		runner.testListener(listener);
		return this;
	}

	/**
	 * Executes all test classes and prints the results.
	 *
	 * @return true if all tests passed, false otherwise
	 */
	public boolean executeWithoutThrowing() {
		runner.execute();
		System.out.println("\nResults:\n\n" + new JnrTestResultAggregator().aggregate(recorder));
		return recorder.isSuccess();
	}

	/**
	 * Executes all test classes, prints the results, and throws an exception if any tests fail.
	 */
	public void execute() {
		if (!executeWithoutThrowing()) {
			throw new RuntimeException("There are test failures");
		}
	}
}