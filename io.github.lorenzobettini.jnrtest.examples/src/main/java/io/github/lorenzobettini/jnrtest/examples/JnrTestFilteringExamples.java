package io.github.lorenzobettini.jnrtest.examples;

import io.github.lorenzobettini.jnrtest.core.JnrTest;
import io.github.lorenzobettini.jnrtest.core.JnrTestConsoleExecutor;
import io.github.lorenzobettini.jnrtest.core.JnrTestFilters;

/**
 * Examples of using filters in JnrTest.
 * 
 * <p>
 * This class demonstrates how to use various filtering capabilities
 * to selectively run tests based on test class description or
 * test specification description.
 * </p>
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestFilteringExamples {

	/**
	 * A simple test class for demonstration purposes.
	 */
	public static class CalculatorTest extends JnrTest {
		
		public CalculatorTest() {
			super("Calculator Test Suite");
		}

		@Override
		protected void specify() {
			test("Addition Test", () -> {
				System.out.println("Running addition test");
			});
			
			test("Subtraction Test", () -> {
				System.out.println("Running subtraction test");
			});
			
			test("Critical: Division Test", () -> {
				System.out.println("Running division test");
			});
		}
	}
	
	/**
	 * Another test class for demonstration purposes.
	 */
	public static class StringUtilsTest extends JnrTest {
		
		public StringUtilsTest() {
			super("String Utils Test Suite");
		}

		@Override
		protected void specify() {
			test("Trim Test", () -> {
				System.out.println("Running trim test");
			});
			
			test("Critical: Format Test", () -> {
				System.out.println("Running format test");
			});
		}
	}
	
	/**
	 * Main method that demonstrates various filtering capabilities.
	 */
	public static void main(String[] args) {
		// Create test classes
		var calculatorTests = new CalculatorTest();
		var stringUtilsTests = new StringUtilsTest();
		
		System.out.println("\n=== Running all tests (no filter) ===");
		new JnrTestConsoleExecutor()
			.add(calculatorTests)
			.add(stringUtilsTests)
			.execute();
		
		System.out.println("\n=== Filtering by test class description ===");
		new JnrTestConsoleExecutor()
			.add(calculatorTests)
			.add(stringUtilsTests)
			.filterByTestClassDescription("Calculator.*")
			.execute();
		
		System.out.println("\n=== Filtering by test specification description ===");
		new JnrTestConsoleExecutor()
			.add(calculatorTests)
			.add(stringUtilsTests)
			.filterByTestSpecificationDescription(".*Subtraction.*")
			.execute();
		
		System.out.println("\n=== Filtering by combined conditions (AND) ===");
		new JnrTestConsoleExecutor()
			.add(calculatorTests)
			.add(stringUtilsTests)
			.filter(JnrTestFilters.all(
				JnrTestFilters.byTestClassDescription("String.*"),
				JnrTestFilters.byTestSpecificationDescription("Critical.*")
			))
			.execute();
		
		System.out.println("\n=== Filtering by combined conditions (OR) ===");
		new JnrTestConsoleExecutor()
			.add(calculatorTests)
			.add(stringUtilsTests)
			.filter(JnrTestFilters.any(
				JnrTestFilters.byTestClassDescription("Calculator.*"),
				JnrTestFilters.byTestSpecificationDescription(".*Format.*")
			))
			.execute();
		
		System.out.println("\n=== Using negation in filters ===");
		new JnrTestConsoleExecutor()
			.add(calculatorTests)
			.add(stringUtilsTests)
			.filter(JnrTestFilters.not(
				JnrTestFilters.byTestSpecificationDescription("Critical.*")
			))
			.execute();
		
		System.out.println("\n=== Creating a custom filter ===");
		new JnrTestConsoleExecutor()
			.add(calculatorTests)
			.add(stringUtilsTests)
			.filter((testClass, runnable) -> 
				// Custom logic to determine which tests to run
				!runnable.description().contains("Division") && 
				!testClass.getDescription().contains("String")
			)
			.execute();
	}
}
