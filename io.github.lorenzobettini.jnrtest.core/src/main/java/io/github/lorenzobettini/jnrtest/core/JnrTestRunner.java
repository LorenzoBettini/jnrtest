package io.github.lorenzobettini.jnrtest.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs the tests of {@link JnrTestCase}; the actual test execution is
 * performed by {@link #execute()}.
 * 
 * @author Lorenzo Bettini
 *
 */
public class JnrTestRunner {

	private List<JnrTestCase> testCases = new ArrayList<>();

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
			execute(testCase);
		}
	}

	private void execute(JnrTestCase testCase) {
		var description = testCase.getDescription();
		var store = testCase.getStore();
		notifyListenersTestCaseResult(new JnrTestCaseResult(description, JnrTestCaseStatus.START));
		for (var beforeAll : store.getBeforeAllRunnables()) {
			executeSafely(beforeAll);
		}
		for (var runnableSpecification : store.getRunnableSpecifications()) {
			for (var extension : testExtensions) {
				extension.beforeTest(testCase);
			}
			for (var beforeEach : store.getBeforeEachRunnables()) {
				executeSafely(beforeEach);
			}
			executeSafely(runnableSpecification);
			for (var afterEach : store.getAfterEachRunnables()) {
				executeSafely(afterEach);
			}
			for (var extension : testExtensions) {
				extension.afterTest(testCase);
			}
		}
		for (var afterAll : store.getAfterAllRunnables()) {
			executeSafely(afterAll);
		}
		notifyListenersTestCaseResult(new JnrTestCaseResult(description, JnrTestCaseStatus.END));
	}

	private void executeSafely(JnrTestRunnableSpecification testRunnableSpecification) {
		var description = testRunnableSpecification.description();
		var testRunnable = testRunnableSpecification.testRunnable();
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
