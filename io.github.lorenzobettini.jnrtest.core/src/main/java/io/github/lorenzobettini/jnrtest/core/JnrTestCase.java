package io.github.lorenzobettini.jnrtest.core;

/**
 * Specifies the tests represented by {@link JnrTestSpecification}, by
 * overriding {@link #specify()}.
 * 
 * @author Lorenzo Bettini
 *
 */
public abstract class JnrTestCase {

	private String description;

	private JnrTestStore store = null;

	protected JnrTestCase(String description) {
		this.description = description;
	}

	/**
	 * Returns the {@link JnrTestStore}, created, the first time,
	 * by callying {@link #specify()}.
	 * 
	 * @return
	 */
	public JnrTestStore getStore() {
		if (store == null) {
			store = new JnrTestStore();
			specify();
		}
		return store;
	}

	/**
	 * Responsible of specifying the tests by calling the method
	 * {@link #test(String, JnrTestRunnable)}.
	 */
	protected abstract void specify();

	/**
	 * Specify a test to run (in the shape of a {@link JnrTestRunnable}, with the
	 * given description.
	 * 
	 * @param description
	 * @param testRunnable
	 */
	protected void test(String description, JnrTestRunnable testRunnable) {
		store.test(description, testRunnable);
	}

	/**
	 * Specifies a code to run before all tests.
	 * 
	 * @param beforeAllRunnable
	 */
	protected void beforeAll(JnrTestRunnable beforeAllRunnable) {
		store.beforeAll(beforeAllRunnable);
	}

	/**
	 * Specifies a code to run before each test.
	 * 
	 * @param beforeEachRunnable
	 */
	protected void beforeEach(JnrTestRunnable beforeEachRunnable) {
		store.beforeEach(beforeEachRunnable);
	}

	/**
	 * Specifies a code to run after all tests.
	 * 
	 * @param afterAllRunnable
	 */
	protected void afterAll(JnrTestRunnable afterAllRunnable) {
		store.afterAll(afterAllRunnable);
	}

	/**
	 * Specifies a code to run after each test.
	 * 
	 * @param afterEachRunnable
	 */
	protected void afterEach(JnrTestRunnable afterEachRunnable) {
		store.afterEach(afterEachRunnable);
	}

	public String getDescription() {
		return description;
	}

}
