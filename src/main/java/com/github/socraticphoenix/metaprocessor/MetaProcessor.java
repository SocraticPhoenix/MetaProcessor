package com.github.socraticphoenix.metaprocessor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;

@SupportedAnnotationTypes("com.github.socraticphoenix.metaprocessor.MetaDebug")
@AutoService(Processor.class)
public class MetaProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<Element> annotatedElements = new HashSet<>();
        for (TypeElement annotation : annotations) {
            annotatedElements.addAll(roundEnv.getElementsAnnotatedWith(annotation));
        }

        annotatedElements.forEach(this::report);
        return true;
    }

    private void report(Element element) {
        log(element, "Element has kind: %s", element.getKind());
        log(element, "Simple name: %s", element.getSimpleName());

        if (element instanceof QualifiedNameable nameable) {
            log(element, "Qualified name: %s", nameable.getQualifiedName());
        }

        log(element, "Modifiers: %s", element.getModifiers());
        report(element, element.asType());
    }

    private void report(Element element, TypeMirror type) {
        log(element, "Element type has kind: %s", type.getKind());
        log(element, "Element type string: %s", type);

    }

    private void log(Element element, String message, Object... elements) {
        MetaDebug metaDebug = element.getAnnotation(MetaDebug.class);
        Diagnostic.Kind kind;
        if (metaDebug == null) {
            kind = Diagnostic.Kind.NOTE;
        } else {
            kind = switch (metaDebug.policy()) {
                case INFO -> Diagnostic.Kind.NOTE;
                case WARNING -> Diagnostic.Kind.WARNING;
                case ERROR -> Diagnostic.Kind.ERROR;
            };
        }

        this.processingEnv.getMessager()
                .printMessage(kind, String.format(message, elements), element);
    }

}
