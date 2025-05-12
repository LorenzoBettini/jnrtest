package io.github.lorenzobettini.jnrtest.core;

public class JnrTestRunnerTestJnrTest extends JnrTest {

	private JnrTestRunnerTest originalTest = new JnrTestRunnerTest();

	public JnrTestRunnerTestJnrTest() {
		super("JnrTestRunnerTest in JnrTest");
	}

	@Override
	protected void specify() {
		test("should run all the tests",
			() -> originalTest.shouldRunAllTheTests());
		test("should notify listeners",
			() -> originalTest.shouldNotifyListeners());
		test("should runs all tests with parameters",
			() -> originalTest.shouldRunsAllTestsWithParameters());
		test("should record results",
			() -> originalTest.shouldRecordResults());
		test("should specify the tests only once",
			() -> originalTest.shouldSpecifyTestsOnlyOnce());
		test("should execute lifecycle",
			() -> originalTest.shouldExecuteLifecycle());
		test("should run extensions",
			() -> originalTest.shouldRunExtensions());
	}
}
