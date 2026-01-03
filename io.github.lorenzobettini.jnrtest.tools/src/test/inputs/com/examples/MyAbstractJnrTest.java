package com.examples;

import io.github.lorenzobettini.jnrtest.core.JnrTest;

public abstract class MyAbstractJnrTest extends JnrTest {

	protected MyAbstractJnrTest() {
		super("MyAbstractJnrTest");
	}

	@Override
	protected void specify() {
		// nothing for the moment
	}

}
