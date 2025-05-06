package io.github.lorenzobettini.jnrtest.core;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

/**
 * Thread-safe implementation of JnrTestListener for reporting on standard output.
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestThreadSafeConsoleReporter implements JnrTestListener {

	private final ThreadLocal<JnrTestConsoleReporter> currentReporter = new ThreadLocal<>();
	private final ThreadLocal<ByteArrayOutputStream> currentOutputStream = new ThreadLocal<>();
	private final ThreadLocal<String> currentKey = new ThreadLocal<>();
    private boolean withElapsedTime = false;

    public JnrTestThreadSafeConsoleReporter withElapsedTime() {
        this.withElapsedTime = true;
        return this;
    }

	@Override
	public void notify(JnrTestCaseLifecycleEvent event) {
		if (event.status() == JnrTestCaseStatus.START) {
			String key = event.toString();
			currentKey.set(key);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			PrintStream printStream = new PrintStream(outputStream);
			currentOutputStream.set(outputStream);
			currentReporter.set(new JnrTestConsoleReporter(printStream).withElapsedTime(withElapsedTime));
            currentReporter.get().notify(event);
		} else if (event.status() == JnrTestCaseStatus.END) {
			JnrTestConsoleReporter reporter = currentReporter.get();
            reporter.notify(event);
			ByteArrayOutputStream outputStream = currentOutputStream.get();
			System.out.print(outputStream.toString());
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
