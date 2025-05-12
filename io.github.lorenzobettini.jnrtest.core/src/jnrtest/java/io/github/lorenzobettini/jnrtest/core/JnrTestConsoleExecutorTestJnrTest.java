package io.github.lorenzobettini.jnrtest.core;

public class JnrTestConsoleExecutorTestJnrTest extends JnrTest {

	private JnrTestConsoleExecutorTest originalTest = new JnrTestConsoleExecutorTest();

	public JnrTestConsoleExecutorTestJnrTest() {
		super("JnrTestConsoleExecutorTest in JnrTest");
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
		test("should throw exception when execute fails",
			() -> originalTest.shouldThrowExceptionWhenExecuteFails());
		test("should return false when executeWithoutThrowing fails",
			() -> originalTest.shouldReturnFalseWhenExecuteWithoutThrowingFails());
	}
}
