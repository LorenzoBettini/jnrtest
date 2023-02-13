package io.github.lorenzobettini.jnrtest.core;

public class JnrTestStandardReporter extends JnrTestListenerAdapter {

	private int succeeded;
	private int failed;
	private int errors;

	private void reset() {
		succeeded = 0;
		failed = 0;
		errors = 0;
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
					succeeded, failed, errors));
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
		show(result.toString());
	}

	public void show(String message) {
		System.out.println(message);
	}

}
