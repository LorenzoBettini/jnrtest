package com.examplesdelegated.tests;

public class ExampleTest2ClassJnrTest extends io.github.lorenzobettini.jnrtest.core.JnrTest { // NOSONAR

	private ExampleTest2Class originalTest = new ExampleTest2Class();

	public ExampleTest2ClassJnrTest() {
		super("ExampleTest2Class in JnrTest");
	}

	@Override
	protected void specify() {
		beforeAll("call setUpBeforeClass",
			() -> ExampleTest2Class.setUpBeforeClass());
		beforeEach("call setUp",
			() -> originalTest.setUp());
		afterAll("call tearDownAfterClass",
			() -> ExampleTest2Class.tearDownAfterClass());
		afterEach("call tearDown",
			() -> originalTest.tearDown());
		test("testWithoutDisplayName",
			() -> originalTest.testWithoutDisplayName());
		test("test with display name",
			() -> originalTest.testWithDisplayName());
	}
}
