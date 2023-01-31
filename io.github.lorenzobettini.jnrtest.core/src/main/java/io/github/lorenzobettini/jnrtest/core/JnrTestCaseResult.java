package io.github.lorenzobettini.jnrtest.core;

/**
 * The result of a test case.
 * 
 * @author Lorenzo Bettini
 *
 */
public record JnrTestCaseResult(String description, JnrTestCaseStatus status) {

	@Override
	public String toString() {
		return String.format("[%7s] %s", status, description);
	}

}
