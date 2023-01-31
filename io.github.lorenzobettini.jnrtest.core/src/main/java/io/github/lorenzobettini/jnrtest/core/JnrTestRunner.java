package io.github.lorenzobettini.jnrtest.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Runs the tests of {@link JnrTestCase}; the actual test execution is
 * performed by {@link #execute()}.
 * 
 * @author Lorenzo Bettini
 *
 */
public class JnrTestRunner {

	private List<JnrTestCase> testCases = new ArrayList<>();

	private Set<JnrTestCase> alreadySpecified = new HashSet<>();

	private List<JnrTestExtension> testExtensions = new ArrayList<>();

	public JnrTestRunner testCase(JnrTestCase testCase) {
		testCases.add(testCase);
		return this;
	}

	public void execute() {
		for (var testCase : testCases) {
			if (!alreadySpecified.contains(testCase)) {
				testCase.specify();
				alreadySpecified.add(testCase);
			}
			execute(testCase);
		}
	}

	private void execute(JnrTestCase testCase) {
		for (var beforeAll : testCase.getBeforeAllRunnables()) {
			executeSafely(beforeAll);
		}
		for (var runnableSpecification : testCase.getRunnableSpecifications()) {
			for (var extension : testExtensions) {
				extension.beforeTest(testCase);
			}
			for (var beforeEach : testCase.getBeforeEachRunnables()) {
				executeSafely(beforeEach);
			}
			executeSafely(runnableSpecification.testRunnable());
			for (var afterEach : testCase.getAfterEachRunnables()) {
				executeSafely(afterEach);
			}
			for (var extension : testExtensions) {
				extension.afterTest(testCase);
			}
		}
		for (var afterAll : testCase.getAfterAllRunnables()) {
			executeSafely(afterAll);
		}
	}

	private void executeSafely(JnrTestRunnable testRunnable) {
		try {
			testRunnable.runTest();
		} catch (Exception e) {
			// TODO report it
		} catch (AssertionError error) {
			// TODO report it
		}
	}

	public void extendWith(JnrTestExtension testDecorator) {
		testExtensions.add(testDecorator);
	}

}
