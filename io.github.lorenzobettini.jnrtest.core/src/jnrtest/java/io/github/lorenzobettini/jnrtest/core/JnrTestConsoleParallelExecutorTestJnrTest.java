package io.github.lorenzobettini.jnrtest.core;

public class JnrTestConsoleParallelExecutorTestJnrTest extends JnrTest {

	private JnrTestConsoleParallelExecutorTest originalTest = new JnrTestConsoleParallelExecutorTest();

	public JnrTestConsoleParallelExecutorTestJnrTest() {
		super("JnrTestConsoleParallelExecutorTest in JnrTest");
	}

	@Override
	protected void specify() {
		beforeEach("call setUpStreams",
			() -> originalTest.setUpStreams());
		afterEach("call restoreStreams",
			() -> originalTest.restoreStreams());
		test("should add test classes correctly",
			() -> originalTest.shouldAddTestClassesCorrectly());
		test("should add listeners correctly",
			() -> originalTest.shouldAddListenersCorrectly());
		test("should execute tests without throwing when all tests pass",
			() -> originalTest.shouldExecuteWithoutThrowingWhenAllTestsPass());
		test("should show detailed reports",
			() -> originalTest.shouldShowDetailedReports());
		test("should show only summaries",
			() -> originalTest.shouldShowOnlyReports());
		test("should throw exception when execute fails",
			() -> originalTest.shouldThrowExceptionWhenExecuteFails());
		test("should return false when executeWithoutThrowing fails",
			() -> originalTest.shouldReturnFalseWhenExecuteWithoutThrowingFails());
		test("should handle multiple test classes in parallel",
			() -> originalTest.shouldHandleMultipleTestClassesInParallel());
		test("should filter tests correctly in parallel mode",
			() -> originalTest.shouldFilterTestsCorrectlyInParallelMode());
	}
}
