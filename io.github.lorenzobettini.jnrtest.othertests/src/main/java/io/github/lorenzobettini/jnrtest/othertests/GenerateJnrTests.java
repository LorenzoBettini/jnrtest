package io.github.lorenzobettini.jnrtest.othertests;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class GenerateJnrTests {

	private static final List<Integer> TEST_SIZES = List.of(1, 10, 100, 1000, 3000, 5000);

	public static void main(String[] args) throws FileNotFoundException {
		var generator = new GeneratorJnrTest();
		for (var size : TEST_SIZES) {
			generate(generator, size);
		}

		// generate the main file
		var fileName = "src/test/java/com/example/demos/jnrtest/MyJnrTestMain.java";
		var outputFile = new java.io.File(fileName);
		try (var writer = new PrintWriter(outputFile)) {
			writer.println("""
package com.example.demos.jnrtest;

import io.github.lorenzobettini.jnrtest.core.JnrTestConsoleExecutor;

public class MyJnrTestMain {

	public static void main(String[] args) {
		var executor = new JnrTestConsoleExecutor();
		executor.getReporter().withOnlySummaries(true);
""");
		for (var size : TEST_SIZES) {
			writer.println("		executor.add(new MyJnr" + size + "(\"MyJnr" + size + "\"));");
		}
		writer.println("""
		executor.execute();
	}
}
			""");
		}
	}

	private static void generate(GeneratorJnrTest generator, int numOfTests) throws FileNotFoundException {
		var test = generator.generate(numOfTests);
		var fileName = String.format("src/test/java/com/example/demos/jnrtest/MyJnr%d.java", numOfTests);
		var outputFile = new java.io.File(fileName);
		try (var writer = new PrintWriter(outputFile)) {
			writer.println(test);
		}
	}

}
