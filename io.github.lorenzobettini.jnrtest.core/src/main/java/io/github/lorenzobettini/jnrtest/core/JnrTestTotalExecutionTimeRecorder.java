package io.github.lorenzobettini.jnrtest.core;

/**
 * Records the time for executing all the tests of all test classes.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestTotalExecutionTimeRecorder extends JnrTestListenerAdapter {

	private long startTime;
	private long totalTime = 0;

	public long getTotalTime() {
		return totalTime;
	}

	@Override
	public void notify(JnrTestLifecycleEvent event) {
		if (event.status() == JnrTestStatus.BEGIN) {
			startTime = System.currentTimeMillis();
		} else if (event.status() == JnrTestStatus.FINISH) {
			totalTime += (System.currentTimeMillis() - startTime);
		}
	}
}
