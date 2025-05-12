package io.github.lorenzobettini.jnrtest.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe implementation of JnrTestRecorder.
 * 
 * Records test results for each test case in a multi-threaded context.
 * 
 * @author Lorenzo Bettini
 * 
 */
public class JnrTestThreadSafeRecorder extends JnrTestListenerAdapter implements JnrTestRecorderInterface {

	private Map<String, List<JnrTestResult>> results = new ConcurrentHashMap<>();

	private ThreadLocal<String> currentKey = ThreadLocal.withInitial(() -> null);
	private ThreadLocal<Long> startTime = ThreadLocal.withInitial(() -> 0L);

	private boolean success = true;

	private boolean withElapsedTime = false;
	private AtomicLong totalTime = new AtomicLong(0);

	public JnrTestThreadSafeRecorder withElapsedTime() {
		withElapsedTime = true;
		return this;
	}

	@Override
	public long getTotalTime() {
		return totalTime.get();
	}

	@Override
	public void notify(JnrTestLifecycleEvent event) {
		if (event.status() == JnrTestStatus.START && withElapsedTime) {
			startTime.set(System.currentTimeMillis());
		}
		if (event.status() != JnrTestStatus.START) {
			if (withElapsedTime) {
				totalTime.addAndGet(System.currentTimeMillis() - startTime.get());
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

	@Override
	public Map<String, List<JnrTestResult>> getResults() {
		return results;
	}

	@Override
	public boolean isSuccess() {
		return success;
	}
}
