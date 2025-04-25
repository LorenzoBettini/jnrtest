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
		JnrTestResult successResult = new JnrTestResult("Test1", JnrTestResultStatus.SUCCESS, null);

		recorder.notify(startEvent);
		recorder.notify(successResult);
		recorder.notify(endEvent);

		Map<String, List<JnrTestResult>> results = recorder.getResults();
		assertThat(results)
			.containsOnlyKeys("Test1")
			.extractingByKey("Test1", InstanceOfAssertFactories.list(JnrTestResult.class))
			.containsExactly(successResult);
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
					JnrTestResult successResult = new JnrTestResult(testName, JnrTestResultStatus.SUCCESS, null);

					recorder.notify(startEvent);
					recorder.notify(successResult);
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
			assertThat(results)
				.containsKey(testName)
				.extractingByKey(testName, InstanceOfAssertFactories.list(JnrTestResult.class))
				.containsExactly(new JnrTestResult(testName, JnrTestResultStatus.SUCCESS, null));
		}
		assertThat(recorder.isSuccess()).isTrue();
	}

	@Test
	public void testMultiThreadedFailures() throws InterruptedException {
        // Even in case of failure, pass a null Throwable because it does not
        // implement equals and the verification would fail

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

					JnrTestResult result = threadIndex % 2 == 0
							? new JnrTestResult(testName, JnrTestResultStatus.SUCCESS, null)
							: new JnrTestResult(testName, JnrTestResultStatus.FAILED, null);

					recorder.notify(startEvent);
					recorder.notify(result);
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
			JnrTestResult expectedResult = i % 2 == 0
					? new JnrTestResult(testName, JnrTestResultStatus.SUCCESS, null)
					: new JnrTestResult(testName, JnrTestResultStatus.FAILED, null); // Use null for Throwable
			assertThat(results)
				.containsKey(testName)
				.extractingByKey(testName, InstanceOfAssertFactories.list(JnrTestResult.class))
				.containsExactly(expectedResult);
		}
		assertThat(recorder.isSuccess()).isFalse();
	}
}
