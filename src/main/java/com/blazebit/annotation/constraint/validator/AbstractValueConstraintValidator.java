/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.annotation.constraint.validator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.blazebit.annotation.constraint.ValueConstraintValidator;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public abstract class AbstractValueConstraintValidator implements
		ValueConstraintValidator {

	@Override
	public void validate(ProcessingEnvironment procEnv,
			RoundEnvironment roundEnv, TypeElement annotationType,
			AnnotationMirror annotation, Element e) {

	}
}
