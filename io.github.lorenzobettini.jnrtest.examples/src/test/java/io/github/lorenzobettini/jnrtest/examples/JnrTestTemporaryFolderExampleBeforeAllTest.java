package io.github.lorenzobettini.jnrtest.examples;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import io.github.lorenzobettini.jnrtest.core.JnrTest;
import io.github.lorenzobettini.jnrtest.core.JnrTestRunnableKind;

public class JnrTestTemporaryFolderExampleBeforeAllTest extends JnrTest {

	private JnrTestTemporaryFolder testTemporaryFolder;

	public JnrTestTemporaryFolderExampleBeforeAllTest() {
		super("JnrTestTemporaryFolder BEFORE_ALL example");
		this.testTemporaryFolder = new JnrTestTemporaryFolder(this, JnrTestRunnableKind.BEFORE_ALL);
	}

	@Override
	protected void specify() {
		test("temporary folder exists",
			() -> assertThat(testTemporaryFolder.getTemporaryFolder())
				.exists()
		);
		test("temporary folder can be used",
			() -> {
				File temporaryFolder = testTemporaryFolder.getTemporaryFolder();
				var file = File.createTempFile("a-test-file", null, temporaryFolder);
				assertThat(file)
					.isFile()
					.exists();
			}
		);
		test("temporary folder is NOT empty",
			() -> {
				// because the temporary folder is created once before ALL tests
				assertThat(testTemporaryFolder.getTemporaryFolder())
					.isNotEmptyDirectory();
			}
		);
	}

}
