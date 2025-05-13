package io.github.lorenzobettini.jnrtest.othertests;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class GenerateJUnitTests {

	public static void main(String[] args) throws FileNotFoundException {
		var generator = new GeneratorJUnit();
		generate(generator, 10);
		generate(generator, 100);
		generate(generator, 1000);
		generate(generator, 5000);
	}

	private static void generate(GeneratorJUnit generator, int numOfTests) throws FileNotFoundException {
		var test = generator.generate(numOfTests);
		var fileName = String.format("src/test/java/com/example/demos/junit/MyJUnit%dTests.java", numOfTests);
		var outputFile = new java.io.File(fileName);
		try (var writer = new PrintWriter(outputFile)) {
			writer.println(test);
		}
	}

}
