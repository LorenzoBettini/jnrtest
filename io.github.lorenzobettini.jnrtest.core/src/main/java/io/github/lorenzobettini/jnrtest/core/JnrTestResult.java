package io.github.lorenzobettini.jnrtest.core;

/**
 * The result of a test.
 * 
 * @author Lorenzo Bettini
 *
 */
public record JnrTestResult(String description, JnrTestResultStatus status) {

	@Override
	public String toString() {
		return String.format("[%s] %s", status, description);
	}

}
