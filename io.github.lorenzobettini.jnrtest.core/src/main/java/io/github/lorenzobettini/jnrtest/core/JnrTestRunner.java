package io.github.lorenzobettini.jnrtest.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Runs the tests of {@link JnrTestCase}; the actual test execution is
 * performed by {@link #execute()}.
 * 
 * @author Lorenzo Bettini
 *
 */
public class JnrTestRunner {

	private List<JnrTestCase> testCases = new ArrayList<>();

	private List<JnrTestListener> listeners = new ArrayList<>();

	public JnrTestRunner testCase(JnrTestCase testCase) {
		testCases.add(testCase);
		return this;
	}

	public JnrTestRunner testListener(JnrTestListener listener) {
		listeners.add(listener);
		return this;
	}

	public void execute() {
		for (var testCase : testCases) {
			var description = testCase.getDescription();
			notifyTestCaseResult(new JnrTestCaseLifecycleEvent(description, JnrTestCaseStatus.START));
			execute(testCase);
			notifyTestCaseResult(new JnrTestCaseLifecycleEvent(description, JnrTestCaseStatus.END));
		}
	}

	private void execute(JnrTestCase testCase) {
		var store = testCase.getStore();
		for (var beforeAll : store.getBeforeAllRunnables()) {
			executeSafely(beforeAll, JnrTestRunnableKind.BEFORE_ALL, null);
		}
		for (var runnableSpecification : store.getRunnableSpecifications()) {
			for (var beforeEach : store.getBeforeEachRunnables()) {
				executeSafely(beforeEach, JnrTestRunnableKind.BEFORE_EACH, null);
			}
			executeSafely(runnableSpecification,
				JnrTestRunnableKind.TEST,
				d -> notifyTestResult(new JnrTestResult(d, JnrTestResultStatus.SUCCESS, null)));
			for (var afterEach : store.getAfterEachRunnables()) {
				executeSafely(afterEach, JnrTestRunnableKind.AFTER_EACH, null);
			}
		}
		for (var afterAll : store.getAfterAllRunnables()) {
			executeSafely(afterAll, JnrTestRunnableKind.AFTER_ALL, null);
		}
	}

	private void executeSafely(JnrTestRunnableSpecification testRunnableSpecification,
			JnrTestRunnableKind kind,
			Consumer<String> successConsumer) {
		var description = testRunnableSpecification.description();
		var testRunnable = testRunnableSpecification.testRunnable();
		try {
			notifyTestCaseResult(new JnrTestRunnableLifecycleEvent(description, kind, JnrTestRunnableStatus.START));
			testRunnable.runTest();
			if (successConsumer != null)
				successConsumer.accept(description);
		} catch (Exception e) {
			notifyTestResult(new JnrTestResult(description, JnrTestResultStatus.ERROR, e));
		} catch (AssertionError assertionError) {
			notifyTestResult(new JnrTestResult(description, JnrTestResultStatus.FAILED, assertionError));
		} finally {
			notifyTestCaseResult(new JnrTestRunnableLifecycleEvent(description, kind, JnrTestRunnableStatus.END));
		}
	}

	private void notifyTestResult(JnrTestResult result) {
		listeners.forEach(l -> l.notify(result));
	}

	private void notifyTestCaseResult(JnrTestCaseLifecycleEvent event) {
		listeners.forEach(l -> l.notify(event));
	}

	private void notifyTestCaseResult(JnrTestRunnableLifecycleEvent event) {
		listeners.forEach(l -> l.notify(event));
	}

}
