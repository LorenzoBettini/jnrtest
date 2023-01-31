package io.github.lorenzobettini.jnrtest.core;

/**
 * Listener for test executions.
 * 
 * @author Lorenzo Bettini
 *
 */
public interface JnrTestListener {

	void notify(JnrTestCaseResult result);

	void notify(JnrTestResult result);
}
