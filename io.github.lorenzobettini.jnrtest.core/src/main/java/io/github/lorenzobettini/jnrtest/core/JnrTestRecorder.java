package io.github.lorenzobettini.jnrtest.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Records test results for each test class.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestRecorder extends JnrTestListenerAdapter implements JnrTestRecorderInterface {

	private Map<String, List<JnrTestResult>> results = new LinkedHashMap<>();

	private String currentKey;

	private boolean success = true;

	private boolean withElapsedTime = false;
	private long startTime;
	private long totalTime = 0;

	@Override
	public JnrTestRecorderInterface withElapsedTime(boolean withElapsedTime) {
		this.withElapsedTime = withElapsedTime;
		return this;
	}

	@Override
	public long getTotalTime() {
		return totalTime;
	}

	@Override
	public void notify(JnrTestLifecycleEvent event) {
		if (event.status() != JnrTestStatus.START) {
			return;
		}
		currentKey = event.description();
		results.computeIfAbsent(currentKey,
			desc -> new ArrayList<>());
	}

	@Override
	public void notify(JnrTestRunnableLifecycleEvent event) {
		if (!withElapsedTime || event.kind() != JnrTestRunnableKind.TEST) {
			return;
		}
		if (event.status() == JnrTestRunnableStatus.START) {
			startTime = System.currentTimeMillis();
		} else {
			totalTime += (System.currentTimeMillis() - startTime);
		}
	}

	@Override
	public void notify(JnrTestResult result) {
		if (result.status() != JnrTestResultStatus.SUCCESS) {
			success = false;
		}
		results.get(currentKey).add(result);
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
