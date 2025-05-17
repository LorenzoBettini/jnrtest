package io.github.lorenzobettini.jnrtest.core;

public class JnrTestFilterTestJnrTest extends JnrTest {

	private JnrTestFilterTest originalTest = new JnrTestFilterTest();

	public JnrTestFilterTestJnrTest() {
		super("JnrTestFilterTest in JnrTest");
	}

	@Override
	protected void specify() {
		test("should filter by test class description",
			() -> originalTest.shouldFilterByTestClassDescription());
		test("should filter by test specification description",
			() -> originalTest.shouldFilterByTestSpecificationDescription());
		test("should combine multiple filters with AND logic",
			() -> originalTest.shouldCombineFiltersWithAnd());
		test("should combine multiple filters with OR logic",
			() -> originalTest.shouldCombineFiltersWithOr());
		test("should negate filter",
			() -> originalTest.shouldNegateFilter());
	}
}
