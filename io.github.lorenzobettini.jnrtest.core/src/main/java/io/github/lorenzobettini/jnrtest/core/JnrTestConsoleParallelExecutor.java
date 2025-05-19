package io.github.lorenzobettini.jnrtest.core;

/**
 * A high-level class that provides a simplified API for setting up and
 * executing test classes in a parallel environment.
 * It handles the creation of thread-safe recorders and reporters, execution of
 * tests, and reporting of results.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestConsoleParallelExecutor extends JnrTestConsoleExecutor {

	/**
	 * Overrides the default recorder with a thread-safe recorder.
	 */
	@Override
	protected JnrTestRecorderInterface createRecorder() {
		return new JnrTestThreadSafeRecorder();
	}

	/**
	 * Overrides the default reporter with a thread-safe reporter.
	 */
	@Override
	protected JnrTestReporterInterface createReporter() {
		return new JnrTestThreadSafeConsoleReporter();
	}

	/**
	 * Overrides the default test runner with a parallel test runner.
	 */
	@Override
	protected JnrTestRunner createTestRunner() {
		return new JnrTestParallelRunner();
	}

	/**
	 * Executes all test classes and prints the results; it also measures the
	 * total execution time.
	 *
	 * @return true if all tests passed, false otherwise
	 */
	public boolean executeWithoutThrowing() {
		var startTime = System.currentTimeMillis();
		var result = super.executeWithoutThrowing();
		var totalTime = System.currentTimeMillis() - startTime;
		System.out.println("\nTotal Execution Time: " + // NOSONAR
				(float) totalTime / 1000 + " s");
		return result;
	}

}