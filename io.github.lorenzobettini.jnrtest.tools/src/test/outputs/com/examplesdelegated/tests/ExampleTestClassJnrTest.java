package com.examplesdelegated.tests;

public class ExampleTestClassJnrTest extends io.github.lorenzobettini.jnrtest.core.JnrTest { // NOSONAR

	private ExampleTestClass originalTest = new ExampleTestClass();

	public ExampleTestClassJnrTest() {
		super("ExampleTestClass in JnrTest");
	}

	@Override
	protected void specify() {
		beforeAll("call setUpBeforeClass",
			() -> ExampleTestClass.setUpBeforeClass());
		beforeEach("call setUp",
			() -> originalTest.setUp());
		afterAll("call tearDownAfterClass",
			() -> ExampleTestClass.tearDownAfterClass());
		afterEach("call tearDown",
			() -> originalTest.tearDown());
		test("testWithoutDisplayName",
			() -> originalTest.testWithoutDisplayName());
		test("test with display name",
			() -> originalTest.testWithDisplayName());
	}
}
