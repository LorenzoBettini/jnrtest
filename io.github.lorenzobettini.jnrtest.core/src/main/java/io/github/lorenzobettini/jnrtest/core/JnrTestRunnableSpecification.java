package io.github.lorenzobettini.jnrtest.core;

/**
 * Represents a specification for a test runnable with a description.
 * 
 * @author Lorenzo Bettini
 * @param description The description of the test specification
 * @param testRunnable The runnable that implements the test
 */
public record JnrTestRunnableSpecification(String description, JnrTestRunnable testRunnable) {

}
