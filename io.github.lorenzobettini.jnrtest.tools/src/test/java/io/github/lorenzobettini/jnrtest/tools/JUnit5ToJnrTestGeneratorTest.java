package io.github.lorenzobettini.jnrtest.tools;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JUnit5ToJnrTestGeneratorTest {

	private static final String OUTPUT = "target/output-junit2jnrtest-generator";

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
	 * Scan the "src/test/inputs" directory for classes containing JUnit 5 tests and
	 * generate the corresponding JnrTest subclasses in the "target/output-junit2jnrtest-generator" directory.
	 * Then, verify that the output directory contains only the expected files in
	 * "src/test/outputs/tests".
	 */
	@Test
	void testGeneratedJnrTests() throws IOException {
		var inputDir = "src/test/inputs";

		// Generate the JnrTest subclasses
		JUnit5ToJnrTestGenerator.generate(inputDir, OUTPUT);

		// Read the expected output directory
		var expectedOutputDir = Path.of("src/test/outputs/com/examples/tests");
		// Read the actual output directory
		var actualOutputDir = Path.of(OUTPUT, "com/examples/tests");
		
		// verify that the actual output directory contains only the expected files
		try (var expectedFiles = Files.walk(expectedOutputDir)) {
			var expectedFilePaths = expectedFiles
					.filter(Files::isRegularFile)
					.map(expectedOutputDir::relativize)
					.sorted()
					.toList();
			
			try (var actualFiles = Files.walk(actualOutputDir)) {
				var actualFilePaths = actualFiles
						.filter(Files::isRegularFile)
						.map(actualOutputDir::relativize)
						.sorted()
						.toList();
				
				// Verify same file names
				assertThat(actualFilePaths).isEqualTo(expectedFilePaths);
				
				// Verify contents of each file
				for (var relativePath : expectedFilePaths) {
					var expectedFile = expectedOutputDir.resolve(relativePath);
					var actualFile = actualOutputDir.resolve(relativePath);
					assertThat(actualFile).hasSameTextualContentAs(expectedFile);
				}
			}
		}
	}
}
