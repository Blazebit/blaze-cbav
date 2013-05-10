/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.apt.validation.constraint.validator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import com.blazebit.apt.validation.AnnotationProcessingUtils;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class ReturnTypeConstraintValidator extends
		AbstractExecutableConstraintValidator {

	@Override
	public void validate(ProcessingEnvironment procEnv,
			RoundEnvironment roundEnv, TypeElement annotationType,
			AnnotationMirror annotation, ExecutableElement e) {
		AnnotationValue expectedReturnType = AnnotationProcessingUtils
				.getAnnotationElementValue(procEnv, annotation,
						"expectedReturnType");
		AnnotationValue strictValue = AnnotationProcessingUtils
				.getAnnotationElementValue(procEnv, annotation, "strict");
		Boolean strict = (Boolean) strictValue.getValue();

		if (!matches(procEnv, strict, e.getReturnType(),
				(TypeMirror) expectedReturnType.getValue())) {
			procEnv.getMessager().printMessage(
					Diagnostic.Kind.ERROR,
					(String) AnnotationProcessingUtils
							.getAnnotationElementValue(procEnv, annotation,
									"errorMessage").getValue(), e, annotation,
					expectedReturnType);
		}
	}

	public boolean matches(ProcessingEnvironment procEnv, boolean strict,
			TypeMirror concreteReturnType, TypeMirror expectedReturnType) {
		if (strict) {
			return procEnv.getTypeUtils().isSameType(concreteReturnType,
					expectedReturnType);
		} else {
			return procEnv.getTypeUtils().isSubtype(concreteReturnType,
					expectedReturnType);
		}
	}
}
