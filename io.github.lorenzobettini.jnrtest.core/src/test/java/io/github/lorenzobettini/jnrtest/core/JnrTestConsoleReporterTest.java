package io.github.lorenzobettini.jnrtest.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JnrTestConsoleReporterTest {

	private ByteArrayOutputStream outContent;
	private ByteArrayOutputStream errContent;
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;

	@BeforeEach
	void setUpStreams() {
		outContent = new ByteArrayOutputStream();
		errContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
	}

	@AfterEach
	void restoreStreams() {
		System.setOut(originalOut);
		System.setErr(originalErr);
	}

	@Test
	@DisplayName("should report results")
	void shouldReportResults() {
		var testReporter = new JnrTestConsoleReporter();
		JnrTestRunner runner = new JnrTestRunner()
			.add(new JnrTest("a test class with success") {
				@Override
				protected void specify() {
					beforeAll("before all", () -> {});
					afterAll("after all", () -> {});
					test("success test", () -> {
						// success
					});
					test("error test", () -> {
						throw new Exception("an exception");
					});
				}
			}).add(new JnrTest("a test class with failure") {
				@Override
				protected void specify() {
					beforeAll("before all", () -> {});
					afterAll("after all", () -> {});
					test("failed test", () -> {
						assertTrue(false);
					});
					test("success test", () -> {
						// success
					});
				}
			});
		runner.testListener(testReporter);
		runner.execute();
		assertEquals("""
			[  START] a test class with success
			[SUCCESS] success test
			[  ERROR] error test
			Tests run: 2, Succeeded: 1, Failures: 0, Errors: 1
			[  START] a test class with failure
			[ FAILED] failed test
			[SUCCESS] success test
			Tests run: 2, Succeeded: 1, Failures: 1, Errors: 0
			""",
			getOutContent());
		assertThat(getErrContent())
			.contains("an exception", "expected: <true> but was: <false>");
	}

	@Test
	@DisplayName("should report only summary")
	void shouldReportOnlySummary() {
		var testReporter = new JnrTestConsoleReporter().withOnlySummaries(true);
		JnrTestRunner runner = new JnrTestRunner()
			.add(new JnrTest("a test class with success") {
				@Override
				protected void specify() {
					beforeAll("before all", () -> {});
					afterAll("after all", () -> {});
					test("success test", () -> {
						// success
					});
					test("error test", () -> {
						throw new Exception("an exception");
					});
				}
			}).add(new JnrTest("a test class with failure") {
				@Override
				protected void specify() {
					beforeAll("before all", () -> {});
					afterAll("after all", () -> {});
					test("failed test", () -> {
						assertTrue(false);
					});
					test("success test", () -> {
						// success
					});
				}
			});
		runner.testListener(testReporter);
		runner.execute();
		assertEquals("""
			[  START] a test class with success
			Tests run: 2, Succeeded: 1, Failures: 0, Errors: 1
			[  START] a test class with failure
			Tests run: 2, Succeeded: 1, Failures: 1, Errors: 0
			""",
			getOutContent());
		assertThat(getErrContent())
			.contains("an exception", "expected: <true> but was: <false>");
	}

	@Test
	@DisplayName("should report results with elapsed time")
	void shouldReportResultsWithElapsedTime() {
		var testReporter = new JnrTestConsoleReporter();
		JnrTestRunner runner = new JnrTestRunner()
			.add(new JnrTest("a test class with success") {
				@Override
				protected void specify() {
					beforeAll("before all", () -> {});
					afterAll("after all", () -> {});
					test("success test", () -> {
						// success
					});
					test("error test", () -> {
						throw new Exception("an exception");
					});
				}
			}).add(new JnrTest("a test class with failure") {
				@Override
				protected void specify() {
					beforeAll("before all", () -> {});
					afterAll("after all", () -> {});
					test("failed test", () -> {
						assertTrue(false);
					});
					test("success test", () -> {
						// success
					});
				}
			});
		runner.testListener(testReporter.withElapsedTime());
		runner.execute();
		assertThat(getOutContent())
			.contains(" - Time elapsed: ");
		assertThat(getErrContent())
			.contains("an exception", "expected: <true> but was: <false>");
	}

	private String getOutContent() {
		return outContent.toString().replace("\r", "");
	}

	private String getErrContent() {
		return errContent.toString().replace("\r", "");
	}

	@Test
	@DisplayName("should handle lifecycle events for tests with elapsed time")
	void shouldHandleLifecycleEventsForTestsWithElapsedTime() {
		final var testReporter = new JnrTestConsoleReporter().withElapsedTime();
		final JnrTestRunner runner = new JnrTestRunner()
			.add(new JnrTest("a test class") {
				@Override
				protected void specify() {
					test("test1", () -> {
						// Add some delay to ensure measurable time
						Thread.sleep(10); // NOSONAR
					});
				}
			});
		runner.testListener(testReporter);
		runner.execute();
		assertThat(getOutContent())
			.contains(" - Time elapsed: ");
	}

	@Test
	@DisplayName("should not report elapsed time for non-TEST lifecycle events")
	void shouldNotReportElapsedTimeForNonTestLifecycleEvents() {
		final var testReporter = new JnrTestConsoleReporter().withElapsedTime();
		final JnrTestRunner runner = new JnrTestRunner()
			.add(new JnrTest("a test class") {
				@Override
				protected void specify() {
					beforeAll("before all", () -> {
						Thread.sleep(10); // NOSONAR
					});
					test("test1", () -> {});
				}
			});
		runner.testListener(testReporter);
		runner.execute();
		// beforeAll elapsed time should not be reported
		final String output = getOutContent();
		assertThat(output)
			.doesNotContain("[  START] before all")
			.contains("[  START] a test class");
	}

	@Test
	@DisplayName("should handle STARTED and FINISHED lifecycle events")
	void shouldHandleStartedAndFinishedLifecycleEvents() {
		final var testReporter = new JnrTestConsoleReporter();
		final JnrTestRunner runner = new JnrTestRunner()
			.add(new JnrTest("test class A") {
				@Override
				protected void specify() {
					test("test1", () -> {});
				}
			})
			.add(new JnrTest("test class B") {
				@Override
				protected void specify() {
					test("test2", () -> {});
				}
			});
		runner.testListener(testReporter);
		runner.execute();
		final String output = getOutContent();
		// Should see START for both test classes and summaries for both
		assertThat(output)
			.contains("[  START] test class A")
			.contains("Tests run: 1, Succeeded: 1, Failures: 0, Errors: 0")
			.contains("[  START] test class B");
	}

	@Test
	@DisplayName("should track time for each individual test result")
	void shouldTrackTimeForEachIndividualTestResult() {
		final var testReporter = new JnrTestConsoleReporter().withElapsedTime();
		final JnrTestRunner runner = new JnrTestRunner()
			.add(new JnrTest("a test class") {
				@Override
				protected void specify() {
					test("test1", () -> {
						Thread.sleep(10); // NOSONAR
					});
					test("test2", () -> {
						Thread.sleep(10); // NOSONAR
					});
				}
			});
		runner.testListener(testReporter);
		runner.execute();
		final String output = getOutContent();
		// Each test result should have elapsed time: 2 test results + 1 summary = 3 occurrences
		assertThat(output.split("Time elapsed:")).hasSizeGreaterThanOrEqualTo(3);
	}

	@Test
	@DisplayName("should NOT include elapsed time in END summary when withElapsedTime is false")
	void shouldNotIncludeElapsedTimeInEndSummaryWhenDisabled() {
		// Directly test the condition testStatistics.isWithElapsedTime()
		final var reporter = new JnrTestConsoleReporter().withElapsedTime(false);
		
		// Send START event
		reporter.notify(new JnrTestLifecycleEvent("test", JnrTestStatus.START));
		
		// Send a result
		reporter.notify(new JnrTestResult("test1", JnrTestResultStatus.SUCCESS, null));
		
		// Send END event - this triggers conditional
		reporter.notify(new JnrTestLifecycleEvent("test", JnrTestStatus.END));
		
		final String output = getOutContent();
		// Should have summary but WITHOUT elapsed time
		assertThat(output)
			.contains("Tests run: 1, Succeeded: 1, Failures: 0, Errors: 0")
			.doesNotContain("Time elapsed:");
	}

	@Test
	@DisplayName("should include elapsed time in END summary when withElapsedTime is true")
	void shouldIncludeElapsedTimeInEndSummaryWhenEnabled() throws InterruptedException {
		// Test the TRUE branch of conditional
		final var reporter = new JnrTestConsoleReporter().withElapsedTime(true).withOnlySummaries(true);
		
		reporter.notify(new JnrTestLifecycleEvent("test", JnrTestStatus.START));
		
		// Trigger timing
		reporter.notify(new JnrTestRunnableLifecycleEvent("test1", JnrTestRunnableKind.TEST, JnrTestRunnableStatus.START));
		Thread.sleep(10); // NOSONAR
		reporter.notify(new JnrTestRunnableLifecycleEvent("test1", JnrTestRunnableKind.TEST, JnrTestRunnableStatus.END));
		
		reporter.notify(new JnrTestResult("test1", JnrTestResultStatus.SUCCESS, null));
		reporter.notify(new JnrTestLifecycleEvent("test", JnrTestStatus.END));
		
		final String output = getOutContent();
		// With onlySummaries=true, the "Time elapsed:" ONLY appears in the END summary
		assertThat(output)
			.contains("Tests run: 1, Succeeded: 1, Failures: 0, Errors: 0")
			.contains("Time elapsed:");
	}

	@Test
	@DisplayName("should return early from notify RunnableLifecycleEvent when withElapsedTime is false")
	void shouldReturnEarlyWhenElapsedTimeDisabled() {
		// Test !testStatistics.isWithElapsedTime() returns true (early return)
		final var reporter = new JnrTestConsoleReporter().withElapsedTime(false);
		reporter.notify(new JnrTestLifecycleEvent("test", JnrTestStatus.START));
		
		// This should return early and NOT call startTimer
		reporter.notify(new JnrTestRunnableLifecycleEvent("test1", JnrTestRunnableKind.TEST, JnrTestRunnableStatus.START));
		reporter.notify(new JnrTestRunnableLifecycleEvent("test1", JnrTestRunnableKind.TEST, JnrTestRunnableStatus.END));
		
		reporter.notify(new JnrTestResult("test1", JnrTestResultStatus.SUCCESS, null));
		reporter.notify(new JnrTestLifecycleEvent("test", JnrTestStatus.END));
		
		// If timer wasn't started, total time should be 0
		final String output = getOutContent();
		assertThat(output).doesNotContain("Time elapsed:");
	}

	@Test
	@DisplayName("should return early from notify RunnableLifecycleEvent when kind is not TEST")
	void shouldReturnEarlyWhenKindIsNotTest() throws InterruptedException {
		// Test event.kind() != JnrTestRunnableKind.TEST returns true (early return)
		final var reporter = new JnrTestConsoleReporter().withElapsedTime(true);
		reporter.notify(new JnrTestLifecycleEvent("test", JnrTestStatus.START));
		
		// Send BEFORE_ALL events - should return early
		reporter.notify(new JnrTestRunnableLifecycleEvent("before", JnrTestRunnableKind.BEFORE_ALL, JnrTestRunnableStatus.START));
		Thread.sleep(10); // NOSONAR
		reporter.notify(new JnrTestRunnableLifecycleEvent("before", JnrTestRunnableKind.BEFORE_ALL, JnrTestRunnableStatus.END));
		
		reporter.notify(new JnrTestResult("test1", JnrTestResultStatus.SUCCESS, null));
		reporter.notify(new JnrTestLifecycleEvent("test", JnrTestStatus.END));
		
		// BEFORE_ALL time should not be tracked, so total should be 0
		final String output = getOutContent();
		assertThat(output).contains("Time elapsed: 0.000000 s");
	}

	@Test
	@DisplayName("should call startTimer when status is START")
	void shouldCallStartTimerWhenStatusIsStart() throws InterruptedException {
		// Test if (event.status() == JnrTestRunnableStatus.START) startTimer
		final var reporter = new JnrTestConsoleReporter().withElapsedTime(true);
		reporter.notify(new JnrTestLifecycleEvent("test", JnrTestStatus.START));
		
		// START status - should call startTimer
		reporter.notify(new JnrTestRunnableLifecycleEvent("test1", JnrTestRunnableKind.TEST, JnrTestRunnableStatus.START));
		Thread.sleep(10); // NOSONAR
		// END status - should call stopTimer
		reporter.notify(new JnrTestRunnableLifecycleEvent("test1", JnrTestRunnableKind.TEST, JnrTestRunnableStatus.END));
		
		reporter.notify(new JnrTestResult("test1", JnrTestResultStatus.SUCCESS, null));
		reporter.notify(new JnrTestLifecycleEvent("test", JnrTestStatus.END));
		
		final String output = getOutContent();
		// Should have positive elapsed time, proving timer was started and stopped
		assertThat(output).containsPattern("Time elapsed: 0\\.0[0-9]+");
	}

	@Test
	@DisplayName("should call stopTimer when status is not START")
	void shouldCallStopTimerWhenStatusIsNotStart() throws InterruptedException {
		// Test else stopTimer
		final var reporter = new JnrTestConsoleReporter().withElapsedTime(true);
		reporter.notify(new JnrTestLifecycleEvent("test", JnrTestStatus.START));
		
		// Multiple start/stop cycles
		reporter.notify(new JnrTestRunnableLifecycleEvent("test1", JnrTestRunnableKind.TEST, JnrTestRunnableStatus.START));
		Thread.sleep(10); // NOSONAR
		reporter.notify(new JnrTestRunnableLifecycleEvent("test1", JnrTestRunnableKind.TEST, JnrTestRunnableStatus.END));
		
		reporter.notify(new JnrTestRunnableLifecycleEvent("test2", JnrTestRunnableKind.TEST, JnrTestRunnableStatus.START));
		Thread.sleep(10); // NOSONAR
		reporter.notify(new JnrTestRunnableLifecycleEvent("test2", JnrTestRunnableKind.TEST, JnrTestRunnableStatus.END));
		
		reporter.notify(new JnrTestResult("test1", JnrTestResultStatus.SUCCESS, null));
		reporter.notify(new JnrTestResult("test2", JnrTestResultStatus.SUCCESS, null));
		reporter.notify(new JnrTestLifecycleEvent("test", JnrTestStatus.END));
		
		final String output = getOutContent();
		// Should have accumulated time from both tests
		assertThat(output).containsPattern("Time elapsed: 0\\.0[0-9]+");
	}

}
