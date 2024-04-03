package com.aaron.compiler;

import com.aaron.diview.DIView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;


@AutoService(Processor.class)
public class DIViewProcessor extends AbstractProcessor {
    private Filer mFiler;
    private Messager mMessager;
    private Elements elementUtils;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(DIView.class.getCanonicalName());
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
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        log("============== Start Process @DIView ==============");

        processDIView(roundEnv);
        return true;
    }

    private void processDIView(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(DIView.class);
        if (CommonUtils.isEmpty(elements)) {
            log("[DIView]: 获取的注解为空");
            return;
        }

        String className = "";
        for (Element element : elements) {
            log("当前处理的element：" + element);
            TypeElement activity = (TypeElement) element.getEnclosingElement();
            // 重复创建文件会报错，所以创建过了就不再创建
            if (className.equals(activity.getQualifiedName().toString())) {
                continue;
            }
            className = activity.getQualifiedName().toString();
            log("className = " + className);
            MethodSpec.Builder bindViewBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.get(activity.asType()), "target");

            List<? extends Element> members = elementUtils.getAllMembers(activity);
            for (Element member : members) {
                DIView diView = member.getAnnotation(DIView.class);
                if (diView == null) {
                    continue;
                }

                bindViewBuilder.addStatement(String.format("target.%s = (%s) target.findViewById(%s)", member.getSimpleName(), ClassName.get(member.asType()).toString(), diView.value()));
            }

            String simpleClassName = activity.getSimpleName() + "Blade";
            TypeSpec typeSpec = TypeSpec.classBuilder(simpleClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(bindViewBuilder.build())
                    .addSuperinterface(ClassName.get("com.aaron.dibinder", "ViewBinder"))
                    .build();

            try {
                JavaFile javaFile = JavaFile.builder(elementUtils.getPackageOf(activity).getQualifiedName().toString(), typeSpec).build();
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void log(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }
}
