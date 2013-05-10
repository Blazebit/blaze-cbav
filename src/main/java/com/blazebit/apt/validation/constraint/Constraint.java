/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.apt.validation.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
@Target(ElementType.ANNOTATION_TYPE)
public @interface Constraint {
	Class<? extends ConstraintValidator> value();
}
