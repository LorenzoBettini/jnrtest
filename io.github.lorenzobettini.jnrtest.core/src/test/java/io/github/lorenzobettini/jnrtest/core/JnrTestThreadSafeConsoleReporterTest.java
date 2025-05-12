package io.github.lorenzobettini.jnrtest.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class JnrTestThreadSafeConsoleReporterTest {

	private ByteArrayOutputStream outputStream;
	private PrintStream originalOut;

	@BeforeEach
	void setUp() {
		outputStream = new ByteArrayOutputStream();
		originalOut = System.out;
		System.setOut(new PrintStream(outputStream));
	}

	@AfterEach
	void tearDown() {
		System.setOut(originalOut);
	}

	@Test
	void testThreadSafetyWithResultsAndVerification() throws InterruptedException {
		JnrTestThreadSafeConsoleReporter reporter = new JnrTestThreadSafeConsoleReporter();

		ExecutorService executorService = Executors.newFixedThreadPool(10);

		for (int i = 0; i < 10; i++) {
			final int threadId = i;
			executorService.submit(() -> {
				String testCaseName = "TestClass-" + threadId;
				JnrTestLifecycleEvent startEvent = new JnrTestLifecycleEvent(
						testCaseName, JnrTestStatus.START);
				JnrTestLifecycleEvent endEvent = new JnrTestLifecycleEvent(
						testCaseName, JnrTestStatus.END);

				reporter.notify(startEvent);

				// Notify multiple results
				for (int j = 0; j < 5; j++) {
					JnrTestResult result = new JnrTestResult(testCaseName + "-Test-" + j, JnrTestResultStatus.SUCCESS, null);
					reporter.notify(result);
				}

				reporter.notify(endEvent);
			});
		}

		executorService.shutdown();
		assertThat(executorService.awaitTermination(10, TimeUnit.SECONDS)).isTrue();

		 // Normalize output to handle different EOL characters
		String output = outputStream.toString().replace("\r\n", "\n");
		for (int i = 0; i < 10; i++) {
			String testCaseName = "TestClass-" + i;

			// Generate the expected block for this test class
			StringBuilder expectedBlock = new StringBuilder();
			expectedBlock.append("[  START] ").append(testCaseName).append("\n");
			for (int j = 0; j < 5; j++) {
				expectedBlock.append("[SUCCESS] ").append(testCaseName).append("-Test-").append(j).append("\n");
			}
			expectedBlock.append("Tests run: 5, Succeeded: 5, Failures: 0, Errors: 0\n");

			// Verify the output contains the expected block
			assertThat(output).contains(expectedBlock.toString().replace("\r\n", "\n"));
		}
	}

	@Test
	void testSingleThreadCompleteOutputVerification() {
		JnrTestThreadSafeConsoleReporter reporter = new JnrTestThreadSafeConsoleReporter();

		String testCaseName = "TestClass-1";
		JnrTestLifecycleEvent startEvent = new JnrTestLifecycleEvent(
				testCaseName, JnrTestStatus.START);
		JnrTestLifecycleEvent endEvent = new JnrTestLifecycleEvent(
				testCaseName, JnrTestStatus.END);

		reporter.notify(startEvent);

		// Notify multiple results
		for (int i = 0; i < 3; i++) {
			JnrTestResult result = new JnrTestResult(testCaseName + "-Test-" + i, JnrTestResultStatus.SUCCESS, null);
			reporter.notify(result);
		}

		reporter.notify(endEvent);

		// Expected output using Java text blocks
		String expectedOutput = """
			[  START] TestClass-1
			[SUCCESS] TestClass-1-Test-0
			[SUCCESS] TestClass-1-Test-1
			[SUCCESS] TestClass-1-Test-2
			Tests run: 3, Succeeded: 3, Failures: 0, Errors: 0
			""";

		 // Normalize output to handle different EOL characters
		String normalizedOutput = outputStream.toString().replace("\r\n", "\n");
		assertThat(normalizedOutput).isEqualTo(expectedOutput.replace("\r\n", "\n"));
	}
}
