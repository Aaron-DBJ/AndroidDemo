package com.aaron.custom

import com.android.SdkConstants
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.ide.common.internal.WaitableExecutor
import com.android.ide.common.workers.WorkerExecutorFacade
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.workers.WorkerExecutor

import java.util.concurrent.Callable
import java.util.jar.JarEntry
import java.util.jar.JarFile

public class CustomPlugin extends Transform implements Plugin<Project> {
    Project mProject;
    static final String CLASS_SERVICE_LOADER = "com.aaron.dibinder.ServiceLoader";
    static final String SUFFIX_META_INF = "IService_auto";
    private static final Map<String, String> services = new HashMap<>();
    private WaitableExecutor executor = WaitableExecutor.useGlobalSharedThreadPool()



    @Override
    void apply(Project project) {
        mProject = project
//        project.extensions.findByType(BaseExtension.class).registerTransform(this)
        project.android.registerTransform(this)
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        println("******************************************************************************")
        println("******                                                                  ******")
        println("******                        FFmpegKit 编译插件启动                      ******")
        println("******                                                                  ******")
        println("******************************************************************************")
        // 删除上次所有编译内容
        transformInvocation.outputProvider.deleteAll();

        // 使用javassist框架hook原程序
        ClassPool classPool = ClassPool.getDefault()
        // 添加搜索路径，这样hook时才能找到对应类
        //project.android.bootClasspath 加入android.jar，不然找不到android相关的所有类
        classPool.appendClassPath(mProject.android.bootClasspath[0].toString())
        classPool.importPackage('android.os.Bundle')

        // 设置hook后的文件输出路径
        def outDir = transformInvocation.outputProvider.getContentLocation("main", outputTypes, scopes, Format.DIRECTORY)
        println("插桩后的文件输出路径：$outDir.absolutePath")

        // directoryInputs内的类
        ArrayList<String> mClassNames = new ArrayList<>()

        // jarInputs内的类
        ArrayList<String> mJarClassNames = new ArrayList<>()


        // 遍历编译时的输入内容，TransformInput是输入数据的抽象
        transformInvocation.inputs.each { TransformInput input ->
            // 输入数据有2类，DirectoryInput和JarInput，前者表示工程中所有的文件，后者表示依赖的jar、aar文件
            input.directoryInputs.each { DirectoryInput directoryInput ->
                def dirPath = directoryInput.getFile().getAbsolutePath()
                println("DirectoryInput 根路径：$dirPath")
                // 把该路径加入可搜索范围，确保javassist能够访问到
                classPool.insertClassPath(dirPath)

                // 遍历所有文件，获取文件全限定名，用于后续修改
                FileUtils.listFiles(directoryInput.file, null, true).each { file ->
                    println("DirectoryInput 文件绝对路径：$file.absolutePath")
                    if (file.absolutePath.endsWith(SUFFIX_META_INF)) {
                        parseMetaInfo(file.absolutePath)
                        return
                    }

                    if (file.absolutePath.endsWith("Blade")) {
                        return
                    }

                    if (file.absolutePath.endsWith(SdkConstants.DOT_CLASS)) {
                        def className = file.absolutePath.substring(dirPath.length() + 1, file.absolutePath.length() - SdkConstants.DOT_CLASS.length()).replaceAll('/', '.')
                        println("DirectoryInput类名：$className")
                        mClassNames.add(className)
                    }
                }
            }

            input.jarInputs.each { JarInput jarInput ->
                def dirPath = jarInput.getFile().getAbsolutePath()
                println("JarInput 根路径：$dirPath")
                // 把该路径加入可搜索范围，确保javassist能够访问到
                classPool.insertClassPath(dirPath)

                JarFile jarFile = new JarFile(jarInput.file)
                println("jarFile name = " + jarFile.getName())
                Enumeration<JarEntry> entries = jarFile.entries()
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (!entryName.endsWith(SdkConstants.DOT_CLASS)) {
                        continue
                    }
                    println("JarInput 文件绝对路径：$entryName")
                    def className = entryName.replace(SdkConstants.DOT_CLASS, "").replaceAll('/', '.')
                    println("JarInput 类名：$className")
                    mJarClassNames.add(className)
                }
            }
        }

        executor.execute(new Callable<Object>() {
            @Override
            Object call() throws Exception {
                doDirectoryTransform(classPool, mClassNames, outDir)
                return null
            }
        })

        executor.execute(new Callable<Object>() {
            @Override
            Object call() throws Exception {
                doJarTransform(classPool, mJarClassNames, outDir)
                return null
            }
        })
        //等待所有任务结束
        executor.waitForTasksWithQuickFail(true)
    }

    // 具体的hook逻辑，例如我们想给所有Activity的生命周期方法onCreate末尾添加一个toast
    static void doDirectoryTransform(ClassPool classPool, List<String> classNames, File outDir) {
        println("============= doDirectoryTransform =============")
        if (classPool == null || classNames == null) {
            return
        }

        classNames.each { className ->
            CtClass ctClass = classPool.get(className)
            if (ctClass.isFrozen()) {
                ctClass.defrost()
            }

            if (ctClass != null) {
                println("ctClass.name = $ctClass.name")
                if (ctClass.name.endsWith("com.aaron.hookpractice.utils.Utils")) {
                    CtMethod onCreateMethod = ctClass.getDeclaredMethod("toast")
//                    String toastStr = """android.widget.Toast.makeText(this,"我是被插入的Toast代码~!!",android.widget.Toast.LENGTH_SHORT).show();"""
                    String toastStr = "android.util.Log.d(\"插桩的日志\", \"我是被插入日志\");"
                    String checkNull = "{if(\$1 == null){return;}}"
                    onCreateMethod.insertBefore(checkNull)
                    onCreateMethod.insertAfter(toastStr)
                }
            }

            ctClass.writeFile(outDir.absolutePath)
            ctClass.detach()
        }
    }

    static void parseMetaInfo(String metaInfoPath) {
        if (isEmpty(metaInfoPath)) {
            return
        }

        println("========= parseMetaInfo =========")
        try {
            BufferedReader br = new BufferedReader(new FileReader(metaInfoPath))
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] pair = line.trim().split(":", 2)
                println("key = " + pair[0] + "; value = " + pair[1])
                services.put(pair[0], pair[1])
            }

            br.close()
        } catch (IOException e) {
            println(e.message)
        }
    }

    static void doJarTransform(ClassPool classPool, List<String> classNames, File outDir) {
        println("============= doJarTransform =============")
        if (classPool == null || classNames == null) {
            return
        }

        classNames.each { className ->
            CtClass ctClass = classPool.get(className)
            if (ctClass.isFrozen()) {
                ctClass.defrost()
            }

            if (CLASS_SERVICE_LOADER.equals(ctClass.name)) {
                println("[doJarTransform]: class name is " + ctClass.name)
                CtMethod ctMethod = ctClass.getDeclaredMethod("getServicesMap")
                println("[doJarTransform]: method name is " + ctMethod.name)
                CtClass mapType = classPool.get(Map.class.getName())
                ctMethod.addLocalVariable("map", mapType)
                ctMethod.insertBefore("map = new java.util.HashMap();")

                for (Map.Entry<String, String> entry : services.entrySet()) {
                    String key = entry.key;
                    String value = entry.value
                    println("key = ${key}, value = ${value}")
                    ctMethod.insertAfter("map.put(\"$key\", \"$value\");")
                }
                ctMethod.insertAfter("servicesMap.put(\"com.aaron.dibinder.IService\", map);")
            }
            ctClass.writeFile(outDir.absolutePath)
            ctClass.detach()
        }
    }

    @Override
    String getName() {
        return "MyPlugin"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }
}