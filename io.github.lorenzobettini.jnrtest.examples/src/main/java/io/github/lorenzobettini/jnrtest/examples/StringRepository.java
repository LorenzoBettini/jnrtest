package io.github.lorenzobettini.jnrtest.examples;

import java.util.Collection;

public interface StringRepository {

	Collection<String> findAll();

	void saveAll(Collection<String> strings);

	void deleteAll();
}
