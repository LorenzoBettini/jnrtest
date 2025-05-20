package io.github.lorenzobettini.jnrtest.core;

/**
 * The lifecycle event of a test runnable.
 * 
 * @author Lorenzo Bettini
 * @param description The description of the test runnable
 * @param kind The kind of test runnable (TEST, BEFORE_ALL, BEFORE_EACH, AFTER_EACH, or AFTER_ALL)
 * @param status The status of the test runnable lifecycle (START or END)
 */
public record JnrTestRunnableLifecycleEvent(String description, JnrTestRunnableKind kind, JnrTestRunnableStatus status) {

	@Override
	public String toString() {
		return String.format("[%7s] %s %s", status, kind, description);
	}

}
