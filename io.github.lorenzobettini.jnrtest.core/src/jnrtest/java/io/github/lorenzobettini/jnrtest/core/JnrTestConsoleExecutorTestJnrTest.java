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
 * Tests for {@link JnrTestConsoleExecutor}.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestConsoleExecutorTestJnrTest extends JnrTest {

	public JnrTestConsoleExecutorTestJnrTest() {
		super("JnrTestConsoleExecutorTest in JnrTest");
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
			JnrTestConsoleExecutor executor = new JnrTestConsoleExecutor();
			JnrTestConsoleExecutor result = executor.add(testClass);

			// Verify the test class was added and the executor returned itself
			assertThat(result).isSameAs(executor);
		});
		test("should add listeners correctly", () -> {
			// Create a mock listener
			JnrTestListener listener = mock(JnrTestListener.class);

			// Create executor and add listener
			JnrTestConsoleExecutor executor = new JnrTestConsoleExecutor();
			JnrTestConsoleExecutor result = executor.testListener(listener);

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
			JnrTestConsoleExecutor executor = new JnrTestConsoleExecutor();
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
			JnrTestConsoleExecutor executor = new JnrTestConsoleExecutor();
			executor.add(passingTestClass);

			executor.execute();

			// Verify result
			assertThat(outContent.toString()).contains("[SUCCESS] passing test")
					.contains("Tests run: 1, Succeeded: 1, Failures: 0, Errors: 0");
		});
		test("should show only reports", () -> {
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
			JnrTestConsoleExecutor executor = new JnrTestConsoleExecutor();
			executor.getRecorder().withElapsedTime();
			executor.getReporter().withOnlySummaries();
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
			JnrTestConsoleExecutor executor = new JnrTestConsoleExecutor();
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
			JnrTestConsoleExecutor executor = new JnrTestConsoleExecutor();
			executor.add(failingTestClass);

			// Execute without throwing
			boolean result = executor.executeWithoutThrowing();

			// Verify result
			assertThat(result).isFalse();
		});
		test("should filter tests correctly", () -> {
			// Create test classes with different tests
			JnrTestConsoleExecutor executor = new JnrTestConsoleExecutor();

			// Add a simple test class
			boolean[] methodsCalled = new boolean[4];
			executor.add(new JnrTest("FirstTestClass") {
				@Override
				protected void specify() {
					test("regular test", () -> {
						methodsCalled[0] = true;
					});
					test("important test", () -> {
						methodsCalled[1] = true;
					});
				}
			});

			executor.add(new JnrTest("SecondTestClass") {
				@Override
				protected void specify() {
					test("another test", () -> {
						methodsCalled[2] = true;
					});
					test("important test 2", () -> {
						methodsCalled[3] = true;
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
		test("should apply class filter correctly", () -> {
			// Create test classes for filtering
			JnrTestConsoleExecutor executor = new JnrTestConsoleExecutor();

			boolean[] methodsCalled = new boolean[4];
			executor.add(new JnrTest("CalculatorTest") {
				@Override
				protected void specify() {
					test("addition test", () -> {
						methodsCalled[0] = true;
					});
					test("subtraction test", () -> {
						methodsCalled[1] = true;
					});
				}
			});

			executor.add(new JnrTest("StringUtilsTest") {
				@Override
				protected void specify() {
					test("trim test", () -> {
						methodsCalled[2] = true;
					});
					test("format test", () -> {
						methodsCalled[3] = true;
					});
				}
			});

			// Apply a class filter that only accepts Calculator tests
			executor.classFilter(testClass -> testClass.getDescription().contains("Calculator"));

			// Execute tests
			boolean result = executor.executeWithoutThrowing();

			// Verify only Calculator tests were executed
			assertTrue(result);
			assertThat(methodsCalled[0]).isTrue(); // Calculator addition
			assertThat(methodsCalled[1]).isTrue(); // Calculator subtraction
			assertThat(methodsCalled[2]).isFalse(); // StringUtils trim (should be filtered out)
			assertThat(methodsCalled[3]).isFalse(); // StringUtils format (should be filtered out)
		});
		test("should apply specification filter correctly", () -> {
			// Create test classes for filtering
			JnrTestConsoleExecutor executor = new JnrTestConsoleExecutor();

			boolean[] methodsCalled = new boolean[4];
			executor.add(new JnrTest("TestClass") {
				@Override
				protected void specify() {
					test("critical operation", () -> {
						methodsCalled[0] = true;
					});
					test("normal operation", () -> {
						methodsCalled[1] = true;
					});
					test("critical validation", () -> {
						methodsCalled[2] = true;
					});
					test("simple check", () -> {
						methodsCalled[3] = true;
					});
				}
			});

			// Apply a specification filter that only accepts critical tests
			executor.specificationFilter(spec -> spec.description().contains("critical"));

			// Execute tests
			boolean result = executor.executeWithoutThrowing();

			// Verify only critical tests were executed
			assertTrue(result);
			assertThat(methodsCalled[0]).isTrue(); // critical operation
			assertThat(methodsCalled[1]).isFalse(); // normal operation (filtered out)
			assertThat(methodsCalled[2]).isTrue(); // critical validation
			assertThat(methodsCalled[3]).isFalse(); // simple check (filtered out)
		});
		test("should apply multiple filters with AND logic", () -> {
			// Create test classes for filtering
			JnrTestConsoleExecutor executor = new JnrTestConsoleExecutor();

			boolean[] methodsCalled = new boolean[6];
			executor.add(new JnrTest("CalculatorTest") {
				@Override
				protected void specify() {
					test("critical addition", () -> {
						methodsCalled[0] = true;
					});
					test("normal addition", () -> {
						methodsCalled[1] = true;
					});
				}
			});

			executor.add(new JnrTest("StringUtilsTest") {
				@Override
				protected void specify() {
					test("critical trim", () -> {
						methodsCalled[2] = true;
					});
					test("normal format", () -> {
						methodsCalled[3] = true;
					});
				}
			});

			executor.add(new JnrTest("MathTest") {
				@Override
				protected void specify() {
					test("critical calculation", () -> {
						methodsCalled[4] = true;
					});
					test("normal computation", () -> {
						methodsCalled[5] = true;
					});
				}
			});

			// Apply both class and specification filters (AND logic)
			executor.classFilter(testClass -> testClass.getDescription().contains("Test"))
					.specificationFilter(spec -> spec.description().contains("critical"));

			// Execute tests
			boolean result = executor.executeWithoutThrowing();

			// Verify only tests that match both filters were executed
			assertTrue(result);
			assertThat(methodsCalled[0]).isTrue(); // CalculatorTest + critical addition
			assertThat(methodsCalled[1]).isFalse(); // CalculatorTest + normal addition (spec filtered out)
			assertThat(methodsCalled[2]).isTrue(); // StringUtilsTest + critical trim
			assertThat(methodsCalled[3]).isFalse(); // StringUtilsTest + normal format (spec filtered out)
			assertThat(methodsCalled[4]).isTrue(); // MathTest + critical calculation
			assertThat(methodsCalled[5]).isFalse(); // MathTest + normal computation (spec filtered out)
		});
		test("should apply class description filter correctly", () -> {
			// Create test classes for filtering
			JnrTestConsoleExecutor executor = new JnrTestConsoleExecutor();

			boolean[] methodsCalled = new boolean[4];
			executor.add(new JnrTest("CalculatorService") {
				@Override
				protected void specify() {
					test("addition test", () -> {
						methodsCalled[0] = true;
					});
					test("multiplication test", () -> {
						methodsCalled[1] = true;
					});
				}
			});

			executor.add(new JnrTest("DatabaseManager") {
				@Override
				protected void specify() {
					test("connect test", () -> {
						methodsCalled[2] = true;
					});
					test("query test", () -> {
						methodsCalled[3] = true;
					});
				}
			});

			// Apply a class description filter using regex pattern
			executor.filterByClassDescription("Calculator.*");

			// Execute tests
			boolean result = executor.executeWithoutThrowing();

			// Verify only Calculator tests were executed
			assertTrue(result);
			assertThat(methodsCalled[0]).isTrue(); // Calculator addition
			assertThat(methodsCalled[1]).isTrue(); // Calculator multiplication
			assertThat(methodsCalled[2]).isFalse(); // Database connect (filtered out)
			assertThat(methodsCalled[3]).isFalse(); // Database query (filtered out)
		});
		test("filterByClassDescription should return this for chaining", () -> {
			final var executor = new JnrTestConsoleExecutor();
			final var result = executor.filterByClassDescription(".*");
			assertThat(result).isSameAs(executor);
		});
		test("filterBySpecificationDescription should return this for chaining", () -> {
			final var executor = new JnrTestConsoleExecutor();
			final var result = executor.filterBySpecificationDescription(".*");
			assertThat(result).isSameAs(executor);
		});
		test("specificationFilter should return this for chaining", () -> {
			final var executor = new JnrTestConsoleExecutor();
			final var result = executor.specificationFilter(spec -> true);
			assertThat(result).isSameAs(executor);
		});
	}

	private ByteArrayOutputStream outContent;
	private ByteArrayOutputStream errContent;
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;
}
