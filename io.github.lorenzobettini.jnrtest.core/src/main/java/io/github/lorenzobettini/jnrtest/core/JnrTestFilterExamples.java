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
	 *     .filterByClassDescription("UserTests.*");
	 * 
	 * runner.execute(); // Only tests in "UserTests" will be executed
	 * }
	 * </pre>
	 */
	public static void filterByClassDescription() {
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
	 *     .filterBySpecificationDescription(".*important.*");
	 * 
	 * runner.execute(); // Only "important test" will be executed
	 * }
	 * </pre>
	 */
	public static void filterBySpecificationDescription() {
		// Documentation only
	}

	/**
	 * Example of combining test class and test specification filters.
	 * 
	 * <pre>
	 * {@code
	 * // Only run tests that satisfy all conditions
	 * JnrTestRunner runner = new JnrTestRunner()
	 *     .add(new JnrTest("UserTests") { ... })
	 *     .add(new JnrTest("ProductTests") { ... });
	 *     
	 * runner.classFilter(JnrTestFilters.byClassDescription("User.*"));
	 * runner.specificationFilter(JnrTestFilters.bySpecificationDescription(".*important.*"));
	 * 
	 * // Only tests from "UserTests" with "important" in their description will be executed
	 * runner.execute();
	 * }
	 * </pre>
	 */
	public static void combineFilters() {
		// Documentation only
	}

	/**
	 * Example of negating a filter.
	 * 
	 * <pre>
	 * {@code
	 * // Run all tests except those matching the negated filter
	 * JnrTestRunner runner = new JnrTestRunner()
	 *     .add(new JnrTest("Tests") { ... });
	 *     
	 * runner.specificationFilter(JnrTestFilters.notSpecification(
	 *     JnrTestFilters.bySpecificationDescription(".*slow.*")
	 * ));
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
	 * // Create a custom class filter that only runs tests with short descriptions
	 * JnrTestClassFilter shortClassDescriptionFilter = testClass -> 
	 *     testClass.getDescription().length() < 20;
	 * 
	 * // Create a custom specification filter that only runs tests with short descriptions
	 * JnrTestSpecificationFilter shortSpecDescriptionFilter = runnableSpecification -> 
	 *     runnableSpecification.description().length() < 20;
	 *     
	 * JnrTestRunner runner = new JnrTestRunner()
	 *     .add(new JnrTest("Tests") { ... })
	 *     .classFilter(shortClassDescriptionFilter)
	 *     .specificationFilter(shortSpecDescriptionFilter);
	 * 
	 * // Only tests with short descriptions will be executed
	 * runner.execute();
	 * }
	 * </pre>
	 */
	public static void customFilter() {
		// Documentation only
	}

	/**
	 * Example of using allClasses to combine multiple class filters with logical AND.
	 * Similar patterns can be applied to specifications using the allSpecifications method.
	 * 
	 * <pre>
	 * {@code
	 * // Run only tests that satisfy ALL class filters
	 * JnrTestRunner runner = new JnrTestRunner()
	 *     .add(new JnrTest("UserAcceptanceTests") { ... })
	 *     .add(new JnrTest("UserIntegrationTests") { ... })
	 *     .add(new JnrTest("ProductTests") { ... });
	 *     
	 * runner.classFilter(JnrTestFilters.allClasses(
	 *     JnrTestFilters.byClassDescription("User.*"), // Must be a user test
	 *     testClass -> !testClass.getDescription().contains("Integration") // Must not be an integration test
	 * ));
	 * 
	 * // Only "UserAcceptanceTests" will be executed, as it satisfies both conditions
	 * runner.execute();
	 * }
	 * </pre>
	 */
	public static void useAllClassesFilter() {
		// Documentation only
	}
	
	/**
	 * Example of using anyClass to combine multiple class filters with logical OR.
	 * Similar patterns can be applied to specifications using the anySpecification method.
	 * 
	 * <pre>
	 * {@code
	 * // Run tests that satisfy ANY of the class filters
	 * JnrTestRunner runner = new JnrTestRunner()
	 *     .add(new JnrTest("UserTests") { ... })
	 *     .add(new JnrTest("ProductTests") { ... })
	 *     .add(new JnrTest("InventoryTests") { ... });
	 *     
	 * runner.classFilter(JnrTestFilters.anyClass(
	 *     JnrTestFilters.byClassDescription("User.*"),
	 *     JnrTestFilters.byClassDescription("Product.*")
	 * ));
	 * 
	 * // Both "UserTests" and "ProductTests" will be executed, but not "InventoryTests"
	 * runner.execute();
	 * }
	 * </pre>
	 */
	public static void useAnyClassFilter() {
		// Documentation only
	}

}
