package io.github.lorenzobettini.jnrtest.core;

/**
 * The lifecycle event of a test class.
 * 
 * @author Lorenzo Bettini
 *
 */
public record JnrTestLifecycleEvent(String description, JnrTestCaseStatus status) {

	@Override
	public String toString() {
		return String.format("[%7s] %s", status, description);
	}

}
