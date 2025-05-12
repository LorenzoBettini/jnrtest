package io.github.lorenzobettini.jnrtest.examples;

import static io.github.lorenzobettini.jnrtest.core.JnrTest.Pair.pair;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import io.github.lorenzobettini.jnrtest.core.JnrTest;

public class FactorialJnrParameterizedWithDescriptionTest extends JnrTest {

	private Factorial factorial;

	public FactorialJnrParameterizedWithDescriptionTest() {
		super("tests for factorial (parameterized with description)");
	}

	@Override
	protected void specify() {
		beforeAll("create factorial SUT", () -> factorial = new Factorial());
		
		testWithParameters("",
			() -> List.of(
				pair(0, 1),
				pair(1, 1),
				pair(2, 2),
				pair(3, 6),
				pair(4, 24)
			),
			p -> String.format("factorial(%d) -> %d",
					p.first(), p.second()),
			p -> assertEquals(p.second(), factorial.compute(p.first()))
		);
	}

}
