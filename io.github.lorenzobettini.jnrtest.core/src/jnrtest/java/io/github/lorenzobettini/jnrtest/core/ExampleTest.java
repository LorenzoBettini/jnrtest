package io.github.lorenzobettini.jnrtest.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExampleTest {

	private int count = 0;

	@BeforeAll
	static void beforeAllTests() {
		System.out.println("Before all tests");
	}

	@BeforeEach
	void beforeEachTest() {
		count = 1;
		System.out.println("Before each test");
	}

	@AfterAll
	static void afterAllTests() {
		System.out.println("After all tests");
	}

	@AfterEach
	void afterEachTest() {
		System.out.println("After each test");
	}

	@DisplayName("Test 1")
	@Test
	void test1() {
		System.out.println("Test 1");
		assertEquals(1, count);
	}

	@Test
	void test2() {
		System.out.println("Test 2");
		assertEquals(1, count);
	}

	@Test
	@DisplayName("Test 3")
	void test3() {
		System.out.println("Test 3");
		assertEquals(1, count);
	}

}
