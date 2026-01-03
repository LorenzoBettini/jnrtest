package io.github.lorenzobettini.jnrtest.core;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JnrTestReporterInterface} default methods.
 */
public class JnrTestReporterInterfaceTestJnrTest extends JnrTest {

	public JnrTestReporterInterfaceTestJnrTest() {
		super("JnrTestReporterInterfaceTest in JnrTest");
	}

	protected @Override void specify() {
		test("withOnlySummaries() should return this for chaining", () -> {
			final TestReporter reporter = new TestReporter();
			final JnrTestReporterInterface result = reporter.withOnlySummaries();
			assertThat(result).isSameAs(reporter);
			assertThat(reporter.isOnlySummaries()).isTrue();
		});
		test("withElapsedTime() should return this for chaining", () -> {
			final TestReporter reporter = new TestReporter();
			final JnrTestReporterInterface result = reporter.withElapsedTime();
			assertThat(result).isSameAs(reporter);
			assertThat(reporter.isWithElapsedTime()).isTrue();
		});
		test("withOnlySummaries(boolean) should return this for chaining", () -> {
			final TestReporter reporter = new TestReporter();
			final JnrTestReporterInterface result = reporter.withOnlySummaries(true);
			assertThat(result).isSameAs(reporter);
			assertThat(reporter.isOnlySummaries()).isTrue();
		});
		test("withElapsedTime(boolean) should return this for chaining", () -> {
			final TestReporter reporter = new TestReporter();
			final JnrTestReporterInterface result = reporter.withElapsedTime(true);
			assertThat(result).isSameAs(reporter);
			assertThat(reporter.isWithElapsedTime()).isTrue();
		});
	}

	/**
	 * Implementation of JnrTestReporterInterface for testing.
	 */
	private static class TestReporter implements JnrTestReporterInterface {
		private boolean onlySummaries = false;
		private boolean withElapsedTime = false;

		@Override
		public JnrTestReporterInterface withOnlySummaries(boolean onlySummaries) {
			this.onlySummaries = onlySummaries;
			return this;
		}

		@Override
		public JnrTestReporterInterface withElapsedTime(boolean withElapsedTime) {
			this.withElapsedTime = withElapsedTime;
			return this;
		}

		@Override
		public void notify(JnrTestResult result) {
			// Not needed for this test
		}

		@Override
		public void notify(JnrTestLifecycleEvent event) {
			// Not needed for this test
		}

		@Override
		public void notify(JnrTestRunnableLifecycleEvent event) {
			// Not needed for this test
		}

		public boolean isOnlySummaries() {
			return onlySummaries;
		}

		public boolean isWithElapsedTime() {
			return withElapsedTime;
		}
	}
}
