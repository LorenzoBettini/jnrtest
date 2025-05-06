package io.github.lorenzobettini.jnrtest.core;

/**
 * The lifecycle event of a test runnable.
 * 
 * @author Lorenzo Bettini
 */
public record JnrTestRunnableLifecycleEvent(String description, JnrTestRunnableKind kind, JnrTestRunnableStatus status) {

	@Override
	public String toString() {
		return String.format("[%7s] %s %s", status, kind, description);
	}

}
