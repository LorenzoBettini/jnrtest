package io.github.lorenzobettini.jnrtest.othertests;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class GenerateJnrTests {

	public static void main(String[] args) throws FileNotFoundException {
		var generator = new GeneratorJnrTest();
		generate(generator, 10);
		generate(generator, 100);
		generate(generator, 1000);
		generate(generator, 5000);

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
		executor.add(new MyJnr10("MyJnr10"));
		executor.add(new MyJnr100("MyJnr100"));
		executor.add(new MyJnr1000("MyJnr1000"));
		executor.add(new MyJnr5000("MyJnr5000"));
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
