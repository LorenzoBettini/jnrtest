package io.github.lorenzobettini.jnrtest.tools;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class JnrTestDiscoveryTest {

	@Test
	void testDiscovery() throws IOException {
		var discoveredTests = JnrTestDiscovery.discover("src/test/inputs/com/examples");
		assertThat(discoveredTests)
			.containsExactlyInAnyOrder(
				"com.examples.discovery.MyJnrTest",
				"com.examples.discovery.MyJnrTest2",
				"com.examples.discovery.subpackage.MyConcreteJnrTest"
			);
	}

}
