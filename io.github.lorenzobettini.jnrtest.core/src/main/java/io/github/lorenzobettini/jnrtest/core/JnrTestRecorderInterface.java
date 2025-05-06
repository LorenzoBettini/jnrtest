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
public interface JnrTestRecorderInterface {

	long getTotalTime();

	Map<String, List<JnrTestResult>> getResults();

	boolean isSuccess();
}
