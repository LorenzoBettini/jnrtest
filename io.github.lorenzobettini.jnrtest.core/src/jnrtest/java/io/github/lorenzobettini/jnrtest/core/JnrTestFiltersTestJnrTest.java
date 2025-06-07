package io.github.lorenzobettini.jnrtest.core;

public class JnrTestFiltersTestJnrTest extends JnrTest {

	private JnrTestFiltersTest originalTest = new JnrTestFiltersTest();

	public JnrTestFiltersTestJnrTest() {
		super("JnrTestFiltersTest in JnrTest");
	}

	@Override
	protected void specify() {
		test("testFiltersAreInitiallyNull",
			() -> originalTest.testFiltersAreInitiallyNull());
		test("testClassFilterAcceptsWhenMatches",
			() -> originalTest.testClassFilterAcceptsWhenMatches());
		test("testSpecificationFilterAcceptsWhenMatches",
			() -> originalTest.testSpecificationFilterAcceptsWhenMatches());
		test("testMultipleClassFiltersWithAnd",
			() -> originalTest.testMultipleClassFiltersWithAnd());
		test("testMultipleSpecificationFiltersWithAnd",
			() -> originalTest.testMultipleSpecificationFiltersWithAnd());
		test("testByClassDescriptionFilterMatchesCorrectPattern",
			() -> originalTest.testByClassDescriptionFilterMatchesCorrectPattern());
		test("testBySpecificationDescriptionFilterMatchesCorrectPattern",
			() -> originalTest.testBySpecificationDescriptionFilterMatchesCorrectPattern());
		test("testPredicateNegation",
			() -> originalTest.testPredicateNegation());
		test("testPredicateOr",
			() -> originalTest.testPredicateOr());
		test("testDirectPredicateOrWithConvenienceMethods",
			() -> originalTest.testDirectPredicateOrWithConvenienceMethods());
		test("testConvenienceMethodsNegation",
			() -> originalTest.testConvenienceMethodsNegation());
	}
}
