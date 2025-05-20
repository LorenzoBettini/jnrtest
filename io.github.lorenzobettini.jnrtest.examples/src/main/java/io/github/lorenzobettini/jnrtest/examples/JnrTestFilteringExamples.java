package io.github.lorenzobettini.jnrtest.examples;

import java.util.function.Predicate;

import io.github.lorenzobettini.jnrtest.core.JnrTest;
import io.github.lorenzobettini.jnrtest.core.JnrTestConsoleExecutor;
import io.github.lorenzobettini.jnrtest.core.JnrTestRunnableSpecification;

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
        // Create a filter using a lambda expression
        Predicate<JnrTest> calculatorClassFilter = 
            testClass -> testClass.getDescription().matches("Calculator.*");
        
        new JnrTestConsoleExecutor()
            .add(calculatorTests)
            .add(stringUtilsTests)
            .classFilter(calculatorClassFilter)
            .execute();

        System.out.println("\n=== Filtering by test specification description ===");
        // Create a filter using a lambda expression
        Predicate<JnrTestRunnableSpecification> subtractionSpecFilter = 
            spec -> spec.description().matches(".*Subtraction.*");
            
        new JnrTestConsoleExecutor()
            .add(calculatorTests)
            .add(stringUtilsTests)
            .specificationFilter(subtractionSpecFilter)
            .execute();

        System.out.println("\n=== Filtering by combined conditions (AND) ===");
        // Create filter for String* classes
        Predicate<JnrTest> stringClassFilter = 
            testClass -> testClass.getDescription().matches("String.*");
        
        // Create filter for Critical* specifications
        Predicate<JnrTestRunnableSpecification> criticalSpecFilter = 
            spec -> spec.description().matches("Critical.*");
        
        new JnrTestConsoleExecutor()
            .add(calculatorTests)
            .add(stringUtilsTests)
            .classFilter(stringClassFilter)
            .specificationFilter(criticalSpecFilter)
            .execute();

        System.out.println("\n=== Using multiple class filters with OR logic ===");
        // Create filter for Calculator* classes
        Predicate<JnrTest> calculatorFilter = 
            testClass -> testClass.getDescription().matches("Calculator.*");
        
        // Combine filters using OR
        Predicate<JnrTest> combinedClassFilter = 
            calculatorFilter.or(stringClassFilter);
        
        new JnrTestConsoleExecutor()
            .add(calculatorTests)
            .add(stringUtilsTests)
            .classFilter(combinedClassFilter)
            .execute();

        System.out.println("\n=== Using multiple specification filters with OR logic ===");
        // Create filter for Addition* specifications
        Predicate<JnrTestRunnableSpecification> additionFilter = 
            spec -> spec.description().matches(".*Addition.*");
        
        // Create filter for Format* specifications
        Predicate<JnrTestRunnableSpecification> formatFilter = 
            spec -> spec.description().matches(".*Format.*");
        
        // Combine filters using OR
        Predicate<JnrTestRunnableSpecification> combinedSpecFilter = 
            additionFilter.or(formatFilter);
        
        new JnrTestConsoleExecutor()
            .add(calculatorTests)
            .add(stringUtilsTests)
            .specificationFilter(combinedSpecFilter)
            .execute();

        System.out.println("\n=== Using negation in filters ===");
        // Create filter that negates Critical* specifications 
        Predicate<JnrTestRunnableSpecification> notCriticalFilter = 
            criticalSpecFilter.negate();
        
        new JnrTestConsoleExecutor()
            .add(calculatorTests)
            .add(stringUtilsTests)
            .specificationFilter(notCriticalFilter)
            .execute();

        System.out.println("\n=== Using multiple filters with AND combination ===");
        // Create filter for Calculator* classes (reusing from before)
        
        // Create filter that is NOT Utils* classes
        Predicate<JnrTest> notUtilsFilter = 
            testClass -> !testClass.getDescription().contains("Utils");
        
        // Combine filters using AND (chaining in classFilter adds with AND automatically)
        new JnrTestConsoleExecutor()
            .add(calculatorTests)
            .add(stringUtilsTests)
            .classFilter(calculatorFilter)
            .classFilter(notUtilsFilter)
            .execute();

        System.out.println("\n=== Creating custom filters ===");
        // Custom class filter
        Predicate<JnrTest> customClassFilter = testClass ->
            !testClass.getDescription().contains("String");

        // Custom specification filter
        Predicate<JnrTestRunnableSpecification> customSpecFilter = runnable ->
            !runnable.description().contains("Division");

        new JnrTestConsoleExecutor()
            .add(calculatorTests)
            .add(stringUtilsTests)
            .classFilter(customClassFilter)
            .specificationFilter(customSpecFilter)
            .execute();
    }
}
