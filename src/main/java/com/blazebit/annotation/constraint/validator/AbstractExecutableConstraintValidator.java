/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.annotation.constraint.validator;

import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.blazebit.annotation.apt.AnnotationProcessingUtil;
import com.blazebit.annotation.constraint.ExecutableConstraintValidator;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public abstract class AbstractExecutableConstraintValidator implements
		ExecutableConstraintValidator {

	@SuppressWarnings("unchecked")
	@Override
	public void validate(ProcessingEnvironment procEnv,
			RoundEnvironment roundEnv, TypeElement annotationType,
			AnnotationMirror annotation, Element e) {
		AnnotationMirror steretypeAnnotation = AnnotationProcessingUtil
				.findAnnotationMirror(procEnv, e,
						"javax.enterprise.inject.Stereotype");

		if (!(e instanceof ExecutableElement) && steretypeAnnotation == null) {
			procEnv.getMessager()
					.printMessage(
							Diagnostic.Kind.ERROR,
							"Non executable element '"
									+ e.getSimpleName()
									+ "' has been annotated with the constrainted annotation '"
									+ annotationType.getQualifiedName()
									+ "' that expects executable element", e,
							annotation);
			return;
		}

		if (steretypeAnnotation != null) {
			// Iterate over all elements that are annotated with the stereotype
			// annotation
			// and call this method recursivly to also support arbitrary depth
			// of stereotyping
			for (Element newE : (Set<Element>) roundEnv
					.getElementsAnnotatedWith((TypeElement) e)) {
				validate(procEnv, roundEnv, annotationType, annotation, newE);
			}
		} else {
			validate(procEnv, roundEnv, annotationType, annotation,
					(ExecutableElement) e);
		}
	}
}
