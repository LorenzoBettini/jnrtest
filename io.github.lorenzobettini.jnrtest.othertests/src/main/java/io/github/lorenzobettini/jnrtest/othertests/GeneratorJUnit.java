package io.github.lorenzobettini.jnrtest.othertests;

public class GeneratorJUnit {

	public String generate(int numberOfTests) {
		var testCase = """
		package com.example.demos.junit;
		
		import static org.junit.jupiter.api.Assertions.assertTrue;
		
		import org.junit.jupiter.api.Test;
		
		class MyJUnit%dTests {
		%s
		}
		""";
		var test = """
			@Test
			void testSomething%d() throws Exception {
				assertTrue(true);
			}
		""";
		StringBuilder testBuilder = new StringBuilder();
		for (int i = 0; i < numberOfTests; i++) {
			testBuilder.append(test.formatted(i));
		}
		return testCase.formatted(numberOfTests, testBuilder.toString());
	}
}
