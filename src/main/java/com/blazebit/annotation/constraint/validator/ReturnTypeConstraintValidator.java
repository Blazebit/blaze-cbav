/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.annotation.constraint.validator;

import com.blazebit.annotation.apt.AnnotationProcessingUtil;
import com.blazebit.annotation.constraint.BooleanValueConstraint;
import com.blazebit.annotation.constraint.ExecutableConstraintValidator;
import com.blazebit.annotation.constraint.ReturnTypeConstraint;
import com.blazebit.reflection.ReflectionUtil;
import java.util.List;
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
public class ReturnTypeConstraintValidator extends AbstractExecutableConstraintValidator {

    @Override
    public void validate(ProcessingEnvironment procEnv, RoundEnvironment roundEnv, TypeElement annotationType, AnnotationMirror annotation, ExecutableElement e) {        
        AnnotationValue expectedReturnType = AnnotationProcessingUtil.getAnnotationElementValue(procEnv, annotation, "expectedReturnType");
        AnnotationValue strictValue = AnnotationProcessingUtil.getAnnotationElementValue(procEnv, annotation, "strict");
        Boolean strict = (Boolean) strictValue.getValue();

        if(!matches(procEnv, strict, e.getReturnType(), (TypeMirror) expectedReturnType.getValue())){
            procEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, (String) AnnotationProcessingUtil.getAnnotationElementValue(procEnv, annotation, "errorMessage").getValue(), e, annotation, expectedReturnType);
        }
    }
    
    public boolean matches(ProcessingEnvironment procEnv, boolean strict, TypeMirror concreteReturnType, TypeMirror expectedReturnType){
        if(strict){
            return procEnv.getTypeUtils().isSameType(concreteReturnType, expectedReturnType);
        }else{
            return procEnv.getTypeUtils().isSubtype(concreteReturnType, expectedReturnType);
        }
    }
}
