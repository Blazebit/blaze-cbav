/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.annotation.constraint.validator;

import com.blazebit.annotation.apt.AnnotationProcessingUtil;
import com.blazebit.annotation.constraint.*;
import com.blazebit.reflection.ReflectionUtil;
import java.util.ArrayList;
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
public abstract class AbstractValueConstraintValidator implements ValueConstraintValidator {

    @Override
    public void validate(ProcessingEnvironment procEnv, RoundEnvironment roundEnv, TypeElement annotationType, AnnotationMirror annotation, Element e) {        
        
    }
}
