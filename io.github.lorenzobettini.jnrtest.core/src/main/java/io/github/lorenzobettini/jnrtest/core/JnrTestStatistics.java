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

	public void reset() {
		succeeded = 0;
		failed = 0;
		errors = 0;
		totalTime = 0;
	}

	public void startTimer() {
		this.startTime = System.currentTimeMillis();
	}

	public void stopTimer() {
		this.elapsedTime = System.currentTimeMillis() - startTime;
		this.totalTime += elapsedTime;
	}

	public void incrementSucceeded() {
		succeeded++;
	}

	public void incrementFailed() {
		failed++;
	}

	public void incrementErrors() {
		errors++;
	}

	public int getSucceeded() {
		return succeeded;
	}

	public int getFailed() {
		return failed;
	}

	public int getErrors() {
		return errors;
	}

	public int getTotalTests() {
		return succeeded + failed + errors;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public boolean isWithElapsedTime() {
		return withElapsedTime;
	}

	public void setWithElapsedTime(boolean withElapsedTime) {
		this.withElapsedTime = withElapsedTime;
	}
}
