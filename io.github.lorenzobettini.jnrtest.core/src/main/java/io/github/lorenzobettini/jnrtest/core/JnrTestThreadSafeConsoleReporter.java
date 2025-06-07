package io.github.lorenzobettini.jnrtest.core;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

/**
 * Thread-safe implementation of JnrTestListener for reporting on standard
 * output.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestThreadSafeConsoleReporter implements JnrTestReporterInterface {

	private final ThreadLocal<JnrTestConsoleReporter> currentReporter = new ThreadLocal<>();
	private final ThreadLocal<ByteArrayOutputStream> currentOutputStream = new ThreadLocal<>();
	private final ThreadLocal<String> currentKey = new ThreadLocal<>();
	private boolean withElapsedTime = false;
	private boolean onlySummaries = false;

	@Override
	public JnrTestThreadSafeConsoleReporter withElapsedTime(boolean withElapsedTime) {
		this.withElapsedTime = withElapsedTime;
		return this;
	}

	@Override
	public JnrTestThreadSafeConsoleReporter withOnlySummaries(boolean onlySummaries) {
		this.onlySummaries = onlySummaries;
		return this;
	}

	@Override
	public void notify(JnrTestLifecycleEvent event) {
		if (event.status() == JnrTestStatus.START) {
			String key = event.toString();
			currentKey.set(key);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			PrintStream printStream = new PrintStream(outputStream);
			currentOutputStream.set(outputStream);
			currentReporter.set(new JnrTestConsoleReporter(printStream)
					.withElapsedTime(withElapsedTime)
					.withOnlySummaries(onlySummaries));
			currentReporter.get().notify(event);
		} else { // i.e., JnrTestStatus.END
			JnrTestConsoleReporter reporter = currentReporter.get();
			reporter.notify(event);
			ByteArrayOutputStream outputStream = currentOutputStream.get();
			System.out.print(outputStream.toString()); // NOSONAR
			currentReporter.remove();
			currentOutputStream.remove();
			currentKey.remove();
		}
	}

	@Override
	public void notify(JnrTestRunnableLifecycleEvent event) {
		JnrTestConsoleReporter reporter = currentReporter.get();
		reporter.notify(event);
	}

	@Override
	public void notify(JnrTestResult result) {
		JnrTestConsoleReporter reporter = currentReporter.get();
		reporter.notify(result);
	}
}
