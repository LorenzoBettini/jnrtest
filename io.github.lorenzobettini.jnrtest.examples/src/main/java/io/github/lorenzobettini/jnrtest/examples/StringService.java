package io.github.lorenzobettini.jnrtest.examples;

import java.util.Collection;

public class StringService {

	private StringRepository repository;

	public StringService(StringRepository repository) {
		this.repository = repository;
	}

	public Collection<String> allToUpperCase() {
		return repository.findAll().stream()
			.map(String::toUpperCase)
			.toList();
	}
}
