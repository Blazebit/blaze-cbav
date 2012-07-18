/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.annotation.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.blazebit.annotation.constraint.validator.ParameterConstraintValidator;

/**
 * An annotation type A annotated with this annotation enforces methods that are
 * annotated with the annotation type A, to have the given parameter types.
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
@Constraint(ParameterConstraintValidator.class)
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParameterConstraint {
	Class<?>[] expectedParameterTypes() default {};

	// When true, the types have to match exactly, otherwise the concrete types
	// may be super types of the declared ones
	boolean strict() default false;

	String errorMessage() default "The parameter types of the method does not match the expected";
}
