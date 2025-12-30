package io.github.lorenzobettini.jnrtest.core;

public class JnrTestConsoleReporterTestJnrTest extends JnrTest { // NOSONAR

	private JnrTestConsoleReporterTest originalTest = new JnrTestConsoleReporterTest();

	public JnrTestConsoleReporterTestJnrTest() {
		super("JnrTestConsoleReporterTest in JnrTest");
	}

	@Override
	protected void specify() {
		beforeEach("call setUpStreams",
			() -> originalTest.setUpStreams());
		afterEach("call restoreStreams",
			() -> originalTest.restoreStreams());
		test("should report results",
			() -> originalTest.shouldReportResults());
		test("should report only summary",
			() -> originalTest.shouldReportOnlySummary());
		test("should report results with elapsed time",
			() -> originalTest.shouldReportResultsWithElapsedTime());
		test("should handle lifecycle events for tests with elapsed time",
			() -> originalTest.shouldHandleLifecycleEventsForTestsWithElapsedTime());
		test("should not report elapsed time for non-TEST lifecycle events",
			() -> originalTest.shouldNotReportElapsedTimeForNonTestLifecycleEvents());
		test("should handle STARTED and FINISHED lifecycle events",
			() -> originalTest.shouldHandleStartedAndFinishedLifecycleEvents());
		test("should track time for each individual test result",
			() -> originalTest.shouldTrackTimeForEachIndividualTestResult());
		test("should NOT include elapsed time in END summary when withElapsedTime is false",
			() -> originalTest.shouldNotIncludeElapsedTimeInEndSummaryWhenDisabled());
		test("should include elapsed time in END summary when withElapsedTime is true",
			() -> originalTest.shouldIncludeElapsedTimeInEndSummaryWhenEnabled());
		test("should return early from notify RunnableLifecycleEvent when withElapsedTime is false",
			() -> originalTest.shouldReturnEarlyWhenElapsedTimeDisabled());
		test("should return early from notify RunnableLifecycleEvent when kind is not TEST",
			() -> originalTest.shouldReturnEarlyWhenKindIsNotTest());
		test("should call startTimer when status is START",
			() -> originalTest.shouldCallStartTimerWhenStatusIsStart());
		test("should call stopTimer when status is not START",
			() -> originalTest.shouldCallStopTimerWhenStatusIsNotStart());
	}
}
