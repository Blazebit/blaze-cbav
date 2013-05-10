/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.apt.validation.constraint;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.blazebit.apt.validation.constraint.validator.ReferenceValueConstraintValidator;

/**
 * An annotation method annotated with this annotation enforces the methods
 * return type to be a boolean and that the value of the annotation may only be
 * the specified one.
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
@ValueConstraint(ReferenceValueConstraintValidator.class)
@Target(ElementType.METHOD)
public @interface ReferenceValueConstraint {
	Class<? extends Annotation> referencedAnnotationClass();

	String referencedAnnotationMember() default "value";

	boolean nullable() default false;

	ConstraintScope scope();

	String errorMessage() default "The given referencing value does not exist";
}
