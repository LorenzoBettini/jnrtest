package io.github.lorenzobettini.jnrtest.core;

public class JnrTestThreadSafeConsoleReporterTestJnrTest extends JnrTestCase {

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
		test("testSingleThreadCompleteOutputVerification",
			() -> originalTest.testSingleThreadCompleteOutputVerification());
	}
}
