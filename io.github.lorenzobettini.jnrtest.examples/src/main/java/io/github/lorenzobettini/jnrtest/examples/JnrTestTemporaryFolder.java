package io.github.lorenzobettini.jnrtest.examples;

import java.io.File;
import java.nio.file.Files;

import io.github.lorenzobettini.jnrtest.core.JnrTestCase;
import io.github.lorenzobettini.jnrtest.core.JnrTestRunnableKind;

/**
 * Similar to JUnit TemporaryFolder
 * 
 * @author Lorenzo Bettini
 *
 */
public class JnrTestTemporaryFolder {

	private JnrTestCase testCase;

	private File temporaryFolder;

	/**
	 * The temporary folder will be created before each test and removed recursively
	 * after each test.
	 * 
	 * @param testCase
	 */
	public JnrTestTemporaryFolder(JnrTestCase testCase) {
		this(testCase, JnrTestRunnableKind.TEST);
	}

	/**
	 * The temporary folder will be created before each test and removed recursively
	 * after each test, unless {@link JnrTestRunnableKind#BEFORE_ALL} is passed: in
	 * that case the temporary folder will be created once before all tests and
	 * removed recursively after all tests.
	 * 
	 * @param testCase
	 * @param kind
	 */
	public JnrTestTemporaryFolder(JnrTestCase testCase, JnrTestRunnableKind kind) {
		this.testCase = testCase;
		if (kind == JnrTestRunnableKind.BEFORE_ALL) {
			this.testCase.getStore().beforeAll("create temporary folder",
				() ->
					temporaryFolder = 
						Files.createTempDirectory("jnrtest-temp-folder").toFile()
			);
			this.testCase.getStore().afterAll("delete temporary folder",
				() -> delete()
			);
		} else {
			this.testCase.getStore().beforeEach("create temporary folder",
				() ->
					temporaryFolder = 
						Files.createTempDirectory("jnrtest-temp-folder").toFile()
			);
			this.testCase.getStore().afterEach("delete temporary folder",
				() -> delete()
			);
		}
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
		file.delete();
	}
}
