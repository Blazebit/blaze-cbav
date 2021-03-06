/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.apt.validation.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.blazebit.apt.validation.constraint.validator.ReturnTypeConstraintValidator;

/**
 * An annotation type A annotated with this annotation enforces methods that are
 * annotated with the annotation type A, to have the given return type.
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
@Constraint(ReturnTypeConstraintValidator.class)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ReturnTypeConstraint {
	Class<?> expectedReturnType();

	// When true, the types have to match exactly, otherwise the concrete type
	// may be subtype of the declared one
	boolean strict() default false;

	String errorMessage() default "The return type of the method does not match the expected";
}
