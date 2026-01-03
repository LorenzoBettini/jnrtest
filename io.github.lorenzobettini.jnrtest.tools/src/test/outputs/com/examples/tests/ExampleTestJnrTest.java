package com.examples.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import io.github.lorenzobettini.jnrtest.core.JnrTest;

public class ExampleTestJnrTest extends JnrTest {

	public ExampleTestJnrTest() {
		super("ExampleTest in JnrTest");
	}

	protected @Override void specify() {
		beforeAll("call setUpBeforeClass", () -> {
			// this is the setupBeforeClass method
			System.out.println("Setting up before all tests");
		});
		beforeEach("call setUp", () -> {
			System.out.println("Setting up before each test");
			aList = List.of("ExampleTest in JnrTest");
		});
		afterAll("call tearDownAfterClass", () -> {
			System.out.println("Tearing down after all tests");
		});
		afterEach("call tearDown", () -> {
			System.out.println("Tearing down after each test");
		});
		test("testWithoutDisplayName", () -> {
			helperMethod();
			assertThat(aList).hasSize(1).contains(aString);
			fail("Not yet implemented");
		});
		test("test with display name", () -> {
			helperMethod();
			assertTrue(true);
		});
	}

	String aString = "ExampleTest in JnrTest";
	int anInt = 42;
	List<String> aList;

	/**
	 * An inner helper class, not a test
	 */
	private static class InnerHelper {
		// This is a helper class, not a test
		public void doSomething() {
			System.out.println("InnerHelper doing something");
		}
	}

	private void helperMethod() {
		// This is a helper method, not a test
	}

}
