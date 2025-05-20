package io.github.lorenzobettini.jnrtest.core;

/**
 * The status of an executed test.
 * 
 * @author Lorenzo Bettini
 *
 */
public enum JnrTestResultStatus {

	/**
	 * Test executed successfully without errors
	 */
	SUCCESS, 
	
	/**
	 * Test failed due to an assertion error
	 */
	FAILED, 
	
	/**
	 * Test encountered an unexpected error during execution
	 */
	ERROR

}
