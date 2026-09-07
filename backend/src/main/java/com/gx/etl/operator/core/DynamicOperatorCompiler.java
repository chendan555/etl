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
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 动态编译 .java 源文件并注册算子
 */
@Component
public class DynamicOperatorCompiler {

    private static final Logger log = LoggerFactory.getLogger(DynamicOperatorCompiler.class);
    private static final String CUSTOM_PACKAGE = "com.gx.etl.operators.custom";
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+[\\w.]+\\s*;");
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("public\\s+class\\s+(\\w+)");

    private final OperatorRegistry operatorRegistry;

    public DynamicOperatorCompiler(OperatorRegistry operatorRegistry) {
        this.operatorRegistry = operatorRegistry;
    }

    /** 源码/编译输出目录：锚定到启动目录，避免 jar 部署时受工作目录影响 */
    public static Path getCustomDir() {
        return Paths.get(System.getProperty("user.dir"), "operators", "custom");
    }

    /**
     * 解码上传的算子源码：先按 UTF-8 严格解码，失败则回退 GBK（兼容 Windows 记事本保存的 ANSI 文件）。
     * 之后源码统一以 UTF-8 落盘和编译。
     */
    public static String decodeSourceCode(byte[] bytes) {
        try {
            return java.nio.charset.Charset.forName("UTF-8").newDecoder()
                    .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)
                    .onUnmappableCharacter(java.nio.charset.CodingErrorAction.REPORT)
                    .decode(java.nio.ByteBuffer.wrap(bytes)).toString();
        } catch (java.nio.charset.CharacterCodingException e) {
            log.info("上传源码不是合法 UTF-8，按 GBK 解码");
            return new String(bytes, java.nio.charset.Charset.forName("GBK"));
        }
    }

    /**
     * 编译并注册算子
     * @param sourceCode .java 源码
     * @return 编译结果
     */
    public synchronized CompileResult compileAndRegister(String sourceCode) {
        try {
            // 1. 提取类名
            String className = extractClassName(sourceCode);
            if (className == null) {
                return CompileResult.fail("无法从源码中提取类名");
            }

            // 2. 去掉原 package 声明，添加固定包名
            String processedCode = processSourceCode(sourceCode, className);

            // 3. 创建存储目录（绝对路径）
            Path customDir = getCustomDir();
            Files.createDirectories(customDir);

            // 4. 写入 .java 文件
            Path javaFile = customDir.resolve(className + ".java");
            Files.write(javaFile, processedCode.getBytes(StandardCharsets.UTF_8));

            // 5. 编译（返回 null 表示成功，否则为诊断信息）
            Path classesDir = customDir.resolve("classes");
            Files.createDirectories(classesDir);
            String diagnostics = compile(javaFile, classesDir);
            if (diagnostics != null) {
                return CompileResult.fail("编译失败: " + diagnostics);
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

    /**
     * 编译单个源文件。
     * @return 成功返回 null；失败返回可读的诊断信息
     */
    private String compile(Path javaFile, Path classesDir) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            log.error("未找到 Java 编译器，请确保使用 JDK 运行（而非 JRE）");
            return "未找到 Java 编译器，请确保使用 JDK 运行（而非 JRE）";
        }

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        Iterable<? extends JavaFileObject> compilationUnits =
                fileManager.getJavaFileObjectsFromFiles(Arrays.asList(javaFile.toFile()));

        String classpath = buildCompileClasspath();
        log.info("动态编译 classpath: {}", classpath);

        // 源码统一按 UTF-8 写盘，必须显式告诉 javac，否则按平台默认编码（中文 Windows 是 GBK）读取会乱码
        List<String> options = Arrays.asList(
                "-d", classesDir.toAbsolutePath().toString(),
                "-classpath", classpath,
                "-encoding", "UTF-8"
        );

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
        boolean success = task.call();

        String message = null;
        if (!success) {
            StringBuilder sb = new StringBuilder();
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                log.error("编译错误: {} - {}", diagnostic.getLineNumber(), diagnostic.getMessage(null));
                if (sb.length() > 0) sb.append("; ");
                sb.append("第").append(diagnostic.getLineNumber()).append("行: ")
                  .append(diagnostic.getMessage(null));
            }
            message = sb.length() > 0 ? sb.toString() : "未知编译错误";
        }

        try {
            fileManager.close();
        } catch (IOException e) {
            // ignore
        }

        return message;
    }

    /**
     * 构建 javac 的 classpath。
     * 关键点：打成 Spring Boot fat jar 后 java.class.path 只有 jar 本身，
     * 项目的类在 jar 内 BOOT-INF/classes、依赖在 BOOT-INF/lib，javac 无法直接读取，
     * 因此首次编译时把这两部分解压到本地缓存目录，给 javac 一份真实文件系统的 classpath。
     */
    private String buildCompileClasspath() {
        String javaClassPath = System.getProperty("java.class.path", "");
        String[] entries = javaClassPath.split(File.pathSeparator);
        List<String> result = new ArrayList<>();
        boolean extracted = false;

        for (String entry : entries) {
            if (entry.isEmpty()) continue;
            Path path = Paths.get(entry);
            if (isSpringBootFatJar(path)) {
                result.addAll(extractFatJarClasspath(path));
                extracted = true;
            } else {
                result.add(entry);
            }
        }
        if (extracted) {
            log.info("检测到 Spring Boot fat jar，已解压依赖供动态编译使用");
        }
        return String.join(File.pathSeparator, result);
    }

    /** 判断是否为 Spring Boot fat jar（jar 内含 BOOT-INF 目录） */
    private boolean isSpringBootFatJar(Path path) {
        if (!Files.isRegularFile(path) || !path.toString().toLowerCase().endsWith(".jar")) {
            return false;
        }
        try (JarFile jar = new JarFile(path.toFile())) {
            return jar.getJarEntry("BOOT-INF/classes/") != null;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 解压 fat jar 的 BOOT-INF/classes 与 BOOT-INF/lib 到缓存目录，返回 javac classpath 条目。
     * 带标记文件（记录源 jar 大小+修改时间），jar 没变时直接复用缓存。
     */
    private List<String> extractFatJarClasspath(Path fatJar) {
        Path cacheDir = getCustomDir().resolve("compiler-cp").resolve(
                fatJar.getFileName().toString().replaceAll("[^\\w.\\-]", "_"));
        Path classesDir = cacheDir.resolve("classes");
        Path libsDir = cacheDir.resolve("libs");
        Path marker = cacheDir.resolve(".marker");

        String fingerprint = fatJar.toFile().length() + ":" + fatJar.toFile().lastModified();
        boolean needExtract = true;
        if (Files.exists(marker)) {
            try {
                needExtract = !fingerprint.equals(new String(Files.readAllBytes(marker), StandardCharsets.UTF_8).trim());
            } catch (IOException ignore) {
            }
        }

        if (needExtract) {
            log.info("开始解压 fat jar 依赖到 {}（首次约需数秒）", cacheDir);
            try {
                if (Files.exists(cacheDir)) {
                    deleteRecursively(cacheDir);
                }
                Files.createDirectories(classesDir);
                Files.createDirectories(libsDir);
                try (JarFile jar = new JarFile(fatJar.toFile())) {
                    Enumeration<JarEntry> entries = jar.entries();
                    byte[] buffer = new byte[8192];
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (entry.isDirectory()) continue;
                        if (name.startsWith("BOOT-INF/classes/")) {
                            Path target = classesDir.resolve(name.substring("BOOT-INF/classes/".length()));
                            Files.createDirectories(target.getParent());
                            copyEntry(jar.getInputStream(entry), target, buffer);
                        } else if (name.startsWith("BOOT-INF/lib/") && name.endsWith(".jar")) {
                            Path target = libsDir.resolve(name.substring("BOOT-INF/lib/".length()));
                            Files.createDirectories(target.getParent());
                            copyEntry(jar.getInputStream(entry), target, buffer);
                        }
                    }
                }
                Files.write(marker, fingerprint.getBytes(StandardCharsets.UTF_8));
                log.info("fat jar 依赖解压完成");
            } catch (IOException e) {
                throw new IllegalStateException("解压 fat jar 依赖失败: " + e.getMessage(), e);
            }
        }

        List<String> cp = new ArrayList<>();
        cp.add(classesDir.toAbsolutePath().toString());
        File[] jars = libsDir.toFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
        if (jars != null) {
            for (File jar : jars) {
                cp.add(jar.getAbsolutePath());
            }
        }
        return cp;
    }

    private void copyEntry(InputStream in, Path target, byte[] buffer) throws IOException {
        try (OutputStream out = Files.newOutputStream(target)) {
            int n;
            while ((n = in.read(buffer)) > 0) {
                out.write(buffer, 0, n);
            }
        } finally {
            in.close();
        }
    }

    private void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) return;
        try (java.nio.file.DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path child : stream) {
                if (Files.isDirectory(child)) {
                    deleteRecursively(child);
                } else {
                    Files.delete(child);
                }
            }
        }
        Files.delete(path);
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
