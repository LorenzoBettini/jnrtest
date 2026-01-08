package io.github.lorenzobettini.jnrtest.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import io.github.lorenzobettini.jnrtest.core.JnrTest;

/**
 * Discovers JnrTest subclasses in a Java source directory by parsing source files
 * and analyzing type hierarchies.
 * <p>
 * This class uses the Eclipse JDT compiler to parse Java source files and identify
 * all classes that extend {@link JnrTest}, are concrete and instantiable with a public no-argument constructor.
 * <p>
 * Example usage:
 * {@snippet :
 * JnrTestDiscovery discovery = new JnrTestDiscovery();
 * List<String> testClasses = discovery.discover("src/test/java");
 * 
 * // testClasses contains fully qualified names of instantiable JnrTest subclasses
 * for (String className : testClasses) {
 *     System.out.println("Found test class: " + className);
 * }
 * }
 *
 * @author Lorenzo Bettini
 */
public final class JnrTestDiscovery {

	/**
	 * Discovers all instantiable JnrTest subclasses in the specified source directory.
	 * <p>
	 * This method walks the source directory tree, parses all Java files, and identifies
	 * classes that:
	 * <ul>
	 * <li>Extend {@link JnrTest} (directly or indirectly)</li>
	 * <li>Are public and not abstract</li>
	 * <li>Have a public no-argument constructor</li>
	 * <li>Are not inner classes (or are static inner classes)</li>
	 * </ul>
	 * <p>
	 * The method prints discovery information to System.out, including the total number
	 * of subtypes found and which ones are instantiable.
	 *
	 * @param srcDir the source directory to scan (relative to current working directory)
	 * @return a sorted list of fully qualified class names that can be instantiated
	 * @throws IOException if there is an error reading the source directory or files
	 * @throws IllegalArgumentException if srcDir is not a valid directory
	 */
	public List<String> discover(String srcDir) throws IOException {
		String superTypeFqn = JnrTest.class.getCanonicalName();
		Path projectRoot = Path.of("").toAbsolutePath();

		Path srcRoot = projectRoot.resolve(srcDir);
		if (!Files.isDirectory(srcRoot)) {
			throw new IllegalArgumentException("Not found: " + srcRoot.toAbsolutePath());
		}

		ParserConfig cfg = ParserConfig.from(projectRoot, srcRoot);

		List<TypeHit> hits = new ArrayList<>();

		try (Stream<Path> s = Files.walk(srcRoot)) {
			List<Path> javaFiles = s
					.filter(p -> p.toString().endsWith(".java"))
					.toList();

			for (Path f : javaFiles) {
				CompilationUnit cu = parseCompilationUnit(cfg, srcRoot, f);

				cu.accept(new ASTVisitor() {
					@Override
					public boolean visit(TypeDeclaration node) {
						handleType(node.resolveBinding());
						return true;
					}

					@Override
					public boolean visit(EnumDeclaration node) {
						handleType(node.resolveBinding());
						return true;
					}

					@Override
					public boolean visit(AnnotationTypeDeclaration node) {
						handleType(node.resolveBinding());
						return true;
					}

					private void handleType(ITypeBinding tb) {
						if (tb == null) {
							return;
						}
						String qn = tb.getQualifiedName();
						if (qn == null || qn.isEmpty()) {
							return;
						}
						if (!isSubtypeOf(tb, superTypeFqn)) {
							return;
						}
						boolean newable = isNewableNoArgPublicCtor(tb);
						hits.add(new TypeHit(qn, newable));
					}
				});
			}
		}

		List<String> subtypes = hits.stream()
				.map(h -> h.qualifiedName)
				.distinct()
				.sorted()
				.toList();

		List<String> newables = hits.stream()
				.filter(h -> h.newable)
				.map(h -> h.qualifiedName)
				.distinct()
				.sorted()
				.toList();

		System.out.println("Supertype: " + superTypeFqn);
		System.out.println("Found " + subtypes.size() + " subtype(s) under " + srcRoot.toAbsolutePath());
		System.out.println("Subtype(s): " + subtypes);
		System.out.println("Newable (public no-arg ctor): " + newables);

		return newables;
	}

	private static CompilationUnit parseCompilationUnit(ParserConfig cfg, Path srcRoot, Path javaFile) throws IOException {
		ASTParser p = cfg.newParser();

		String unitName = srcRoot.relativize(javaFile).toString().replace(File.separatorChar, '/');
		p.setUnitName(unitName); // required for bindings when using char[] source
		p.setSource(Files.readString(javaFile, StandardCharsets.UTF_8).toCharArray());

		return (CompilationUnit) p.createAST(null);
	}

	private static boolean isSubtypeOf(ITypeBinding tb, String superTypeFqn) {
		if (tb == null) {
			return false;
		}

		// Work with erasures so generics don't get in the way.
		tb = tb.getErasure();
		String qn = tb.getQualifiedName();
		if (superTypeFqn.equals(qn)) {
			return true;
		}

		// Arrays: recurse on element type (optional, but harmless).
		if (tb.isArray()) {
			return isSubtypeOf(tb.getElementType(), superTypeFqn);
		}

		ITypeBinding sc = tb.getSuperclass();
		if (sc != null && isSubtypeOf(sc, superTypeFqn)) {
			return true;
		}

		for (ITypeBinding itf : tb.getInterfaces()) {
			if (itf != null && isSubtypeOf(itf, superTypeFqn)) {
				return true;
			}
		}

		return false;
	}

	private static boolean isNewableNoArgPublicCtor(ITypeBinding tb) {
		int mods = tb.getModifiers();
		if (!java.lang.reflect.Modifier.isPublic(mods) || java.lang.reflect.Modifier.isAbstract(mods)) {
			return false;
		}
		if (tb.isMember() && !java.lang.reflect.Modifier.isStatic(mods)) {
			return false;
		}

		var declaredMethods = tb.getDeclaredMethods();
		for (IMethodBinding mb : declaredMethods) {
			if (!mb.isConstructor() || mb.getParameterTypes().length != 0 ||
					!java.lang.reflect.Modifier.isPublic(mb.getModifiers())) {
				continue;
			}
			return true;
		}

		return false;
	}

	private static record TypeHit(String qualifiedName, boolean newable) {}

	private static record ParserConfig(String[] classpathEntries, String[] sourcepathEntries, // NOSONAR we don't need equals/hashCode
			Map<String, String> compilerOptions) {

		static ParserConfig from(Path projectRoot, Path srcRoot) {
			List<String> cp = new ArrayList<>();

			Path targetClasses = projectRoot.resolve("target/classes");
			if (Files.isDirectory(targetClasses)) {
				cp.add(targetClasses.toAbsolutePath().toString());
			}

			String runtimeCp = System.getProperty("java.class.path", "");
			if (!runtimeCp.isBlank()) {
				for (String e : runtimeCp.split(java.util.regex.Pattern.quote(File.pathSeparator))) {
					if (!e.isBlank()) {
						cp.add(Path.of(e).toAbsolutePath().toString());
					}
				}
			}

			Map<String, String> opts = new HashMap<>(JavaCore.getOptions());
			JavaCore.setComplianceOptions(JavaCore.VERSION_17, opts);

			return new ParserConfig(
					cp.toArray(new String[0]),
					new String[] { srcRoot.toAbsolutePath().toString() },
					opts
			);
		}

		ASTParser newParser() {
			ASTParser p = ASTParser.newParser(AST.getJLSLatest());
			p.setKind(ASTParser.K_COMPILATION_UNIT);
			p.setResolveBindings(true);
			p.setBindingsRecovery(true);
			p.setCompilerOptions(compilerOptions);
			p.setEnvironment(classpathEntries, sourcepathEntries, null, true); // absolute paths required
			return p;
		}
	}
}
