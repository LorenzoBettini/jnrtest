package io.github.lorenzobettini.jnrtest.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link JnrTestConsoleParallelExecutor}.
 * 
 * author Lorenzo Bettini
 */
class JnrTestConsoleParallelExecutorTest {

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
	@DisplayName("should add test classes correctly")
	void shouldAddTestCasesCorrectly() {
		// Create a mock test case
		JnrTest testCase = mock(JnrTest.class);
		when(testCase.getDescription()).thenReturn("Mock Test Case");
		
		// Create executor and add test case
		JnrTestConsoleParallelExecutor executor = new JnrTestConsoleParallelExecutor();
		JnrTestConsoleParallelExecutor result = executor.testCase(testCase);
		
		// Verify the test case was added and the executor returned itself
		assertThat(result).isSameAs(executor);
	}
	
	@Test
	@DisplayName("should add listeners correctly")
	void shouldAddListenersCorrectly() {
		// Create a mock listener
		JnrTestListener listener = mock(JnrTestListener.class);
		
		// Create executor and add listener
		JnrTestConsoleParallelExecutor executor = new JnrTestConsoleParallelExecutor();
		JnrTestConsoleParallelExecutor result = executor.testListener(listener);
		
		// Verify the listener was added and the executor returned itself
		assertThat(result).isSameAs(executor);
	}
	
	@Test
	@DisplayName("should execute tests without throwing when all tests pass")
	void shouldExecuteWithoutThrowingWhenAllTestsPass() {
		// Create a test case that passes
		JnrTest passingTestCase = new JnrTest("Passing Test Case") {
			@Override
			protected void specify() {
				test("passing test", () -> {
					// Test passes
				});
			}
		};
		
		// Create executor and add passing test
		JnrTestConsoleParallelExecutor executor = new JnrTestConsoleParallelExecutor();
		executor.testCase(passingTestCase);
		
		// Execute without throwing
		boolean result = executor.executeWithoutThrowing();
		
		// Verify result
		assertTrue(result);
		assertThat(outContent.toString()).contains("Results:");
	}
	
	@Test
	@DisplayName("should throw exception when execute fails")
	void shouldThrowExceptionWhenExecuteFails() {
		// Create a test case that fails
		JnrTest failingTestCase = new JnrTest("Failing Test Case") {
			@Override
			protected void specify() {
				test("failing test", () -> {
					throw new AssertionError("Test failure");
				});
			}
		};
		
		// Create executor and add failing test
		JnrTestConsoleParallelExecutor executor = new JnrTestConsoleParallelExecutor();
		executor.testCase(failingTestCase);
		
		// Execute and expect exception
		Exception exception = assertThrows(RuntimeException.class, () -> {
			executor.execute();
		});
		
		// Verify exception message
		assertEquals("There are test failures", exception.getMessage());
		assertThat(outContent.toString()).contains("Results:");
	}
	
	@Test
	@DisplayName("should return false when executeWithoutThrowing fails")
	void shouldReturnFalseWhenExecuteWithoutThrowingFails() {
		// Create a test case that fails
		JnrTest failingTestCase = new JnrTest("Failing Test Case") {
			@Override
			protected void specify() {
				test("failing test", () -> {
					throw new AssertionError("Test failure");
				});
			}
		};
		
		// Create executor and add failing test
		JnrTestConsoleParallelExecutor executor = new JnrTestConsoleParallelExecutor();
		executor.testCase(failingTestCase);
		
		// Execute without throwing
		boolean result = executor.executeWithoutThrowing();
		
		// Verify result
		assertThat(result).isFalse();
	}
	
	@Test
	@DisplayName("should handle multiple test classes in parallel")
	void shouldHandleMultipleTestCasesInParallel() {
		// Create multiple test classes
		JnrTest testCase1 = new JnrTest("Test Case 1") {
			@Override
			protected void specify() {
				test("test 1", () -> {
					// Simulate a short delay to ensure parallel execution
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				});
			}
		};
		
		JnrTest testCase2 = new JnrTest("Test Case 2") {
			@Override
			protected void specify() {
				test("test 2", () -> {
					// Simulate a short delay to ensure parallel execution
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				});
			}
		};
		
		// Create executor and add both test classes
		JnrTestConsoleParallelExecutor executor = new JnrTestConsoleParallelExecutor();
		executor.testCase(testCase1).testCase(testCase2);
		
		// Execute without throwing
		boolean result = executor.executeWithoutThrowing();
		
		// Verify result
		assertTrue(result);
		assertThat(outContent.toString()).contains("Results:");
	}
}