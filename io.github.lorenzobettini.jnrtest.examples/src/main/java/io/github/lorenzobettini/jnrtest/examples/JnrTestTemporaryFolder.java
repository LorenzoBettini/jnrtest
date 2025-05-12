package io.github.lorenzobettini.jnrtest.examples;

import java.io.File;
import java.nio.file.Files;

import io.github.lorenzobettini.jnrtest.core.JnrTest;
import io.github.lorenzobettini.jnrtest.core.JnrTestRunnableKind;
import io.github.lorenzobettini.jnrtest.core.JnrTestRunnableSpecification;

/**
 * Similar to JUnit TemporaryFolder
 * 
 * @author Lorenzo Bettini
 *
 */
public class JnrTestTemporaryFolder {

	private File temporaryFolder;

	/**
	 * The temporary folder will be created before each test and removed recursively
	 * after each test.
	 * 
	 * @param testCase
	 */
	public JnrTestTemporaryFolder(JnrTest testCase) {
		this(testCase, JnrTestRunnableKind.TEST);
	}

	/**
	 * The temporary folder will be created before each test and removed recursively
	 * after each test, unless {@link JnrTestRunnableKind#BEFORE_ALL} is passed: in
	 * that case the temporary folder will be created once before all tests and
	 * removed recursively after all tests.
	 * 
	 * In both cases, the "before" execution is ensured to be executed before
	 * possible test class's "before" executions, since the latter can rely on the
	 * temporary folder to be already created. Similarly, the "after" is executed
	 * after possible test class's "after" executions, since the latter might still
	 * need the temporary folder.
	 * 
	 * @param testCase
	 * @param kind
	 */
	public JnrTestTemporaryFolder(JnrTest testCase, JnrTestRunnableKind kind) {
		var before = testCase.getStore().getBeforeEachRunnables();
		var after = testCase.getStore().getAfterEachRunnables();
		if (kind == JnrTestRunnableKind.BEFORE_ALL) {
			before = testCase.getStore().getBeforeAllRunnables();
			after = testCase.getStore().getAfterAllRunnables();
		}
		// add to the head of the list, i.e., before test class's "before" executions
		before.add(0, new JnrTestRunnableSpecification("create temporary folder",
			() ->
				temporaryFolder = 
					Files.createTempDirectory("jnrtest-temp-folder").toFile()
		));
		// add to the end of the list
		after.add(new JnrTestRunnableSpecification("delete temporary folder",
			this::delete
		));
	}

	public File getTemporaryFolder() {
		return temporaryFolder;
	}

	private void delete() {
		recursiveDelete(temporaryFolder);
	}

	private void recursiveDelete(File file) {
		var files= file.listFiles();
		if (files != null)
			for (var each : files)
				recursiveDelete(each);
		file.delete(); // NOSONAR we ignore the outcome
	}
}
