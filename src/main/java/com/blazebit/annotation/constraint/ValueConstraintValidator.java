/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.annotation.constraint;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public interface ValueConstraintValidator extends ConstraintValidator {

	/**
	 * 
	 * @param procEnv
	 * @param annotation
	 * @param value
	 *            Never null, this value is wether the value declared for an
	 *            annotation memeber or the default value
	 */
	public void validate(ProcessingEnvironment procEnv,
			RoundEnvironment roundEnv, TypeElement annotationType,
			AnnotationMirror annotation, ExecutableElement annotationMember,
			Element e, Object value);
}
