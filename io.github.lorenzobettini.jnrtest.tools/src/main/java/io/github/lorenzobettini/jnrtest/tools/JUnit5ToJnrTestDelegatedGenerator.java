package io.github.lorenzobettini.jnrtest.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import java.util.Arrays;

/**
 * Generates JnrTest subclasses from JUnit Jupiter test classes using a delegation pattern.
 * <p>
 * For each Java file containing JUnit Jupiter {@code @Test} annotations, this generator
 * creates a corresponding JnrTest subclass with the same name plus the suffix "JnrTest".
 * It also generates a main class for running all the generated JnrTest classes.
 * <p>
 * This generator uses the Java Compiler API ({@link JavaCompiler}) to parse Java files
 * and extract information about test methods and their annotations.
 * <p>
 * <strong>Delegation Pattern:</strong><br>
 * Unlike {@link JUnit5ToJnrTestGenerator}, this version uses a delegated approach where
 * each generated JnrTest class contains an instance of the original test class and delegates
 * test method calls to that instance. This preserves any instance state in the original test class.
 * <p>
 * Example usage:
 * {@snippet :
 * JUnit5ToJnrTestDelegatedGenerator generator = new JUnit5ToJnrTestDelegatedGenerator();
 * List<String> generatedClasses = generator.generate(
 *     "src/test/java",
 *     "target/generated-test-sources"
 * );
 * 
 * System.out.println("Generated " + generatedClasses.size() + " test classes");
 * }
 * <p>
 * Given this JUnit test:
 * {@snippet :
 * package com.example;
 * 
 * import org.junit.jupiter.api.Test;
 * import org.junit.jupiter.api.BeforeEach;
 * 
 * class MyTest {
 *     private int counter;
 *     
 *     @BeforeEach
 *     void setUp() {
 *         counter = 0;
 *     }
 *     
 *     @Test
 *     void testIncrement() {
 *         counter++;
 *         assertEquals(1, counter);
 *     }
 * }
 * }
 * <p>
 * The generator produces:
 * {@snippet :
 * package com.example;
 * 
 * public class MyTestJnrTest extends io.github.lorenzobettini.jnrtest.core.JnrTest {
 *     
 *     private MyTest originalTest = new MyTest();
 *     
 *     public MyTestJnrTest() {
 *         super("MyTest in JnrTest");
 *     }
 *     
 *     @Override
 *     protected void specify() {
 *         beforeEach("call setUp",
 *             () -> originalTest.setUp());
 *         test("testIncrement",
 *             () -> originalTest.testIncrement());
 *     }
 * }
 * }
 *
 * @author Lorenzo Bettini
 */
public class JUnit5ToJnrTestDelegatedGenerator {
	
	private static final String TEST_ANNOTATION = "org.junit.jupiter.api.Test";
	private static final String BEFORE_ALL_ANNOTATION = "org.junit.jupiter.api.BeforeAll";
	private static final String BEFORE_EACH_ANNOTATION = "org.junit.jupiter.api.BeforeEach";
	private static final String AFTER_ALL_ANNOTATION = "org.junit.jupiter.api.AfterAll";
	private static final String AFTER_EACH_ANNOTATION = "org.junit.jupiter.api.AfterEach";
	private static final String DISPLAY_NAME_ANNOTATION = "org.junit.jupiter.api.DisplayName";
	
	private Path sourceDirectory;
	private Path outputDirectory;
	private List<String> generatedClasses;

	/**
	 * Generates JnrTest subclasses from all JUnit Jupiter test classes found in the source directory.
	 * <p>
	 * This method:
	 * <ul>
	 * <li>Scans the source directory recursively for Java files</li>
	 * <li>Identifies JUnit test classes (those with {@code @Test} annotations)</li>
	 * <li>Generates a corresponding JnrTest subclass for each test class</li>
	 * <li>Generates a {@code JnrTestMain} class to run all generated tests</li>
	 * </ul>
	 * <p>
	 * The generated classes use a delegation pattern where test methods are delegated
	 * to an instance of the original test class.
	 * <p>
	 * Supported JUnit annotations:
	 * <ul>
	 * <li>{@code @Test} - converted to {@code test(...)} calls</li>
	 * <li>{@code @BeforeAll} - converted to {@code beforeAll(...)} calls</li>
	 * <li>{@code @BeforeEach} - converted to {@code beforeEach(...)} calls</li>
	 * <li>{@code @AfterAll} - converted to {@code afterAll(...)} calls</li>
	 * <li>{@code @AfterEach} - converted to {@code afterEach(...)} calls</li>
	 * <li>{@code @DisplayName} - used as test description</li>
	 * </ul>
	 * 
	 * @param srcDir the directory to scan for JUnit test files (relative or absolute)
	 * @param outputDir the directory where to generate the JnrTest files (will be created if needed)
	 * @return list of fully qualified names of the generated JnrTest classes
	 * @throws IOException if there's an error reading the files or writing the output
	 * @throws IllegalArgumentException if srcDir is not a valid directory
	 */
	public List<String> generate(String srcDir, String outputDir) throws IOException {
		sourceDirectory = Path.of(srcDir).toAbsolutePath().normalize();
		outputDirectory = Path.of(outputDir).toAbsolutePath().normalize();
		generatedClasses = new ArrayList<>();
		
		if (!Files.isDirectory(sourceDirectory)) {
			throw new IllegalArgumentException("Not a directory: " + sourceDirectory);
		}
		Files.createDirectories(outputDirectory);
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			throw new IOException("No Java compiler available. Make sure JDK (not JRE) is used.");
		}
		
		// Find all Java files in source directory that are JUnit test classes
		List<Path> javaFilePaths = new ArrayList<>();
		try (Stream<Path> paths = Files.walk(sourceDirectory)) {
			paths.filter(Files::isRegularFile)
				.filter(p -> p.toString().endsWith(".java"))
				.filter(this::isJUnitTestClass)
				.sorted() // Sort files to ensure consistent order
				.forEach(javaFilePaths::add);
		}
		
		if (!javaFilePaths.isEmpty()) {
			// Process each file and generate the corresponding JnrTest file
			for (Path javaFile : javaFilePaths) {
				processJavaFile(javaFile);
			}
			
			// Generate a main class to run all tests
			if (!generatedClasses.isEmpty()) {
				generateJnrTestMain();
			}
		}
		
		return generatedClasses;
	}
	
	/**
	 * Check if a file is a JUnit test class using basic content checks.
	 * We'll do full processing later.
	 */
	private boolean isJUnitTestClass(Path file) {
		try {
			// Skip files that already have JnrTest suffix
			String filename = file.getFileName().toString();
			if (filename.endsWith("JnrTest.java")) {
				return false;
			}

			// Read the file content and do a basic check
			String content = Files.readString(file);
			return content.contains("@Test") && 
				  content.contains("import org.junit.jupiter.api.Test");

		} catch (IOException e) {
			System.err.println("Error reading file: " + file);
			return false;
		}
	}
	
	/**
	 * Process a single Java file using the Java Compiler API and generate a JnrTest class.
	 */
	private void processJavaFile(Path javaFile) {
		try {
			// Parse the Java file using a source-only approach
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
			
			try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {
				// Set up the compilation task
				Iterable<? extends JavaFileObject> compilationUnits = 
					fileManager.getJavaFileObjects(javaFile.toFile());
				
				// Create a temporary in-memory compilation - parse only, no bytecode generation
				List<String> options = Arrays.asList(
					"-proc:only",      // Only annotation processing, no compilation
					"-implicit:none"    // Don't generate class files for implicitly referenced files
				);
				
				CompilationTask task = compiler.getTask(
					null,              // Writer - null for System.err
					fileManager,       // File manager
					diagnostics,       // Diagnostic listener
					options,           // Options to the compiler
					null,              // Classes to compile - null means compile everything
					compilationUnits   // Compilation units to compile
				);
				
				// Create our custom processor to analyze the file
				TestFileProcessor processor = new TestFileProcessor();
				task.setProcessors(Collections.singletonList(processor));
				
				// Parse the file (doesn't actually compile, just parses)
				boolean success = task.call();
				
				// Check for errors
				if (!success) {
					System.err.println("Failed to process " + javaFile);
					for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
						System.err.format("Error on line %d: %s%n", 
							diagnostic.getLineNumber(), 
							diagnostic.getMessage(null));
					}
					return;
				}
				
				// Get processing results
				if (processor.getClassInfo() != null) {
					ClassInfo classInfo = processor.getClassInfo();
					
					// Generate the JnrTest class
					String jnrTestClassName = classInfo.className + "JnrTest";
					String fullyQualifiedName = classInfo.packageName + "." + jnrTestClassName;
					
					String jnrTestContent = generateJnrTestClass(classInfo);
					
					// Write the output file
					Path outputPath = createOutputPath(classInfo.packageName, jnrTestClassName);
					Files.createDirectories(outputPath.getParent());
					try (PrintWriter writer = new PrintWriter(outputPath.toFile())) {
						writer.println(jnrTestContent);
					}
					
					// Add the generated class to our list
					generatedClasses.add(fullyQualifiedName);
					
					System.out.println("Generated: " + outputPath);
				}
			}
		} catch (IOException e) {
			System.err.println("Error processing file: " + javaFile);
			e.printStackTrace();
		}
	}
	
	/**
	 * Custom annotation processor to analyze a Java test file.
	 */
	@SupportedAnnotationTypes({
		TEST_ANNOTATION,
		BEFORE_ALL_ANNOTATION,
		BEFORE_EACH_ANNOTATION,
		AFTER_ALL_ANNOTATION,
		AFTER_EACH_ANNOTATION,
		DISPLAY_NAME_ANNOTATION
	})
	@SupportedSourceVersion(SourceVersion.RELEASE_21)
	private static class TestFileProcessor extends AbstractProcessor {
		private ClassInfo classInfo = null;
		
		@Override
		public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
			if (roundEnv.processingOver()) {
				return false;
			}
			
			// Find classes with @Test methods
			for (TypeElement annotation : annotations) {
				String annotationType = annotation.getQualifiedName().toString();
				
				// Process elements based on annotation type
				for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
					if (element.getKind() == ElementKind.METHOD) {
						ExecutableElement methodElement = (ExecutableElement) element;
						TypeElement classElement = (TypeElement) methodElement.getEnclosingElement();
						
						// Initialize class info if not already done
						if (classInfo == null || !classInfo.className.equals(classElement.getSimpleName().toString())) {
							classInfo = new ClassInfo();
							classInfo.className = classElement.getSimpleName().toString();
							
							// Get package name
							Element enclosing = classElement.getEnclosingElement();
							if (enclosing instanceof PackageElement) {
								classInfo.packageName = ((PackageElement) enclosing).getQualifiedName().toString();
							}
						}
						
						String methodName = methodElement.getSimpleName().toString();
						
						// Process based on annotation type
						switch (annotationType) {
							case TEST_ANNOTATION:
								classInfo.testMethods.add(methodName);
								break;
							case BEFORE_ALL_ANNOTATION:
								if (methodElement.getModifiers().contains(Modifier.STATIC)) {
									classInfo.beforeAllMethods.add(methodName);
								}
								break;
							case BEFORE_EACH_ANNOTATION:
								classInfo.beforeEachMethods.add(methodName);
								break;
							case AFTER_ALL_ANNOTATION:
								if (methodElement.getModifiers().contains(Modifier.STATIC)) {
									classInfo.afterAllMethods.add(methodName);
								}
								break;
							case AFTER_EACH_ANNOTATION:
								classInfo.afterEachMethods.add(methodName);
								break;
							case DISPLAY_NAME_ANNOTATION:
								// Extract the display name value from the annotation
								String displayName = extractDisplayNameValue(methodElement);
								if (displayName != null) {
									classInfo.displayNames.put(methodName, displayName);
								}
								break;
						}
					}
				}
			}
			
			return true;
		}
		
		/**
		 * Extract display name value from the DisplayName annotation if present.
		 */
		private String extractDisplayNameValue(ExecutableElement methodElement) {
			for (AnnotationMirror annotationMirror : methodElement.getAnnotationMirrors()) {
				if (annotationMirror.getAnnotationType().toString().equals(DISPLAY_NAME_ANNOTATION)) {
					for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : 
							annotationMirror.getElementValues().entrySet()) {
						if (entry.getKey().getSimpleName().toString().equals("value")) {
							// Remove quotes from the string literal
							String value = entry.getValue().toString();
							return value.substring(1, value.length() - 1);
						}
					}
				}
			}
			return null;
		}
		
		public ClassInfo getClassInfo() {
			return classInfo;
		}
		
		@Override
		public Set<String> getSupportedAnnotationTypes() {
			return Set.of(
				TEST_ANNOTATION,
				BEFORE_ALL_ANNOTATION,
				BEFORE_EACH_ANNOTATION,
				AFTER_ALL_ANNOTATION,
				AFTER_EACH_ANNOTATION,
				DISPLAY_NAME_ANNOTATION
			);
		}
		
		@Override
		public SourceVersion getSupportedSourceVersion() {
			return SourceVersion.RELEASE_21;
		}
	}
	
	/**
	 * Class to store information about a test class
	 */
	private static class ClassInfo {
		String packageName = "";
		String className = "";
		List<String> beforeAllMethods = new ArrayList<>();
		List<String> beforeEachMethods = new ArrayList<>();
		List<String> afterAllMethods = new ArrayList<>();
		List<String> afterEachMethods = new ArrayList<>();
		List<String> testMethods = new ArrayList<>();
		Map<String, String> displayNames = new HashMap<>();
	}

	/**
	 * Create the output path for the generated file.
	 */
	private Path createOutputPath(String packageName, String className) {
		String packagePath = packageName.replace('.', '/');
		return outputDirectory.resolve(packagePath).resolve(className + ".java");
	}

	/**
	 * Generate the content of the JnrTest class.
	 * 
	 * @param classInfo The ClassInfo object containing all test class information
	 * @return The generated JnrTest class content as a string
	 */
	private String generateJnrTestClass(ClassInfo classInfo) {
		String jnrTestClassName = classInfo.className + "JnrTest";

		// Generate the class header
		String classHeader = """
			package %s;

			public class %s extends io.github.lorenzobettini.jnrtest.core.JnrTest { // NOSONAR

				private %s originalTest = new %s();

				public %s() {
					super("%s in JnrTest");
				}

				@Override
				protected void specify() {
			""".formatted(
				classInfo.packageName,
				jnrTestClassName,
				classInfo.className, classInfo.className,
				jnrTestClassName,
				classInfo.className
			);

		StringBuilder methodsBuilder = new StringBuilder(classHeader);
		
		// Add beforeAll methods
		for (String methodName : classInfo.beforeAllMethods) {
			methodsBuilder.append("\t\tbeforeAll(\"call " + methodName + "\",\n")
					.append("\t\t\t() -> " + classInfo.className + "." + methodName + "());\n");
		}

		// Add beforeEach methods
		for (String methodName : classInfo.beforeEachMethods) {
			methodsBuilder.append("\t\tbeforeEach(\"call " + methodName + "\",\n")
					.append("\t\t\t() -> originalTest." + methodName + "());\n");
		}

		// Add afterAll methods
		for (String methodName : classInfo.afterAllMethods) {
			methodsBuilder.append("\t\tafterAll(\"call " + methodName + "\",\n")
					.append("\t\t\t() -> " + classInfo.className + "." + methodName + "());\n");
		}

		// Add afterEach methods
		for (String methodName : classInfo.afterEachMethods) {
			methodsBuilder.append("\t\tafterEach(\"call " + methodName + "\",\n")
					.append("\t\t\t() -> originalTest." + methodName + "());\n");
		}

		// Add test methods
		for (String methodName : classInfo.testMethods) {
			String displayName = classInfo.displayNames.get(methodName);
			String testDescription = displayName != null ? displayName : methodName;

			methodsBuilder.append("\t\ttest(\"" + testDescription + "\",\n")
					.append("\t\t\t() -> originalTest." + methodName + "());\n");
		}

		// Class footer
		methodsBuilder.append("\t}\n}");

		return methodsBuilder.toString();
	}

	/**
	 * Generate a Java file with a main method that runs all JnrTest classes.
	 * 
	 * @throws IOException If there's an error writing the file
	 */
	private void generateJnrTestMain() throws IOException {
		// Create the path for JnrTestMain.java
		String packageName = getCommonPackage();
		String packagePath = packageName.replace('.', '/');
		Path outputPath = outputDirectory.resolve(packagePath).resolve("JnrTestMain.java");
		Files.createDirectories(outputPath.getParent());

		String classHeader = """
			package %s;

			/**
			 * Main class to run all generated JnrTest classes.
			 * Automatically generated by JnrTestJUnitProcessor.
			 */
			public class JnrTestMain {

				public static void main(String[] args) {
					var executor = new io.github.lorenzobettini.jnrtest.core.JnrTestConsoleExecutor();
					executor.getReporter().withOnlySummaries();

			""".formatted(packageName);

		StringBuilder contentBuilder = new StringBuilder(classHeader);

		for (String testClass : generatedClasses) {
			contentBuilder.append("\t\texecutor.add(new ")
					.append(testClass)
					.append("());\n");
		}

		String footer = """

					executor.execute();
				}
			}
			""";
		
		contentBuilder.append(footer);

		try (PrintWriter writer = new PrintWriter(outputPath.toFile())) {
			writer.print(contentBuilder.toString());
		}

		System.out.println("Generated JnrTestMain.java at: " + outputPath);
	}

	/**
	 * Get the common package from the generated classes.
	 * 
	 * @return The common package name
	 */
	private String getCommonPackage() {
		if (generatedClasses.isEmpty()) {
			return "";
		}
		
		// Use the package of the first class as a starting point
		String firstClass = generatedClasses.get(0);
		int lastDotIndex = firstClass.lastIndexOf('.');
		if (lastDotIndex < 0) {
			return "";
		}
		
		return firstClass.substring(0, lastDotIndex);
	}

	/**
	 * Main method for running the processor from the command line.
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Usage: JnrTestJUnitProcessor <sourceDir> <outputDir>");
			System.exit(1);
		}

		JUnit5ToJnrTestDelegatedGenerator processor = new JUnit5ToJnrTestDelegatedGenerator();
		try {
			List<String> generatedClasses = processor.generate(args[0], args[1]);
			System.out.println("Generated " + generatedClasses.size() + " JnrTest classes");
		} catch (IOException e) {
			System.err.println("Error processing files:");
			e.printStackTrace();
			System.exit(1);
		}
	}
}