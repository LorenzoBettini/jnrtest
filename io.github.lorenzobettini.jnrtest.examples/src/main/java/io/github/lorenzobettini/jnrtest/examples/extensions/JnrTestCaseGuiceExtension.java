package io.github.lorenzobettini.jnrtest.examples.extensions;

import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Module;

import io.github.lorenzobettini.jnrtest.core.JnrTest;
import io.github.lorenzobettini.jnrtest.core.JnrTestExtension;
import io.github.lorenzobettini.jnrtest.core.JnrTestRunnableSpecification;

/**
 * A Guice extension for JnrTestCase.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestCaseGuiceExtension extends JnrTestExtension {

	private Module module;

	public JnrTestCaseGuiceExtension(Module module) {
		this.module = module;
	}

	@Override
	protected <T extends JnrTest> void extend(T testCase, List<JnrTestRunnableSpecification> before,
			List<JnrTestRunnableSpecification> after) {
		before.add(new JnrTestRunnableSpecification("inject members", () ->
			Guice.createInjector(module).injectMembers(testCase)));
	}

}
