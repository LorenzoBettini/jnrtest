package io.github.lorenzobettini.jnrtest.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JnrTestParallelRunnerTest {

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
	@DisplayName("should run in parallel")
	void shouldReportResults() {
		var testReporter = new JnrTestThreadSafeConsoleReporter();
		var testRecorderWithElapsed = new JnrTestThreadSafeRecorder().withElapsedTime();
		JnrTestRunner runner = new JnrTestParallelRunner()
			.testCase(new JnrTest("a test case with success") {
				@Override
				protected void specify() {
					beforeAll("before all", () -> {});
					afterAll("after all", () -> {});
					test("success test", () -> {
						// success
						// since we record time, let's make sure to have some
						// delay, otherwise the elapsed time might be 0 on fast machines
						Thread.sleep(10); // NOSONAR
					});
					test("error test", () -> {
						throw new Exception("an exception");
					});
				}
			}).testCase(new JnrTest("a test case with failure") {
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
		for (int i = 0; i < 10; i++) {
			String index = "" + i;
			runner.testCase(new JnrTest("a test case " + index) {
				@Override
				protected void specify() {
					beforeAll("before all " + index, () -> {});
					afterAll("after all " + index, () -> {});
					test("success test " + index, () -> {
						// success
					});
					test("error test " + index, () -> {
						throw new Exception("an exception");
					});
				}
			});
		}
		runner.testListener(testReporter);
		runner.testListener(testRecorderWithElapsed);
		runner.execute();
		String out = getOutContent();
		assertThat(out).contains("""
			[  START] a test case with success
			[SUCCESS] success test
			[  ERROR] error test
			Tests run: 2, Succeeded: 1, Failures: 0, Errors: 1
			""");
		assertThat(out).contains("""
			[  START] a test case with failure
			[ FAILED] failed test
			[SUCCESS] success test
			Tests run: 2, Succeeded: 1, Failures: 1, Errors: 0
			""");
		for (int i = 0; i < 10; i++) {
			String index = "" + i;
			var expected = String.format("""
				[  START] a test case %s
				[SUCCESS] success test %s
				[  ERROR] error test %s
				Tests run: 2, Succeeded: 1, Failures: 0, Errors: 1
				""", index, index, index);
			assertThat(out).contains(expected);
		}
		assertThat(getErrContent())
			.contains("an exception", "expected: <true> but was: <false>");
		var aggregatedResults = new JnrTestResultAggregator()
				.aggregate(testRecorderWithElapsed);
		assertThat(aggregatedResults.getSucceeded()).isEqualTo(12);
		assertThat(aggregatedResults.getFailed()).isEqualTo(1);
		assertThat(aggregatedResults.getErrors()).isEqualTo(11);
		assertThat(aggregatedResults.getTotalTime()).isPositive();
		assertThat(aggregatedResults.toString()).contains("Tests run: 24, Succeeded: 12, Failures: 1, Errors: 11");
		assertThat(aggregatedResults.toString()).contains("Time elapsed: ");
		assertThat(aggregatedResults.toString()).contains("s");
	}

	private String getOutContent() {
		return outContent.toString().replace("\r", "");
	}

	private String getErrContent() {
		return errContent.toString().replace("\r", "");
	}

}
