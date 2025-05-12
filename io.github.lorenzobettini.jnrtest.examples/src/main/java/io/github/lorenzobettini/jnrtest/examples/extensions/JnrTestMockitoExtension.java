package io.github.lorenzobettini.jnrtest.examples.extensions;

import java.util.List;

import org.mockito.MockitoAnnotations;

import io.github.lorenzobettini.jnrtest.core.JnrTest;
import io.github.lorenzobettini.jnrtest.core.JnrTestExtension;
import io.github.lorenzobettini.jnrtest.core.JnrTestRunnableSpecification;

/**
 * A Mockito extension for {@link JnrTest}.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestMockitoExtension extends JnrTestExtension {

	private AutoCloseable autoCloseable;

	@Override
	protected <T extends JnrTest> void extend(T t, List<JnrTestRunnableSpecification> before,
			List<JnrTestRunnableSpecification> after) {
		before.add(new JnrTestRunnableSpecification("open mocks", () ->
			autoCloseable = MockitoAnnotations.openMocks(t)));
		after.add(new JnrTestRunnableSpecification("release mocks", () ->
			autoCloseable.close()));
	}

}
