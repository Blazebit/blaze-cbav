/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.annotation.constraint;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public interface ConstraintValidator {

	/**
	 * 
	 * @param procEnv
	 *            The processing environment
	 * @param roundEnv
	 *            The round environment
	 * @param constraintedAnnotationType
	 *            The annotation type on which Constraint annotations exist
	 * @param constraintAnnotation
	 *            The constraint annotation
	 * @param constraintedElement
	 *            The constrainted element annotated with
	 *            constraintedAnnotationType
	 */
	public void validate(ProcessingEnvironment procEnv,
			RoundEnvironment roundEnv, TypeElement constraintedAnnotationType,
			AnnotationMirror constraintAnnotation, Element constraintedElement);
}
