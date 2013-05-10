/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.apt.validation.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.blazebit.apt.validation.constraint.validator.ExceptionConstraintValidator;

/**
 * An annotation type A annotated with this annotation enforces methods that are
 * annotated with the annotation type A, to have the given return type.
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
@Constraint(ExceptionConstraintValidator.class)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ExceptionConstraint {
	Class<? extends Throwable>[] expectedExceptions() default {};

	// When true, the types have to match exactly, otherwise the concrete types
	// may be subtypes of the declared ones
	boolean strict() default false;

	String errorMessage() default "The exceptions in the throws clause of the method does not match the expected";
}
