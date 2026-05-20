package com.gx.etl.operator.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.tools.*;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 动态编译 .java 源文件并注册算子
 */
@Component
public class DynamicOperatorCompiler {

    private static final Logger log = LoggerFactory.getLogger(DynamicOperatorCompiler.class);
    private static final String CUSTOM_PACKAGE = "com.gx.etl.operators.custom";
    private static final String CUSTOM_DIR = "operators/custom";
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+[\\w.]+\\s*;");
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("public\\s+class\\s+(\\w+)");

    private final OperatorRegistry operatorRegistry;

    public DynamicOperatorCompiler(OperatorRegistry operatorRegistry) {
        this.operatorRegistry = operatorRegistry;
    }

    /**
     * 编译并注册算子
     * @param sourceCode .java 源码
     * @return 编译结果
     */
    public CompileResult compileAndRegister(String sourceCode) {
        try {
            // 1. 提取类名
            String className = extractClassName(sourceCode);
            if (className == null) {
                return CompileResult.fail("无法从源码中提取类名");
            }

            // 2. 去掉原 package 声明，添加固定包名
            String processedCode = processSourceCode(sourceCode, className);

            // 3. 创建存储目录
            Path customDir = Paths.get(CUSTOM_DIR);
            Files.createDirectories(customDir);

            // 4. 写入 .java 文件
            Path javaFile = customDir.resolve(className + ".java");
            Files.write(javaFile, processedCode.getBytes(StandardCharsets.UTF_8));

            // 5. 编译
            Path classesDir = customDir.resolve("classes");
            Files.createDirectories(classesDir);

            boolean compiled = compile(javaFile, classesDir);
            if (!compiled) {
                return CompileResult.fail("编译失败");
            }

            // 6. 加载类
            Class<?> clazz = loadClass(classesDir, CUSTOM_PACKAGE + "." + className);
            if (clazz == null) {
                return CompileResult.fail("加载类失败");
            }

            // 7. 验证继承 AbstractOperator
            if (!AbstractOperator.class.isAssignableFrom(clazz)) {
                return CompileResult.fail("文件内容有误，请确认后在上传");
            }

            // 8. 实例化
            AbstractOperator operator;
            try {
                operator = (AbstractOperator) clazz.newInstance();
            } catch (Exception e) {
                return CompileResult.fail("实例化失败: " + e.getMessage());
            }

            // 9. 注册到 OperatorRegistry
            operatorRegistry.registerOperator(operator);

            String pluginId = operator.getId();
            String name = operator.getName();
            String category = operator.getCategory();

            log.info("动态算子注册成功: pluginId={}, name={}, class={}", pluginId, name, className);

            return CompileResult.success(pluginId, name, category, CUSTOM_PACKAGE + "." + className);

        } catch (Exception e) {
            log.error("动态编译失败", e);
            return CompileResult.fail("编译失败: " + e.getMessage());
        }
    }

    private String extractClassName(String sourceCode) {
        Matcher matcher = CLASS_NAME_PATTERN.matcher(sourceCode);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String processSourceCode(String sourceCode, String className) {
        // 去掉原 package 声明
        String code = PACKAGE_PATTERN.matcher(sourceCode).replaceAll("");
        // 添加固定包名
        return "package " + CUSTOM_PACKAGE + ";\n" + code;
    }

    private boolean compile(Path javaFile, Path classesDir) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            log.error("未找到 Java 编译器，请确保使用 JDK 运行");
            return false;
        }

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        Iterable<? extends JavaFileObject> compilationUnits =
                fileManager.getJavaFileObjectsFromFiles(Arrays.asList(javaFile.toFile()));

        List<String> options = Arrays.asList(
                "-d", classesDir.toAbsolutePath().toString(),
                "-classpath", System.getProperty("java.class.path")
        );

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
        boolean success = task.call();

        if (!success) {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                log.error("编译错误: {} - {}", diagnostic.getLineNumber(), diagnostic.getMessage(null));
            }
        }

        try {
            fileManager.close();
        } catch (IOException e) {
            // ignore
        }

        return success;
    }

    private Class<?> loadClass(Path classesDir, String fullClassName) {
        try {
            URL url = classesDir.toAbsolutePath().toUri().toURL();
            URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, this.getClass().getClassLoader());
            return classLoader.loadClass(fullClassName);
        } catch (Exception e) {
            log.error("加载类失败: {}", fullClassName, e);
            return null;
        }
    }

    /**
     * 编译结果
     */
    public static class CompileResult {
        private boolean success;
        private String message;
        private String pluginId;
        private String name;
        private String category;
        private String className;

        public static CompileResult success(String pluginId, String name, String category, String className) {
            CompileResult r = new CompileResult();
            r.success = true;
            r.pluginId = pluginId;
            r.name = name;
            r.category = category;
            r.className = className;
            return r;
        }

        public static CompileResult fail(String message) {
            CompileResult r = new CompileResult();
            r.success = false;
            r.message = message;
            return r;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getPluginId() { return pluginId; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getClassName() { return className; }
    }
}
