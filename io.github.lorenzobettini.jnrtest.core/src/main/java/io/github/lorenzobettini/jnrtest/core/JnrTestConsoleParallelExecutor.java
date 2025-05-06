package io.github.lorenzobettini.jnrtest.core;

/**
 * A high-level class that provides a simplified API for setting up and executing test cases
 * in a parallel environment.
 * It handles the creation of thread-safe recorders and reporters, execution of tests, and reporting of results.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestConsoleParallelExecutor {
    private final JnrTestParallelRunner runner;
    private final JnrTestThreadSafeRecorder recorder;
    private final JnrTestThreadSafeConsoleReporter reporter;

    /**
     * Creates a new JnrTestConsoleParallelExecutor with default thread-safe recorder and reporter configured with elapsed time.
     */
    public JnrTestConsoleParallelExecutor() {
        this.recorder = new JnrTestThreadSafeRecorder().withElapsedTime();
        this.reporter = new JnrTestThreadSafeConsoleReporter().withElapsedTime();
        this.runner = new JnrTestParallelRunner();
        this.runner.testListener(recorder);
        this.runner.testListener(reporter);
    }

    /**
     * Adds a test case to be executed.
     *
     * @param testCase the test case to add
     * @return this instance for method chaining
     */
    public JnrTestConsoleParallelExecutor testCase(JnrTestCase testCase) {
        runner.testCase(testCase);
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
     * Executes all test cases, prints the results, and throws an exception if any tests fail.
     */
    public void execute() {
        runner.execute();
        System.out.println("\nResults:\n\n" + new JnrTestResultAggregator().aggregate(recorder));
        if (!recorder.isSuccess()) {
            throw new RuntimeException("There are test failures");
        }
    }

    /**
     * Executes all test cases and prints the results.
     *
     * @return true if all tests passed, false otherwise
     */
    public boolean executeWithoutThrowing() {
        runner.execute();
        System.out.println("\nResults:\n\n" + new JnrTestResultAggregator().aggregate(recorder));
        return recorder.isSuccess();
    }

    /**
     * Gets the test recorder used by this executor.
     *
     * @return the test recorder
     */
    public JnrTestThreadSafeRecorder getRecorder() {
        return recorder;
    }

    /**
     * Gets the test runner used by this executor.
     *
     * @return the test runner
     */
    public JnrTestParallelRunner getRunner() {
        return runner;
    }
}