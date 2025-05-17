package io.github.lorenzobettini.jnrtest.core;

public class JnrTestFiltersTestJnrTest extends JnrTest {

	private JnrTestFiltersTest originalTest = new JnrTestFiltersTest();

	public JnrTestFiltersTestJnrTest() {
		super("JnrTestFiltersTest in JnrTest");
	}

	@Override
	protected void specify() {
		test("testEmptyArrayWithAllClassesReturnsTrue",
			() -> originalTest.testEmptyArrayWithAllClassesReturnsTrue());
		test("testEmptyArrayWithAllSpecificationsReturnsTrue",
			() -> originalTest.testEmptyArrayWithAllSpecificationsReturnsTrue());
		test("testEmptyArrayWithAnyClassReturnsTrue",
			() -> originalTest.testEmptyArrayWithAnyClassReturnsTrue());
		test("testEmptyArrayWithAnySpecificationReturnsTrue",
			() -> originalTest.testEmptyArrayWithAnySpecificationReturnsTrue());
		test("testAllClassesReturnsTrueWhenAllFiltersReturnTrue",
			() -> originalTest.testAllClassesReturnsTrueWhenAllFiltersReturnTrue());
		test("testAllClassesReturnsFalseWhenAnyFilterReturnsFalse",
			() -> originalTest.testAllClassesReturnsFalseWhenAnyFilterReturnsFalse());
		test("testAnyClassReturnsTrueWhenAnyFilterReturnsTrue",
			() -> originalTest.testAnyClassReturnsTrueWhenAnyFilterReturnsTrue());
		test("testAnyClassReturnsFalseWhenAllFiltersReturnFalse",
			() -> originalTest.testAnyClassReturnsFalseWhenAllFiltersReturnFalse());
		test("testByClassDescriptionFilterMatchesCorrectPattern",
			() -> originalTest.testByClassDescriptionFilterMatchesCorrectPattern());
		test("testBySpecificationDescriptionFilterMatchesCorrectPattern",
			() -> originalTest.testBySpecificationDescriptionFilterMatchesCorrectPattern());
		test("testNotClassNegatesResult",
			() -> originalTest.testNotClassNegatesResult());
		test("testNotSpecificationNegatesResult",
			() -> originalTest.testNotSpecificationNegatesResult());
	}
}
