package io.github.lorenzobettini.jnrtest.examples;

import io.github.lorenzobettini.jnrtest.core.JnrTest;
import io.github.lorenzobettini.jnrtest.core.JnrTestClassFilter;
import io.github.lorenzobettini.jnrtest.core.JnrTestConsoleExecutor;
import io.github.lorenzobettini.jnrtest.core.JnrTestFilters;
import io.github.lorenzobettini.jnrtest.core.JnrTestSpecificationFilter;

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
            .filterByClassDescription("Calculator.*")
            .execute();

        System.out.println("\n=== Filtering by test specification description ===");
        new JnrTestConsoleExecutor()
            .add(calculatorTests)
            .add(stringUtilsTests)
            .filterBySpecificationDescription(".*Subtraction.*")
            .execute();

        System.out.println("\n=== Filtering by combined conditions (AND) ===");
        new JnrTestConsoleExecutor()
            .add(calculatorTests)
            .add(stringUtilsTests)
            .classFilter(JnrTestFilters.byClassDescription("String.*"))
            .specificationFilter(JnrTestFilters.bySpecificationDescription("Critical.*"))
            .execute();

        System.out.println("\n=== Using multiple class filters with OR logic ===");
        new JnrTestConsoleExecutor()
            .add(calculatorTests)
            .add(stringUtilsTests)
            .classFilter(JnrTestFilters.anyClass(
                JnrTestFilters.byClassDescription("Calculator.*"),
                JnrTestFilters.byClassDescription("String.*")
            ))
            .execute();

        System.out.println("\n=== Using multiple specification filters with OR logic ===");
        new JnrTestConsoleExecutor()
            .add(calculatorTests)
            .add(stringUtilsTests)
            .specificationFilter(JnrTestFilters.anySpecification(
                JnrTestFilters.bySpecificationDescription(".*Addition.*"),
                JnrTestFilters.bySpecificationDescription(".*Format.*")
            ))
            .execute();

        System.out.println("\n=== Using negation in filters ===");
        new JnrTestConsoleExecutor()
            .add(calculatorTests)
            .add(stringUtilsTests)
            .specificationFilter(JnrTestFilters.notSpecification(
                JnrTestFilters.bySpecificationDescription("Critical.*")
            ))
            .execute();

        System.out.println("\n=== Using multiple filters with AND combination ===");
        new JnrTestConsoleExecutor()
            .add(calculatorTests)
            .add(stringUtilsTests)
            .classFilter(JnrTestFilters.allClasses(
                JnrTestFilters.byClassDescription("Calculator.*"),
                JnrTestFilters.notClass(JnrTestFilters.byClassDescription(".*Utils.*"))
            ))
            .execute();

        System.out.println("\n=== Creating custom filters ===");
        // Custom class filter
        JnrTestClassFilter customClassFilter = testClass ->
            !testClass.getDescription().contains("String");

        // Custom specification filter
        JnrTestSpecificationFilter customSpecFilter = runnable ->
            !runnable.description().contains("Division");

        new JnrTestConsoleExecutor()
            .add(calculatorTests)
            .add(stringUtilsTests)
            .classFilter(customClassFilter)
            .specificationFilter(customSpecFilter)
            .execute();
    }
}
