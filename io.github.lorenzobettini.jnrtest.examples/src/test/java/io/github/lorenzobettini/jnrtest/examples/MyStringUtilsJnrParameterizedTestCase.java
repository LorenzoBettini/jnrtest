package io.github.lorenzobettini.jnrtest.examples;

import static io.github.lorenzobettini.jnrtest.core.JnrTestCase.Pair.pair;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import io.github.lorenzobettini.jnrtest.core.JnrTestCase;

public class MyStringUtilsJnrParameterizedTestCase extends JnrTestCase {

	private MyStringUtils stringUtils;

	public MyStringUtilsJnrParameterizedTestCase() {
		super("tests for leftTrim (parameterized)");
	}

	@Override
	protected void specify() {
		beforeAll("create factorial SUT", () -> stringUtils = new MyStringUtils());

		test("leftTrim(null) -> null", () -> assertNull(stringUtils.leftTrim(null)));
		testWithParameters("",
			() -> List.of(
				pair("", ""),
				pair(" abc", "abc"),
				pair("abc", "abc"),
				pair("\tabc", "abc"),
				pair("  abc", "abc"),
				pair("  ", "")
			),
			p -> String.format("leftTrim(\"%s\") -> \"%s\"",
				p.first().replace("\t", "\\t"),
				p.second()),
			p -> assertEquals(p.second(), stringUtils.leftTrim(p.first()))
		);
	}

}
