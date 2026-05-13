# JnrTest
An experimental framework for automated tests in Java without reflection

[![Maven Central](https://img.shields.io/maven-central/v/io.github.lorenzobettini.jnrtest/io.github.lorenzobettini.jnrtest.core.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.lorenzobettini.jnrtest/io.github.lorenzobettini.jnrtest.core)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=LorenzoBettini_jnrtest&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=LorenzoBettini_jnrtest)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=LorenzoBettini_jnrtest&metric=coverage)](https://sonarcloud.io/summary/new_code?id=LorenzoBettini_jnrtest)

# Goals

- No reflection (in our code)
- No static methods

JnrTest is designed around explicit composition instead of runtime magic.
Test classes are instantiated directly in user code, registered directly on a runner, and executed through ordinary Java objects.
There is no hidden runtime discovery phase, no reflective invocation model in the framework, and no annotation-driven engine deciding what should run behind the scenes.

That keeps the execution model straightforward to reason about:

- test registration is explicit and visible in code
- execution order is controlled by the runner and by the order in which tests and hooks are declared
- extension points are plain Java abstractions such as listeners, recorders, reporters, and extensions
- code-generation tools can assist migration and bootstrapping without changing the runtime programming model

The second design goal, avoiding static methods in the framework API, follows the same philosophy.
Runners, executors, reporters, and extensions are regular objects that can be instantiated, configured, composed, subclassed, or replaced.

JnrTest is also intentionally assertion-agnostic.
It does not provide its own assertion library.
Test bodies are just Java lambdas, so you are expected to use the assertion library you already use in your project, for example JUnit Jupiter assertions, AssertJ, or Hamcrest. The examples in this repository use mostly JUnit Jupiter assertions, and some examples also use AssertJ.

---

## Table of Contents

- [Goals](#goals)
- [Overview](#overview)
- [Getting Started](#getting-started)
- [Writing Tests](#writing-tests)
  - [Basic Tests](#basic-tests)
  - [Lifecycle Hooks](#lifecycle-hooks)
  - [Parameterized Tests](#parameterized-tests)
- [Running Tests](#running-tests)
  - [JnrTestRunner](#jnrtestrunner)
  - [JnrTestConsoleExecutor](#jnrtestconsoleexecutor)
  - [JnrTestConsoleParallelExecutor](#jnrtestconsoleparallelexecutor)
- [Filtering Tests](#filtering-tests)
- [Listeners](#listeners)
- [Recording Results](#recording-results)
- [Reporting](#reporting)
- [Extensions](#extensions)
  - [JnrTestExtension](#jnrtestextension)
  - [Mockito Extension Example](#mockito-extension-example)
  - [Guice Extension Example](#guice-extension-example)
  - [Temporary Folder Example](#temporary-folder-example)
- [Tools Module](#tools-module)
  - [JnrTestDiscovery](#jnrtestdiscovery)
  - [JnrTestMainGenerator](#jnrtestmaingenerator)
  - [JUnit5ToJnrTestGenerator](#junit5tojnrtestgenerator)
- [API Reference](#api-reference)

---

## Overview

**JnrTest** is a Java testing framework built around two core principles:

- **No reflection** — test discovery and execution are completely explicit; no classpath scanning or annotation processing at runtime.
- **No static methods** — the entire API is instance-based, enabling clean composition and subclassing.

Tests are defined by subclassing `JnrTest` and overriding `specify()`. The framework ships with sequential and parallel executors, a flexible listener/reporter API, support for parameterized tests, lifecycle hooks, and a tools module for code generation.

JnrTest does not include an assertion library. In practice, test code normally imports assertions from existing libraries such as JUnit Jupiter or AssertJ.

JnrTest requires **Java 21**.

---

## Getting Started

Add the core dependency to your Maven project:

```xml
<dependency>
    <groupId>io.github.lorenzobettini.jnrtest</groupId>
    <artifactId>io.github.lorenzobettini.jnrtest.core</artifactId>
    <version><!-- see Maven Central badge above --></version>
</dependency>
```

JnrTest only provides the test definition and execution model.
It does **not** provide assertion methods such as `assertEquals`, `assertTrue`, or fluent assertion APIs.
Add the assertion library you want to use explicitly.

If you want to follow the examples in this repository, add **JUnit Jupiter**:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>6.0.3</version>
    <scope>test</scope>
</dependency>
```

If you also want fluent assertions like some examples in the `examples` module,
add **AssertJ** too:

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.27.7</version>
    <scope>test</scope>
</dependency>
```

To use the code-generation tools, also add:

```xml
<dependency>
    <groupId>io.github.lorenzobettini.jnrtest</groupId>
    <artifactId>io.github.lorenzobettini.jnrtest.tools</artifactId>
    <version><!-- same version --></version>
</dependency>
```

---

## Writing Tests

### Basic Tests

Create a test class by extending `JnrTest` and overriding `specify()`. Call `test(description, runnable)` for each test case:

```java
import io.github.lorenzobettini.jnrtest.core.JnrTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FactorialJnrTest extends JnrTest {

    private Factorial factorial;

    public FactorialJnrTest() {
        super("tests for factorial");
    }

    @Override
    protected void specify() {
        beforeAll("create factorial SUT", () -> factorial = new Factorial());
        test("case 0", () -> assertEquals(1, factorial.compute(0)));
        test("case 1", () -> assertEquals(1, factorial.compute(1)));
        test("case 2", () -> assertEquals(2, factorial.compute(2)));
        test("case 3", () -> assertEquals(6, factorial.compute(3)));
    }
}
```

The constructor takes a human-readable **description** for the test class. This description is used in reporting and filtering.

The `test()` method takes a description and a `JnrTestRunnable` (a functional interface that may throw `Exception`).

In the example above, `assertEquals` comes from JUnit Jupiter, not from JnrTest.

### Lifecycle Hooks

JnrTest provides four lifecycle hook methods. All hooks accept a description and a `JnrTestRunnable`:

| Method | When executed |
|--------|---------------|
| `beforeAll(description, runnable)` | Once before all tests in the class |
| `beforeEach(description, runnable)` | Before each individual test |
| `afterEach(description, runnable)` | After each individual test |
| `afterAll(description, runnable)` | Once after all tests in the class |

```java
@Override
protected void specify() {
    beforeAll("initialise database", () -> db = Database.connect());
    beforeEach("clear tables", () -> db.clearAll());
    afterEach("log test end", () -> System.out.println("test done"));
    afterAll("close database", () -> db.close());

    test("insert row", () -> { /* ... */ });
    test("query row", () -> { /* ... */ });
}
```

Multiple hooks of the same type can be registered; they are executed in registration order.

### Parameterized Tests

Use `testWithParameters()` to run the same test body against a collection of inputs. Parameters are supplied lazily via a `Supplier<Collection<T>>`.

**Simple parameterized test** (parameter's `toString()` is appended to the description):

```java
testWithParameters("value: ",
    () -> List.of(1, 2, 3, 4),
    value -> assertTrue(value > 0)
);
```

**Parameterized test with custom description**:

```java
testWithParameters("",
    () -> List.of(
        pair(0, 1),
        pair(1, 1),
        pair(2, 2)
    ),
    p -> String.format("factorial(%d) -> %d", p.first(), p.second()),
    p -> assertEquals(p.second(), factorial.compute(p.first()))
);
```

#### `Pair<T1, T2>` and `Triple<T1, T2, T3>`

JnrTest provides two static nested classes for grouping parameters:

```java
import static io.github.lorenzobettini.jnrtest.core.JnrTest.Pair.pair;
import static io.github.lorenzobettini.jnrtest.core.JnrTest.Triple.triple;

// Pair: two values
pair(0, 1)        // Pair<Integer, Integer>

// Triple: three values
triple(2, 3, 6)   // Triple<Integer, Integer, Integer>
```

Example using `Triple`:

```java
testWithParameters("addition test: ",
    () -> List.of(
        triple(1, 2, 3),
        triple(5, 10, 15),
        triple(0, 0, 0)
    ),
    t -> String.format("%d + %d = %d", t.first(), t.second(), t.third()),
    t -> assertEquals(t.third(), t.first() + t.second())
);
```

---

## Running Tests

### JnrTestRunner

`JnrTestRunner` is the low-level executor. Add `JnrTest` instances, optionally register listeners, then call `execute()`:

```java
new JnrTestRunner()
    .add(new FactorialJnrTest())
    .add(new MyStringUtilsJnrTest())
    .execute();
```

`JnrTestRunner` runs test classes **sequentially** by default. It provides method chaining for all configuration calls.

### JnrTestConsoleExecutor

`JnrTestConsoleExecutor` is a higher-level convenience class that wires up a recorder and a reporter automatically:

```java
new JnrTestConsoleExecutor()
    .add(new FactorialJnrTest())
    .add(new MyStringUtilsJnrTest())
    .execute();          // throws RuntimeException if any test fails
```

Or, to get a boolean result without throwing:

```java
boolean allPassed = new JnrTestConsoleExecutor()
    .add(new FactorialJnrTest())
    .executeWithoutThrowing();
```

`executeWithoutThrowing()` prints a results summary including total counts and elapsed time, then returns `true` if all tests passed.

`execute()` calls `executeWithoutThrowing()` and throws `RuntimeException` if there are any failures.

The executor can be subclassed to override the factory methods `createRecorder()`, `createReporter()`, and `createTestRunner()` for custom behaviour.

### JnrTestConsoleParallelExecutor

`JnrTestConsoleParallelExecutor` extends `JnrTestConsoleExecutor` and runs test **classes** in parallel using Java's parallel streams. It automatically substitutes thread-safe implementations of the recorder and reporter:

```java
new JnrTestConsoleParallelExecutor()
    .add(new FactorialJnrTest())
    .add(new MyStringUtilsJnrTest())
    .execute();
```

> **Note:** Individual tests within a single `JnrTest` subclass are still executed sequentially. Parallelism is at the class level.

The underlying `JnrTestParallelRunner` overrides `getTestClassesStream()` to return a parallel stream.

---

## Filtering Tests

Both `JnrTestRunner` and `JnrTestConsoleExecutor` support filtering at two levels:

| Method | Filters |
|--------|---------|
| `classFilter(Predicate<JnrTest>)` | Which test classes to run |
| `specificationFilter(Predicate<JnrTestRunnableSpecification>)` | Which individual tests to run |
| `filterByClassDescription(String pattern)` | Classes whose description matches a regex |
| `filterBySpecificationDescription(String pattern)` | Tests whose description matches a regex |

Multiple filters of the same kind are combined with logical AND.

```java
// Run only the "Calculator" test class
new JnrTestConsoleExecutor()
    .filterByClassDescription(".*Calculator.*")
    .add(new CalculatorTest())
    .add(new StringUtilsTest())
    .execute();

// Run only tests whose description contains "Critical"
new JnrTestConsoleExecutor()
    .filterBySpecificationDescription(".*Critical.*")
    .add(new CalculatorTest())
    .execute();

// Custom predicate filter
new JnrTestConsoleExecutor()
    .classFilter(t -> t.getDescription().startsWith("My"))
    .add(new MyTest())
    .execute();
```

`JnrTestFilters` manages the filter state and can also be used standalone.

---

## Listeners

`JnrTestListener` is notified of three kinds of events:

```java
public interface JnrTestListener {
    void notify(JnrTestLifecycleEvent event);          // class START/END
    void notify(JnrTestRunnableLifecycleEvent event);  // runnable START/END
    void notify(JnrTestResult result);                 // test outcome
}
```

Register listeners on `JnrTestRunner` or `JnrTestConsoleExecutor`:

```java
new JnrTestConsoleExecutor()
    .testListener(myCustomListener)
    .add(new FactorialJnrTest())
    .execute();
```

`JnrTestListenerAdapter` is an abstract adapter that provides empty implementations of all three methods. Extend it to override only what you need:

```java
public class MyTimingListener extends JnrTestListenerAdapter {
    @Override
    public void notify(JnrTestResult result) {
        System.out.println("Test finished: " + result.description());
    }
}
```

#### Event Types

| Type | Fields |
|------|--------|
| `JnrTestLifecycleEvent` | `description`, `status` (`START`/`END`) |
| `JnrTestRunnableLifecycleEvent` | `description`, `kind` (`TEST`/`BEFORE_ALL`/`BEFORE_EACH`/`AFTER_EACH`/`AFTER_ALL`), `status` (`START`/`END`) |
| `JnrTestResult` | `description`, `status` (`SUCCESS`/`FAILED`/`ERROR`), `throwable` |

---

## Recording Results

`JnrTestRecorder` implements `JnrTestListener` and stores results per test class. It is registered automatically by `JnrTestConsoleExecutor`.

```java
JnrTestRecorder recorder = new JnrTestRecorder().withElapsedTime();
// ...
Map<String, List<JnrTestResult>> results = recorder.getResults();
boolean success = recorder.isSuccess();
long totalTime = recorder.getTotalTime();
```

`JnrTestThreadSafeRecorder` is the thread-safe variant used by `JnrTestConsoleParallelExecutor`. It uses `ConcurrentHashMap` and `ThreadLocal` for isolation.

`JnrTestResultAggregator` aggregates results from a recorder into totals:

```java
JnrTestResultAggregator agg = new JnrTestResultAggregator().aggregate(recorder);
System.out.printf("Passed: %d, Failed: %d, Errors: %d%n",
    agg.getSucceeded(), agg.getFailed(), agg.getErrors());
```

---

## Reporting

`JnrTestConsoleReporter` writes test events to a `PrintStream` (defaults to `System.out`). It is registered automatically by `JnrTestConsoleExecutor`.

Configuration options (fluent API):

```java
new JnrTestConsoleReporter()
    .withElapsedTime()      // include elapsed time per test
    .withOnlySummaries();   // suppress individual test lines, show only class summary
```

`JnrTestThreadSafeConsoleReporter` is the thread-safe variant used by `JnrTestConsoleParallelExecutor`. It buffers output per thread and flushes atomically when the test class ends, preventing interleaved output.

---

## Extensions

### JnrTestExtension

`JnrTestExtension` is an abstract class for adding before/after behaviour to a test class without modifying it. Implement `extend(testClass, before, after)` and manipulate the two lists of `JnrTestRunnableSpecification`:

```java
public class MyExtension extends JnrTestExtension {
    @Override
    protected <T extends JnrTest> void extend(
            T testClass,
            List<JnrTestRunnableSpecification> before,
            List<JnrTestRunnableSpecification> after) {
        before.add(new JnrTestRunnableSpecification("set up resource",
            () -> resource = Resource.open()));
        after.add(new JnrTestRunnableSpecification("release resource",
            () -> resource.close()));
    }
}
```

Apply the extension via `extendAll()` (before/after **all** tests) or `extendEach()` (before/after **each** test):

```java
new JnrTestConsoleExecutor()
    .add(new MyExtension().extendEach(new MyTest()))
    .execute();
```

Both `extendAll()` and `extendEach()` return the test class for chaining, so the extended test class can be passed directly to `add()`.

### Mockito Extension Example

`JnrTestMockitoExtension` (in the examples module) opens and closes Mockito mocks around each test:

```java
public class JnrTestMockitoExtension extends JnrTestExtension {
    private AutoCloseable autoCloseable;

    @Override
    protected <T extends JnrTest> void extend(T t,
            List<JnrTestRunnableSpecification> before,
            List<JnrTestRunnableSpecification> after) {
        before.add(new JnrTestRunnableSpecification("open mocks",
            () -> autoCloseable = MockitoAnnotations.openMocks(t)));
        after.add(new JnrTestRunnableSpecification("release mocks",
            () -> autoCloseable.close()));
    }
}
```

Usage:

```java
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
        test("when repository is empty", () ->
            assertThat(service.allToUpperCase()).isEmpty());
        test("when repository is not empty", () -> {
            when(repository.findAll()).thenReturn(List.of("first", "second"));
            assertThat(service.allToUpperCase())
                .containsExactlyInAnyOrder("FIRST", "SECOND");
        });
    }
}

// Wire the extension:
new JnrTestConsoleExecutor()
    .add(new JnrTestMockitoExtension().extendEach(new StringServiceWithMockTest()))
    .execute();
```

### Guice Extension Example

`JnrTestGuiceExtension` (in the examples module) performs Guice injection before all tests:

```java
new JnrTestConsoleExecutor()
    .add(new JnrTestGuiceExtension(new MyGuiceModule())
        .extendAll(new StringServiceWithGuiceTest()))
    .execute();
```

### Temporary Folder Example

`JnrTestTemporaryFolder` (in the examples module) creates a fresh temporary directory before each test (or before all tests) and deletes it afterwards. It is constructed directly in the test class constructor:

```java
public class JnrTestTemporaryFolderExampleTest extends JnrTest {
    private final JnrTestTemporaryFolder testTemporaryFolder;

    public JnrTestTemporaryFolderExampleTest() {
        super("JnrTestTemporaryFolder example");
        this.testTemporaryFolder = new JnrTestTemporaryFolder(this);
    }

    @Override
    protected void specify() {
        test("temporary folder exists", () ->
            assertThat(testTemporaryFolder.getTemporaryFolder()).exists());
        test("temporary folder is empty", () ->
            assertThat(testTemporaryFolder.getTemporaryFolder()).isEmptyDirectory());
    }
}
```

Pass `JnrTestRunnableKind.BEFORE_ALL` to share one folder across all tests:

```java
this.testTemporaryFolder = new JnrTestTemporaryFolder(this, JnrTestRunnableKind.BEFORE_ALL);
```

---

## Tools Module

The `io.github.lorenzobettini.jnrtest.tools` module provides utilities for automated discovery and code generation. It uses the Eclipse JDT compiler for source analysis and [JavaPoet](https://github.com/palantir/javapoet) for code generation. No reflection is used at runtime.

### JnrTestDiscovery

`JnrTestDiscovery` scans a Java source directory and returns a sorted list of fully-qualified class names of all instantiable `JnrTest` subclasses. A class is considered instantiable when it:

- Extends `JnrTest` (directly or indirectly)
- Is `public` and not `abstract`
- Has a `public` no-argument constructor
- Is not a non-static inner class

```java
JnrTestDiscovery discovery = new JnrTestDiscovery();
List<String> testClasses = discovery.discover("src/test/java");
// e.g. ["com.example.FactorialJnrTest", "com.example.StringUtilsJnrTest"]
```

### JnrTestMainGenerator

`JnrTestMainGenerator` combines `JnrTestDiscovery` with code generation to produce a ready-to-run main class. The generated class contains:

- A `fillTestRunner(JnrTestRunner runner)` static method that adds all discovered test instances.
- A `main(String[] args)` method that creates a `JnrTestConsoleExecutor` using the runner.

```java
new JnrTestMainGenerator().generateMain(
    "src/test/java",               // source directory to scan
    "target/generated-sources",   // output directory
    "com.example.JnrTestMain"     // fully-qualified name of the generated class
);
```

**Generated output example:**

```java
package com.example;

import io.github.lorenzobettini.jnrtest.core.JnrTestConsoleExecutor;
import io.github.lorenzobettini.jnrtest.core.JnrTestRunner;

/**
 * This class is generated by the JnrTestMainGenerator tool.
 */
public class JnrTestMain {
    public static void fillTestRunner(JnrTestRunner runner) {
        runner.add(new com.example.FactorialJnrTest());
        runner.add(new com.example.StringUtilsJnrTest());
    }

    public static void main(String[] args) {
        var executor = new JnrTestConsoleExecutor() {
            @Override
            protected JnrTestRunner createTestRunner() {
                var runner = new JnrTestRunner();
                JnrTestMain.fillTestRunner(runner);
                return runner;
            }
        };
        executor.execute();
    }
}
```

### JUnit5ToJnrTestGenerator

`JUnit5ToJnrTestGenerator` reads JUnit Jupiter test source files and produces equivalent `JnrTest` subclass source files. The original files are not modified.

**Supported JUnit annotations:**

| JUnit annotation | Converted to |
|-----------------|--------------|
| `@Test` | `test(methodName, ...)` call |
| `@BeforeAll` | `beforeAll("call methodName", ...)` call |
| `@BeforeEach` | `beforeEach("call methodName", ...)` call |
| `@AfterAll` | `afterAll("call methodName", ...)` call |
| `@AfterEach` | `afterEach("call methodName", ...)` call |
| `@DisplayName("…")` | used as the test description string |

```java
new JUnit5ToJnrTestGenerator().generate(
    "src/test/java",              // input: directory with JUnit test sources
    "target/generated-sources"   // output: directory for generated JnrTest files
);
```

**Input example:**

```java
class CalculatorTest {
    private Calculator calc;

    @BeforeEach
    void setUp() { calc = new Calculator(); }

    @Test
    @DisplayName("Addition should work correctly")
    void testAddition() { assertEquals(4, calc.add(2, 2)); }
}
```

**Generated output:**

```java
public class CalculatorTestJnrTest extends JnrTest {
    public CalculatorTestJnrTest() {
        super("CalculatorTest in JnrTest");
    }

    @Override
    protected void specify() {
        beforeEach("call setUp", () -> {
            calc = new Calculator();
        });
        test("Addition should work correctly", () -> {
            assertEquals(4, calc.add(2, 2));
        });
    }
}
```

---

## API Reference

### Core Classes and Interfaces (`io.github.lorenzobettini.jnrtest.core`)

| Class / Interface | Description |
|-------------------|-------------|
| `JnrTest` | Abstract base class for all test classes. Override `specify()` to define tests and hooks. |
| `JnrTest.Pair<T1,T2>` | Value pair for parameterized tests. Factory method: `Pair.pair(a, b)`. |
| `JnrTest.Triple<T1,T2,T3>` | Value triple for parameterized tests. Factory method: `Triple.triple(a, b, c)`. |
| `JnrTestStore` | Stores test and lifecycle `JnrTestRunnableSpecification` instances for a `JnrTest`. |
| `JnrTestRunnable` | Functional interface for a test body or lifecycle hook; may throw `Exception`. |
| `JnrTestRunnableWithParameters<T>` | Functional interface for a parameterized test body; receives one parameter. |
| `JnrTestRunnableSpecification` | Record pairing a description with a `JnrTestRunnable`. |
| `JnrTestRunner` | Executes a list of `JnrTest` instances sequentially. Supports filters and listeners. |
| `JnrTestParallelRunner` | Subclass of `JnrTestRunner` that executes test classes in parallel. |
| `JnrTestConsoleExecutor` | High-level executor with built-in recorder, reporter, and console output. |
| `JnrTestConsoleParallelExecutor` | Parallel variant of `JnrTestConsoleExecutor` using thread-safe recorder and reporter. |
| `JnrTestFilters` | Manages class-level and specification-level filter predicates. |
| `JnrTestListener` | Interface for observing test lifecycle events, runnable events, and results. |
| `JnrTestListenerAdapter` | Abstract adapter with empty implementations of all `JnrTestListener` methods. |
| `JnrTestRecorderInterface` | Interface for test result recorders; extends `JnrTestListener`. |
| `JnrTestRecorder` | Standard (non-thread-safe) implementation of `JnrTestRecorderInterface`. |
| `JnrTestThreadSafeRecorder` | Thread-safe recorder using `ConcurrentHashMap` and `ThreadLocal`. |
| `JnrTestReporterInterface` | Interface for console reporters; extends `JnrTestListener`. |
| `JnrTestConsoleReporter` | Writes test events and summaries to a `PrintStream`. |
| `JnrTestThreadSafeConsoleReporter` | Thread-safe reporter that buffers output per thread and flushes atomically. |
| `JnrTestResultAggregator` | Aggregates results from a `JnrTestRecorderInterface` into total counts. |
| `JnrTestStatistics` | Tracks test counts and elapsed time for a single test class execution. |
| `JnrTestExtension` | Abstract base for test extensions that add before/after hooks to a `JnrTest`. |
| `JnrTestResult` | Record holding a test's description, `JnrTestResultStatus`, and optional `Throwable`. |
| `JnrTestResultStatus` | Enum: `SUCCESS`, `FAILED`, `ERROR`. |
| `JnrTestLifecycleEvent` | Record for a test class start/end event. |
| `JnrTestRunnableLifecycleEvent` | Record for a runnable (test or hook) start/end event. |
| `JnrTestStatus` | Enum: `START`, `END` (for test class lifecycle). |
| `JnrTestRunnableStatus` | Enum: `START`, `END` (for runnable lifecycle). |
| `JnrTestRunnableKind` | Enum: `TEST`, `BEFORE_ALL`, `BEFORE_EACH`, `AFTER_EACH`, `AFTER_ALL`. |

### Tools Classes (`io.github.lorenzobettini.jnrtest.tools`)

| Class | Description |
|-------|-------------|
| `JnrTestDiscovery` | Scans a Java source directory and discovers all instantiable `JnrTest` subclasses using the Eclipse JDT compiler. |
| `JnrTestMainGenerator` | Generates a main class that discovers and runs all `JnrTest` subclasses in a source directory. |
| `JUnit5ToJnrTestGenerator` | Converts JUnit Jupiter test source files to equivalent `JnrTest` subclass source files. |
