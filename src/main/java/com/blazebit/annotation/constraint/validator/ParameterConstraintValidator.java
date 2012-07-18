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
import javax.lang.model.element.Element;
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
public class ParameterConstraintValidator extends
		AbstractExecutableConstraintValidator {

	@SuppressWarnings("unchecked")
	@Override
	public void validate(ProcessingEnvironment procEnv,
			RoundEnvironment roundEnv, TypeElement annotationType,
			AnnotationMirror annotation, ExecutableElement e) {
		AnnotationValue expectedReturnType = AnnotationProcessingUtil
				.getAnnotationElementValue(procEnv, annotation,
						"expectedParameterTypes");
		AnnotationValue strictValue = AnnotationProcessingUtil
				.getAnnotationElementValue(procEnv, annotation, "strict");
		Boolean strict = (Boolean) strictValue.getValue();

		if (!matches(procEnv, strict, getElementTypeMirrors(e.getParameters()),
				getTypeMirrors((List<AnnotationValue>) expectedReturnType
						.getValue()))) {
			procEnv.getMessager().printMessage(
					Diagnostic.Kind.ERROR,
					(String) AnnotationProcessingUtil
							.getAnnotationElementValue(procEnv, annotation,
									"errorMessage").getValue(), e, annotation,
					expectedReturnType);
		}
	}

	private List<? extends TypeMirror> getElementTypeMirrors(
			List<? extends Element> elements) {
		List<TypeMirror> list = new ArrayList<TypeMirror>();

		for (Element o : elements) {
			list.add(o.asType());
		}

		return list;
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
		if (parameterTypes.size() != expectedParameterTypes.size()) {
			return false;
		}

		if (strict) {
			for (int i = 0; i < parameterTypes.size(); i++) {
				if (!procEnv.getTypeUtils().isSameType(
						expectedParameterTypes.get(i), parameterTypes.get(i))) {
					return false;
				}
			}
		} else {
			for (int i = 0; i < parameterTypes.size(); i++) {
				if (!procEnv.getTypeUtils().isSubtype(
						expectedParameterTypes.get(i), parameterTypes.get(i))) {
					return false;
				}
			}
		}

		return true;
	}
}
