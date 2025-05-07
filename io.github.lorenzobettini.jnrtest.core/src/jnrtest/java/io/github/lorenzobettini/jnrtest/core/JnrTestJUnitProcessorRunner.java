package io.github.lorenzobettini.jnrtest.core;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is a runner for the JnrTestJUnitProcessor.
 * It processes JUnit test files and generates JnrTest files.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestJUnitProcessorRunner {

	public static void main(String[] args) throws Exception {
		// Get the current directory
		Path currentDir = Paths.get("").toAbsolutePath();

		// Source directory containing JUnit tests
		Path sourceDirectory = Paths.get(currentDir.toString(), "io.github.lorenzobettini.jnrtest.core", "src",
				"test", "java", "io", "github", "lorenzobettini", "jnrtest", "core");

		// Output directory for generated JnrTest files
		Path outputDirectory = Paths.get(currentDir.toString(), "io.github.lorenzobettini.jnrtest.core", "src",
				"jnrtest", "java");

		System.out.println("Processing test files from: " + sourceDirectory);
		System.out.println("Writing output to: " + outputDirectory);

		// Create and run the processor
		JnrTestJUnitProcessor processor = new JnrTestJUnitProcessor(sourceDirectory, outputDirectory);
		processor.process();

		System.out.println("Processing complete!");
	}
}