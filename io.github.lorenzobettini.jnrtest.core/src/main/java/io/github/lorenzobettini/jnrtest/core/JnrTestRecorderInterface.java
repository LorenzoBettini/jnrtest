package io.github.lorenzobettini.jnrtest.core;

import java.util.List;
import java.util.Map;

/**
 * Interface for recording test results.
 * 
 * Provides methods for managing test results and elapsed time.
 * 
 * @author Lorenzo Bettini
 */
public interface JnrTestRecorderInterface extends JnrTestListener {

	/**
	 * Enables elapsed time tracking for the recorder.
	 * 
	 * @return this recorder for method chaining
	 */
	default JnrTestRecorderInterface withElapsedTime() {
		return withElapsedTime(true);
	}

	/**
	 * Enables or disables elapsed time tracking for the recorder.
	 * 
	 * @param withElapsedTime true to enable elapsed time tracking, false to disable
	 * @return this recorder for method chaining
	 */
	JnrTestRecorderInterface withElapsedTime(boolean withElapsedTime);

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
