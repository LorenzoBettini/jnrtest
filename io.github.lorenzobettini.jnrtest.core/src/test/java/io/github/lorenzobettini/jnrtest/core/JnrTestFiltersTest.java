package io.github.lorenzobettini.jnrtest.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link JnrTestFilters} class focusing on corner cases and
 * ensuring the expected behavior.
 */
class JnrTestFiltersTest {

	// Simple fake test class implementation
	private static class FakeTest extends JnrTest {

		public FakeTest(String description) {
			super(description);
		}

		@Override
		protected void specify() {
			// Empty implementation for testing
		}
	}

	// Empty runnable for testing
	private static final JnrTestRunnable EMPTY_RUNNABLE = () -> {
	};

	// Reusable test instances
	private final JnrTest calculatorTest = new FakeTest("Calculator");
	private final JnrTest stringTest = new FakeTest("String");
	private final JnrTestRunnableSpecification additionSpec = new JnrTestRunnableSpecification("Addition",
			EMPTY_RUNNABLE);
	private final JnrTestRunnableSpecification subtractionSpec = new JnrTestRunnableSpecification("Subtraction",
			EMPTY_RUNNABLE);

	@Test
	void testFiltersAreInitiallyNull() {
		// When we create a new filter instance
		JnrTestFilters filters = new JnrTestFilters();

		// Then the filter should be null
		assertNull(filters.getClassFilter());
		assertNull(filters.getSpecificationFilter());
	}

	@Test
	void testClassFilterAcceptsWhenMatches() {
		// Given a JnrTestFilters instance
		JnrTestFilters filters = new JnrTestFilters();

		// When we add a class filter that matches calculator test
		filters.classFilter(testClass -> testClass.getDescription().equals("Calculator"));

		// Then the filter should accept calculator test
		assertTrue(filters.getClassFilter().test(calculatorTest));

		// And reject string test
		assertFalse(filters.getClassFilter().test(stringTest));
	}

	@Test
	void testSpecificationFilterAcceptsWhenMatches() {
		// Given a JnrTestFilters instance
		JnrTestFilters filters = new JnrTestFilters();

		// When we add a specification filter that matches addition test
		filters.specificationFilter(spec -> spec.description().equals("Addition"));

		// Then the filter should accept addition spec
		assertTrue(filters.getSpecificationFilter().test(additionSpec));

		// And reject subtraction spec
		assertFalse(filters.getSpecificationFilter().test(subtractionSpec));
	}

	@Test
	void testMultipleClassFiltersWithAnd() {
		// Given a JnrTestFilters instance
		JnrTestFilters filters = new JnrTestFilters();

		// When we add multiple class filters with AND logic
		filters.classFilter(testClass -> true);
		filters.classFilter(testClass -> false);

		// Then the combined filter should reject (return false)
		assertFalse(filters.getClassFilter().test(calculatorTest));
	}

	@Test
	void testMultipleSpecificationFiltersWithAnd() {
		// Given a JnrTestFilters instance
		JnrTestFilters filters = new JnrTestFilters();

		// When we add multiple specification filters with AND logic
		filters.specificationFilter(spec -> spec.description().contains("Addition"));
		filters.specificationFilter(spec -> spec.description().length() > 3);

		// Then the combined filter should accept only when both conditions are true
		assertTrue(filters.getSpecificationFilter().test(additionSpec)); // Contains "Addition" AND length > 3

		// Create a specification that matches only one condition
		JnrTestRunnableSpecification shortSpec = new JnrTestRunnableSpecification("Add", EMPTY_RUNNABLE);
		assertFalse(filters.getSpecificationFilter().test(shortSpec)); // Contains "Add" but length <= 3

		// And reject when neither condition is met
		assertFalse(filters.getSpecificationFilter().test(subtractionSpec)); // Doesn't contain "Addition"
	}

	@Test
	void testByClassDescriptionFilterMatchesCorrectPattern() {
		// Given a JnrTestFilters instance
		JnrTestFilters filters = new JnrTestFilters();

		// When we add a class description filter
		filters.byClassDescription("Calculator.*");

		// Then the filter should accept a matching class
		assertTrue(filters.getClassFilter().test(new FakeTest("Calculator Test")));

		// And reject a non-matching class
		assertFalse(filters.getClassFilter().test(new FakeTest("String Utils")));
	}

	@Test
	void testBySpecificationDescriptionFilterMatchesCorrectPattern() {
		// Given a JnrTestFilters instance
		JnrTestFilters filters = new JnrTestFilters();

		// When we add a specification description filter
		filters.bySpecificationDescription("Addition.*");

		// Then the filter should accept a matching specification
		assertTrue(filters.getSpecificationFilter()
				.test(new JnrTestRunnableSpecification("Addition Test", EMPTY_RUNNABLE)));

		// And reject a non-matching specification
		assertFalse(filters.getSpecificationFilter()
				.test(new JnrTestRunnableSpecification("Subtraction Test", EMPTY_RUNNABLE)));
	}

	@Test
	void testPredicateNegation() {
		// Given a JnrTestFilters instance with a filter
		JnrTestFilters filters = new JnrTestFilters();
		filters.classFilter(testClass -> testClass.getDescription().equals("Calculator"));

		// When we negate the filter
		Predicate<JnrTest> original = filters.getClassFilter();
		Predicate<JnrTest> negated = original.negate();

		// Then the results should be inverted
		assertTrue(original.test(calculatorTest));
		assertFalse(negated.test(calculatorTest));

		assertFalse(original.test(stringTest));
		assertTrue(negated.test(stringTest));
	}

	@Test
	void testPredicateOr() {
		// Given two predicates
		Predicate<JnrTest> predicate1 = testClass -> testClass.getDescription().equals("Calculator");
		Predicate<JnrTest> predicate2 = testClass -> testClass.getDescription().equals("String");

		// When we combine them with OR
		Predicate<JnrTest> combined = predicate1.or(predicate2);

		// Then the combined predicate should accept either
		assertTrue(combined.test(calculatorTest));
		assertTrue(combined.test(stringTest));
		assertFalse(combined.test(new FakeTest("Other")));
	}

	@Test
	void testDirectPredicateOrWithConvenienceMethods() {
		// Given a filters instance
		JnrTestFilters filters = new JnrTestFilters();

		// When we use the convenience methods
		filters.byClassDescription("Calculator.*");

		// And then use the predicate directly
		Predicate<JnrTest> directPredicate = testClass -> testClass.getDescription().contains("String");

		// We can combine them with OR logic
		Predicate<JnrTest> combined = filters.getClassFilter().or(directPredicate);

		// Then the filter should accept both types of classes
		assertTrue(combined.test(calculatorTest));
		assertTrue(combined.test(stringTest));
		assertFalse(combined.test(new FakeTest("Other")));
	}

	@Test
	void testConvenienceMethodsNegation() {
		// Given a filters instance
		JnrTestFilters filters = new JnrTestFilters();

		// When we use the convenience method
		filters.bySpecificationDescription("Addition.*");

		// And then negate the filter
		Predicate<JnrTestRunnableSpecification> negated = filters.getSpecificationFilter().negate();

		// Then the filter should reject additions but accept other types
		assertFalse(negated.test(additionSpec));
		assertTrue(negated.test(subtractionSpec));
	}

	@Test
	void byClassDescriptionShouldReturnThisForChaining() {
		final var filters = new JnrTestFilters();
		final var result = filters.byClassDescription(".*");
		assertThat(result).isSameAs(filters);
	}

	@Test
	void bySpecificationDescriptionShouldReturnThisForChaining() {
		final var filters = new JnrTestFilters();
		final var result = filters.bySpecificationDescription(".*");
		assertThat(result).isSameAs(filters);
	}

	@Test
	void classFilterShouldReturnThisForChaining() {
		final var filters = new JnrTestFilters();
		final var result = filters.classFilter(test -> true);
		assertThat(result).isSameAs(filters);
	}

	@Test
	void specificationFilterShouldReturnThisForChaining() {
		final var filters = new JnrTestFilters();
		final var result = filters.specificationFilter(spec -> true);
		assertThat(result).isSameAs(filters);
	}

	@Test
	void shouldSetClassFilterWhenNullAndCombineWithAndWhenNotNull() {
		// Kill line 54 mutant: if (this.classFilter != null)
		// Mutant makes it always false, so always executes else (overwrites instead of ANDing)
		
		JnrTestFilters f = new JnrTestFilters();
		Predicate<JnrTest> p1 = t -> false; // Rejects everything
		Predicate<JnrTest> p2 = t -> true; // Accepts everything
		
		f.classFilter(p1); // Sets filter to p1 (rejects all)
		f.classFilter(p2); // Should AND with p1, result: rejects all (false AND true = false)
		
		// Test with any input:
		// Original: false AND true = FALSE
		// Mutant (overwrite): true = TRUE
		JnrTest anyTest = new FakeTest("anything");
		boolean result = f.getClassFilter().test(anyTest);
		
		// MUST be false (original behavior)
		// If mutant (overwrite), would be true
		assertFalse(result);
	}

	@Test
	void shouldSetSpecificationFilterWhenNullAndCombineWithAndWhenNotNull() {
		// Same test for specification filter
		final var filters = new JnrTestFilters();
		
		// First call - filter is null, sets it directly
		// Reject anything containing "x"
		filters.specificationFilter(spec -> !spec.description().contains("x"));
		assertThat(filters.getSpecificationFilter()).isNotNull();
		
		// Second call - filter is NOT null, should AND
		// Accept anything longer than 3 characters
		filters.specificationFilter(spec -> spec.description().length() > 3);
		
		// Test "xyz" - has x (rejected by first) but length > 3 (accepted by second)
		// Original (AND): false AND true = FALSE
		// Mutant (overwrite): true (only checks length)
		assertFalse(filters.getSpecificationFilter().test(
			new JnrTestRunnableSpecification("xyz", EMPTY_RUNNABLE)));
		
		// Test "abcd" - no x (accepted by first) and length > 3 (accepted by second)
		// Both: true AND true = TRUE
		assertTrue(filters.getSpecificationFilter().test(
			new JnrTestRunnableSpecification("abcd", EMPTY_RUNNABLE)));
	}

}