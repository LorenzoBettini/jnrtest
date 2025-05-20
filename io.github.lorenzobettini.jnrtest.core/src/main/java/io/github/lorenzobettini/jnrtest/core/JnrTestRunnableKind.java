package io.github.lorenzobettini.jnrtest.core;

/**
 * The kind of an executed runnable.
 * 
 * @author Lorenzo Bettini
 */
public enum JnrTestRunnableKind {

	/**
	 * A standard test case
	 */
	TEST, 
	
	/**
	 * A runnable that executes before all tests in a class
	 */
	BEFORE_ALL, 
	
	/**
	 * A runnable that executes before each test
	 */
	BEFORE_EACH, 
	
	/**
	 * A runnable that executes after each test
	 */
	AFTER_EACH, 
	
	/**
	 * A runnable that executes after all tests in a class
	 */
	AFTER_ALL

}
