package io.github.lorenzobettini.jnrtest.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Records test results for each test case.
 * 
 * @author Lorenzo Bettini
 *
 */
public class JnrTestRecorder extends JnrTestListenerAdapter {

	private Map<String, List<JnrTestResult>> results = new LinkedHashMap<>();

	private String current;

	private boolean success = true;

	@Override
	public void notify(JnrTestCaseLifecycleEvent event) {
		if (event.status() != JnrTestCaseStatus.START)
			return;
		current = event.description();
		results.computeIfAbsent(current,
			desc -> new ArrayList<>());
	}

	@Override
	public void notify(JnrTestResult result) {
		if (result.status() != JnrTestResultStatus.SUCCESS)
			success = false;
		results.get(current).add(result);
	}

	public Map<String, List<JnrTestResult>> getResults() {
		return results;
	}

	public boolean isSuccess() {
		return success;
	}
}
