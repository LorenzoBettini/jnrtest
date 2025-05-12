package io.github.lorenzobettini.jnrtest.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Runs the tests of {@link JnrTest}; the actual test execution is
 * performed by {@link #execute()}.
 * 
 * @author Lorenzo Bettini
 *
 */
public class JnrTestRunner {

	private final List<JnrTest> testClasses = new ArrayList<>();
	private final List<JnrTestListener> listeners = new ArrayList<>();

	public JnrTestRunner add(JnrTest testClass) {
		testClasses.add(testClass);
		return this;
	}

	public JnrTestRunner testListener(JnrTestListener listener) {
		listeners.add(listener);
		return this;
	}

	public void execute() {
		notifyTestLifecycleEvent(new JnrTestLifecycleEvent("Starting...", JnrTestStatus.BEGIN));
		getTestClassesStream().forEach(this::executeTestClass);
		notifyTestLifecycleEvent(new JnrTestLifecycleEvent("Finished", JnrTestStatus.FINISH));
	}

	/**
	 * Returns a stream of test classes to be executed. Subclasses can override this
	 * method to customize the stream of test classes.
	 */
	protected Stream<JnrTest> getTestClassesStream() {
		return testClasses.stream();
	}

	private void executeTestClass(JnrTest testClass) {
		var description = testClass.getDescription();
		notifyTestLifecycleEvent(new JnrTestLifecycleEvent(description, JnrTestStatus.START));
		executeTestClass(testClass.getStore());
		notifyTestLifecycleEvent(new JnrTestLifecycleEvent(description, JnrTestStatus.END));
	}

	private void executeTestClass(JnrTestStore store) {
		executeBeforeAll(store);
		executeTestRunnables(store);
		executeAfterAll(store);
	}

	private void executeBeforeAll(JnrTestStore store) {
		executeLifecycleRunnables(store.getBeforeAllRunnables(), JnrTestRunnableKind.BEFORE_ALL);
	}

	private void executeAfterAll(JnrTestStore store) {
		executeLifecycleRunnables(store.getAfterAllRunnables(), JnrTestRunnableKind.AFTER_ALL);
	}

	private void executeBeforeEach(JnrTestStore store) {
		executeLifecycleRunnables(store.getBeforeEachRunnables(), JnrTestRunnableKind.BEFORE_EACH);
	}

	private void executeAfterEach(JnrTestStore store) {
		executeLifecycleRunnables(store.getAfterEachRunnables(), JnrTestRunnableKind.AFTER_EACH);
	}

	private void executeLifecycleRunnables(List<JnrTestRunnableSpecification> runnables, JnrTestRunnableKind kind) {
		for (var runnable : runnables) {
			executeSafely(runnable, kind, null);
		}
	}

	private void executeTestRunnables(JnrTestStore store) {
		for (var runnableSpecification : store.getRunnableSpecifications()) {
			executeBeforeEach(store);
			executeSafely(runnableSpecification, JnrTestRunnableKind.TEST,
					d -> notifyTestResult(new JnrTestResult(d, JnrTestResultStatus.SUCCESS, null)));
			executeAfterEach(store);
		}
	}

	private void executeSafely(JnrTestRunnableSpecification testRunnableSpecification,
			JnrTestRunnableKind kind,
			Consumer<String> successConsumer) {
		var description = testRunnableSpecification.description();
		var testRunnable = testRunnableSpecification.testRunnable();
		try {
			notifyTestRunnableLifecycleEvent(
					new JnrTestRunnableLifecycleEvent(description, kind, JnrTestRunnableStatus.START));
			testRunnable.runTest();
			if (successConsumer != null) {
				successConsumer.accept(description);
			}
		} catch (Exception e) {
			notifyTestResult(new JnrTestResult(description, JnrTestResultStatus.ERROR, e));
		} catch (AssertionError assertionError) {
			notifyTestResult(new JnrTestResult(description, JnrTestResultStatus.FAILED, assertionError));
		} finally {
			notifyTestRunnableLifecycleEvent(
					new JnrTestRunnableLifecycleEvent(description, kind, JnrTestRunnableStatus.END));
		}
	}

	private void notifyTestLifecycleEvent(JnrTestLifecycleEvent event) {
		listeners.forEach(l -> l.notify(event));
	}

	private void notifyTestResult(JnrTestResult result) {
		listeners.forEach(l -> l.notify(result));
	}

	private void notifyTestRunnableLifecycleEvent(JnrTestRunnableLifecycleEvent event) {
		listeners.forEach(l -> l.notify(event));
	}
}
