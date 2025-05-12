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
public abstract class JnrTestExtension {

	public <T extends JnrTest> T extendAll(T testClass) {
		var store = testClass.getStore();
		extend(testClass, store.getBeforeAllRunnables(), store.getAfterAllRunnables());
		return testClass;
	}

	public <T extends JnrTest> T extendEach(T testClass) {
		var store = testClass.getStore();
		extend(testClass, store.getBeforeEachRunnables(), store.getAfterEachRunnables());
		return testClass;
	}

	protected abstract <T extends JnrTest> void extend(T testClass, List<JnrTestRunnableSpecification> before,
			List<JnrTestRunnableSpecification> after);

}
