package io.github.lorenzobettini.jnrtest.core;

public class JnrTestTotalExecutionTimeRecorderTestJnrTest extends JnrTest {

	private JnrTestTotalExecutionTimeRecorderTest originalTest = new JnrTestTotalExecutionTimeRecorderTest();

	public JnrTestTotalExecutionTimeRecorderTestJnrTest() {
		super("JnrTestTotalExecutionTimeRecorderTest in JnrTest");
	}

	@Override
	protected void specify() {
		test("testTotalTime",
			() -> originalTest.testTotalTime());
	}
}
