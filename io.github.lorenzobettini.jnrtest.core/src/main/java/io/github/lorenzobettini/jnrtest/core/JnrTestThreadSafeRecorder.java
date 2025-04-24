package io.github.lorenzobettini.jnrtest.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe implementation of JnrTestRecorder.
 * 
 * Records test results for each test case in a multi-threaded context.
 * 
 * @author Lorenzo Bettini
 * 
 */
public class JnrTestThreadSafeRecorder extends JnrTestListenerAdapter {

	private Map<String, List<JnrTestResult>> results = new ConcurrentHashMap<>();

	private ThreadLocal<String> currentKey = ThreadLocal.withInitial(() -> null);
	private ThreadLocal<Long> startTime = ThreadLocal.withInitial(() -> 0L);

	private boolean success = true;

	private boolean withElapsedTime = false;
	private long totalTime = 0;

	public JnrTestThreadSafeRecorder withElapsedTime() {
		withElapsedTime = true;
		return this;
	}

	public long getTotalTime() {
		return totalTime;
	}

	@Override
	public void notify(JnrTestCaseLifecycleEvent event) {
		if (event.status() == JnrTestCaseStatus.START && withElapsedTime) {
			startTime.set(System.currentTimeMillis());
		}
		if (event.status() != JnrTestCaseStatus.START) {
			if (withElapsedTime) {
				totalTime += (System.currentTimeMillis() - startTime.get());
			}
			return;
		}
		String key = event.description();
		currentKey.set(key);
		results.computeIfAbsent(key, desc -> new ArrayList<>());
	}

	@Override
	public void notify(JnrTestResult result) {
		if (result.status() != JnrTestResultStatus.SUCCESS) {
			success = false;
		}
		results.get(currentKey.get()).add(result);
	}

	public Map<String, List<JnrTestResult>> getResults() {
		return results;
	}

	public boolean isSuccess() {
		return success;
	}
}
