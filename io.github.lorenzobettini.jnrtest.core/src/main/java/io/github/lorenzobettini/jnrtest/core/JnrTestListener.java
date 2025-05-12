package io.github.lorenzobettini.jnrtest.core;

/**
 * Listener for test executions.
 * 
 * @author Lorenzo Bettini
 */
public interface JnrTestListener {

	void notify(JnrTestLifecycleEvent event);

	void notify(JnrTestRunnableLifecycleEvent event);

	void notify(JnrTestResult result);
}
