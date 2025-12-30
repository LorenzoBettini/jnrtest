package io.github.lorenzobettini.jnrtest.core;

public class JnrTestRecorderTestJnrTest extends JnrTest { // NOSONAR

	private JnrTestRecorderTest originalTest = new JnrTestRecorderTest();

	public JnrTestRecorderTestJnrTest() {
		super("JnrTestRecorderTest in JnrTest");
	}

	@Override
	protected void specify() {
		beforeEach("call setUp",
			() -> originalTest.setUp());
		test("should handle lifecycle events for non-START status",
			() -> originalTest.shouldHandleLifecycleEventsForNonStartStatus());
		test("should handle runnable lifecycle events for non-TEST kind",
			() -> originalTest.shouldHandleRunnableLifecycleEventsForNonTestKind());
		test("should handle runnable lifecycle events when elapsed time is disabled",
			() -> originalTest.shouldHandleRunnableLifecycleEventsWhenElapsedTimeIsDisabled());
		test("should track elapsed time for test runnables",
			() -> originalTest.shouldTrackElapsedTimeForTestRunnables());
		test("withElapsedTime should return this for chaining",
			() -> originalTest.withElapsedTimeShouldReturnThisForChaining());
		test("should record results correctly",
			() -> originalTest.shouldRecordResultsCorrectly());
		test("should start timer when status is START and kind is TEST",
			() -> originalTest.shouldStartTimerWhenStatusIsStartAndKindIsTest());
	}
}
