package io.github.lorenzobettini.jnrtest.core;

public class JnrTestFilterTestJnrTest extends JnrTest { // NOSONAR

	private JnrTestFilterTest originalTest = new JnrTestFilterTest();

	public JnrTestFilterTestJnrTest() {
		super("JnrTestFilterTest in JnrTest");
	}

	@Override
	protected void specify() {
		test("should filter using class filter methods",
			() -> originalTest.shouldFilterUsingClassFilterMethods());
		test("should filter by test specification description",
			() -> originalTest.shouldFilterByTestSpecificationDescription());
		test("should combine multiple filters with AND logic",
			() -> originalTest.shouldCombineFiltersWithAnd());
		test("should negate filter",
			() -> originalTest.shouldNegateFilter());
		test("should combine class filters with AND logic",
			() -> originalTest.shouldCombineClassFiltersWithAnd());
		test("should combine specification filters with OR logic",
			() -> originalTest.shouldCombineSpecificationFiltersWithOr());
	}
}
