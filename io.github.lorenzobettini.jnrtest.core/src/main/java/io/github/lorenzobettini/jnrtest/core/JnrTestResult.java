package io.github.lorenzobettini.jnrtest.core;

/**
 * The result of a test.
 * 
 * @author Lorenzo Bettini
 */
public record JnrTestResult(String description, JnrTestResultStatus status, Throwable throwable) {

	@Override
	public String toString() {
		return String.format("[%7s] %s", status, description);
	}

}
