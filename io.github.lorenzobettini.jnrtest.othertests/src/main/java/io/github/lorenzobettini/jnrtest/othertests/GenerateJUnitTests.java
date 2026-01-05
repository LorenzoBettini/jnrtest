package io.github.lorenzobettini.jnrtest.othertests;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class GenerateJUnitTests {

	private static final List<Integer> TEST_SIZES = List.of(1, 10, 100, 1000, 5000);

	public static void main(String[] args) throws FileNotFoundException {
		var generator = new GeneratorJUnit();
		var generatorOneInstancePerClass = new GeneratorJUnitOneInstancePerClass();
		for (var size : TEST_SIZES) {
			generate(generator, size);
			generateOneInstancePerClass(generatorOneInstancePerClass, size);
		}

		// generate the suite files
		var fileName = "src/test/java/com/example/demos/junit/MyAllTest.java";
		var outputFile = new java.io.File(fileName);
		try (var writer = new PrintWriter(outputFile)) {
			writer.println("""
package com.example.demos.junit;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({""");
			for (var size : TEST_SIZES) {
				writer.println("		MyJUnit" + size + "Spec.class,");
			}
			writer.println("""
})
public class MyAllTest {

}

			""");
		}

		fileName = "src/test/java/com/example/demos/junit/MyAllOneInstancePerClassTest.java";
		outputFile = new java.io.File(fileName);
		try (var writer = new PrintWriter(outputFile)) {
			writer.println("""
package com.example.demos.junit;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({""");
			for (var size : TEST_SIZES) {
				writer.println("		MyJUnit" + size + "OneInstancePerClassSpec.class,");
			}
			writer.println("""
})
public class MyAllOneInstancePerClassTest {

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

	private static void generateOneInstancePerClass(GeneratorJUnitOneInstancePerClass generator, int numOfTests) throws FileNotFoundException {
		var test = generator.generate(numOfTests);
		var fileName = String.format("src/test/java/com/example/demos/junit/MyJUnit%dOneInstancePerClassSpec.java", numOfTests);
		var outputFile = new java.io.File(fileName);
		try (var writer = new PrintWriter(outputFile)) {
			writer.println(test);
		}
	}

}
