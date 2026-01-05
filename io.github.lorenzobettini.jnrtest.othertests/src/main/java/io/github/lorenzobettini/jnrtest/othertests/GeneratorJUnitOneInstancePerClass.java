package io.github.lorenzobettini.jnrtest.othertests;

public class GeneratorJUnitOneInstancePerClass {

	public String generate(int numberOfTests) {
		var testCase = """
		package com.example.demos.junit;
		
		import org.junit.jupiter.api.Test;
		import org.junit.jupiter.api.TestInstance;
		import org.junit.jupiter.api.TestInstance.Lifecycle;
		
		@TestInstance(Lifecycle.PER_CLASS)
		class MyJUnit%dOneInstancePerClassSpec {
		%s
		}
		""";
		var test = """
			@Test
			void testSomething%d() throws Exception {
				com.example.testutils.CommonTestUtils.assertStringIsPresent("findme");
				com.example.testutils.CommonTestUtils.assertStringIsAbsent("foobar");
			}
		""";
		StringBuilder testBuilder = new StringBuilder();
		for (int i = 0; i < numberOfTests; i++) {
			testBuilder.append(test.formatted(i));
		}
		return testCase.formatted(numberOfTests, testBuilder.toString());
	}
}
