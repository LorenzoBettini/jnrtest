package io.github.lorenzobettini.jnrtest.examples;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileOutputStream;

import io.github.lorenzobettini.jnrtest.core.JnrTest;

/**
 * This shows that the {@link JnrTestTemporaryFolder}'s temporary folder
 * can be used in a test class before and after execution.
 * 
 * @author Lorenzo Bettini
 *
 */
public class JnrTestTemporaryFolderAnotherExampleTestCase extends JnrTest {

	private JnrTestTemporaryFolder testTemporaryFolder;

	private File tempFile;

	public JnrTestTemporaryFolderAnotherExampleTestCase() {
		super("JnrTestTemporaryFolder another example");
		this.testTemporaryFolder = new JnrTestTemporaryFolder(this);
	}

	@Override
	protected void specify() {
		beforeEach("create a temporary file",
			() -> tempFile =
				File.createTempFile("a-test-file", null,
					testTemporaryFolder.getTemporaryFolder())
		);
		afterEach("truncate the temporary file",
			// if the file does not exist, it creates it
			() -> {
				try (FileOutputStream fos = new FileOutputStream(tempFile, true)) {
					fos.getChannel().truncate(0);
				}
			}
		);
		test("temporary file exists",
			() -> assertThat(tempFile)
				.exists()
		);
		test("temporary file can be removed",
			() -> {
				tempFile.delete();
				assertThat(tempFile)
					.doesNotExist();
			}
		);
		test("temporary file is automatically re-created",
			() -> {
				assertThat(tempFile)
				.exists();
			}
		);
		test("temporary file can be written",
			() -> {
				try (FileOutputStream fos = new FileOutputStream(tempFile, true)) {
					fos.write(1);
				}
				assertThat(tempFile)
					.hasSize(1);
			}
		);
		test("temporary file has initially size 0",
			() -> {
				assertThat(tempFile)
					.isEmpty();
			}
		);
	}

}
