package io.github.lorenzobettini.jnrtest.core;

public class ExampleTestJnrTest extends JnrTestCase {

	private ExampleTest originalTest = new ExampleTest();

	public ExampleTestJnrTest() {
		super("ExampleTest in JnrTest");
	}

	@Override
	protected void specify() {
		beforeAll("call beforeAllTests",
			() -> ExampleTest.beforeAllTests());
		beforeEach("call beforeEachTest",
		() -> {
			originalTest.beforeEachTest();
		});
		afterAll("call afterAllTests",
			() ->ExampleTest.afterAllTests());
		afterEach("call afterEachTest",
			() -> originalTest.afterEachTest());

		test("Test 1", () -> {
			originalTest.test1();
		});

		test("test2", () -> {
			originalTest.test2();
		});
	}
	
}
