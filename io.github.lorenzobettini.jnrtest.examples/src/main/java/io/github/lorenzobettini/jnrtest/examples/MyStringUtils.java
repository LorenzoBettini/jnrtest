package io.github.lorenzobettini.jnrtest.examples;

/**
 * Utility methods for string manipulation.
 * 
 * @author Lorenzo Bettini
 */
public class MyStringUtils {

	public String leftTrim(String input) {
		if (input == null)
			return input;
		int beginIndex = 0;
		final int length = input.length();
		while (beginIndex < length &&
				Character.isWhitespace(input.charAt(beginIndex)))
			beginIndex++;
		return input.substring(beginIndex);
	}

}