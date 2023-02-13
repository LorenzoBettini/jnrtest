package io.github.lorenzobettini.jnrtest.core;

/**
 * The lifecycle event of a test case.
 * 
 * @author Lorenzo Bettini
 *
 */
public record JnrTestCaseLifecycleEvent(String description, JnrTestCaseStatus status) {

	@Override
	public String toString() {
		return String.format("[%7s] %s", status, description);
	}

}
