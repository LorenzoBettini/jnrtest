package io.github.lorenzobettini.jnrtest.examples;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.google.inject.Inject;

import io.github.lorenzobettini.jnrtest.core.JnrTest;

public class StringServiceWithGuiceTestCase extends JnrTest {

	@Inject
	private StringRepository repository;

	@Inject
	private StringService service;

	public StringServiceWithGuiceTestCase() {
		super("Guice extension test class");
	}

	@Override
	protected void specify() {
		beforeEach("clear repository", () ->
			repository.deleteAll());
		test("when repository is empty", () -> {
			assertThat(service.allToUpperCase())
				.isEmpty();
		});
		test("when repository is not empty", () -> {
			repository.saveAll(List.of("first", "second"));
			assertThat(service.allToUpperCase())
				.containsExactlyInAnyOrder("FIRST", "SECOND");
		});
		test("when repository is empty again", () -> {
			assertThat(service.allToUpperCase())
				.isEmpty();
		});
	}

}
