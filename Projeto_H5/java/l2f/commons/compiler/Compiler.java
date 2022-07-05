package l2f.commons.compiler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Compiler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Compiler.class);

	private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	private final MemoryClassLoader memoryClassLoader = AccessController.doPrivileged((PrivilegedAction<MemoryClassLoader>) MemoryClassLoader::new);

	public boolean compile(final Iterable<File> files)
	{
		if (compiler == null)
		{
			throw new RuntimeException("Error: server started by JRE instead JDK! Please start server with Java Development Kit.");
		}
		// compiler options
		final List<String> options = new ArrayList<>();
		options.add("-Xlint:all");
		options.add("-g");
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8);
					JavaFileManager memFileManager = new MemoryJavaFileManager(fileManager, memoryClassLoader))
		{
			final CompilationTask compile = compiler.getTask(null, memFileManager, diagnostics, options, null, fileManager.getJavaFileObjectsFromFiles(files));
			return compile.call();
		}
		catch (final IOException e)
		{
			LOGGER.error("Can't compile", e);
			return false;
		}
		finally
		{
			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics())
			{
				if (diagnostic.getKind() == Kind.ERROR)
				{
					LOGGER.error("{}{}: {}", diagnostic.getSource().getName(), diagnostic.getPosition() == Diagnostic.NOPOS ? "" : ":" + diagnostic.getLineNumber() + ',' + diagnostic.getColumnNumber());
				}
				else
				{
					String sourceName = diagnostic.getSource() == null ? "" : diagnostic.getSource().getName();
					LOGGER.debug("{}{}: {}", sourceName, diagnostic.getPosition() == Diagnostic.NOPOS ? "" : ":" + diagnostic.getLineNumber() + ',' + diagnostic.getColumnNumber());
				}
			}
		}
	}

	public MemoryClassLoader getClassLoader()
	{
		return memoryClassLoader;
	}
}