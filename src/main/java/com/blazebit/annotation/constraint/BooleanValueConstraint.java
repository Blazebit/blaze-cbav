/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.annotation.constraint;

import com.blazebit.annotation.constraint.validator.BooleanValueConstraintValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation method annotated with this annotation enforces the methods
 * return type to be a boolean and that the value of the annotation may only
 * be the specified one.
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
@ReturnTypeConstraint(expectedReturnType=boolean.class)
@ValueConstraint(BooleanValueConstraintValidator.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BooleanValueConstraint {
    boolean expectedValue();
    String errorMessage() default "The given boolean value does not match the expected";
}
