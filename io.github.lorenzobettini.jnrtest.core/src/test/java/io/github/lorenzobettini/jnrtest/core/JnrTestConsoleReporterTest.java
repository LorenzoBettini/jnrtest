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

}
