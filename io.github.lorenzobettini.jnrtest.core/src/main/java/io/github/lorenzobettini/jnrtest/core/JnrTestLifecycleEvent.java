package io.github.lorenzobettini.jnrtest.core;

/**
 * The lifecycle event of a test class.
 * 
 * @author Lorenzo Bettini
 * @param description The description of the test class
 * @param status The status of the test lifecycle (START or END)
 */
public record JnrTestLifecycleEvent(String description, JnrTestStatus status) {

	@Override
	public String toString() {
		return String.format("[%7s] %s", status, description);
	}

}
