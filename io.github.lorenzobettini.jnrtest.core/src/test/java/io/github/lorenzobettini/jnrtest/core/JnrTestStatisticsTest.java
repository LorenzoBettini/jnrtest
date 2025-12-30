package io.github.lorenzobettini.jnrtest.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link JnrTestStatistics}.
 */
class JnrTestStatisticsTest {

	private JnrTestStatistics statistics;

	@BeforeEach
	void setUp() {
		statistics = new JnrTestStatistics();
	}

	@Test
	@DisplayName("should track elapsed time correctly")
	void shouldTrackElapsedTimeCorrectly() throws InterruptedException {
		statistics.setWithElapsedTime(true);
		
		statistics.startTimer();
		Thread.sleep(10); // NOSONAR
		statistics.stopTimer();
		
		final long elapsedTime = statistics.getElapsedTime();
		assertThat(elapsedTime).isPositive();
	}

	@Test
	@DisplayName("should accumulate total time correctly")
	void shouldAccumulateTotalTimeCorrectly() throws InterruptedException {
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
	}

	@Test
	@DisplayName("should return zero elapsed time when not enabled")
	void shouldReturnZeroElapsedTimeWhenNotEnabled() {
		statistics.setWithElapsedTime(false);
		
		statistics.startTimer();
		statistics.stopTimer();
		
		assertEquals(0L, statistics.getElapsedTime());
		assertEquals(0L, statistics.getTotalTime());
	}

	@Test
	@DisplayName("should check if elapsed time tracking is enabled")
	void shouldCheckIfElapsedTimeTrackingIsEnabled() {
		assertFalse(statistics.isWithElapsedTime());
		
		statistics.setWithElapsedTime(true);
		assertTrue(statistics.isWithElapsedTime());
		
		statistics.setWithElapsedTime(false);
		assertFalse(statistics.isWithElapsedTime());
	}

	@Test
	@DisplayName("getElapsedTime should return non-zero when timer is running")
	void getElapsedTimeShouldReturnNonZeroWhenTimerIsRunning() throws InterruptedException {
		statistics.setWithElapsedTime(true);
		
		statistics.startTimer();
		Thread.sleep(10); // NOSONAR
		statistics.stopTimer();
		
		assertThat(statistics.getElapsedTime()).isNotZero();
	}

	@Test
	@DisplayName("getTotalTime should return accumulated time")
	void getTotalTimeShouldReturnAccumulatedTime() throws InterruptedException {
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
	}

	@Test
	@DisplayName("should reset state correctly")
	void shouldResetStateCorrectly() {
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
	}
}
