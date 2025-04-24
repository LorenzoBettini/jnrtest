package io.github.lorenzobettini.jnrtest.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import io.github.lorenzobettini.jnrtest.core.JnrTestCase;
import io.github.lorenzobettini.jnrtest.core.JnrTestRunner;
import io.github.lorenzobettini.jnrtest.core.JnrTestStandardReporter;

/**
 * Note that since we redirect output, when running this tests, we will not
 * see the results on the console.
 * 
 * @author Lorenzo Bettini
 *
 */
public class JnrTestStandardReporterTestCase extends JnrTestCase {

	private ByteArrayOutputStream outContent;
	private ByteArrayOutputStream errContent;
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;

	public JnrTestStandardReporterTestCase() {
		super("tests for JnrTestStandardReporter with jnrtest");
	}

	@Override
	protected void specify() {
		beforeEach("setup streams", () -> {
			outContent = new ByteArrayOutputStream();
			errContent = new ByteArrayOutputStream();
			System.setOut(new PrintStream(outContent));
			System.setErr(new PrintStream(errContent));
		});
		afterEach("reset streams", () -> {
			System.setOut(originalOut);
			System.setErr(originalErr);
		});
		test("should report results", () -> {
			var testReporter = new JnrTestStandardReporter();
			JnrTestRunner runner = new JnrTestRunner()
				.testCase(new JnrTestCase("a test case with success") {
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
				}).testCase(new JnrTestCase("a test case with failure") {
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
		});
		test("should report results with elapsed time", () -> {
			var testReporter = new JnrTestStandardReporter();
			JnrTestRunner runner = new JnrTestRunner()
				.testCase(new JnrTestCase("a test case with success") {
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
				}).testCase(new JnrTestCase("a test case with failure") {
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
		});
	}

	private String getOutContent() {
		return outContent.toString().replace("\r", "");
	}

	private String getErrContent() {
		return errContent.toString().replace("\r", "");
	}
}
