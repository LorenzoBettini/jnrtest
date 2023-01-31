package io.github.lorenzobettini.jnrtest.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Runs the tests of {@link JnrTestCase}; the actual test execution is
 * performed by {@link #execute()}.
 * 
 * @author Lorenzo Bettini
 *
 */
public class JnrTestRunner {

	private List<JnrTestCase> testCases = new ArrayList<>();

	private Set<JnrTestCase> alreadySpecified = new HashSet<>();

	private List<JnrTestExtension> testExtensions = new ArrayList<>();

	private List<JnrTestResultListener> listeners = new ArrayList<>();

	public JnrTestRunner testCase(JnrTestCase testCase) {
		testCases.add(testCase);
		return this;
	}

	public JnrTestRunner extendWith(JnrTestExtension testDecorator) {
		testExtensions.add(testDecorator);
		return this;
	}

	public JnrTestRunner testListener(JnrTestResultListener listener) {
		listeners.add(listener);
		return this;
	}

	public void execute() {
		for (var testCase : testCases) {
			if (!alreadySpecified.contains(testCase)) {
				testCase.specify();
				alreadySpecified.add(testCase);
			}
			execute(testCase);
		}
	}

	private void execute(JnrTestCase testCase) {
		var description = testCase.getDescription();
		for (var beforeAll : testCase.getBeforeAllRunnables()) {
			executeSafely("before all " + description, beforeAll);
		}
		for (var runnableSpecification : testCase.getRunnableSpecifications()) {
			for (var extension : testExtensions) {
				extension.beforeTest(testCase);
			}
			for (var beforeEach : testCase.getBeforeEachRunnables()) {
				executeSafely("before each " + description, beforeEach);
			}
			executeSafely(runnableSpecification.description(), runnableSpecification.testRunnable());
			for (var afterEach : testCase.getAfterEachRunnables()) {
				executeSafely("after each " + description, afterEach);
			}
			for (var extension : testExtensions) {
				extension.afterTest(testCase);
			}
		}
		for (var afterAll : testCase.getAfterAllRunnables()) {
			executeSafely("after all " + description, afterAll);
		}
	}

	private void executeSafely(String description, JnrTestRunnable testRunnable) {
		try {
			testRunnable.runTest();
			notifyListeners(
				new JnrTestResult(description, JnrTestResultStatus.SUCCESS, null));
		} catch (Exception e) {
			notifyListeners(
				new JnrTestResult(description, JnrTestResultStatus.ERROR, e));
		} catch (AssertionError assertionError) {
			notifyListeners(
				new JnrTestResult(description, JnrTestResultStatus.FAILED, assertionError));
		}
	}

	private void notifyListeners(JnrTestResult result) {
		listeners.forEach(l -> l.testResult(result));
	}

}
