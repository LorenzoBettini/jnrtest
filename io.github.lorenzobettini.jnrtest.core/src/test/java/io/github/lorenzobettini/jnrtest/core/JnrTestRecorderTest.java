package io.github.lorenzobettini.jnrtest.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link JnrTestRecorder}.
 */
class JnrTestRecorderTest {

	private JnrTestRecorder recorder;

	@BeforeEach
	void setUp() {
		recorder = new JnrTestRecorder();
	}

	@Test
	@DisplayName("should handle lifecycle events for non-START status")
	void shouldHandleLifecycleEventsForNonStartStatus() {
		// Create a lifecycle event with END status
		final var event = new JnrTestLifecycleEvent("test class", JnrTestStatus.END);
		
		// Notify recorder
		recorder.notify(event);
		
		// Should not create any entry in results
		assertTrue(recorder.getResults().isEmpty());
	}

	@Test
	@DisplayName("should handle runnable lifecycle events for non-TEST kind")
	void shouldHandleRunnableLifecycleEventsForNonTestKind() {
		recorder.withElapsedTime(true);
		
		// Create a lifecycle event for BEFORE_ALL (not TEST)
		final var event = new JnrTestRunnableLifecycleEvent(
			"before all",
			JnrTestRunnableKind.BEFORE_ALL,
			JnrTestRunnableStatus.START
		);
		
		// Notify recorder - should be ignored
		recorder.notify(event);
		
		// Total time should still be zero
		assertEquals(0L, recorder.getTotalTime());
	}

	@Test
	@DisplayName("should handle runnable lifecycle events when elapsed time is disabled")
	void shouldHandleRunnableLifecycleEventsWhenElapsedTimeIsDisabled() {
		recorder.withElapsedTime(false);
		
		// Create a lifecycle event for TEST kind
		final var event = new JnrTestRunnableLifecycleEvent(
			"test",
			JnrTestRunnableKind.TEST,
			JnrTestRunnableStatus.START
		);
		
		// Notify recorder - should be ignored
		recorder.notify(event);
		
		// Total time should still be zero
		assertEquals(0L, recorder.getTotalTime());
	}

	@Test
	@DisplayName("should track elapsed time for test runnables")
	void shouldTrackElapsedTimeForTestRunnables() throws InterruptedException {
		// Start recording a test class
		recorder.notify(new JnrTestLifecycleEvent("test class", JnrTestStatus.START));
		
		// Enable elapsed time
		recorder.withElapsedTime(true);
		
		// Start a test
		recorder.notify(new JnrTestRunnableLifecycleEvent(
			"test1",
			JnrTestRunnableKind.TEST,
			JnrTestRunnableStatus.START
		));
		
		Thread.sleep(10); // NOSONAR
		
		// End the test
		recorder.notify(new JnrTestRunnableLifecycleEvent(
			"test1",
			JnrTestRunnableKind.TEST,
			JnrTestRunnableStatus.END
		));
		
		// Record a result
		recorder.notify(new JnrTestResult("test1", JnrTestResultStatus.SUCCESS, null));
		
		// Total time should be positive
		assertThat(recorder.getTotalTime()).isPositive();
	}

	@Test
	@DisplayName("withElapsedTime should return this for chaining")
	void withElapsedTimeShouldReturnThisForChaining() {
		final var result = recorder.withElapsedTime(true);
		assertThat(result).isSameAs(recorder);
	}

	@Test
	@DisplayName("should record results correctly")
	void shouldRecordResultsCorrectly() {
		// Start test class
		recorder.notify(new JnrTestLifecycleEvent("test class", JnrTestStatus.START));
		
		// Add results
		recorder.notify(new JnrTestResult("test1", JnrTestResultStatus.SUCCESS, null));
		recorder.notify(new JnrTestResult("test2", JnrTestResultStatus.FAILED, new AssertionError("fail")));
		
		// Check results
		assertFalse(recorder.isSuccess());
		assertThat(recorder.getResults()).containsKey("test class");
		assertThat(recorder.getResults().get("test class")).hasSize(2);
	}
}
