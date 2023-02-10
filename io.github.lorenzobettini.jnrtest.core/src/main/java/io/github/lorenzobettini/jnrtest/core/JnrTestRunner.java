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
		notifyTestCaseResult(new JnrTestCaseLifecycleEvent(description, JnrTestCaseStatus.START));
		for (var beforeAll : store.getBeforeAllRunnables()) {
			executeSafely(beforeAll, null);
		}
		for (var runnableSpecification : store.getRunnableSpecifications()) {
			for (var extension : testExtensions) {
				extension.beforeTest(testCase);
			}
			for (var beforeEach : store.getBeforeEachRunnables()) {
				executeSafely(beforeEach, null);
			}
			executeSafely(runnableSpecification,
				d -> notifyTestResult(new JnrTestResult(d, JnrTestResultStatus.SUCCESS, null)));
			for (var afterEach : store.getAfterEachRunnables()) {
				executeSafely(afterEach, null);
			}
			for (var extension : testExtensions) {
				extension.afterTest(testCase);
			}
		}
		for (var afterAll : store.getAfterAllRunnables()) {
			executeSafely(afterAll, null);
		}
		notifyTestCaseResult(new JnrTestCaseLifecycleEvent(description, JnrTestCaseStatus.END));
	}

	private void executeSafely(JnrTestRunnableSpecification testRunnableSpecification,
			Consumer<String> successConsumer) {
		var description = testRunnableSpecification.description();
		var testRunnable = testRunnableSpecification.testRunnable();
		try {
			notifyTestCaseResult(new JnrTestRunnableLifecycleEvent(description, JnrTestRunnableStatus.START));
			testRunnable.runTest();
			if (successConsumer != null)
				successConsumer.accept(description);
		} catch (Exception e) {
			notifyTestResult(new JnrTestResult(description, JnrTestResultStatus.ERROR, e));
		} catch (AssertionError assertionError) {
			notifyTestResult(new JnrTestResult(description, JnrTestResultStatus.FAILED, assertionError));
		} finally {
			notifyTestCaseResult(new JnrTestRunnableLifecycleEvent(description, JnrTestRunnableStatus.END));
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
