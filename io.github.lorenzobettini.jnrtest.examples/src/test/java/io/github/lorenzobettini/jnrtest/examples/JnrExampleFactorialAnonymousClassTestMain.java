package io.github.lorenzobettini.jnrtest.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.lorenzobettini.jnrtest.core.JnrTest;
import io.github.lorenzobettini.jnrtest.core.JnrTestConsoleExecutor;

public class JnrExampleFactorialAnonymousClassTestMain {

	public static void main(String[] args) {
		new JnrTestConsoleExecutor()
		.add(new JnrTest("tests for factorial") {
			private Factorial factorial;

			@Override
			protected void specify() {
				beforeAll("create factorial SUT", () -> factorial = new Factorial());
				test("case 0", () -> assertEquals(1, factorial.compute(0)));
				test("case 1", () -> assertEquals(1, factorial.compute(1)));
				test("case 2", () -> assertEquals(2, factorial.compute(2)));
				test("case 3", () -> assertEquals(6, factorial.compute(3)));
				test("case 4", () -> assertEquals(24, factorial.compute(4)));
			}
		})
		.execute();
	}
}
