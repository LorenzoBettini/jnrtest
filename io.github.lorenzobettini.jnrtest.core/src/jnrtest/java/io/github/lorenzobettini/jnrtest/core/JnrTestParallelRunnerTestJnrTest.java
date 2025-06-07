package io.github.lorenzobettini.jnrtest.core;

public class JnrTestParallelRunnerTestJnrTest extends JnrTest { // NOSONAR

	private JnrTestParallelRunnerTest originalTest = new JnrTestParallelRunnerTest();

	public JnrTestParallelRunnerTestJnrTest() {
		super("JnrTestParallelRunnerTest in JnrTest");
	}

	@Override
	protected void specify() {
		beforeEach("call setUpStreams",
			() -> originalTest.setUpStreams());
		afterEach("call restoreStreams",
			() -> originalTest.restoreStreams());
		test("should run in parallel",
			() -> originalTest.shouldReportResults());
	}
}
