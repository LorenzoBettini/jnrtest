package io.github.lorenzobettini.jnrtest.core;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

class JnrTestThreadSafeRecorderTest {

	@Test
	void testSingleThreadedRecording() {
		JnrTestThreadSafeRecorder recorder = new JnrTestThreadSafeRecorder();

		JnrTestLifecycleEvent startEvent = new JnrTestLifecycleEvent("Test1", JnrTestStatus.START);
		JnrTestLifecycleEvent endEvent = new JnrTestLifecycleEvent("Test1", JnrTestStatus.END);

		recorder.notify(startEvent);

		// Notify multiple results
		List<JnrTestResult> expectedResults = List.of(
			new JnrTestResult("Test1-Result0", JnrTestResultStatus.SUCCESS, null),
			new JnrTestResult("Test1-Result1", JnrTestResultStatus.SUCCESS, null),
			new JnrTestResult("Test1-Result2", JnrTestResultStatus.SUCCESS, null)
		);
		expectedResults.forEach(recorder::notify);

		recorder.notify(endEvent);

		// also notify spurious non JnrTestRunnableLifecycleEvent.TEST events
		recorder.notify(new JnrTestRunnableLifecycleEvent("spurious", JnrTestRunnableKind.BEFORE_ALL, JnrTestRunnableStatus.START));

		Map<String, List<JnrTestResult>> results = recorder.getResults();
		assertThat(results)
			.containsOnlyKeys("Test1")
			.extractingByKey("Test1", InstanceOfAssertFactories.list(JnrTestResult.class))
			.containsExactlyElementsOf(expectedResults);
		assertThat(recorder.isSuccess()).isTrue();
	}

	@Test
	void testMultiThreadedRecording() throws InterruptedException {
		JnrTestThreadSafeRecorder recorder = new JnrTestThreadSafeRecorder();

		int threadCount = 10;
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			final int threadIndex = i;
			executor.submit(() -> {
				try {
					String testName = "Test" + threadIndex;
					JnrTestLifecycleEvent startEvent = new JnrTestLifecycleEvent(testName, JnrTestStatus.START);
					JnrTestLifecycleEvent endEvent = new JnrTestLifecycleEvent(testName, JnrTestStatus.END);

					recorder.notify(startEvent);

					// Notify multiple results
					List<JnrTestResult> expectedResults = List.of(
						new JnrTestResult(testName + "-Result0", JnrTestResultStatus.SUCCESS, null),
						new JnrTestResult(testName + "-Result1", JnrTestResultStatus.SUCCESS, null),
						new JnrTestResult(testName + "-Result2", JnrTestResultStatus.SUCCESS, null)
					);
					expectedResults.forEach(recorder::notify);

					recorder.notify(endEvent);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executor.shutdown();

		Map<String, List<JnrTestResult>> results = recorder.getResults();
		assertThat(results).hasSize(threadCount);
		for (int i = 0; i < threadCount; i++) {
			String testName = "Test" + i;

			// Build the expected list of results
			List<JnrTestResult> expectedResults = List.of(
				new JnrTestResult(testName + "-Result0", JnrTestResultStatus.SUCCESS, null),
				new JnrTestResult(testName + "-Result1", JnrTestResultStatus.SUCCESS, null),
				new JnrTestResult(testName + "-Result2", JnrTestResultStatus.SUCCESS, null)
			);

			assertThat(results)
				.containsKey(testName)
				.extractingByKey(testName, InstanceOfAssertFactories.list(JnrTestResult.class))
				.containsExactlyElementsOf(expectedResults);
		}
		assertThat(recorder.isSuccess()).isTrue();
	}

	@Test
	void testMultiThreadedFailures() throws InterruptedException {
		// Notify multiple results between startEvent and endEvent
		JnrTestThreadSafeRecorder recorder = new JnrTestThreadSafeRecorder();

		int threadCount = 5;
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			final int threadIndex = i;
			executor.submit(() -> {
				try {
					String testName = "Test" + threadIndex;
					JnrTestLifecycleEvent startEvent = new JnrTestLifecycleEvent(testName, JnrTestStatus.START);
					JnrTestLifecycleEvent endEvent = new JnrTestLifecycleEvent(testName, JnrTestStatus.END);

					recorder.notify(startEvent);

					// Notify multiple results
					for (int j = 0; j < 3; j++) {
						JnrTestResult result = (threadIndex + j) % 2 == 0
								? new JnrTestResult(testName + "-Result" + j, JnrTestResultStatus.SUCCESS, null)
								: new JnrTestResult(testName + "-Result" + j, JnrTestResultStatus.FAILED, null);
						recorder.notify(result);
					}

					recorder.notify(endEvent);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executor.shutdown();

		Map<String, List<JnrTestResult>> results = recorder.getResults();
		assertThat(results).hasSize(threadCount);
		for (int i = 0; i < threadCount; i++) {
			String testName = "Test" + i;

			// Build the expected list of results
			List<JnrTestResult> expectedResults = List.of(
				new JnrTestResult(testName + "-Result0", (i + 0) % 2 == 0 ? JnrTestResultStatus.SUCCESS : JnrTestResultStatus.FAILED, null),
				new JnrTestResult(testName + "-Result1", (i + 1) % 2 == 0 ? JnrTestResultStatus.SUCCESS : JnrTestResultStatus.FAILED, null),
				new JnrTestResult(testName + "-Result2", (i + 2) % 2 == 0 ? JnrTestResultStatus.SUCCESS : JnrTestResultStatus.FAILED, null)
			);

			assertThat(results)
				.containsKey(testName)
				.extractingByKey(testName, InstanceOfAssertFactories.list(JnrTestResult.class))
				.containsExactlyElementsOf(expectedResults);
		}
		assertThat(recorder.isSuccess()).isFalse();
	}

	@Test
	void shouldHandleRunnableLifecycleEventsForNonTestKind() {
		final JnrTestThreadSafeRecorder recorder = new JnrTestThreadSafeRecorder();
		recorder.withElapsedTime(true);
		
		// Create a lifecycle event for BEFORE_ALL (not TEST)
		final var event = new JnrTestRunnableLifecycleEvent(
			"before all",
			JnrTestRunnableKind.BEFORE_ALL,
			JnrTestRunnableStatus.START
		);
		
		// Notify recorder - should be ignored
		recorder.notify(event);
		
		// Total time should still be zero since event kind is not TEST
		assertThat(recorder.getTotalTime()).isZero();
	}

	@Test
	void shouldHandleLifecycleEventsForNonStartStatus() {
		final JnrTestThreadSafeRecorder recorder = new JnrTestThreadSafeRecorder();
		
		// Create a lifecycle event with END status (not START)
		final var event = new JnrTestLifecycleEvent("test class", JnrTestStatus.END);
		
		// Notify recorder - should not create any entry
		recorder.notify(event);
		
		// Should not create any entry in results
		assertThat(recorder.getResults()).isEmpty();
	}
}
