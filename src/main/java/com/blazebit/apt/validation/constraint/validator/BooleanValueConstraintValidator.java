/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.apt.validation.constraint.validator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.blazebit.apt.validation.AnnotationProcessingUtils;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class BooleanValueConstraintValidator extends
		AbstractValueConstraintValidator {

	@Override
	public void validate(ProcessingEnvironment procEnv,
			RoundEnvironment roundEnv, TypeElement annotationType,
			AnnotationMirror annotation, ExecutableElement annotationMember,
			Element e, Object value) {
		AnnotationValue expectedValue = AnnotationProcessingUtils
				.getAnnotationElementValue(procEnv, annotation, "expectedValue");

		if (!expectedValue.getValue().equals(value)) {
			procEnv.getMessager().printMessage(
					Diagnostic.Kind.ERROR,
					(String) AnnotationProcessingUtils
							.getAnnotationElementValue(procEnv, annotation,
									"errorMessage").getValue(), e, annotation,
					expectedValue);
		}
	}
}
