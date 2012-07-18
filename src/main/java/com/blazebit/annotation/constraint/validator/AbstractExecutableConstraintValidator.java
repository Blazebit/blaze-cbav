/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.annotation.constraint.validator;

import com.blazebit.annotation.apt.AnnotationProcessingUtil;
import com.blazebit.annotation.constraint.BooleanValueConstraint;
import com.blazebit.annotation.constraint.ExceptionConstraint;
import com.blazebit.annotation.constraint.ExecutableConstraintValidator;
import com.blazebit.annotation.constraint.ReturnTypeConstraint;
import com.blazebit.reflection.ReflectionUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public abstract class AbstractExecutableConstraintValidator implements ExecutableConstraintValidator {

    @Override
    public void validate(ProcessingEnvironment procEnv, RoundEnvironment roundEnv, TypeElement annotationType, AnnotationMirror annotation, Element e) {
        AnnotationMirror steretypeAnnotation = AnnotationProcessingUtil.findAnnotationMirror(procEnv, e, "javax.enterprise.inject.Stereotype");
        
        if(!(e instanceof ExecutableElement) && steretypeAnnotation == null){
            procEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Non executable element '" + e.getSimpleName() + "' has been annotated with the constrainted annotation '" + annotationType.getQualifiedName() + "' that expects executable element", e, annotation);
            return;
        }
        
        if(steretypeAnnotation != null){
            // Iterate over all elements that are annotated with the stereotype annotation
            // and call this method recursivly to also support arbitrary depth of stereotyping
            for(Element newE : (Set<Element>) roundEnv.getElementsAnnotatedWith((TypeElement)e)){
                validate(procEnv, roundEnv, annotationType, annotation, newE);
            }
        } else {
            validate(procEnv, roundEnv, annotationType, annotation, (ExecutableElement) e);
        }
    }
}
