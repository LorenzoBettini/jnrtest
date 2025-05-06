package io.github.lorenzobettini.jnrtest.examples;

import java.util.Collection;

import com.google.inject.Inject;

/**
 * Provides string-related services.
 * 
 * @author Lorenzo Bettini
 */
public class StringService {

	private StringRepository repository;

	@Inject
	public StringService(StringRepository repository) {
		this.repository = repository;
	}

	public Collection<String> allToUpperCase() {
		return repository.findAll().stream()
			.map(String::toUpperCase)
			.toList();
	}
}
