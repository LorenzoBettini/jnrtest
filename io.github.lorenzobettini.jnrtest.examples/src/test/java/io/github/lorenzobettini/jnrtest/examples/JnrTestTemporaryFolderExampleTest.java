package io.github.lorenzobettini.jnrtest.examples;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import io.github.lorenzobettini.jnrtest.core.JnrTest;

public class JnrTestTemporaryFolderExampleTest extends JnrTest {

	private JnrTestTemporaryFolder testTemporaryFolder;

	public JnrTestTemporaryFolderExampleTest() {
		super("JnrTestTemporaryFolder example");
		this.testTemporaryFolder = new JnrTestTemporaryFolder(this);
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
		test("temporary folder is empty",
			() -> {
				// because the temporary folder is created before EACH test
				assertThat(testTemporaryFolder.getTemporaryFolder())
					.isEmptyDirectory();
			}
		);
	}

}
