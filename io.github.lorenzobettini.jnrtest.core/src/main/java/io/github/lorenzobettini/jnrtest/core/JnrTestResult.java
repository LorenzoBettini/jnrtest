package io.github.lorenzobettini.jnrtest.core;

/**
 * The result of a test.
 * 
 * @author Lorenzo Bettini
 * @param description The description of the test
 * @param status The status of the test result (SUCCESS, FAILED, ERROR)
 * @param throwable The exception thrown during test execution, if any
 */
public record JnrTestResult(String description, JnrTestResultStatus status, Throwable throwable) {

	@Override
	public String toString() {
		return String.format("[%7s] %s", status, description);
	}

}
