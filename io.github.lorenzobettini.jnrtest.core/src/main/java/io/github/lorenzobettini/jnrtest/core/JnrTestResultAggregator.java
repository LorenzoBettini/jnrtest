package io.github.lorenzobettini.jnrtest.core;

/**
 * Aggregates results recorded by {@link JnrTestRecorderInterface}.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestResultAggregator {

	private int succeeded;
	private int failed;
	private int errors;
	private long totalTime;

	/**
	 * Aggregates the results from a test recorder into this aggregator.
	 * 
	 * @param testRecorder the recorder containing test results to aggregate
	 * @return this aggregator instance for method chaining
	 */
	public JnrTestResultAggregator aggregate(JnrTestRecorderInterface testRecorder) {
		testRecorder.getResults().values().stream()
			.flatMap(l -> l.stream())
			.forEach(result -> {
				switch (result.status()) {
				case FAILED: {
					failed++;
					break;
				}
				case ERROR: {
					errors++;
					break;
				}
				default: { // SUCCESS
					succeeded++;
					break;
				}
				}
			});
		this.totalTime = testRecorder.getTotalTime();
		return this;
	}

	/**
	 * Gets the number of successful tests.
	 * 
	 * @return the number of tests that succeeded
	 */
	public int getSucceeded() {
		return succeeded;
	}

	/**
	 * Gets the number of failed tests.
	 * 
	 * @return the number of tests that failed
	 */
	public int getFailed() {
		return failed;
	}

	/**
	 * Gets the number of tests with errors.
	 * 
	 * @return the number of tests that resulted in an error
	 */
	public int getErrors() {
		return errors;
	}

	/**
	 * Gets the total execution time for all tests.
	 * 
	 * @return the total time in milliseconds
	 */
	public long getTotalTime() {
		return totalTime;
	}

	@Override
	public String toString() {
		return String.format("Tests run: %d, Succeeded: %d, Failures: %d, Errors: %d",
				succeeded + failed + errors,
				succeeded, failed, errors) + (
					totalTime > 0 ? String.format(" - Time elapsed: %f s", (float) totalTime/1000) : "");
	}
}
