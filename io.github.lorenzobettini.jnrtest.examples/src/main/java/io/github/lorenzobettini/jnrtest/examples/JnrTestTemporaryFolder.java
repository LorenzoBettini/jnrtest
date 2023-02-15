package io.github.lorenzobettini.jnrtest.examples;

import java.io.File;
import java.nio.file.Files;

import io.github.lorenzobettini.jnrtest.core.JnrTestCase;

/**
 * Similar to JUnit TemporaryFolder
 * 
 * @author Lorenzo Bettini
 *
 */
public class JnrTestTemporaryFolder {

	private JnrTestCase testCase;

	private File temporaryFolder;

	public JnrTestTemporaryFolder(JnrTestCase testCase) {
		this.testCase = testCase;
		this.testCase.getStore().beforeEach("create temporary folder",
			() ->
				temporaryFolder = Files.createTempDirectory("jnrtest-temp-folder").toFile()
		);
		this.testCase.getStore().afterEach("delete temporary folder",
			() -> delete()
		);
	}

	public File getTemporaryFolder() {
		return temporaryFolder;
	}

	private void delete() {
		recursiveDelete(temporaryFolder);
	}

	private void recursiveDelete(File file) {
		File[] files= file.listFiles();
		if (files != null)
			for (File each : files)
				recursiveDelete(each);
		file.delete();
	}
}
