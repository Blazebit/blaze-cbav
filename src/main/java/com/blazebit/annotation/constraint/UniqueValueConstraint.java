/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.annotation.constraint;

import com.blazebit.annotation.constraint.validator.UniqueValueConstraintValidator;
import java.lang.annotation.*;

/**
 * An annotation method annotated with this annotation enforces the methods
 * return type to be a boolean and that the value of the annotation may only
 * be the specified one.
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
@ValueConstraint(UniqueValueConstraintValidator.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueValueConstraint {
    ConstraintScope scope();
    String errorMessage() default "The given value has to unique within the defined scope";
}
