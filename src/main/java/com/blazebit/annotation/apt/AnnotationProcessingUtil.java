/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.annotation.apt;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class AnnotationProcessingUtil {

    private AnnotationProcessingUtil() {
    }

    public static Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> getAnnotationElement(AnnotationMirror annotation, String member) {
        return getAnnotationElement(null, annotation, member);
    }

    public static Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> getAnnotationElement(ProcessingEnvironment processEnv, AnnotationMirror annotation, String member) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> annotationElementValues = null;
        
        if (processEnv != null) {
            annotationElementValues = processEnv.getElementUtils().getElementValuesWithDefaults(annotation);
        } else {
            annotationElementValues = annotation.getElementValues();
        }
        
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationElementValues.entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals(member)) {
                return entry;
            }
        }

        return null;
    }

    public static AnnotationValue getAnnotationElementValue(AnnotationMirror annotation, String member) {
        return getAnnotationElementValue(null, annotation, member);
    }

    public static AnnotationValue getAnnotationElementValue(ProcessingEnvironment processEnv, AnnotationMirror annotation, String member) {
        Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry = getAnnotationElement(processEnv, annotation, member);
        return entry == null ? null : entry.getValue();
    }

    public static AnnotationMirror findAnnotationMirror(Element element, TypeElement annotationTypeElement) {
        return findAnnotationMirror(element, annotationTypeElement.getQualifiedName().toString());
    }

    public static AnnotationMirror findAnnotationMirror(Element element, Class<? extends Annotation> annotation) {
        return findAnnotationMirror(element, annotation.getName());
    }

    public static AnnotationMirror findAnnotationMirror(Element element, String qualifiedAnnotationName) {
        return findAnnotationMirror(null, element, qualifiedAnnotationName);
    }

    public static AnnotationMirror findAnnotationMirror(ProcessingEnvironment processEnv, Element element, TypeElement annotationTypeElement) {
        return findAnnotationMirror(processEnv, element, annotationTypeElement.getQualifiedName().toString());
    }

    public static AnnotationMirror findAnnotationMirror(ProcessingEnvironment processEnv, Element element, Class<? extends Annotation> annotation) {
        return findAnnotationMirror(processEnv, element, annotation.getName());
    }

    public static AnnotationMirror findAnnotationMirror(ProcessingEnvironment processEnv, Element element, String qualifiedAnnotationName) {
        List<? extends AnnotationMirror> annotationMirrors = null;
        
        if(processEnv != null){
            annotationMirrors = processEnv.getElementUtils().getAllAnnotationMirrors(element);
        } else {
            annotationMirrors = element.getAnnotationMirrors();
        }
        
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            if (((TypeElement) annotationMirror.getAnnotationType().asElement()).getQualifiedName().toString().equals(qualifiedAnnotationName)) {
                return annotationMirror;
            }
        }

        return null;
    }
}
