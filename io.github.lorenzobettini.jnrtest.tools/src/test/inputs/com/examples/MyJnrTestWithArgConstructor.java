package com.examples;

import io.github.lorenzobettini.jnrtest.core.JnrTest;

public class MyJnrTestWithArgConstructor extends JnrTest {

	public MyJnrTestWithArgConstructor(String name) {
		super(name);
	}

	@Override
	protected void specify() {
		// nothing for the moment
	}

}
