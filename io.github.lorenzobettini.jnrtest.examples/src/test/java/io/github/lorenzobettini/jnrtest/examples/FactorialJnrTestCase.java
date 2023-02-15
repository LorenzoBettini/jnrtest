package io.github.lorenzobettini.jnrtest.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.lorenzobettini.jnrtest.core.JnrTestCase;

public class FactorialJnrTestCase extends JnrTestCase {

	private Factorial factorial;

	public FactorialJnrTestCase() {
		super("tests for factorial");
	}

	@Override
	protected void specify() {
		beforeAll("create factorial SUT", () -> factorial = new Factorial());
		test("case 0", () -> assertEquals(1, factorial.compute(0)));
		test("case 1", () -> assertEquals(1, factorial.compute(1)));
		test("case 2", () -> assertEquals(2, factorial.compute(2)));
		test("case 3", () -> assertEquals(6, factorial.compute(3)));
		test("case 4", () -> assertEquals(24, factorial.compute(4)));
	}

}
