package com.aaron.compiler;

import com.aaron.diview.ServiceLoaderInterface;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * @author dbj
 * @date 1/25/24
 * @description
 */

@AutoService(Processor.class)
public class ServiceLoaderProcessor extends AbstractProcessor {
    private final static boolean IS_DEBUG = true;

    private Filer mFiler;
    private Messager mMessager;
    private Elements elementUtils;
    private Types typeUtils;

    /**
     * @see com.aaron.diview.ServiceLoaderInterface
     * 通过注解解析出来 <interfaceName, <key, implClassName>> 的集合
     */
    private Map<String, Map<String, String>> servicesMap = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(ServiceLoaderInterface.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            /**
             * 生成apt注册文件
             */
            generateConfigFile();
        } else {
            processServiceRegistry(roundEnv);
        }

        return true;
    }

    private void processServiceRegistry(RoundEnvironment roundEnv) {
        log("========= Start Process @ServiceInterface =========");
        Set<? extends Element> services = roundEnv.getElementsAnnotatedWith(ServiceLoaderInterface.class);
        if (CommonUtils.isEmpty(services)) {
            log("processServiceRegistry: 获取的注解数据为空");
            return;
        }

        String implClassName = "";
        for (Element element : services) {
            if (element.getKind() != ElementKind.CLASS) {
                log("不是class类型，遍历下一个文件");
                continue;
            }

            // 转换成Type类型
            TypeElement typeElement = (TypeElement) element;
            // 获取实现类的全限定类名（注解都是标在实现类上的）
            implClassName = elementUtils.getBinaryName(typeElement).toString();

            /**
             * 解析注解参数
             */
            ServiceLoaderInterface annotation = typeElement.getAnnotation(ServiceLoaderInterface.class);
            if (annotation == null) {
                log("annotation为空，遍历下一个文件");
                continue;
            }

            String key = annotation.key();
            String interfaceName = null;
            try {
                Class<?> clazz = annotation.interfaceClass();
                if (clazz != null && clazz != Void.class) {
                    interfaceName = clazz.getName();
                }
            } catch (MirroredTypeException e) {
                interfaceName = processMirroredTypeException(e);
            }

            if (CommonUtils.isEmpty(interfaceName)) {
                interfaceName = annotation.interfaceName();
            }

            /**
             * 以<interfaceName, <key, implClassName>>形式储存数据到 {@link servicesMap}
             */
            storeInterface(key, interfaceName, implClassName);
        }
    }

    private String processMirroredTypeException(MirroredTypeException mte) {
        /**
         * 如果接口类还没有被编译，也就是没有Class文件，这种情况下，
         * 直接获取Class会抛出MirroredTypeException异常，
         * 但是MirroredTypeException包含一个TypeMirror，
         * 它表示未被编译的类，可以通过TypeMirror可以得到类名
         */
        return processExceptionTypeMirror(mte.getTypeMirror());
    }

    private String processExceptionTypeMirror(TypeMirror typeMirror) {
        String interfaceName = null;
        // DeclaredType方式和typeUtils等价，随便选择一个使用
//        DeclaredType classTypeMirror = (DeclaredType)typeMirror;
//        TypeElement classTypeElement = (TypeElement)classTypeMirror.asElement();
        TypeElement classTypeElement = (TypeElement) typeUtils.asElement(typeMirror);
        String qualifiedSuperClassName = classTypeElement.getQualifiedName().toString();
        if (!"java.lang.Void".equals(qualifiedSuperClassName)) {
            interfaceName = qualifiedSuperClassName;
        }
        return interfaceName;
    }

    private void storeInterface(String key, String interfaceName, String className) {
        if (CommonUtils.isEmpty(key) || CommonUtils.isEmpty(interfaceName) || CommonUtils.isEmpty(className)) {
            log("获取的注解数据为空");
            return;
        }

        Map<String, String> map = servicesMap.get(interfaceName);
        if (map == null) {
            map = new HashMap<>();
            servicesMap.put(interfaceName, map);
        }
        map.put(key, className);
        log("key = " + key + ", interfaceName = " + interfaceName + ", className = " + className);
    }

    private void generateConfigFile() {
        log("~~~~ generate config file ~~~");
        for (String interfaceName : servicesMap.keySet()) {
            String resourceFile = "META-INF/services/serviceloader/" + interfaceName + "_auto";
            try {
                FileObject existingFile = mFiler.getResource(StandardLocation.CLASS_OUTPUT, "", resourceFile);
                existingFile.delete();

                Map<String, String> map = servicesMap.get(interfaceName);
                if (map == null) {
                    continue;
                }

                FileObject fileObject = mFiler.createResource(StandardLocation.CLASS_OUTPUT, "", resourceFile);
                log("fileObject=" + fileObject.toUri().toString());
                Writer writer = fileObject.openWriter();

                for (String key : map.keySet()) {
                    String implClass = map.get(key);
                    String line = key + ":" + implClass;

                    writer.write(line);
                    writer.write("\n");
                }

                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void log(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }
}
