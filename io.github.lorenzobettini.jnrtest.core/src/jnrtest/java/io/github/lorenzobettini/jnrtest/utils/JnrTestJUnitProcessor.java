package io.github.lorenzobettini.jnrtest.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
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
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * An annotation processor that generates JnrTestCase subclasses from JUnit
 * Jupiter test classes. For each Java file containing Jupiter @Test
 * annotations, it generates a corresponding JnrTestCase subclass with the same
 * name plus the suffix "JnrTest".
 * 
 * @author Lorenzo Bettini
 */
@SupportedAnnotationTypes({
    "org.junit.jupiter.api.Test",
    "org.junit.jupiter.api.BeforeAll",
    "org.junit.jupiter.api.BeforeEach",
    "org.junit.jupiter.api.AfterAll",
    "org.junit.jupiter.api.AfterEach",
    "org.junit.jupiter.api.DisplayName"
})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class JnrTestJUnitProcessor extends AbstractProcessor {
    
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
     * Creates a new processor.
     * 
     * @param sourceDirectory The directory to scan for JUnit test files
     * @param outputDirectory The directory where to generate the JnrTest files
     */
    public JnrTestJUnitProcessor(Path sourceDirectory, Path outputDirectory) {
        this.sourceDirectory = sourceDirectory;
        this.outputDirectory = outputDirectory;
        this.generatedClasses = new ArrayList<>();
    }

    /**
     * Process all Java files in the source directory and generates corresponding
     * JnrTest files.
     * 
     * @return List of fully qualified names of the generated JnrTest classes
     * @throws IOException If there's an error reading the files or writing the
     *                     output
     */
    public List<String> process() throws IOException {
        generatedClasses.clear();
        
        // Get the Java compiler
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IOException("No Java compiler available. Make sure JDK (not JRE) is used.");
        }
        
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            // Find all Java files in source directory
            List<Path> javaFilePaths = new ArrayList<>();
            try (Stream<Path> paths = Files.walk(sourceDirectory)) {
                paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .filter(this::isJUnitTestClass)
                    .sorted()
                    .forEach(javaFilePaths::add);
            }
            
            if (javaFilePaths.isEmpty()) {
                return generatedClasses;
            }
            
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
            if (filename.endsWith("JnrTest.java") || !filename.contains("Test.java")) {
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
            // Read file content
            String content = Files.readString(javaFile);
            
            // Parse the Java file using the Compiler API
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
                // Set up the compilation task
                Iterable<? extends JavaFileObject> compilationUnits = 
                    fileManager.getJavaFileObjects(javaFile.toFile());
                
                // Create a temporary in-memory compilation
                JavaCompiler.CompilationTask task = compiler.getTask(
                    null, fileManager, null, null, null, compilationUnits);
                
                // Create a custom processor to analyze the file
                TestFileProcessor processor = new TestFileProcessor();
                task.setProcessors(Collections.singletonList(processor));
                
                // Parse the file (doesn't actually compile, just parses)
                task.call();
                
                // Get processing results
                if (processor.getClassInfo() != null) {
                    ClassInfo classInfo = processor.getClassInfo();
                    
                    // Generate the JnrTest class
                    String jnrTestClassName = classInfo.className + "JnrTest";
                    String fullyQualifiedName = classInfo.packageName + "." + jnrTestClassName;
                    
                    String jnrTestContent = generateJnrTestClass(
                        classInfo.packageName, 
                        classInfo.className,
                        jnrTestClassName,
                        classInfo.beforeAllMethods,
                        classInfo.beforeEachMethods, 
                        classInfo.afterAllMethods,
                        classInfo.afterEachMethods,
                        classInfo.testMethods,
                        classInfo.displayNames
                    );
                    
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
         * Extract display name value from the DisplayName annotation.
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
            return SourceVersion.latest();
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
     */
    private String generateJnrTestClass(
            String packageName, 
            String originalClassName, 
            String jnrTestClassName,
            List<String> beforeAllMethods, 
            List<String> beforeEachMethods, 
            List<String> afterAllMethods,
            List<String> afterEachMethods, 
            List<String> testMethods,
            Map<String, String> displayNames) {

        StringBuilder builder = new StringBuilder();

        // Package declaration
        builder.append("package ").append(packageName).append(";\n\n");

        // Class declaration
        builder.append("public class ").append(jnrTestClassName).append(" extends JnrTestCase {\n\n");

        // Original test instance
        builder.append("\tprivate ").append(originalClassName).append(" originalTest = new ").append(originalClassName)
                .append("();\n\n");

        // Constructor
        builder.append("\tpublic ").append(jnrTestClassName).append("() {\n").append("\t\tsuper(\"")
                .append(originalClassName).append(" in JnrTest\");\n").append("\t}\n\n");

        // Specify method
        builder.append("\t@Override\n").append("\tprotected void specify() {\n");

        // Add beforeAll methods
        for (String methodName : beforeAllMethods) {
            builder.append("\t\tbeforeAll(\"call ").append(methodName).append("\",\n").append("\t\t\t() -> ")
                    .append(originalClassName).append(".").append(methodName).append("());\n");
        }

        // Add beforeEach methods
        for (String methodName : beforeEachMethods) {
            builder.append("\t\tbeforeEach(\"call ").append(methodName).append("\",\n").append("\t\t() -> {\n")
                    .append("\t\t\toriginalTest.").append(methodName).append("();\n").append("\t\t});\n");
        }

        // Add afterAll methods
        for (String methodName : afterAllMethods) {
            builder.append("\t\tafterAll(\"call ").append(methodName).append("\",\n").append("\t\t\t() -> ")
                    .append(originalClassName).append(".").append(methodName).append("());\n");
        }

        // Add afterEach methods
        for (String methodName : afterEachMethods) {
            builder.append("\t\tafterEach(\"call ").append(methodName).append("\",\n")
                    .append("\t\t\t() -> originalTest.").append(methodName).append("());\n");
        }

        // Add test methods
        for (String methodName : testMethods) {
            String displayName = displayNames.get(methodName);
            String testDescription = displayName != null ? displayName : methodName;

            builder.append("\n\t\ttest(\"").append(testDescription).append("\", () -> {\n")
                    .append("\t\t\toriginalTest.").append(methodName).append("();\n").append("\t\t});\n");
        }

        // Close specify method
        builder.append("\t}\n");

        // Close class
        builder.append("\t\n}");

        return builder.toString();
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

        // Generate the JnrTestMain class
        StringBuilder content = new StringBuilder();
        content.append("package ").append(packageName).append(";\n\n");
        content.append("/**\n");
        content.append(" * Main class to run all generated JnrTest classes.\n");
        content.append(" * Automatically generated by JnrTestJUnitProcessor.\n");
        content.append(" */\n");
        content.append("public class JnrTestMain {\n\n");
        content.append("\tpublic static void main(String[] args) {\n");
        content.append("\t\tvar executor = new JnrTestConsoleExecutor();\n\n");

        // Add each test class
        for (String testClass : generatedClasses) {
            content.append("\t\texecutor.testCase(new ").append(testClass).append("());\n");
        }

        content.append("\n\t\texecutor.execute();\n");
        content.append("\t}\n");
        content.append("}\n");

        // Write the JnrTestMain.java file
        try (PrintWriter writer = new PrintWriter(outputPath.toFile())) {
            writer.print(content.toString());
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
        
        String commonPackage = firstClass.substring(0, lastDotIndex);
        return commonPackage;
    }

    /**
     * Main method for running the processor from the command line.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: JnrTestJUnitProcessor <sourceDir> <outputDir>");
            System.exit(1);
        }

        Path sourceDir = Paths.get(args[0]);
        Path outputDir = Paths.get(args[1]);

        JnrTestJUnitProcessor processor = new JnrTestJUnitProcessor(sourceDir, outputDir);
        try {
            List<String> generatedClasses = processor.process();
            System.out.println("Generated " + generatedClasses.size() + " JnrTest classes");
        } catch (IOException e) {
            System.err.println("Error processing files:");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // This method is required by AbstractProcessor, but we don't use it
        // in our standalone implementation
        return false;
    }
}