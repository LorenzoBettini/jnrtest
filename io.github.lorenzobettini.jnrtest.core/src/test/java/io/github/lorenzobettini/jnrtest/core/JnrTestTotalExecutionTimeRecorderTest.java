package io.github.lorenzobettini.jnrtest.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JnrTestTotalExecutionTimeRecorderTest {

	@Test
	void testTotalTime() throws InterruptedException {
		var recorder = new JnrTestTotalExecutionTimeRecorder();
		recorder.notify(new JnrTestLifecycleEvent("begin", JnrTestStatus.BEGIN));
		Thread.sleep(50); // NOSONAR we must ensure that the total time is positive
		recorder.notify(new JnrTestLifecycleEvent("finish", JnrTestStatus.FINISH));
		assertThat(recorder.getTotalTime()).isPositive();
	}

}
