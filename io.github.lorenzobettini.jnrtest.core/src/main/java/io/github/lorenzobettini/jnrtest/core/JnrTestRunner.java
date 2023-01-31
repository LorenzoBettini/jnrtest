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

	private List<JnrTestListener> listeners = new ArrayList<>();

	public JnrTestRunner testCase(JnrTestCase testCase) {
		testCases.add(testCase);
		return this;
	}

	public JnrTestRunner extendWith(JnrTestExtension testDecorator) {
		testExtensions.add(testDecorator);
		return this;
	}

	public JnrTestRunner testListener(JnrTestListener listener) {
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
		notifyListenersTestCaseResult(new JnrTestCaseResult(description, JnrTestCaseStatus.START));
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
		notifyListenersTestCaseResult(new JnrTestCaseResult(description, JnrTestCaseStatus.END));
	}

	private void executeSafely(String description, JnrTestRunnable testRunnable) {
		try {
			testRunnable.runTest();
			notifyListenersTestResult(
				new JnrTestResult(description, JnrTestResultStatus.SUCCESS, null));
		} catch (Exception e) {
			notifyListenersTestResult(
				new JnrTestResult(description, JnrTestResultStatus.ERROR, e));
		} catch (AssertionError assertionError) {
			notifyListenersTestResult(
				new JnrTestResult(description, JnrTestResultStatus.FAILED, assertionError));
		}
	}

	private void notifyListenersTestResult(JnrTestResult result) {
		listeners.forEach(l -> l.notify(result));
	}

	private void notifyListenersTestCaseResult(JnrTestCaseResult result) {
		listeners.forEach(l -> l.notify(result));
	}

}
