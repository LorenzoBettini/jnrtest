package io.github.lorenzobettini.jnrtest.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import io.github.lorenzobettini.jnrtest.core.JnrTest;

public final class JUnit5ToJnrTestGenerator {

	private static final String CALL = "call ";

	private static final Set<String> JUNIT_ANNOTATION_IMPORTS = Set.of(
			"org.junit.jupiter.api.BeforeAll",
			"org.junit.jupiter.api.BeforeEach",
			"org.junit.jupiter.api.AfterAll",
			"org.junit.jupiter.api.AfterEach",
			"org.junit.jupiter.api.Test",
			"org.junit.jupiter.api.DisplayName"
	);

	private static final String JNRTEST_FQN = JnrTest.class.getCanonicalName();
	private static final String[] JNRTEST_FQN_PARTS = JNRTEST_FQN.split("\\.");

	public void generate(String srcDir, String outputDir) throws IOException {
		Path inputSrcDirPath = Path.of(srcDir).toAbsolutePath().normalize();
		Path outputDirPath = Path.of(outputDir).toAbsolutePath().normalize();

		if (!Files.isDirectory(inputSrcDirPath)) {
			throw new IllegalArgumentException("Not a directory: " + inputSrcDirPath);
		}
		Files.createDirectories(outputDirPath);

		Map<String, String> options = formatterOptions();

		try (var walk = Files.walk(inputSrcDirPath)) {
			List<Path> javaFiles = walk
					.filter(p -> p.toString().endsWith(".java"))
					.toList();

			for (Path f : javaFiles) {
				String source = Files.readString(f, StandardCharsets.UTF_8);
				CompilationUnit cu = parse(source, options);

				Optional<AbstractTypeDeclaration> primaryType = primaryTopLevelType(cu);
				if (primaryType.isEmpty()) {
					continue;
				}

				AbstractTypeDeclaration td = primaryType.get();
				if (!(td instanceof TypeDeclaration typeDecl)) {
					continue;
				}

				TransformPlan plan = plan(typeDecl);
				if (!plan.hasAnyJUnitMethods()) {
					continue;
				}

				String rewritten = transformOne(source, cu, typeDecl, plan, options);

				String pkg = (cu.getPackage() != null) ? cu.getPackage().getName().getFullyQualifiedName() : "";
				Path outFile = outputPath(outputDirPath, pkg, plan.newClassName + ".java");
				Files.createDirectories(outFile.getParent());
				Files.writeString(outFile, rewritten, StandardCharsets.UTF_8);

				System.out.println("Generated: " + outFile);
			}
		}
	}

	private static CompilationUnit parse(String source, Map<String, String> options) {
		ASTParser p = ASTParser.newParser(AST.getJLSLatest());
		p.setKind(ASTParser.K_COMPILATION_UNIT);
		p.setCompilerOptions(options);
		p.setSource(source.toCharArray());
		p.setResolveBindings(false);
		p.setBindingsRecovery(false);
		p.setStatementsRecovery(true);
		return (CompilationUnit) p.createAST(null);
	}

	private static Optional<AbstractTypeDeclaration> primaryTopLevelType(CompilationUnit cu) {
		for (Object t : cu.types()) {
			if (t instanceof AbstractTypeDeclaration atd) {
				return Optional.of(atd);
			}
		}
		return Optional.empty();
	}

	private static String transformOne(
			String source,
			CompilationUnit cu,
			TypeDeclaration typeDecl,
			TransformPlan plan,
			Map<String, String> options
	) {

		AST ast = cu.getAST();
		ASTRewrite rw = ASTRewrite.create(ast);
		rewriteImports(ast, rw, cu);

		// Class name + visibility
		rw.set(typeDecl, TypeDeclaration.NAME_PROPERTY, ast.newSimpleName(plan.newClassName), null);
		makePublic(ast, rw, typeDecl);

		// extends JnrTest
		rw.set(typeDecl, TypeDeclaration.SUPERCLASS_TYPE_PROPERTY, ast.newSimpleType(ast.newSimpleName("JnrTest")), null);

		// Remove annotated methods (Before*/After*/Test)
		ListRewrite bodyRw = rw.getListRewrite(typeDecl, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
		for (MethodDeclaration md : plan.junitMethodsInOrder) {
			bodyRw.remove(md, null);
		}

		// Add ctor + specify()
		MethodDeclaration ctor = newConstructor(ast, plan.newClassName, plan.originalClassName);
		bodyRw.insertFirst(ctor, null);
		bodyRw.insertAfter(newSpecify(ast, rw, plan, source), ctor, null);

		// Apply edits
		Document doc = new Document(source);

		try {
		TextEdit astEdits = rw.rewriteAST(doc, options);
		astEdits.apply(doc);

		// Format with tabs + include comments
		CodeFormatter formatter = ToolFactory.createCodeFormatter(options);
		TextEdit fmt = formatter.format(
				CodeFormatter.K_COMPILATION_UNIT | CodeFormatter.F_INCLUDE_COMMENTS,
				doc.get(),
				0,
				doc.getLength(),
				0,
				System.lineSeparator()
		);
		if (fmt != null) {
			fmt.apply(doc);
		}
		} catch (BadLocationException e) {
			throw new RuntimeException("Error during transformation of " + plan.originalClassName, e); // NOSONAR
		}

		return doc.get();
	}

	@SuppressWarnings("unchecked")
	private static void rewriteImports(AST ast, ASTRewrite rw, CompilationUnit cu) {
		ListRewrite importsRw = rw.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);

		boolean hasJnrTest = false;

		for (ImportDeclaration id : (List<ImportDeclaration>) cu.imports()) {
			String name = id.getName().getFullyQualifiedName();

			if (!id.isStatic() && !id.isOnDemand() && JNRTEST_FQN.equals(name)) {
				hasJnrTest = true;
			}

			// Remove JUnit 5 annotation imports that won't be used in the generated class
			if (!id.isStatic() && !id.isOnDemand() && JUNIT_ANNOTATION_IMPORTS.contains(name)) {
				importsRw.remove(id, null);
			}
		}

		if (!hasJnrTest) {
			ImportDeclaration jnr = ast.newImportDeclaration();
			jnr.setName(ast.newName(JNRTEST_FQN_PARTS));
			importsRw.insertLast(jnr, null);
		}
	}

	private static void makePublic(AST ast, ASTRewrite rw, TypeDeclaration typeDecl) {
		ListRewrite mods = rw.getListRewrite(typeDecl, TypeDeclaration.MODIFIERS2_PROPERTY);

		boolean alreadyPublic = false;
		for (Object m : typeDecl.modifiers()) {
			if (m instanceof Modifier mod && mod.isPublic()) {
				alreadyPublic = true;
				break;
			}
		}
		if (!alreadyPublic) {
			mods.insertFirst(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD), null);
		}
	}

	@SuppressWarnings("unchecked")
	private static MethodDeclaration newConstructor(AST ast, String newClassName, String originalClassName) {
		MethodDeclaration ctor = ast.newMethodDeclaration();
		ctor.setConstructor(true);
		ctor.setName(ast.newSimpleName(newClassName));
		ctor.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));

		Block body = ast.newBlock();
		SuperConstructorInvocation sci = ast.newSuperConstructorInvocation();

		StringLiteral lit = ast.newStringLiteral();
		lit.setLiteralValue(originalClassName + " in JnrTest");
		sci.arguments().add(lit);

		body.statements().add(sci);
		ctor.setBody(body);
		return ctor;
	}


	@SuppressWarnings("unchecked")
	private static MethodDeclaration newSpecify(AST ast, ASTRewrite rw, TransformPlan plan, String source) {
		MethodDeclaration m = ast.newMethodDeclaration();
		m.setName(ast.newSimpleName("specify"));
		m.setReturnType2(ast.newPrimitiveType(PrimitiveType.VOID));
		m.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PROTECTED_KEYWORD));

		MarkerAnnotation overrideAnn = ast.newMarkerAnnotation();
		overrideAnn.setTypeName(ast.newSimpleName("Override"));
		m.modifiers().add(overrideAnn);

		String blockSrc = buildSpecifyBlockSource(plan, source);

		// Insert raw block source as the method body (keeps original comments in extracted bodies)
		Block placeholder = (Block) rw.createStringPlaceholder(blockSrc, ASTNode.BLOCK);
		m.setBody(placeholder);

		return m;
	}

	private static String buildSpecifyBlockSource(TransformPlan plan, String source) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");

		for (MethodDeclaration md : plan.beforeAll) {
			sb.append(hookCall("beforeAll", CALL + md.getName().getIdentifier(), md, source));
		}
		for (MethodDeclaration md : plan.beforeEach) {
			sb.append(hookCall("beforeEach", CALL + md.getName().getIdentifier(), md, source));
		}
		for (MethodDeclaration md : plan.afterAll) {
			sb.append(hookCall("afterAll", CALL + md.getName().getIdentifier(), md, source));
		}
		for (MethodDeclaration md : plan.afterEach) {
			sb.append(hookCall("afterEach", CALL + md.getName().getIdentifier(), md, source));
		}

		for (MethodDeclaration md : plan.tests) {
			String display = displayNameOrMethodName(md);
			sb.append(hookCall("test", display, md, source));
		}

		sb.append("}\n");
		return sb.toString();
	}

	private static String hookCall(String fn, String label, MethodDeclaration md, String source) {
		Block body = md.getBody();
		if (body == null) {
			return "";
		}
		String bodySrc = source.substring(body.getStartPosition(), body.getStartPosition() + body.getLength());

		// Note: if JnrTest uses a functional interface that doesn't allow checked exceptions,
		// you may need to wrap here. This keeps the original block verbatim (comments included).
		return "\t\t" + fn + "(\"" + escapeJava(label) + "\",\n"
				+ "\t\t\t() -> " + bodySrc + ");\n";
	}

	private static String displayNameOrMethodName(MethodDeclaration md) {
		String dn = annotationStringValue(md, "DisplayName");
		return (dn != null && !dn.isBlank()) ? dn : md.getName().getIdentifier();
	}

	private static String annotationStringValue(MethodDeclaration md, String annotationSimpleName) {
		for (Object o : md.modifiers()) {
			if (!(o instanceof Annotation a)) {
				continue;
			}
			String ann = simpleName(a.getTypeName());
			if (!annotationSimpleName.equals(ann)) {
				continue;
			}

			if (a instanceof SingleMemberAnnotation sma && sma.getValue() instanceof StringLiteral sl) {
				return sl.getLiteralValue();
			}
			if (a instanceof NormalAnnotation na) {
				for (Object v : na.values()) {
					if (v instanceof MemberValuePair mvp
							&& "value".equals(mvp.getName().getIdentifier())
							&& mvp.getValue() instanceof StringLiteral sl) {
						return sl.getLiteralValue();
					}
				}
			}
		}
		return null;
	}

	private static String simpleName(Name name) {
		if (name.isQualifiedName()) {
			return ((QualifiedName) name).getName().getIdentifier();
		}
		return ((SimpleName) name).getIdentifier();
	}

	private static String escapeJava(String s) {
		return s
				.replace("\\", "\\\\")
				.replace("\"", "\\\"");
	}

	private static TransformPlan plan(TypeDeclaration typeDecl) {
		String originalName = typeDecl.getName().getIdentifier();
		String newName = originalName + "JnrTest";

		List<MethodDeclaration> junitMethodsInOrder = new ArrayList<>();
		List<MethodDeclaration> beforeAll = new ArrayList<>();
		List<MethodDeclaration> beforeEach = new ArrayList<>();
		List<MethodDeclaration> afterAll = new ArrayList<>();
		List<MethodDeclaration> afterEach = new ArrayList<>();
		List<MethodDeclaration> tests = new ArrayList<>();

		for (MethodDeclaration md : typeDecl.getMethods()) {
			Set<String> anns = methodAnnotationSimpleNames(md);
			boolean isJUnit = false;

			if (anns.contains("BeforeAll")) {
				beforeAll.add(md);
				isJUnit = true;
			}
			if (anns.contains("BeforeEach")) {
				beforeEach.add(md);
				isJUnit = true;
			}
			if (anns.contains("AfterAll")) {
				afterAll.add(md);
				isJUnit = true;
			}
			if (anns.contains("AfterEach")) {
				afterEach.add(md);
				isJUnit = true;
			}
			if (anns.contains("Test")) {
				tests.add(md);
				isJUnit = true;
			}

			if (isJUnit) {
				junitMethodsInOrder.add(md);
			}
		}

		return new TransformPlan(
				originalName,
				newName,
				junitMethodsInOrder,
				beforeAll,
				beforeEach,
				afterAll,
				afterEach,
				tests
		);
	}

	private static Set<String> methodAnnotationSimpleNames(MethodDeclaration md) {
		Set<String> out = new HashSet<>();
		for (Object o : md.modifiers()) {
			if (o instanceof Annotation a) {
				out.add(simpleName(a.getTypeName()));
			}
		}
		return out;
	}

	private static Path outputPath(Path outRoot, String pkg, String fileName) {
		if (pkg == null || pkg.isBlank()) {
			return outRoot.resolve(fileName);
		}
		return outRoot.resolve(pkg.replace('.', File.separatorChar)).resolve(fileName);
	}

	private static Map<String, String> formatterOptions() {
		Map<String, String> opts = new HashMap<>(JavaCore.getOptions());
		JavaCore.setComplianceOptions(JavaCore.VERSION_17, opts);

		// Tabs (not spaces)
		opts.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, JavaCore.TAB);
		opts.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4");
		opts.put(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE, "4");

		// Format comments too (then use F_INCLUDE_COMMENTS in CodeFormatter.format)
		opts.put(DefaultCodeFormatterConstants.FORMATTER_COMMENT_FORMAT_BLOCK_COMMENT, DefaultCodeFormatterConstants.TRUE);
		opts.put(DefaultCodeFormatterConstants.FORMATTER_COMMENT_FORMAT_LINE_COMMENT, DefaultCodeFormatterConstants.TRUE);
		opts.put(DefaultCodeFormatterConstants.FORMATTER_COMMENT_FORMAT_JAVADOC_COMMENT, DefaultCodeFormatterConstants.TRUE);

		return opts;
	}

	private static final class TransformPlan {
		final String originalClassName;
		final String newClassName;

		final List<MethodDeclaration> junitMethodsInOrder;
		final List<MethodDeclaration> beforeAll;
		final List<MethodDeclaration> beforeEach;
		final List<MethodDeclaration> afterAll;
		final List<MethodDeclaration> afterEach;
		final List<MethodDeclaration> tests;

		TransformPlan( // NOSONAR we need all these parameters
				String originalClassName,
				String newClassName,
				List<MethodDeclaration> junitMethodsInOrder,
				List<MethodDeclaration> beforeAll,
				List<MethodDeclaration> beforeEach,
				List<MethodDeclaration> afterAll,
				List<MethodDeclaration> afterEach,
				List<MethodDeclaration> tests
		) {
			this.originalClassName = originalClassName;
			this.newClassName = newClassName;
			this.junitMethodsInOrder = junitMethodsInOrder;
			this.beforeAll = beforeAll;
			this.beforeEach = beforeEach;
			this.afterAll = afterAll;
			this.afterEach = afterEach;
			this.tests = tests;
		}

		boolean hasAnyJUnitMethods() {
			return !junitMethodsInOrder.isEmpty();
		}
	}
}
