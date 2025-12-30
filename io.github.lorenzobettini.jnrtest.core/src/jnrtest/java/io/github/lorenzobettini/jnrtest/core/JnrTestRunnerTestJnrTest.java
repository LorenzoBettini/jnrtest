package io.github.lorenzobettini.jnrtest.core;

public class JnrTestRunnerTestJnrTest extends JnrTest { // NOSONAR

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
		test("testListener should return this for chaining",
			() -> originalTest.testListenerShouldReturnThisForChaining());
		test("classFilter should return this for chaining",
			() -> originalTest.classFilterShouldReturnThisForChaining());
		test("specificationFilter should return this for chaining",
			() -> originalTest.specificationFilterShouldReturnThisForChaining());
		test("filterByClassDescription should return this for chaining",
			() -> originalTest.filterByClassDescriptionShouldReturnThisForChaining());
		test("filterBySpecificationDescription should return this for chaining",
			() -> originalTest.filterBySpecificationDescriptionShouldReturnThisForChaining());
	}
}
