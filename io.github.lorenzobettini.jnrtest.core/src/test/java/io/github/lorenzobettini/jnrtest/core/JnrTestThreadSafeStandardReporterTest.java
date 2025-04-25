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

class JnrTestThreadSafeStandardReporterTest {

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
		JnrTestThreadSafeStandardReporter reporter = new JnrTestThreadSafeStandardReporter();

		ExecutorService executorService = Executors.newFixedThreadPool(10);

		for (int i = 0; i < 10; i++) {
			final int threadId = i;
			executorService.submit(() -> {
				String testCaseName = "TestCase-" + threadId;
				JnrTestCaseLifecycleEvent startEvent = new JnrTestCaseLifecycleEvent(
						testCaseName, JnrTestCaseStatus.START);
				JnrTestCaseLifecycleEvent endEvent = new JnrTestCaseLifecycleEvent(
						testCaseName, JnrTestCaseStatus.END);

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

		// Verify the output contains each expected block
		String output = outputStream.toString();
		for (int i = 0; i < 10; i++) {
			String testCaseName = "TestCase-" + i;

			// Generate the expected block for this test case
			StringBuilder expectedBlock = new StringBuilder();
			expectedBlock.append("[  START] ").append(testCaseName).append("\n");
			for (int j = 0; j < 5; j++) {
				expectedBlock.append("[SUCCESS] ").append(testCaseName).append("-Test-").append(j).append("\n");
			}
			expectedBlock.append("Tests run: 5, Succeeded: 5, Failures: 0, Errors: 0\n");

			// Verify the output contains the expected block
			assertThat(output).contains(expectedBlock.toString());
		}
	}

	@Test
	void testSingleThreadCompleteOutputVerification() {
		JnrTestThreadSafeStandardReporter reporter = new JnrTestThreadSafeStandardReporter();

		String testCaseName = "TestCase-1";
		JnrTestCaseLifecycleEvent startEvent = new JnrTestCaseLifecycleEvent(
				testCaseName, JnrTestCaseStatus.START);
		JnrTestCaseLifecycleEvent endEvent = new JnrTestCaseLifecycleEvent(
				testCaseName, JnrTestCaseStatus.END);

		reporter.notify(startEvent);

		// Notify multiple results
		for (int i = 0; i < 3; i++) {
			JnrTestResult result = new JnrTestResult(testCaseName + "-Test-" + i, JnrTestResultStatus.SUCCESS, null);
			reporter.notify(result);
		}

		reporter.notify(endEvent);

		// Expected output using Java text blocks
		String expectedOutput = """
			[  START] TestCase-1
			[SUCCESS] TestCase-1-Test-0
			[SUCCESS] TestCase-1-Test-1
			[SUCCESS] TestCase-1-Test-2
			Tests run: 3, Succeeded: 3, Failures: 0, Errors: 0
			""";

		// Verify the output matches the expected output
		assertThat(outputStream.toString()).isEqualTo(expectedOutput);
	}
}
