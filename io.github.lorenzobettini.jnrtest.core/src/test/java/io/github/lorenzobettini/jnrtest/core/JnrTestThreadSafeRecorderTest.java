package io.github.lorenzobettini.jnrtest.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
		assertTrue(results.containsKey("Test1"));
		assertEquals(1, results.get("Test1").size());
		assertEquals(JnrTestResultStatus.SUCCESS, results.get("Test1").get(0).status());
		assertTrue(recorder.isSuccess());
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
		assertEquals(threadCount, results.size());
		results.forEach((key, value) -> {
			assertEquals(1, value.size());
			assertEquals(JnrTestResultStatus.SUCCESS, value.get(0).status());
		});
		assertTrue(recorder.isSuccess());
	}

	@Test
	public void testMultiThreadedFailures() throws InterruptedException {
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
							: new JnrTestResult(testName, JnrTestResultStatus.FAILED, new RuntimeException("Failure"));

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
		assertEquals(threadCount, results.size());
		assertFalse(recorder.isSuccess());
	}
}
