package io.github.lorenzobettini.jnrtest.othertests;

public class GeneratorJnrTest {

	public String generate(int numberOfTests) {
		var testCase = """
		package com.example.demos.jnrtest;
		
		import static org.junit.jupiter.api.Assertions.assertTrue;
		
		import io.github.lorenzobettini.jnrtest.core.JnrTest;
		
		public class MyJnr%d extends JnrTest {

			public MyJnr%d(String description) {
				super(description);
			}

			@Override
			protected void specify() {
		%s
			}
		}
		""";
		var test = """
			test("testSomething%d", () -> { assertTrue(true); });
		""";
		StringBuilder testBuilder = new StringBuilder();
		for (int i = 0; i < numberOfTests; i++) {
			testBuilder.append(test.formatted(i));
		}
		return testCase.formatted(numberOfTests, numberOfTests, testBuilder.toString());
	}
}
