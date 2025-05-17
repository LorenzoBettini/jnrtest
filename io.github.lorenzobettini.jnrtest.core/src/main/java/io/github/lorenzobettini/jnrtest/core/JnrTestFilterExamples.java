package io.github.lorenzobettini.jnrtest.core;

/**
 * This class provides documentation and examples of how to use the filtering 
 * capabilities of the JnrTestRunner.
 * 
 * @author Lorenzo Bettini
 */
public final class JnrTestFilterExamples {

	private JnrTestFilterExamples() {
		// Utility class should not be instantiated
	}

	/**
	 * Example of filtering tests by test class description.
	 * 
	 * <pre>
	 * {@code
	 * // Only run tests from classes whose description matches "UserTests.*"
	 * JnrTestRunner runner = new JnrTestRunner()
	 *     .add(new JnrTest("UserTests") { ... })
	 *     .add(new JnrTest("ProductTests") { ... })
	 *     .filterByTestClassDescription("UserTests.*");
	 * 
	 * runner.execute(); // Only tests in "UserTests" will be executed
	 * }
	 * </pre>
	 */
	public static void filterByTestClassDescription() {
		// Documentation only
	}

	/**
	 * Example of filtering tests by test specification description.
	 * 
	 * <pre>
	 * {@code
	 * // Only run tests whose description contains "important"
	 * JnrTestRunner runner = new JnrTestRunner()
	 *     .add(new JnrTest("Tests") {
	 *         @Override
	 *         protected void specify() {
	 *             test("regular test", () -> { ... });
	 *             test("important test", () -> { ... });
	 *         }
	 *     })
	 *     .filterByTestSpecificationDescription(".*important.*");
	 * 
	 * runner.execute(); // Only "important test" will be executed
	 * }
	 * </pre>
	 */
	public static void filterByTestSpecificationDescription() {
		// Documentation only
	}

	/**
	 * Example of combining multiple filters with AND logic.
	 * 
	 * <pre>
	 * {@code
	 * // Only run tests that satisfy all conditions
	 * JnrTestRunner runner = new JnrTestRunner()
	 *     .add(new JnrTest("UserTests") { ... })
	 *     .add(new JnrTest("ProductTests") { ... })
	 *     .filter(JnrTestFilters.all(
	 *         JnrTestFilters.byTestClassDescription("User.*"),
	 *         JnrTestFilters.byTestSpecificationDescription(".*important.*")
	 *     ));
	 * 
	 * // Only tests from "UserTests" with "important" in their description will be executed
	 * runner.execute();
	 * }
	 * </pre>
	 */
	public static void combineFiltersWithAnd() {
		// Documentation only
	}

	/**
	 * Example of combining multiple filters with OR logic.
	 * 
	 * <pre>
	 * {@code
	 * // Run tests that satisfy any of the conditions
	 * JnrTestRunner runner = new JnrTestRunner()
	 *     .add(new JnrTest("UserTests") { ... })
	 *     .add(new JnrTest("ProductTests") { ... })
	 *     .filter(JnrTestFilters.any(
	 *         JnrTestFilters.byTestClassDescription("User.*"),
	 *         JnrTestFilters.byTestSpecificationDescription(".*critical.*")
	 *     ));
	 * 
	 * // Tests from "UserTests" OR tests with "critical" in their description will be executed
	 * runner.execute();
	 * }
	 * </pre>
	 */
	public static void combineFiltersWithOr() {
		// Documentation only
	}

	/**
	 * Example of negating a filter.
	 * 
	 * <pre>
	 * {@code
	 * // Run all tests except those matching the negated filter
	 * JnrTestRunner runner = new JnrTestRunner()
	 *     .add(new JnrTest("Tests") { ... })
	 *     .filter(JnrTestFilters.not(
	 *         JnrTestFilters.byTestSpecificationDescription(".*slow.*")
	 *     ));
	 * 
	 * // All tests except those with "slow" in their description will be executed
	 * runner.execute();
	 * }
	 * </pre>
	 */
	public static void negateFilter() {
		// Documentation only
	}

	/**
	 * Example of creating a custom filter.
	 * 
	 * <pre>
	 * {@code
	 * // Create a custom filter that only runs tests with short descriptions
	 * JnrTestFilter shortDescriptionFilter = (testClass, runnableSpecification) -> 
	 *     runnableSpecification.description().length() < 20;
	 * 
	 * JnrTestRunner runner = new JnrTestRunner()
	 *     .add(new JnrTest("Tests") { ... })
	 *     .filter(shortDescriptionFilter);
	 * 
	 * // Only tests with short descriptions will be executed
	 * runner.execute();
	 * }
	 * </pre>
	 */
	public static void customFilter() {
		// Documentation only
	}
}
