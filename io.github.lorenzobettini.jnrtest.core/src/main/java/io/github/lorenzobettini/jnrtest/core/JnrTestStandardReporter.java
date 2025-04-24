package io.github.lorenzobettini.jnrtest.core;

public class JnrTestStandardReporter implements JnrTestListener {

	private int succeeded;
	private int failed;
	private int errors;

	private boolean withElapsedTime = false;
	private long startTime;
	private long elapsedTime;
	private long totalTime = 0;

	public JnrTestStandardReporter withElapsedTime() {
		withElapsedTime = true;
		return this;
	}

	private void reset() {
		succeeded = 0;
		failed = 0;
		errors = 0;
		totalTime = 0;
	}

	@Override
	public void notify(JnrTestCaseLifecycleEvent event) {
		if (event.status() == JnrTestCaseStatus.START) {
			reset();
			show(event.toString());
		}
		if (event.status() == JnrTestCaseStatus.END)
			show(String.format("Tests run: %d, Succeeded: %d, Failures: %d, Errors: %d",
					succeeded + failed + errors,
					succeeded, failed, errors) + (
						withElapsedTime ? String.format(" - Time elapsed: %f s", (float) totalTime/3600) : ""));
	}

	@Override
	public void notify(JnrTestRunnableLifecycleEvent event) {
		if (!withElapsedTime || event.kind() != JnrTestRunnableKind.TEST)
			return;
		if (event.status() == JnrTestRunnableStatus.START)
			this.startTime = System.currentTimeMillis();
		else {
			this.elapsedTime = System.currentTimeMillis() - startTime;
			this.totalTime += elapsedTime;
		}
	}

	@Override
	public void notify(JnrTestResult result) {
		switch (result.status()) {
		case SUCCESS: {
			succeeded++;
			break;
		}
		case FAILED: {
			failed++;
			result.throwable().printStackTrace();
			break;
		}
		case ERROR: {
			errors++;
			result.throwable().printStackTrace();
		}
		}
		show(result.toString() + (
			withElapsedTime ? String.format(" - Time elapsed: %f s", (float) elapsedTime/3600) : ""));
	}

	public void show(String message) {
		System.out.println(message); // NOSONAR we really want to print to the console
	}

}
