package io.github.lorenzobettini.jnrtest.core;

/**
 * Tracks and reports test execution statistics.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestStatistics {

	private int succeeded;
	private int failed;
	private int errors;
	private long startTime;
	private long elapsedTime;
	private long totalTime = 0;
	private boolean withElapsedTime = false;

	/**
	 * Resets all the statistics counters.
	 */
	public void reset() {
		succeeded = 0;
		failed = 0;
		errors = 0;
		totalTime = 0;
	}

	/**
	 * Starts a timer for measuring test execution time.
	 */
	public void startTimer() {
		this.startTime = System.currentTimeMillis();
	}

	/**
	 * Stops the timer and adds the elapsed time to the total.
	 */
	public void stopTimer() {
		this.elapsedTime = System.currentTimeMillis() - startTime;
		this.totalTime += elapsedTime;
	}

	/**
	 * Increments the count of successful tests.
	 */
	public void incrementSucceeded() {
		succeeded++;
	}

	/**
	 * Increments the count of failed tests.
	 */
	public void incrementFailed() {
		failed++;
	}

	/**
	 * Increments the count of tests with errors.
	 */
	public void incrementErrors() {
		errors++;
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
	 * Gets the total number of tests executed.
	 * 
	 * @return the total number of tests (succeeded + failed + errors)
	 */
	public int getTotalTests() {
		return succeeded + failed + errors;
	}

	/**
	 * Gets the elapsed time for the most recent test.
	 * 
	 * @return the elapsed time in milliseconds
	 */
	public long getElapsedTime() {
		return elapsedTime;
	}

	/**
	 * Gets the total execution time for all tests.
	 * 
	 * @return the total time in milliseconds
	 */
	public long getTotalTime() {
		return totalTime;
	}

	/**
	 * Checks if elapsed time tracking is enabled.
	 * 
	 * @return true if elapsed time tracking is enabled, false otherwise
	 */
	public boolean isWithElapsedTime() {
		return withElapsedTime;
	}

	/**
	 * Sets whether elapsed time tracking is enabled.
	 * 
	 * @param withElapsedTime true to enable elapsed time tracking, false to disable it
	 */
	public void setWithElapsedTime(boolean withElapsedTime) {
		this.withElapsedTime = withElapsedTime;
	}
}
