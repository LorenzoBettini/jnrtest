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
 * Tests for {@link JnrTestConsoleExecutor}.
 * 
 * @author Lorenzo Bettini
 */
class JnrTestConsoleExecutorTest {

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
	void shouldAddTestClassesCorrectly() {
		// Create a mock test class
		JnrTest testCase = mock(JnrTest.class);
		when(testCase.getDescription()).thenReturn("Mock Test Case");
		
		// Create executor and add test class
		JnrTestConsoleExecutor executor = new JnrTestConsoleExecutor();
		JnrTestConsoleExecutor result = executor.add(testCase);
		
		// Verify the test class was added and the executor returned itself
		assertThat(result).isSameAs(executor);
	}
	
	@Test
	@DisplayName("should add listeners correctly")
	void shouldAddListenersCorrectly() {
		// Create a mock listener
		JnrTestListener listener = mock(JnrTestListener.class);
		
		// Create executor and add listener
		JnrTestConsoleExecutor executor = new JnrTestConsoleExecutor();
		JnrTestConsoleExecutor result = executor.testListener(listener);
		
		// Verify the listener was added and the executor returned itself
		assertThat(result).isSameAs(executor);
	}
	
	@Test
	@DisplayName("should execute tests without throwing when all tests pass")
	void shouldExecuteWithoutThrowingWhenAllTestsPass() {
		// Create a test class that passes
		JnrTest passingTestCase = new JnrTest("Passing Test Case") {
			@Override
			protected void specify() {
				test("passing test", () -> {
					// Test passes
				});
			}
		};
		
		// Create executor and add passing test
		JnrTestConsoleExecutor executor = new JnrTestConsoleExecutor();
		executor.add(passingTestCase);
		
		// Execute without throwing
		boolean result = executor.executeWithoutThrowing();
		
		// Verify result
		assertTrue(result);
		assertThat(outContent.toString()).contains("Results:");
	}
	
	@Test
	@DisplayName("should throw exception when execute fails")
	void shouldThrowExceptionWhenExecuteFails() {
		// Create a test class that fails
		JnrTest failingTestCase = new JnrTest("Failing Test Case") {
			@Override
			protected void specify() {
				test("failing test", () -> {
					throw new AssertionError("Test failure");
				});
			}
		};
		
		// Create executor and add failing test
		JnrTestConsoleExecutor executor = new JnrTestConsoleExecutor();
		executor.add(failingTestCase);
		
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
		// Create a test class that fails
		JnrTest failingTestCase = new JnrTest("Failing Test Case") {
			@Override
			protected void specify() {
				test("failing test", () -> {
					throw new AssertionError("Test failure");
				});
			}
		};
		
		// Create executor and add failing test
		JnrTestConsoleExecutor executor = new JnrTestConsoleExecutor();
		executor.add(failingTestCase);
		
		// Execute without throwing
		boolean result = executor.executeWithoutThrowing();
		
		// Verify result
		assertThat(result).isFalse();
	}
}