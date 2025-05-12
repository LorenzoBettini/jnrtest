package io.github.lorenzobettini.jnrtest.examples.extensions;

import java.util.List;

import org.mockito.MockitoAnnotations;

import io.github.lorenzobettini.jnrtest.core.JnrTest;
import io.github.lorenzobettini.jnrtest.core.JnrTestCaseExtension;
import io.github.lorenzobettini.jnrtest.core.JnrTestRunnableSpecification;

/**
 * A Mockito extension for JnrTestCase.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestCaseMockitoExtension extends JnrTestCaseExtension {

	private AutoCloseable autoCloseable;

	@Override
	protected <T extends JnrTest> void extend(T testCase, List<JnrTestRunnableSpecification> before,
			List<JnrTestRunnableSpecification> after) {
		before.add(new JnrTestRunnableSpecification("open mocks", () ->
			autoCloseable = MockitoAnnotations.openMocks(testCase)));
		after.add(new JnrTestRunnableSpecification("release mocks", () ->
			autoCloseable.close()));
	}

}
