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
	private final JnrTestRecorderInterface recorder;
	private final JnrTestReporterInterface reporter;

	/**
	 * Creates a new JnrTestConsoleExecutor with default recorder and reporter configured with elapsed time.
	 */
	public JnrTestConsoleExecutor() {
		this.recorder = createRecorder().withElapsedTime();
		this.reporter = createReporter().withElapsedTime();
		this.runner = createTestRunner();
		this.runner.testListener(recorder);
		this.runner.testListener(reporter);
	}

	/**
	 * Factory method to create a recorder.
	 * @return a new instance of JnrTestRecorderInterface
	 */
	protected JnrTestRecorderInterface createRecorder() {
		return new JnrTestRecorder();
	}

	/**
	 * Factory method to create a reporter.
	 * @return a new instance of JnrTestReporterInterface
	 */
	protected JnrTestReporterInterface createReporter() {
		return new JnrTestConsoleReporter();
	}

	/**
	 * Factory method to create a test runner.
	 * @return a new instance of JnrTestRunner
	 */
	protected JnrTestRunner createTestRunner() {
		return new JnrTestRunner();
	}

	/**
	 * Adds a test class to be executed.
	 *
	 * @param testClass the test class to add
	 * @return this instance for method chaining
	 */
	public JnrTestConsoleExecutor add(JnrTest testClass) {
		runner.add(testClass);
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
	 * Sets a class filter for the test execution.
	 *
	 * @param filter the filter to apply
	 * @return this instance for method chaining
	 */
	public JnrTestConsoleExecutor classFilter(JnrTestClassFilter filter) {
		runner.classFilter(filter);
		return this;
	}
	
	/**
	 * Sets a specification filter for the test execution.
	 *
	 * @param filter the filter to apply
	 * @return this instance for method chaining
	 */
	public JnrTestConsoleExecutor specificationFilter(JnrTestSpecificationFilter filter) {
		runner.specificationFilter(filter);
		return this;
	}
	
	/**
	 * Sets a filter that only includes tests whose test class description matches the given pattern.
	 * 
	 * @param pattern the regex pattern to match against test class descriptions
	 * @return this instance for method chaining
	 */
	public JnrTestConsoleExecutor filterByClassDescription(String pattern) {
		runner.filterByClassDescription(pattern);
		return this;
	}
	
	/**
	 * Sets a filter that only includes tests whose test specification description matches the given pattern.
	 * 
	 * @param pattern the regex pattern to match against test specification descriptions
	 * @return this instance for method chaining
	 */
	public JnrTestConsoleExecutor filterBySpecificationDescription(String pattern) {
		runner.filterBySpecificationDescription(pattern);
		return this;
	}

	public JnrTestRecorderInterface getRecorder() {
		return recorder;
	}

	public JnrTestReporterInterface getReporter() {
		return reporter;
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
	 * Executes all test classes, prints the results, and throws an exception if any tests fail.
	 */
	public void execute() {
		if (!executeWithoutThrowing()) {
			throw new RuntimeException("There are test failures"); // NOSONAR
		}
	}
}