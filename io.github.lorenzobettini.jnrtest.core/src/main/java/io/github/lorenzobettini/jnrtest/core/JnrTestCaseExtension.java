package io.github.lorenzobettini.jnrtest.core;

import java.util.List;

/**
 * Represents code to be executed before and after a {@link JnrTest}
 * lifecyle.
 * 
 * The implementation must implement the method {@link #extend(List, List)} that
 * receives the lists of runnables to be executed "before" and "after". Whether
 * they are the lists of runnables before and after all or each tests depends on
 * the public method invoked: {@link #extendAll(JnrTest)} or
 * {@link #extendEach(JnrTest)}, respectively.
 * 
 * @author Lorenzo Bettini
 *
 */
public abstract class JnrTestCaseExtension {

	public <T extends JnrTest> T extendAll(T testCase) {
		var store = testCase.getStore();
		extend(testCase, store.getBeforeAllRunnables(), store.getAfterAllRunnables());
		return testCase;
	}

	public <T extends JnrTest> T extendEach(T testCase) {
		var store = testCase.getStore();
		extend(testCase, store.getBeforeEachRunnables(), store.getAfterEachRunnables());
		return testCase;
	}

	protected abstract <T extends JnrTest> void extend(T testCase, List<JnrTestRunnableSpecification> before,
			List<JnrTestRunnableSpecification> after);

}
