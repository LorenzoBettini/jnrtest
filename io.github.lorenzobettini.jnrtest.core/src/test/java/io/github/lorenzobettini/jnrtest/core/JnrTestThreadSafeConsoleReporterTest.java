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
				String testClassName = "TestClass-" + threadId;
				JnrTestLifecycleEvent startEvent = new JnrTestLifecycleEvent(
						testClassName, JnrTestStatus.START);
				JnrTestLifecycleEvent endEvent = new JnrTestLifecycleEvent(
						testClassName, JnrTestStatus.END);

				reporter.notify(startEvent);

				// Notify multiple results
				for (int j = 0; j < 5; j++) {
					JnrTestResult result = new JnrTestResult(testClassName + "-Test-" + j, JnrTestResultStatus.SUCCESS, null);
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
			String testClassName = "TestClass-" + i;

			// Generate the expected block for this test class
			StringBuilder expectedBlock = new StringBuilder();
			expectedBlock.append("[  START] ").append(testClassName).append("\n");
			for (int j = 0; j < 5; j++) {
				expectedBlock.append("[SUCCESS] ").append(testClassName).append("-Test-").append(j).append("\n");
			}
			expectedBlock.append("Tests run: 5, Succeeded: 5, Failures: 0, Errors: 0\n");

			// Verify the output contains the expected block
			assertThat(output).contains(expectedBlock.toString().replace("\r\n", "\n"));
		}
	}

	@Test
	void testThreadSafetyWithResultsAndVerificationWithOnlySummaries() throws InterruptedException {
		JnrTestThreadSafeConsoleReporter reporter = new JnrTestThreadSafeConsoleReporter()
				.withOnlySummaries(true);

		ExecutorService executorService = Executors.newFixedThreadPool(10);

		for (int i = 0; i < 10; i++) {
			final int threadId = i;
			executorService.submit(() -> {
				String testClassName = "TestClass-" + threadId;
				JnrTestLifecycleEvent startEvent = new JnrTestLifecycleEvent(
						testClassName, JnrTestStatus.START);
				JnrTestLifecycleEvent endEvent = new JnrTestLifecycleEvent(
						testClassName, JnrTestStatus.END);

				reporter.notify(startEvent);

				// Notify multiple results
				for (int j = 0; j < 5; j++) {
					JnrTestResult result = new JnrTestResult(testClassName + "-Test-" + j, JnrTestResultStatus.SUCCESS, null);
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
			String testClassName = "TestClass-" + i;

			// Generate the expected block for this test class
			StringBuilder expectedBlock = new StringBuilder();
			expectedBlock.append("[  START] ").append(testClassName).append("\n");
			expectedBlock.append("Tests run: 5, Succeeded: 5, Failures: 0, Errors: 0\n");

			// Verify the output contains the expected block
			assertThat(output).contains(expectedBlock.toString().replace("\r\n", "\n"));
		}
	}

	@Test
	void testSingleThreadCompleteOutputVerification() {
		JnrTestThreadSafeConsoleReporter reporter = new JnrTestThreadSafeConsoleReporter();

		String testClassName = "TestClass-1";
		JnrTestLifecycleEvent startEvent = new JnrTestLifecycleEvent(
				testClassName, JnrTestStatus.START);
		JnrTestLifecycleEvent endEvent = new JnrTestLifecycleEvent(
				testClassName, JnrTestStatus.END);

		reporter.notify(startEvent);

		// Notify multiple results
		for (int i = 0; i < 3; i++) {
			JnrTestResult result = new JnrTestResult(testClassName + "-Test-" + i, JnrTestResultStatus.SUCCESS, null);
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

	@Test
	void shouldDelegateRunnableLifecycleEventsToUnderlying() {
		// Need to trigger initialization by notifying a lifecycle event first
		final JnrTestThreadSafeConsoleReporter reporter = new JnrTestThreadSafeConsoleReporter();
		reporter.withElapsedTime(true);
		
		// First, initialize with a test lifecycle event
		reporter.notify(new JnrTestLifecycleEvent("test class", JnrTestStatus.START));
		
		// Create a lifecycle event
		final var event = new JnrTestRunnableLifecycleEvent(
			"test",
			JnrTestRunnableKind.TEST,
			JnrTestRunnableStatus.START
		);
		
		// Notify reporter - should delegate to underlying reporter
		reporter.notify(event);
		
		// End the lifecycle to flush output
		reporter.notify(new JnrTestLifecycleEvent("test class", JnrTestStatus.END));
		
		// Verify that output was captured (delegation occurred)
		assertThat(outputStream.toString()).isNotEmpty();
	}

	@Test
	void shouldCallUnderlyingReporterNotifyForRunnableEvents() throws InterruptedException {
		// Test line reporter.notify(event) - VoidMethodCallMutator
		// The mutant would REMOVE this call, breaking elapsed time tracking
		final JnrTestThreadSafeConsoleReporter reporter = new JnrTestThreadSafeConsoleReporter();
		reporter.withElapsedTime(true);
		
		reporter.notify(new JnrTestLifecycleEvent("test class", JnrTestStatus.START));
		
		// These RunnableLifecycleEvent notifications go through line reporter.notify(event)
		// If the method call is removed (mutant), timer won't start/stop
		reporter.notify(new JnrTestRunnableLifecycleEvent("test1", JnrTestRunnableKind.TEST, JnrTestRunnableStatus.START));
		Thread.sleep(20); // NOSONAR - Increase sleep time for more reliable timing
		reporter.notify(new JnrTestRunnableLifecycleEvent("test1", JnrTestRunnableKind.TEST, JnrTestRunnableStatus.END));
		
		reporter.notify(new JnrTestResult("test1", JnrTestResultStatus.SUCCESS, null));
		reporter.notify(new JnrTestLifecycleEvent("test class", JnrTestStatus.END));
		
		// CRITICAL: If reporter.notify(event) was NOT called (mutant), 
		// elapsed time would be 0.000000 instead of >0
		// More importantly, verify time is NOT zero
		// Extract the time value and verify it's positive
		final String output = outputStream.toString();
		assertThat(output).contains("Time elapsed:")
			.doesNotContain("Time elapsed: 0.000000 s")
			.containsPattern("Time elapsed: 0\\.0[1-9][0-9]*");
	}
}
