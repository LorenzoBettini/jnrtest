package io.github.lorenzobettini.jnrtest.othertests;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class GenerateJUnitTests {

	public static void main(String[] args) throws FileNotFoundException {
		var generator = new GeneratorJUnit();
		generate(generator, 1);
		generate(generator, 10);
		generate(generator, 100);
		generate(generator, 1000);
//		generate(generator, 5000);

		// generate the main file
		var fileName = "src/test/java/com/example/demos/junit/AllTest.java";
		var outputFile = new java.io.File(fileName);
		try (var writer = new PrintWriter(outputFile)) {
			writer.println("""
package com.example.demos.junit;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	MyJUnit1Spec.class,
	MyJUnit10Spec.class,
	MyJUnit100Spec.class,
	MyJUnit1000Spec.class,
})
public class AllTest {

}

			""");
		}
	}

	private static void generate(GeneratorJUnit generator, int numOfTests) throws FileNotFoundException {
		var test = generator.generate(numOfTests);
		var fileName = String.format("src/test/java/com/example/demos/junit/MyJUnit%dSpec.java", numOfTests);
		var outputFile = new java.io.File(fileName);
		try (var writer = new PrintWriter(outputFile)) {
			writer.println(test);
		}
	}

}
