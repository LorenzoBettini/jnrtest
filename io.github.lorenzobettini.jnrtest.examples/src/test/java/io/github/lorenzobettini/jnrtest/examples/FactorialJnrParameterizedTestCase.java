package io.github.lorenzobettini.jnrtest.examples;

import static io.github.lorenzobettini.jnrtest.core.JnrTestCase.Pair.pair;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import io.github.lorenzobettini.jnrtest.core.JnrTestCase;

public class FactorialJnrParameterizedTestCase extends JnrTestCase {

	private Factorial factorial;

	public FactorialJnrParameterizedTestCase() {
		super("tests for factorial (parameterized)");
	}

	@Override
	protected void specify() {
		beforeAll("create factorial SUT", () -> factorial = new Factorial());
		
		testWithParameters("input,output",
			() -> List.of(
				pair(0, 1),
				pair(1, 1),
				pair(2, 2),
				pair(3, 6),
				pair(4, 24)
			),
			p -> assertEquals(p.second(), factorial.compute(p.first()))
		);
	}

}
