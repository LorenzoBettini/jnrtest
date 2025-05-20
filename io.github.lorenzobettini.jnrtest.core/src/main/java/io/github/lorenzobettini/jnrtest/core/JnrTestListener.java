package io.github.lorenzobettini.jnrtest.core;

/**
 * Listener for test executions.
 * 
 * @author Lorenzo Bettini
 */
public interface JnrTestListener {

	/**
	 * Notifies the listener of a test lifecycle event.
	 * 
	 * @param event The test lifecycle event
	 */
	void notify(JnrTestLifecycleEvent event);

	/**
	 * Notifies the listener of a test runnable lifecycle event.
	 * 
	 * @param event The test runnable lifecycle event
	 */
	void notify(JnrTestRunnableLifecycleEvent event);

	/**
	 * Notifies the listener of a test result.
	 * 
	 * @param result The test result
	 */
	void notify(JnrTestResult result);
}
