package io.github.lorenzobettini.jnrtest.examples;

public class Factorial {

	public int compute(int n) {
		if (n < 0)
			throw new IllegalArgumentException("Negative input: " + n);
		if (n < 2)
			return 1;
		return n * compute(n - 1);
	}

}