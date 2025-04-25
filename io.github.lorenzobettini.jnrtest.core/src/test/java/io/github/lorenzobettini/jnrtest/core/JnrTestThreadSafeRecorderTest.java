package io.github.lorenzobettini.jnrtest.core;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

public class JnrTestThreadSafeRecorderTest {

	@Test
	public void testSingleThreadedRecording() {
		JnrTestThreadSafeRecorder recorder = new JnrTestThreadSafeRecorder();

		JnrTestCaseLifecycleEvent startEvent = new JnrTestCaseLifecycleEvent("Test1", JnrTestCaseStatus.START);
		JnrTestCaseLifecycleEvent endEvent = new JnrTestCaseLifecycleEvent("Test1", JnrTestCaseStatus.END);

		recorder.notify(startEvent);

		// Notify multiple results
		List<JnrTestResult> expectedResults = List.of(
			new JnrTestResult("Test1-Result0", JnrTestResultStatus.SUCCESS, null),
			new JnrTestResult("Test1-Result1", JnrTestResultStatus.SUCCESS, null),
			new JnrTestResult("Test1-Result2", JnrTestResultStatus.SUCCESS, null)
		);
		expectedResults.forEach(recorder::notify);

		recorder.notify(endEvent);

		Map<String, List<JnrTestResult>> results = recorder.getResults();
		assertThat(results)
			.containsOnlyKeys("Test1")
			.extractingByKey("Test1", InstanceOfAssertFactories.list(JnrTestResult.class))
			.containsExactlyElementsOf(expectedResults);
		assertThat(recorder.isSuccess()).isTrue();
	}

	@Test
	public void testMultiThreadedRecording() throws InterruptedException {
		JnrTestThreadSafeRecorder recorder = new JnrTestThreadSafeRecorder();

		int threadCount = 10;
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			final int threadIndex = i;
			executor.submit(() -> {
				try {
					String testName = "Test" + threadIndex;
					JnrTestCaseLifecycleEvent startEvent = new JnrTestCaseLifecycleEvent(testName, JnrTestCaseStatus.START);
					JnrTestCaseLifecycleEvent endEvent = new JnrTestCaseLifecycleEvent(testName, JnrTestCaseStatus.END);

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
	public void testMultiThreadedFailures() throws InterruptedException {
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
					JnrTestCaseLifecycleEvent startEvent = new JnrTestCaseLifecycleEvent(testName, JnrTestCaseStatus.START);
					JnrTestCaseLifecycleEvent endEvent = new JnrTestCaseLifecycleEvent(testName, JnrTestCaseStatus.END);

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
}
