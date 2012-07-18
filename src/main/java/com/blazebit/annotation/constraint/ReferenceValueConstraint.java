/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.annotation.constraint;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.blazebit.annotation.constraint.validator.ReferenceValueConstraintValidator;

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
@Retention(RetentionPolicy.RUNTIME)
public @interface ReferenceValueConstraint {
	Class<? extends Annotation> referencedAnnotationClass();

	String referencedAnnotationMember() default "value";

	boolean nullable() default false;

	ConstraintScope scope();

	String errorMessage() default "The given referencing value does not exist";
}
