package io.github.lorenzobettini.jnrtest.core;

/**
 * Interface for reporters in the JnrTest framework.
 * <p>
 * Provides methods for configuring reporting options such as elapsed time and summary-only output.
 * Implementations should return themselves for fluent configuration.
 *
 * @param <T> the type of the reporter, for fluent API usage
 * @author Lorenzo Bettini
 */
public interface JnrTestReporterInterface<T extends JnrTestReporterInterface<T>> extends JnrTestListener {

	/**
	 * Enables reporting of elapsed time.
	 *
	 * @return this reporter instance with elapsed time enabled
	 */
	default T withElapsedTime() {
		return withElapsedTime(true);
	}

	/**
	 * Enables reporting of only summaries.
	 *
	 * @return this reporter instance with only summaries enabled
	 */
	default T withOnlySummaries() {
		return withOnlySummaries(true);
	}

	/**
	 * Configures whether to report only summaries.
	 *
	 * @param onlySummaries true to report only summaries, false otherwise
	 * @return this reporter instance with the specified summary reporting option
	 */
	T withOnlySummaries(boolean onlySummaries);

	/**
	 * Configures whether to report elapsed time.
	 *
	 * @param withElapsedTime true to report elapsed time, false otherwise
	 * @return this reporter instance with the specified elapsed time reporting option
	 */
	T withElapsedTime(boolean withElapsedTime);

}