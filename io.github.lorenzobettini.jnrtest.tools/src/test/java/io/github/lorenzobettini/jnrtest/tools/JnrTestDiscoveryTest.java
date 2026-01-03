package io.github.lorenzobettini.jnrtest.tools;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class JnrTestDiscoveryTest {

	@Test
	void testDiscovery() throws IOException {
		var discoveredTests = JnrTestDiscovery.discover("src/test/inputs");
		assertThat(discoveredTests)
			.containsExactlyInAnyOrder(
				"com.examples.MyJnrTest",
				"com.examples.MyJnrTest2",
				"com.examples.subpackage.MyConcreteJnrTest"
			);
	}

}
