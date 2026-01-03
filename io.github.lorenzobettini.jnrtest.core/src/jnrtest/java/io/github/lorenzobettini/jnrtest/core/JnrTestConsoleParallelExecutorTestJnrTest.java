package io.github.lorenzobettini.jnrtest.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Tests for {@link JnrTestConsoleParallelExecutor}.
 * 
 * author Lorenzo Bettini
 */
public class JnrTestConsoleParallelExecutorTestJnrTest extends JnrTest {

	public JnrTestConsoleParallelExecutorTestJnrTest() {
		super("JnrTestConsoleParallelExecutorTest in JnrTest");
	}

	protected @Override void specify() {
		beforeEach("call setUpStreams", () -> {
			outContent = new ByteArrayOutputStream();
			errContent = new ByteArrayOutputStream();
			System.setOut(new PrintStream(outContent));
			System.setErr(new PrintStream(errContent));
		});
		afterEach("call restoreStreams", () -> {
			System.setOut(originalOut);
			System.setErr(originalErr);
		});
		test("should add test classes correctly", () -> {
			// Create a mock test class
			JnrTest testClass = mock(JnrTest.class);
			when(testClass.getDescription()).thenReturn("Mock Test Class");

			// Create executor and add test class
			var executor = new JnrTestConsoleParallelExecutor();
			var result = executor.add(testClass);

			// Verify the test class was added and the executor returned itself
			assertThat(result).isSameAs(executor);
		});
		test("should add listeners correctly", () -> {
			// Create a mock listener
			JnrTestListener listener = mock(JnrTestListener.class);

			// Create executor and add listener
			var executor = new JnrTestConsoleParallelExecutor();
			var result = executor.testListener(listener);

			// Verify the listener was added and the executor returned itself
			assertThat(result).isSameAs(executor);
		});
		test("should execute tests without throwing when all tests pass", () -> {
			// Create a test class that passes
			JnrTest passingTestClass = new JnrTest("Passing Test Class") {
				@Override
				protected void specify() {
					test("passing test", () -> {
						// Test passes
					});
				}
			};

			// Create executor and add passing test
			JnrTestConsoleParallelExecutor executor = new JnrTestConsoleParallelExecutor();
			executor.add(passingTestClass);

			// Execute without throwing
			boolean result = executor.executeWithoutThrowing();

			// Verify result
			assertTrue(result);
			assertThat(outContent.toString()).contains("Results:");
		});
		test("should show detailed reports", () -> {
			// Create a test class that passes
			JnrTest passingTestClass = new JnrTest("Passing Test Class") {
				@Override
				protected void specify() {
					test("passing test", () -> {
						// Test passes
					});
				}
			};

			// Create executor and add passing test
			JnrTestConsoleParallelExecutor executor = new JnrTestConsoleParallelExecutor();
			executor.add(passingTestClass);

			// Execute without throwing
			boolean result = executor.executeWithoutThrowing();

			// Verify result
			assertTrue(result);
			assertThat(outContent.toString()).contains("[SUCCESS] passing test")
					.contains("Tests run: 1, Succeeded: 1, Failures: 0, Errors: 0");
		});
		test("should show only summaries", () -> {
			// Create a test class that passes
			JnrTest passingTestClass = new JnrTest("Passing Test Class") {
				@Override
				protected void specify() {
					test("passing test", () -> {
						// Test passes
					});
				}
			};

			// Create executor and add passing test
			JnrTestConsoleParallelExecutor executor = new JnrTestConsoleParallelExecutor();
			executor.getRecorder().withElapsedTime();
			executor.getReporter().withOnlySummaries(true);
			executor.add(passingTestClass);

			// Execute without throwing
			boolean result = executor.executeWithoutThrowing();

			// Verify result
			assertTrue(result);
			assertThat(outContent.toString()).doesNotContain("[SUCCESS] passing test")
					.contains("Tests run: 1, Succeeded: 1, Failures: 0, Errors: 0");
		});
		test("should throw exception when execute fails", () -> {
			// Create a test class that fails
			JnrTest failingTestClass = new JnrTest("Failing Test Class") {
				@Override
				protected void specify() {
					test("failing test", () -> {
						throw new AssertionError("Test failure");
					});
				}
			};

			// Create executor and add failing test
			JnrTestConsoleParallelExecutor executor = new JnrTestConsoleParallelExecutor();
			executor.add(failingTestClass);

			// Execute and expect exception
			Exception exception = assertThrows(RuntimeException.class, executor::execute);

			// Verify exception message
			assertEquals("There are test failures", exception.getMessage());
			assertThat(outContent.toString()).contains("Results:");
		});
		test("should return false when executeWithoutThrowing fails", () -> {
			// Create a test class that fails
			JnrTest failingTestClass = new JnrTest("Failing Test Class") {
				@Override
				protected void specify() {
					test("failing test", () -> {
						throw new AssertionError("Test failure");
					});
				}
			};

			// Create executor and add failing test
			JnrTestConsoleParallelExecutor executor = new JnrTestConsoleParallelExecutor();
			executor.add(failingTestClass);

			// Execute without throwing
			boolean result = executor.executeWithoutThrowing();

			// Verify result
			assertThat(result).isFalse();
		});
		test("should handle multiple test classes in parallel", () -> {
			// Create multiple test classes
			JnrTest testClass1 = new JnrTest("Test Class 1") {
				@Override
				protected void specify() {
					test("test 1", () -> {
						// Simulate a short delay to ensure parallel execution
						try {
							Thread.sleep(50); // NOSONAR
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					});
				}
			};

			JnrTest testClass2 = new JnrTest("Test Class 2") {
				@Override
				protected void specify() {
					test("test 2", () -> {
						// Simulate a short delay to ensure parallel execution
						try {
							Thread.sleep(50); // NOSONAR
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					});
				}
			};

			// Create executor and add both test classes
			JnrTestConsoleParallelExecutor executor = new JnrTestConsoleParallelExecutor();
			executor.add(testClass1).add(testClass2);

			// Execute without throwing
			boolean result = executor.executeWithoutThrowing();

			// Verify result
			assertTrue(result);
			assertThat(outContent.toString()).contains("Results:");
		});
		test("should filter tests correctly in parallel mode", () -> {
			// Create test classes with different tests
			JnrTestConsoleParallelExecutor executor = new JnrTestConsoleParallelExecutor();

			// Use a thread-safe collection for test results
			boolean[] methodsCalled = new boolean[4];

			// Add a simple test class
			executor.add(new JnrTest("FirstTestClass") {
				@Override
				protected void specify() {
					test("regular test", () -> {
						synchronized (methodsCalled) {
							methodsCalled[0] = true;
						}
					});
					test("important test", () -> {
						synchronized (methodsCalled) {
							methodsCalled[1] = true;
						}
					});
				}
			});

			executor.add(new JnrTest("SecondTestClass") {
				@Override
				protected void specify() {
					test("another test", () -> {
						synchronized (methodsCalled) {
							methodsCalled[2] = true;
						}
					});
					test("important test 2", () -> {
						synchronized (methodsCalled) {
							methodsCalled[3] = true;
						}
					});
				}
			});

			// Apply a filter for important tests
			executor.filterBySpecificationDescription(".*important.*");

			// Execute tests
			executor.executeWithoutThrowing();

			// Verify only important tests were executed
			assertThat(methodsCalled[0]).isFalse();
			assertThat(methodsCalled[1]).isTrue();
			assertThat(methodsCalled[2]).isFalse();
			assertThat(methodsCalled[3]).isTrue();
		});
	}

	private ByteArrayOutputStream outContent;
	private ByteArrayOutputStream errContent;
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;
}