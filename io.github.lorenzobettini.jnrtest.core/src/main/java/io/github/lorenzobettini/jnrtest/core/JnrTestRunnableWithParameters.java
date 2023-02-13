package io.github.lorenzobettini.jnrtest.core;

public interface JnrTestRunnableWithParameters<T> {

	void runTest(T parameter) throws Exception; // NOSONAR
}
