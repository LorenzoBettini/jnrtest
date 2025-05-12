package io.github.lorenzobettini.jnrtest.core;

/**
 * Adapter for {@link JnrTestListener} implementing all methods as empty.
 * 
 * @author Lorenzo Bettini
 */
public abstract class JnrTestListenerAdapter implements JnrTestListener {

	@Override
	public void notify(JnrTestLifecycleEvent event) {

	}

	@Override
	public void notify(JnrTestRunnableLifecycleEvent event) {

	}

	@Override
	public void notify(JnrTestResult result) {

	}

}
