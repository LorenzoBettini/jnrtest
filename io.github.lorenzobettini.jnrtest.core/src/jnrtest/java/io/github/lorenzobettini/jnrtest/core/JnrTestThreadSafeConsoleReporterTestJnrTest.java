package io.github.lorenzobettini.jnrtest.core;

public class JnrTestThreadSafeConsoleReporterTestJnrTest extends JnrTest { // NOSONAR

	private JnrTestThreadSafeConsoleReporterTest originalTest = new JnrTestThreadSafeConsoleReporterTest();

	public JnrTestThreadSafeConsoleReporterTestJnrTest() {
		super("JnrTestThreadSafeConsoleReporterTest in JnrTest");
	}

	@Override
	protected void specify() {
		beforeEach("call setUp",
			() -> originalTest.setUp());
		afterEach("call tearDown",
			() -> originalTest.tearDown());
		test("testThreadSafetyWithResultsAndVerification",
			() -> originalTest.testThreadSafetyWithResultsAndVerification());
		test("testThreadSafetyWithResultsAndVerificationWithOnlySummaries",
			() -> originalTest.testThreadSafetyWithResultsAndVerificationWithOnlySummaries());
		test("testSingleThreadCompleteOutputVerification",
			() -> originalTest.testSingleThreadCompleteOutputVerification());
		test("shouldDelegateRunnableLifecycleEventsToUnderlying",
			() -> originalTest.shouldDelegateRunnableLifecycleEventsToUnderlying());
		test("shouldCallUnderlyingReporterNotifyForRunnableEvents",
			() -> originalTest.shouldCallUnderlyingReporterNotifyForRunnableEvents());
	}
}
