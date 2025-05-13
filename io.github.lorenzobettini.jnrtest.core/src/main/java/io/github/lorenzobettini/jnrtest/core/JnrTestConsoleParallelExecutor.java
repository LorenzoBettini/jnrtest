package io.github.lorenzobettini.jnrtest.core;

/**
 * A high-level class that provides a simplified API for setting up and
 * executing test classes
 * in a parallel environment.
 * It handles the creation of thread-safe recorders and reporters, execution of
 * tests, and reporting of results.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestConsoleParallelExecutor {
	private final JnrTestParallelRunner runner;
	private final JnrTestThreadSafeRecorder recorder;
	private final JnrTestThreadSafeConsoleReporter reporter;

	/**
	 * Creates a new JnrTestConsoleParallelExecutor with default thread-safe
	 * recorder and reporter configured with elapsed time.
	 */
	public JnrTestConsoleParallelExecutor() {
		this.recorder = new JnrTestThreadSafeRecorder().withElapsedTime();
		this.reporter = new JnrTestThreadSafeConsoleReporter().withElapsedTime();
		this.runner = new JnrTestParallelRunner();
		this.runner.testListener(recorder);
		this.runner.testListener(reporter);
	}

	/**
	 * Adds a test class to be executed.
	 *
	 * @param testClass the test class to add
	 * @return this instance for method chaining
	 */
	public JnrTestConsoleParallelExecutor add(JnrTest testClass) {
		runner.add(testClass);
		return this;
	}

	/**
	 * Adds a listener to the test execution.
	 *
	 * @param listener the listener to add
	 * @return this instance for method chaining
	 */
	public JnrTestConsoleParallelExecutor testListener(JnrTestListener listener) {
		runner.testListener(listener);
		return this;
	}

	/**
	 * Executes all test classes and prints the results.
	 *
	 * @return true if all tests passed, false otherwise
	 */
	public boolean executeWithoutThrowing() {
		var startTime = System.currentTimeMillis();
		runner.execute();
		var totalTime = System.currentTimeMillis() - startTime;
		System.out.println("\nResults:\n\n" + // NOSONAR
				new JnrTestResultAggregator().aggregate(recorder));
		System.out.println("\nTotal Execution Time: " + // NOSONAR
				(float) totalTime / 1000 + " s");
		return recorder.isSuccess();
	}

	/**
	 * Executes all test classes, prints the results, and throws an exception if any
	 * tests fail.
	 */
	public void execute() {
		if (!executeWithoutThrowing()) {
			throw new RuntimeException("There are test failures"); // NOSONAR
		}
	}
}