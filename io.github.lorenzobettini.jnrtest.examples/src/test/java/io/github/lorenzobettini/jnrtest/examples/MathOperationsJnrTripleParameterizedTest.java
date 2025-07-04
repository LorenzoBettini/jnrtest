package io.github.lorenzobettini.jnrtest.examples;

import static io.github.lorenzobettini.jnrtest.core.JnrTest.Triple.triple;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import io.github.lorenzobettini.jnrtest.core.JnrTest;

/**
 * Example of using Triple for parameterized tests with three parameters.
 * This example demonstrates mathematical operations testing with three values.
 * 
 * @author Lorenzo Bettini
 */
public class MathOperationsJnrTripleParameterizedTest extends JnrTest {

	public MathOperationsJnrTripleParameterizedTest() {
		super("tests for math operations (parameterized with triple)");
	}

	@Override
	protected void specify() {
		testWithParameters("addition test: ",
			() -> List.of(
				triple(1, 2, 3),     // 1 + 2 = 3
				triple(5, 10, 15),   // 5 + 10 = 15
				triple(0, 0, 0),     // 0 + 0 = 0
				triple(-3, 8, 5),    // -3 + 8 = 5
				triple(100, -50, 50) // 100 + (-50) = 50
			),
			t -> String.format("%d + %d = %d", t.first(), t.second(), t.third()),
			t -> assertEquals(t.third(), t.first() + t.second())
		);
		
		testWithParameters("multiplication test: ",
			() -> List.of(
				triple(2, 3, 6),     // 2 * 3 = 6
				triple(4, 5, 20),    // 4 * 5 = 20
				triple(0, 42, 0),    // 0 * 42 = 0
				triple(-2, 3, -6),   // -2 * 3 = -6
				triple(7, 1, 7)      // 7 * 1 = 7
			),
			t -> String.format("%d * %d = %d", t.first(), t.second(), t.third()),
			t -> assertEquals(t.third(), t.first() * t.second())
		);
	}
}
