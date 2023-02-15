package io.github.lorenzobettini.jnrtest.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.github.lorenzobettini.jnrtest.core.JnrTestCase;

public class MyStringUtilsJnrTestCase extends JnrTestCase {

	private MyStringUtils stringUtils;

	public MyStringUtilsJnrTestCase() {
		super("tests for leftTrim");
	}

	@Override
	protected void specify() {
		beforeAll("create factorial SUT", () -> stringUtils = new MyStringUtils());
		test("null string", () -> assertNull(stringUtils.leftTrim(null)));
		test("empty string", () -> assertEquals("", stringUtils.leftTrim("")));
		test("one leading space", () -> assertEquals("abc", stringUtils.leftTrim(" abc")));
		test("no leading space", () -> assertEquals("abc", stringUtils.leftTrim("abc")));
		test("one leading tab", () -> assertEquals("abc", stringUtils.leftTrim("\tabc")));
		test("several leading spaces", () -> assertEquals("abc", stringUtils.leftTrim("  abc")));
		test("all spaces", () -> assertEquals("", stringUtils.leftTrim("  ")));
	}

}
