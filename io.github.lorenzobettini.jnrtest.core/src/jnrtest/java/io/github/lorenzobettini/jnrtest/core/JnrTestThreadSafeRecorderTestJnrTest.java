package io.github.lorenzobettini.jnrtest.core;

public class JnrTestThreadSafeRecorderTestJnrTest extends JnrTest { // NOSONAR

	private JnrTestThreadSafeRecorderTest originalTest = new JnrTestThreadSafeRecorderTest();

	public JnrTestThreadSafeRecorderTestJnrTest() {
		super("JnrTestThreadSafeRecorderTest in JnrTest");
	}

	@Override
	protected void specify() {
		test("testSingleThreadedRecording",
			() -> originalTest.testSingleThreadedRecording());
		test("testMultiThreadedRecording",
			() -> originalTest.testMultiThreadedRecording());
		test("testMultiThreadedFailures",
			() -> originalTest.testMultiThreadedFailures());
	}
}
