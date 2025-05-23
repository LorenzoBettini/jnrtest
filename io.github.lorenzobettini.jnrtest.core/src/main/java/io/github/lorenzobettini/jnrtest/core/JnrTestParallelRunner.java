package io.github.lorenzobettini.jnrtest.core;

import java.util.stream.Stream;

/**
 * A specialized {@link JnrTestRunner} that executes test classes in parallel.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestParallelRunner extends JnrTestRunner {

	@Override
	protected Stream<JnrTest> getTestClassesStream() {
		return super.getTestClassesStream().parallel();
	}
}
