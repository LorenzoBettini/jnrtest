package io.github.lorenzobettini.jnrtest.tools;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JnrTestMainGeneratorTest {

	private static final String OUTPUT = "target/output-main-generator";

	@BeforeEach
	void setUp() throws IOException {
		// prepare output directory: clean if exists, create otherwise
		Path outputPath = Paths.get(OUTPUT);
		if (Files.exists(outputPath)) {
			Files.walk(outputPath)
				.sorted(Comparator.reverseOrder())
				.map(Path::toFile)
				.forEach(File::delete);
		}
		Files.createDirectories(outputPath);
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
		var outputClass = "com.examples.discovery.main.JnrTestMainGenerated";
		
		// Generate the main class
		new JnrTestMainGenerator().generateMain(inputDir, OUTPUT, outputClass);
		
		// Read the generated file
		var generatedFile = Path.of(OUTPUT, "com/examples/discovery/main/JnrTestMainGenerated.java");
		
		// Read the expected file
		var expectedFile = Path.of("src/test/outputs/com/examples/discovery/main/JnrTestMainGenerated.java");
		
		// Compare
		assertThat(generatedFile).hasSameTextualContentAs(expectedFile);
	}

}
