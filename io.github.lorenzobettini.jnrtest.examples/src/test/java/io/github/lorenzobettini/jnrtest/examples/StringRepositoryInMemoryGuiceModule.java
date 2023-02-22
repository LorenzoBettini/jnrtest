package io.github.lorenzobettini.jnrtest.examples;

import java.util.ArrayList;
import java.util.Collection;

import com.google.inject.Binder;

public class StringRepositoryInMemoryGuiceModule implements com.google.inject.Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(StringRepository.class).toInstance(new StringRepository() {
			private Collection<String> db = new ArrayList<>();

			@Override
			public Collection<String> findAll() {
				return this.db;
			}

			@Override
			public void saveAll(Collection<String> strings) {
				db.addAll(strings);
			}

			@Override
			public void deleteAll() {
				db.clear();
			}
		});
	}

}
