package io.github.lorenzobettini.jnrtest.core;

import java.util.List;
import java.util.Map;

/**
 * Interface for recording test results.
 * 
 * Provides methods for managing test results and elapsed time.
 * 
 * @author Lorenzo Bettini
 * @param <T> the concrete type of the recorder for method chaining
 */
public interface JnrTestRecorderInterface<T extends JnrTestRecorderInterface<T>> extends JnrTestListener {

	/**
	 * Enables elapsed time tracking for the recorder.
	 * 
	 * @return this recorder for method chaining
	 */
	T withElapsedTime();

	/**
	 * Gets the total elapsed time for all executed tests.
	 * 
	 * @return the total elapsed time in milliseconds
	 */
	long getTotalTime();

	/**
	 * Gets the recorded test results organized by test class description.
	 * 
	 * @return a map where keys are test class descriptions and values are lists of test results
	 */
	Map<String, List<JnrTestResult>> getResults();

	/**
	 * Checks if all the executed tests have succeeded.
	 * 
	 * @return true if all tests succeeded, false if any test failed or had errors
	 */
	boolean isSuccess();
}
