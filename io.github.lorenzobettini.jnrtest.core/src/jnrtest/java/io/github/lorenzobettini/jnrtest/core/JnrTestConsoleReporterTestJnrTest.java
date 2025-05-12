package io.github.lorenzobettini.jnrtest.core;

public class JnrTestConsoleReporterTestJnrTest extends JnrTest {

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
		test("should report results with elapsed time",
			() -> originalTest.shouldReportResultsWithElapsedTime());
	}
}
