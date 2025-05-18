package io.github.lorenzobettini.jnrtest.core;

/**
 * This class provides documentation and examples of how to use the filtering 
 * capabilities of the {@link JnrTestRunner}. It demonstrates usage of
 * {@link JnrTestClassFilter} for filtering test classes and 
 * {@link JnrTestSpecificationFilter} for filtering test specifications.
 * 
 * @author Lorenzo Bettini
 */
public final class JnrTestFilterExamples {

	private JnrTestFilterExamples() {
		// Utility class should not be instantiated
	}

	/**
	 * Example of filtering tests by test class description.
	 * Uses {@link JnrTestRunner#filterByClassDescription(String)} method which internally
	 * applies {@link JnrTestFilters#byClassDescription(String)}.
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
	 * Uses {@link JnrTestRunner#filterBySpecificationDescription(String)} method which internally
	 * applies {@link JnrTestFilters#bySpecificationDescription(String)}.
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
	 * Uses {@link JnrTestRunner#classFilter(JnrTestClassFilter)} and 
	 * {@link JnrTestRunner#specificationFilter(JnrTestSpecificationFilter)} methods with filters
	 * created via {@link JnrTestFilters#byClassDescription(String)} and 
	 * {@link JnrTestFilters#bySpecificationDescription(String)}.
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
	 * Uses {@link JnrTestFilters#notSpecification(JnrTestSpecificationFilter)} to negate a filter
	 * created with {@link JnrTestFilters#bySpecificationDescription(String)}.
	 * See also {@link JnrTestFilters#notClass(JnrTestClassFilter)} for negating class filters.
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
	 * Demonstrates implementation of {@link JnrTestClassFilter} and {@link JnrTestSpecificationFilter}
	 * interfaces using lambda expressions, then applying them with 
	 * {@link JnrTestRunner#classFilter(JnrTestClassFilter)} and
	 * {@link JnrTestRunner#specificationFilter(JnrTestSpecificationFilter)}.
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
	 * Uses {@link JnrTestFilters#allClasses(JnrTestClassFilter...)} with 
	 * {@link JnrTestFilters#byClassDescription(String)} and a custom lambda filter.
	 * Similar patterns can be applied to specifications using the 
	 * {@link JnrTestFilters#allSpecifications(JnrTestSpecificationFilter...)} method.
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
	 * Uses {@link JnrTestFilters#anyClass(JnrTestClassFilter...)} with multiple 
	 * {@link JnrTestFilters#byClassDescription(String)} filters.
	 * Similar patterns can be applied to specifications using the 
	 * {@link JnrTestFilters#anySpecification(JnrTestSpecificationFilter...)} method.
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

	/**
	 * Example of using specification filters to combine multiple filter conditions with logical AND.
	 * Uses {@link JnrTestFilters#allSpecifications(JnrTestSpecificationFilter...)} with 
	 * {@link JnrTestFilters#bySpecificationDescription(String)} and a custom lambda filter.
	 * 
	 * <pre>
	 * {@code
	 * // Run only specifications that satisfy ALL filters
	 * JnrTestRunner runner = new JnrTestRunner()
	 *     .add(new JnrTest("Tests") {
	 *         @Override
	 *         protected void specify() {
	 *             test("fast user login test", () -> { ... });
	 *             test("slow user login test", () -> { ... });
	 *             test("fast product search test", () -> { ... });
	 *         }
	 *     });
	 *     
	 * runner.specificationFilter(JnrTestFilters.allSpecifications(
	 *     JnrTestFilters.bySpecificationDescription(".*user.*"), // Must be about users
	 *     runnableSpec -> !runnableSpec.description().contains("slow") // Must not be slow
	 * ));
	 * 
	 * // Only "fast user login test" will be executed, as it satisfies both conditions
	 * runner.execute();
	 * }
	 * </pre>
	 */
	public static void useAllSpecificationsFilter() {
		// Documentation only
	}
	
	/**
	 * Example of using specification filters to combine multiple filter conditions with logical OR.
	 * Uses {@link JnrTestFilters#anySpecification(JnrTestSpecificationFilter...)} with multiple
	 * {@link JnrTestFilters#bySpecificationDescription(String)} filters.
	 * 
	 * <pre>
	 * {@code
	 * // Run specifications that satisfy ANY of the filters
	 * JnrTestRunner runner = new JnrTestRunner()
	 *     .add(new JnrTest("Tests") {
	 *         @Override
	 *         protected void specify() {
	 *             test("user login test", () -> { ... });
	 *             test("product search test", () -> { ... });
	 *             test("inventory management test", () -> { ... });
	 *         }
	 *     });
	 *     
	 * runner.specificationFilter(JnrTestFilters.anySpecification(
	 *     JnrTestFilters.bySpecificationDescription(".*user.*"),
	 *     JnrTestFilters.bySpecificationDescription(".*product.*")
	 * ));
	 * 
	 * // Both "user login test" and "product search test" will be executed, but not "inventory management test"
	 * runner.execute();
	 * }
	 * </pre>
	 */
	public static void useAnySpecificationFilter() {
		// Documentation only
	}

	/**
	 * Example of creating complex nested filter combinations.
	 * Demonstrates using {@link JnrTestFilters#anyClass(JnrTestClassFilter...)} with
	 * {@link JnrTestFilters#allClasses(JnrTestClassFilter...)} for class filtering,
	 * and a similar approach with {@link JnrTestFilters#anySpecification(JnrTestSpecificationFilter...)}
	 * and {@link JnrTestFilters#allSpecifications(JnrTestSpecificationFilter...)} for specifications.
	 * 
	 * <pre>
	 * {@code
	 * // Create a complex nested class filter
	 * JnrTestClassFilter complexClassFilter = JnrTestFilters.anyClass(
	 *     // Either must be a critical test
	 *     JnrTestFilters.byClassDescription(".*Critical.*"),
	 *     // Or must be both a user test and not a slow test
	 *     JnrTestFilters.allClasses(
	 *         JnrTestFilters.byClassDescription("User.*"),
	 *         testClass -> !testClass.getDescription().contains("Slow")
	 *     )
	 * );
	 * 
	 * // Create a complex nested specification filter
	 * JnrTestSpecificationFilter complexSpecFilter = JnrTestFilters.allSpecifications(
	 *     // Must not be marked as deprecated
	 *     JnrTestFilters.notSpecification(
	 *         JnrTestFilters.bySpecificationDescription(".*deprecated.*")
	 *     ),
	 *     // And must be either a login test or a high priority test
	 *     JnrTestFilters.anySpecification(
	 *         JnrTestFilters.bySpecificationDescription(".*login.*"),
	 *         JnrTestFilters.bySpecificationDescription(".*priority:high.*")
	 *     )
	 * );
	 * 
	 * // Apply both filters to the runner
	 * JnrTestRunner runner = new JnrTestRunner()
	 *     .add(new JnrTest("UserTests") { ... })
	 *     .add(new JnrTest("CriticalTests") { ... })
	 *     .add(new JnrTest("SlowUserTests") { ... })
	 *     .classFilter(complexClassFilter)
	 *     .specificationFilter(complexSpecFilter);
	 * 
	 * // Only tests that satisfy both the complex class and specification filters will be executed
	 * runner.execute();
	 * }
	 * </pre>
	 */
	public static void createComplexNestedFilters() {
		// Documentation only
	}

	/**
	 * Example of combining filter conditions efficiently using method chaining.
	 * Demonstrates a fluent approach to setting up test filters and runners
	 * using the {@link JnrTestRunner} builder methods together with
	 * filter factories from {@link JnrTestFilters}.
	 * 
	 * <pre>
	 * {@code
	 * // Set up a test runner with chained filters in a single fluent expression
	 * new JnrTestRunner()
	 *     .add(new JnrTest("UserTests") { ... })
	 *     .add(new JnrTest("ProductTests") { ... })
	 *     .add(new JnrTest("AdminTests") { ... })
	 *     // Apply class filter
	 *     .classFilter(
	 *         JnrTestFilters.notClass(
	 *             JnrTestFilters.byClassDescription("Admin.*")
	 *         )
	 *     )
	 *     // Apply specification filter
	 *     .specificationFilter(
	 *         JnrTestFilters.anySpecification(
	 *             JnrTestFilters.bySpecificationDescription(".*important.*"),
	 *             JnrTestFilters.bySpecificationDescription(".*critical.*")
	 *         )
	 *     )
	 *     // Add listeners and execute
	 *     .testListener(new JnrTestConsoleReporter())
	 *     .execute();
	 * }
	 * </pre>
	 */
	public static void useMethodChainingWithFilters() {
		// Documentation only
	}

}
