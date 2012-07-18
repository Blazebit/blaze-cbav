/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.annotation.constraint.validator;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import com.blazebit.annotation.apt.AnnotationProcessingUtil;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class ExceptionConstraintValidator extends
		AbstractExecutableConstraintValidator {

	@SuppressWarnings("unchecked")
	@Override
	public void validate(ProcessingEnvironment procEnv,
			RoundEnvironment roundEnv, TypeElement annotationType,
			AnnotationMirror annotation, ExecutableElement e) {
		AnnotationValue expectedExceptionTypes = AnnotationProcessingUtil
				.getAnnotationElementValue(procEnv, annotation,
						"expectedExceptions");
		AnnotationValue strictValue = AnnotationProcessingUtil
				.getAnnotationElementValue(procEnv, annotation, "strict");
		Boolean strict = (Boolean) strictValue.getValue();

		if (!matches(procEnv, strict, e.getThrownTypes(),
				getTypeMirrors((List<AnnotationValue>) expectedExceptionTypes
						.getValue()))) {
			procEnv.getMessager().printMessage(
					Diagnostic.Kind.ERROR,
					(String) AnnotationProcessingUtil
							.getAnnotationElementValue(procEnv, annotation,
									"errorMessage").getValue(), e, annotation,
					expectedExceptionTypes);
		}
	}

	private List<? extends TypeMirror> getTypeMirrors(
			List<AnnotationValue> annotationValues) {
		List<TypeMirror> list = new ArrayList<TypeMirror>();

		for (AnnotationValue o : annotationValues) {
			list.add((DeclaredType) o.getValue());
		}

		return list;
	}

	private boolean matches(ProcessingEnvironment procEnv, boolean strict,
			List<? extends TypeMirror> parameterTypes,
			List<? extends TypeMirror> expectedParameterTypes) {
		for (int i = 0; i < parameterTypes.size(); i++) {
			boolean foundMatch = false;

			if (strict) {
				for (int j = 0; j < expectedParameterTypes.size(); j++) {
					if (procEnv.getTypeUtils().isSameType(
							parameterTypes.get(i),
							expectedParameterTypes.get(i))) {
						foundMatch = true;
						break;
					}
				}
			} else {
				for (int j = 0; j < expectedParameterTypes.size(); j++) {
					if (procEnv.getTypeUtils().isSubtype(parameterTypes.get(i),
							expectedParameterTypes.get(i))) {
						foundMatch = true;
						break;
					}
				}
			}

			if (!foundMatch) {
				return false;
			}
		}

		return true;
	}
}
