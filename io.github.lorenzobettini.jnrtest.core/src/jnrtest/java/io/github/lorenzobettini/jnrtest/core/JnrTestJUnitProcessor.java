package io.github.lorenzobettini.jnrtest.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An annotation processor that generates JnrTestCase subclasses from JUnit Jupiter test classes.
 * For each Java file containing Jupiter @Test annotations, it generates a corresponding
 * JnrTestCase subclass with the same name plus the suffix "JnrTest".
 * 
 * @author Lorenzo Bettini
 */
public class JnrTestJUnitProcessor {
    // Patterns to identify JUnit annotations and test methods
    private static final Pattern CLASS_PATTERN = Pattern.compile("class\\s+(\\w+)");
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+([^;]+);");
    private static final Pattern BEFORE_ALL_PATTERN = Pattern.compile("@BeforeAll\\s+static\\s+void\\s+(\\w+)\\s*\\(");
    private static final Pattern BEFORE_EACH_PATTERN = Pattern.compile("@BeforeEach\\s+void\\s+(\\w+)\\s*\\(");
    private static final Pattern AFTER_ALL_PATTERN = Pattern.compile("@AfterAll\\s+static\\s+void\\s+(\\w+)\\s*\\(");
    private static final Pattern AFTER_EACH_PATTERN = Pattern.compile("@AfterEach\\s+void\\s+(\\w+)\\s*\\(");
    private static final Pattern TEST_PATTERN = Pattern.compile("@Test\\s+void\\s+(\\w+)\\s*\\(");
    private static final Pattern DISPLAY_NAME_PATTERN = Pattern.compile("@DisplayName\\s*\\(\\s*\"([^\"]*)\"\\s*\\)");

    private Path sourceDirectory;
    private Path outputDirectory;

    /**
     * Creates a new processor.
     * 
     * @param sourceDirectory The directory to scan for JUnit test files
     * @param outputDirectory The directory where to generate the JnrTest files
     */
    public JnrTestJUnitProcessor(Path sourceDirectory, Path outputDirectory) {
        this.sourceDirectory = sourceDirectory;
        this.outputDirectory = outputDirectory;
    }

    /**
     * Process all Java files in the source directory and generates corresponding JnrTest files.
     * 
     * @throws IOException If there's an error reading the files or writing the output
     */
    public void process() throws IOException {
        try (Stream<Path> paths = Files.walk(sourceDirectory)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".java"))
                 .filter(this::isJUnitTestClass)
                 .forEach(this::processFile);
        }
    }

    /**
     * Checks if a file is a JUnit test class.
     */
    private boolean isJUnitTestClass(Path file) {
        try {
            // Skip files that already have JnrTest suffix
            String filename = file.getFileName().toString();
            if (filename.endsWith("JnrTest.java") || 
                !filename.contains("Test.java")) {
                return false;
            }
            
            // Read the file content
            String content = Files.readString(file);
            
            // Check if it's a proper JUnit test class with specific JUnit Jupiter annotations
            boolean hasTestAnnotation = content.contains("@Test");
            boolean importsJUnit = content.contains("import org.junit.jupiter.api.Test");
            
            // Only process actual JUnit test classes
            return hasTestAnnotation && importsJUnit && extractClassName(content) != null;
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + file);
            return false;
        }
    }

    /**
     * Process a single JUnit test file and generate the corresponding JnrTest file.
     */
    private void processFile(Path file) {
        try {
            String content = Files.readString(file);
            String packageName = extractPackageName(content);
            String className = extractClassName(content);
            
            if (className == null) {
                System.err.println("Could not extract class name from: " + file);
                return;
            }
            
            String jnrTestClassName = className + "JnrTest";
            
            // Extract test methods and lifecycle methods
            List<String> beforeAllMethods = extractMethods(content, BEFORE_ALL_PATTERN);
            List<String> beforeEachMethods = extractMethods(content, BEFORE_EACH_PATTERN);
            List<String> afterAllMethods = extractMethods(content, AFTER_ALL_PATTERN);
            List<String> afterEachMethods = extractMethods(content, AFTER_EACH_PATTERN);
            List<String> testMethods = extractMethods(content, TEST_PATTERN);
            
            // Generate the JnrTest class
            String jnrTestContent = generateJnrTestClass(
                packageName, className, jnrTestClassName,
                beforeAllMethods, beforeEachMethods, 
                afterAllMethods, afterEachMethods,
                testMethods, content
            );
            
            // Write the output file
            Path outputPath = createOutputPath(packageName, jnrTestClassName);
            Files.createDirectories(outputPath.getParent());
            try (PrintWriter writer = new PrintWriter(outputPath.toFile())) {
                writer.println(jnrTestContent);
            }
            
            System.out.println("Generated: " + outputPath);
            
        } catch (IOException e) {
            System.err.println("Error processing file: " + file);
            e.printStackTrace();
        }
    }

    /**
     * Extract the package name from the file content.
     */
    private String extractPackageName(String content) {
        Matcher matcher = PACKAGE_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    /**
     * Extract the class name from the file content.
     */
    private String extractClassName(String content) {
        Matcher matcher = CLASS_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Extract method names that match the given pattern.
     */
    private List<String> extractMethods(String content, Pattern pattern) {
        List<String> methods = new ArrayList<>();
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            methods.add(matcher.group(1));
        }
        return methods;
    }

    /**
     * Get the display name for a test method if it exists.
     */
    private String getDisplayName(String content, String methodName) {
        // Look for @DisplayName before the method
        Pattern pattern = Pattern.compile("@DisplayName\\s*\\(\\s*\"([^\"]*)\"\\s*\\)[\\s\\n]*@Test[\\s\\n]*void\\s+" + methodName);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
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
            String packageName, String originalClassName, String jnrTestClassName,
            List<String> beforeAllMethods, List<String> beforeEachMethods,
            List<String> afterAllMethods, List<String> afterEachMethods,
            List<String> testMethods, String originalContent) {
        
        StringBuilder builder = new StringBuilder();
        
        // Package declaration
        builder.append("package ").append(packageName).append(";\n\n");
        
        // Class declaration
        builder.append("public class ").append(jnrTestClassName)
               .append(" extends JnrTestCase {\n\n");
        
        // Original test instance
        builder.append("\tprivate ").append(originalClassName)
               .append(" originalTest = new ").append(originalClassName).append("();\n\n");
        
        // Constructor
        builder.append("\tpublic ").append(jnrTestClassName).append("() {\n")
               .append("\t\tsuper(\"").append(originalClassName).append(" in JnrTest\");\n")
               .append("\t}\n\n");
        
        // Specify method
        builder.append("\t@Override\n")
               .append("\tprotected void specify() {\n");
        
        // Add beforeAll methods
        for (String methodName : beforeAllMethods) {
            builder.append("\t\tbeforeAll(\"call ").append(methodName).append("\",\n")
                   .append("\t\t\t() -> ").append(originalClassName).append(".")
                   .append(methodName).append("());\n");
        }
        
        // Add beforeEach methods
        for (String methodName : beforeEachMethods) {
            builder.append("\t\tbeforeEach(\"call ").append(methodName).append("\",\n")
                   .append("\t\t() -> {\n")
                   .append("\t\t\toriginalTest.").append(methodName).append("();\n")
                   .append("\t\t});\n");
        }
        
        // Add afterAll methods
        for (String methodName : afterAllMethods) {
            builder.append("\t\tafterAll(\"call ").append(methodName).append("\",\n")
                   .append("\t\t\t() ->").append(originalClassName).append(".")
                   .append(methodName).append("());\n");
        }
        
        // Add afterEach methods
        for (String methodName : afterEachMethods) {
            builder.append("\t\tafterEach(\"call ").append(methodName).append("\",\n")
                   .append("\t\t\t() -> originalTest.").append(methodName).append("());\n");
        }
        
        // Add test methods
        for (String methodName : testMethods) {
            String displayName = getDisplayName(originalContent, methodName);
            String testDescription = displayName != null ? displayName : methodName;
            
            builder.append("\n\t\ttest(\"").append(testDescription).append("\", () -> {\n")
                   .append("\t\t\toriginalTest.").append(methodName).append("();\n")
                   .append("\t\t});\n");
        }
        
        // Close specify method
        builder.append("\t}\n");
        
        // Close class
        builder.append("\t\n}");
        
        return builder.toString();
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
            processor.process();
        } catch (IOException e) {
            System.err.println("Error processing files:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}