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
	 * @param description
	 * @param beforeAllRunnable
	 */
	protected void beforeAll(String description, JnrTestRunnable beforeAllRunnable) {
		store.beforeAll(description, beforeAllRunnable);
	}

	/**
	 * Specifies a code to run before each test.
	 * 
	 * @param description
	 * @param beforeEachRunnable
	 */
	protected void beforeEach(String description, JnrTestRunnable beforeEachRunnable) {
		store.beforeEach(description, beforeEachRunnable);
	}

	/**
	 * Specifies a code to run after all tests.
	 * 
	 * @param description
	 * @param afterAllRunnable
	 */
	protected void afterAll(String description, JnrTestRunnable afterAllRunnable) {
		store.afterAll(description, afterAllRunnable);
	}

	/**
	 * Specifies a code to run after each test.
	 * 
	 * @param description
	 * @param afterEachRunnable
	 */
	protected void afterEach(String description, JnrTestRunnable afterEachRunnable) {
		store.afterEach(description, afterEachRunnable);
	}

	public String getDescription() {
		return description;
	}

}
