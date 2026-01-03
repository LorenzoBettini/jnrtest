package io.github.lorenzobettini.jnrtest.tools;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JnrTestMainGeneratorTest {

	private static final String OUTPUT = "target/output-main-generator";

	@BeforeEach
	void setUp() {
		// prepare output directory: clean if exists, create otherwise
		var outputDir = Path.of(OUTPUT);
		if (outputDir.toFile().exists()) {
			outputDir.toFile().delete();
		}
		outputDir.toFile().mkdirs();
	}

	/**
	 * Scan the "src/test/inputs" directory for JnrTest subclasses and generate a main class
	 * JnrTestMainGenerated in the "target/output-main-generator" directory.
	 * Then, verify that the generated main class is equal to the expected one in
	 * "src/test/outputs/com/examples/main/JnrTestMainGenerated.java".
	 */
	@Test
	void testGeneratedMain() throws IOException {
		var inputDir = "src/test/inputs";
		var outputClass = "com.examples.main.JnrTestMainGenerated";
		
		// Generate the main class
		JnrTestMainGenerator.generateMain(inputDir, OUTPUT, outputClass);
		
		// Read the generated file
		var generatedFile = Path.of(OUTPUT, "com/examples/main/JnrTestMainGenerated.java");
		
		// Read the expected file
		var expectedFile = Path.of("src/test/outputs/com/examples/main/JnrTestMainGenerated.java");
		
		// Compare
		assertThat(generatedFile).hasSameTextualContentAs(expectedFile);
	}

}
