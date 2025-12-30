package io.github.lorenzobettini.jnrtest.core;

public class JnrTestStatisticsTestJnrTest extends JnrTest { // NOSONAR

	private JnrTestStatisticsTest originalTest = new JnrTestStatisticsTest();

	public JnrTestStatisticsTestJnrTest() {
		super("JnrTestStatisticsTest in JnrTest");
	}

	@Override
	protected void specify() {
		beforeEach("call setUp",
			() -> originalTest.setUp());
		test("should track elapsed time correctly",
			() -> originalTest.shouldTrackElapsedTimeCorrectly());
		test("should accumulate total time correctly",
			() -> originalTest.shouldAccumulateTotalTimeCorrectly());
		test("should return zero elapsed time when not enabled",
			() -> originalTest.shouldReturnZeroElapsedTimeWhenNotEnabled());
		test("should check if elapsed time tracking is enabled",
			() -> originalTest.shouldCheckIfElapsedTimeTrackingIsEnabled());
		test("getElapsedTime should return non-zero when timer is running",
			() -> originalTest.getElapsedTimeShouldReturnNonZeroWhenTimerIsRunning());
		test("getTotalTime should return accumulated time",
			() -> originalTest.getTotalTimeShouldReturnAccumulatedTime());
		test("should reset state correctly",
			() -> originalTest.shouldResetStateCorrectly());
	}
}
