package io.github.lorenzobettini.jnrtest.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.lorenzobettini.jnrtest.core.JnrTest;

/**
 * Tests for {@link JnrTestStatistics}.
 */
public class JnrTestStatisticsTestJnrTest extends JnrTest {

	public JnrTestStatisticsTestJnrTest() {
		super("JnrTestStatisticsTest in JnrTest");
	}

	protected @Override void specify() {
		beforeEach("call setUp", () -> {
			statistics = new JnrTestStatistics();
		});
		test("should track elapsed time correctly", () -> {
			statistics.setWithElapsedTime(true);

			statistics.startTimer();
			Thread.sleep(10); // NOSONAR
			statistics.stopTimer();

			final long elapsedTime = statistics.getElapsedTime();
			assertThat(elapsedTime).isPositive();
		});
		test("should accumulate total time correctly", () -> {
			statistics.setWithElapsedTime(true);

			// First test
			statistics.startTimer();
			Thread.sleep(10); // NOSONAR
			statistics.stopTimer();
			final long firstElapsed = statistics.getElapsedTime();

			// Second test
			statistics.startTimer();
			Thread.sleep(10); // NOSONAR
			statistics.stopTimer();
			final long secondElapsed = statistics.getElapsedTime();

			final long totalTime = statistics.getTotalTime();
			assertThat(totalTime).isGreaterThanOrEqualTo(firstElapsed + secondElapsed);
		});
		test("should return zero elapsed time when not enabled", () -> {
			statistics.setWithElapsedTime(false);

			statistics.startTimer();
			statistics.stopTimer();

			assertEquals(0L, statistics.getElapsedTime());
			assertEquals(0L, statistics.getTotalTime());
		});
		test("should check if elapsed time tracking is enabled", () -> {
			assertFalse(statistics.isWithElapsedTime());

			statistics.setWithElapsedTime(true);
			assertTrue(statistics.isWithElapsedTime());

			statistics.setWithElapsedTime(false);
			assertFalse(statistics.isWithElapsedTime());
		});
		test("getElapsedTime should return non-zero when timer is running", () -> {
			statistics.setWithElapsedTime(true);

			statistics.startTimer();
			Thread.sleep(10); // NOSONAR
			statistics.stopTimer();

			assertThat(statistics.getElapsedTime()).isNotZero();
		});
		test("getTotalTime should return accumulated time", () -> {
			statistics.setWithElapsedTime(true);

			statistics.startTimer();
			Thread.sleep(10); // NOSONAR
			statistics.stopTimer();

			final long firstTotal = statistics.getTotalTime();

			statistics.startTimer();
			Thread.sleep(10); // NOSONAR
			statistics.stopTimer();

			final long secondTotal = statistics.getTotalTime();

			assertThat(secondTotal).isGreaterThan(firstTotal);
		});
		test("should reset state correctly", () -> {
			statistics.incrementSucceeded();
			statistics.incrementFailed();
			statistics.incrementErrors();
			statistics.setWithElapsedTime(true);

			statistics.reset();

			assertEquals(0, statistics.getSucceeded());
			assertEquals(0, statistics.getFailed());
			assertEquals(0, statistics.getErrors());
			assertEquals(0, statistics.getTotalTests());
			// Note: reset() does not reset withElapsedTime flag by design
			assertTrue(statistics.isWithElapsedTime());
		});
	}

	private JnrTestStatistics statistics;
}
