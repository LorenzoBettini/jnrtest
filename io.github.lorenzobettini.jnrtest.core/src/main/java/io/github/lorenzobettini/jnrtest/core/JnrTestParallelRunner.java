package io.github.lorenzobettini.jnrtest.core;

import java.util.stream.Stream;

/**
 * A specialized {@link JnrTestRunner} that executes test cases in parallel.
 * 
 * @author Lorenzo Bettini
 *
 */
public class JnrTestParallelRunner extends JnrTestRunner {

	@Override
	protected Stream<JnrTestCase> getTestCasesStream() {
		return super.getTestCasesStream().parallel();
	}
}
