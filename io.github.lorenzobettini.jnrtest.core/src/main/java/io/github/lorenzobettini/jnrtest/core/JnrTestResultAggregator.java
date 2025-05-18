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

	public JnrTestResultAggregator aggregate(JnrTestRecorderInterface<?> testRecorder) {
		testRecorder.getResults().values().stream()
			.flatMap(l -> l.stream())
			.forEach(result -> {
				switch (result.status()) {
				case SUCCESS: {
					succeeded++;
					break;
				}
				case FAILED: {
					failed++;
					break;
				}
				case ERROR: {
					errors++;
				}
				}
			});
		this.totalTime = testRecorder.getTotalTime();
		return this;
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
