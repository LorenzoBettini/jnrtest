package io.github.lorenzobettini.jnrtest.core;

public class JnrTestReporterInterfaceTestJnrTest extends JnrTest { // NOSONAR

	private JnrTestReporterInterfaceTest originalTest = new JnrTestReporterInterfaceTest();

	public JnrTestReporterInterfaceTestJnrTest() {
		super("JnrTestReporterInterfaceTest in JnrTest");
	}

	@Override
	protected void specify() {
		test("withOnlySummaries() should return this for chaining",
			() -> originalTest.withOnlySummariesShouldReturnThisForChaining());
		test("withElapsedTime() should return this for chaining",
			() -> originalTest.withElapsedTimeShouldReturnThisForChaining());
		test("withOnlySummaries(boolean) should return this for chaining",
			() -> originalTest.withOnlySummariesBooleanShouldReturnThisForChaining());
		test("withElapsedTime(boolean) should return this for chaining",
			() -> originalTest.withElapsedTimeBooleanShouldReturnThisForChaining());
	}
}
