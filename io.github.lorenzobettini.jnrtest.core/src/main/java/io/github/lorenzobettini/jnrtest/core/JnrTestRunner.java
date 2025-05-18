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
	private JnrTestClassFilter classFilter = null;
	private JnrTestSpecificationFilter specificationFilter = null;

	public JnrTestRunner add(JnrTest testClass) {
		testClasses.add(testClass);
		return this;
	}

	public JnrTestRunner testListener(JnrTestListener listener) {
		listeners.add(listener);
		return this;
	}

	public JnrTestRunner classFilter(JnrTestClassFilter filter) {
		this.classFilter = filter;
		return this;
	}
	
	public JnrTestRunner specificationFilter(JnrTestSpecificationFilter filter) {
		this.specificationFilter = filter;
		return this;
	}

	/**
	 * Set a filter that only includes test classes whose description matches the given pattern.
	 * 
	 * @param pattern the regex pattern to match against test class descriptions
	 * @return this runner for method chaining
	 */
	public JnrTestRunner filterByClassDescription(String pattern) {
		return classFilter(JnrTestFilters.byClassDescription(pattern));
	}
	
	/**
	 * Set a filter that only includes test specifications whose description matches the given pattern.
	 * 
	 * @param pattern the regex pattern to match against test specification descriptions
	 * @return this runner for method chaining
	 */
	public JnrTestRunner filterBySpecificationDescription(String pattern) {
		return specificationFilter(JnrTestFilters.bySpecificationDescription(pattern));
	}

	public void execute() {
		getTestClassesStream().forEach(this::executeTestClass);
	}

	/**
	 * Returns a stream of test classes to be executed. Subclasses can override this
	 * method to customize the stream of test classes.
	 */
	protected Stream<JnrTest> getTestClassesStream() {
		if (classFilter == null) {
			// No filtering needed
			return testClasses.stream();
		}
		// Apply the class filter
		return testClasses.stream()
				.filter(testClass -> classFilter.include(testClass));
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
		List<JnrTestRunnableSpecification> runnablesToExecute;
		
		if (specificationFilter == null) {
			// No specification filtering needed, execute all test runnables
			runnablesToExecute = store.getRunnableSpecifications();
		} else {
			// Apply specification filter
			runnablesToExecute = store.getRunnableSpecifications().stream()
					.filter(specificationFilter::include)
					.toList();
		}
		
		// Execute the filtered (or all) specifications
		for (var runnableSpecification : runnablesToExecute) {
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
			executeSafely(testRunnable, kind, description);
			if (successConsumer != null) {
				successConsumer.accept(description);
			}
		} catch (Exception e) {
			notifyTestResult(new JnrTestResult(description, JnrTestResultStatus.ERROR, e));
		} catch (AssertionError assertionError) {
			notifyTestResult(new JnrTestResult(description, JnrTestResultStatus.FAILED, assertionError));
		}
	}

	private void executeSafely(JnrTestRunnable testRunnable, JnrTestRunnableKind kind, String description) throws Exception {
		try {
			notifyTestRunnableLifecycleEvent(
					new JnrTestRunnableLifecycleEvent(description, kind, JnrTestRunnableStatus.START));
			testRunnable.runTest();
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
