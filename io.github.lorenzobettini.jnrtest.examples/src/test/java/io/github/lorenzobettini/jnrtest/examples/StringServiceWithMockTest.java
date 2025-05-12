package io.github.lorenzobettini.jnrtest.examples;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import io.github.lorenzobettini.jnrtest.core.JnrTest;

public class StringServiceWithMockTest extends JnrTest {

	@Mock
	private StringRepository repository;

	@InjectMocks
	private StringService service;

	public StringServiceWithMockTest() {
		super("Mockito extension test class");
	}

	@Override
	protected void specify() {
		test("when repository is empty", () -> {
			assertThat(service.allToUpperCase())
				.isEmpty();
		});
		test("when repository is not empty", () -> {
			when(repository.findAll())
				.thenReturn(List.of("first", "second"));
			assertThat(service.allToUpperCase())
				.containsExactlyInAnyOrder("FIRST", "SECOND");
		});
	}

}
