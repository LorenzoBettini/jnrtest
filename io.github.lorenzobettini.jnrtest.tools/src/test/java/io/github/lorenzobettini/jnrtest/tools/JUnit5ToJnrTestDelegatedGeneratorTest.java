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

class JUnit5ToJnrTestDelegatedGeneratorTest {

	private static final String OUTPUT = "target/output-junit2jnrtestdelegated-generator";

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
	 * Scan the "src/test/inputs" directory for classes containing JUnit 5 tests and
	 * generate the corresponding JnrTest subclasses in the "target/output-junit2jnrtestdelegated-generator" directory.
	 * Then, verify that the output directory contains only the expected files in
	 * "src/test/outputs/tests".
	 */
	@Test
	void testGeneratedJnrTests() throws IOException {
		var inputDir = "src/test/inputs/com/examplesdelegated/tests";

		// Generate the JnrTest subclasses
		new JUnit5ToJnrTestDelegatedGenerator().generate(inputDir, OUTPUT);

		// Read the expected output directory
		var expectedOutputDir = Path.of("src/test/outputs/com/examplesdelegated/tests");
		// Read the actual output directory
		var actualOutputDir = Path.of(OUTPUT, "com/examplesdelegated/tests");
		
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
