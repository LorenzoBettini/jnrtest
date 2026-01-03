package com.examples.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExampleTestClass {

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

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		// this is the setupBeforeClass method
		System.out.println("Setting up before all tests");
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		System.out.println("Tearing down after all tests");
	}

	@BeforeEach
	void setUp() throws Exception {
		System.out.println("Setting up before each test");
		aList = List.of("ExampleTest in JnrTest");
	}

	@AfterEach
	void tearDown() throws Exception {
		System.out.println("Tearing down after each test");
	}

	@Test
	void testWithoutDisplayName() {
		helperMethod();
		assertThat(aList)
			.hasSize(1)
			.contains(aString);
		fail("Not yet implemented");
	}

	@DisplayName("test with display name")
	@Test
	void testWithDisplayName() {
		helperMethod();
		assertTrue(true);
	}

	private void helperMethod() {
		// This is a helper method, not a test
	}

}
