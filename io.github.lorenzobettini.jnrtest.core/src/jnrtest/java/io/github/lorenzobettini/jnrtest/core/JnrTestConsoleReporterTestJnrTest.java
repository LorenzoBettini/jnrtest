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
	}
}
