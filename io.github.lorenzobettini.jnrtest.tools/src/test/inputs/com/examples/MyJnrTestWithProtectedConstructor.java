package com.examples;

import io.github.lorenzobettini.jnrtest.core.JnrTest;

public class MyJnrTestWithProtectedConstructor extends JnrTest {

	protected MyJnrTestWithProtectedConstructor() {
		super("MyJnrTestWithProtectedConstructor");
	}

	@Override
	protected void specify() {
		// nothing for the moment
	}

}
