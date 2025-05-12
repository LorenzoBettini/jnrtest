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
			.add(new JnrTest("a test case with success") {
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
			}).add(new JnrTest("a test case with failure") {
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
			[  START] a test case with success
			[SUCCESS] success test
			[  ERROR] error test
			Tests run: 2, Succeeded: 1, Failures: 0, Errors: 1
			[  START] a test case with failure
			[ FAILED] failed test
			[SUCCESS] success test
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
			.add(new JnrTest("a test case with success") {
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
			}).add(new JnrTest("a test case with failure") {
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

}
