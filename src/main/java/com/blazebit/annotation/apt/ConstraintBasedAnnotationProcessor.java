/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.annotation.apt;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

import com.blazebit.annotation.constraint.Constraint;
import com.blazebit.annotation.constraint.ConstraintValidator;
import com.blazebit.annotation.constraint.ValueConstraint;
import com.blazebit.annotation.constraint.ValueConstraintValidator;

/**
 * Constraint Validator classes must be available in compiled form!
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ConstraintBasedAnnotationProcessor extends AbstractProcessor {

	private Map<Class<? extends ConstraintValidator>, ConstraintValidator> constraintValidatorMap = new HashMap<Class<? extends ConstraintValidator>, ConstraintValidator>();
	private Map<Class<? extends ValueConstraintValidator>, ValueConstraintValidator> valueConstraintValidatorMap = new HashMap<Class<? extends ValueConstraintValidator>, ValueConstraintValidator>();

	@Override
	@SuppressWarnings("unchecked")
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		for (TypeElement annotationType : annotations) {
			if (shouldSkip(annotationType)) {
				continue;
			}

			// validation processing for annotations with annotation constraint
			for (AnnotationMirror annotationMirror : annotationType
					.getAnnotationMirrors()) {
				TypeElement memberAnnotationTypeElement = (TypeElement) annotationMirror
						.getAnnotationType().asElement();
				Class<? extends ConstraintValidator> constraintValidatorClass = null;

				if (shouldSkip(memberAnnotationTypeElement)) {
					continue;
				}

				constraintValidatorClass = (Class<? extends ConstraintValidator>) getConstraintValidatorClass(
						memberAnnotationTypeElement, Constraint.class);
				ConstraintValidator validator = getConstraintValidator(
						constraintValidatorClass, constraintValidatorMap);

				if (validator != null) {
					for (Element e : roundEnv
							.getElementsAnnotatedWith(annotationType)) {
						validator.validate(processingEnv, roundEnv,
								annotationType, annotationMirror, e);
					}
				}
			}

			// validation processing for annotation member values
			for (ExecutableElement annotationMember : ElementFilter
					.methodsIn(annotationType.getEnclosedElements())) {
				// annotationMember is a method of an annotation
				for (AnnotationMirror annotationMirror : annotationMember
						.getAnnotationMirrors()) {
					// annotationMirror is an annotation on a method of an
					// annotation
					TypeElement memberAnnotationTypeElement = (TypeElement) annotationMirror
							.getAnnotationType().asElement();
					Class<? extends ValueConstraintValidator> constraintValidatorClass = null;

					if (shouldSkip(memberAnnotationTypeElement)) {
						continue;
					}

					constraintValidatorClass = (Class<? extends ValueConstraintValidator>) getConstraintValidatorClass(
							memberAnnotationTypeElement, ValueConstraint.class);
					ValueConstraintValidator validator = getConstraintValidator(
							constraintValidatorClass,
							valueConstraintValidatorMap);

					if (validator != null) {
						for (Element e : roundEnv
								.getElementsAnnotatedWith(annotationType)) {
							AnnotationMirror elementAnnotationMirror = AnnotationProcessingUtil
									.findAnnotationMirror(processingEnv, e,
											annotationType);
							Object actualValue = AnnotationProcessingUtil
									.getAnnotationElementValue(
											processingEnv,
											elementAnnotationMirror,
											annotationMember.getSimpleName()
													.toString()).getValue();
							validator.validate(processingEnv, roundEnv,
									annotationType, annotationMirror,
									annotationMember, e, actualValue);
						}
					}
				}
			}
		}

		return true;
	}

	private Class<?> getConstraintValidatorClass(
			TypeElement memberAnnotationTypeElement,
			Class<? extends Annotation> constraintAnnotationClass) {
		AnnotationMirror constraintAnnotation = AnnotationProcessingUtil
				.findAnnotationMirror(processingEnv,
						memberAnnotationTypeElement, constraintAnnotationClass);
		Class<?> constraintValidatorClass = null;

		if (constraintAnnotation != null) {
			String constraintValidatorClassName = AnnotationProcessingUtil
					.getAnnotationElementValue(processingEnv,
							constraintAnnotation, "value").getValue()
					.toString();

			try {
				constraintValidatorClass = Class
						.forName(constraintValidatorClassName);
			} catch (Exception ex) {
				processingEnv.getMessager().printMessage(
						Diagnostic.Kind.ERROR,
						"Cannot get class object for ConstraintValidator class: "
								+ constraintValidatorClassName);
			}
		}

		return constraintValidatorClass;
	}

	private <T extends ConstraintValidator> T getConstraintValidator(
			Class<? extends T> constraintValidatorClass,
			Map<Class<? extends T>, T> cacheMap) {
		if (constraintValidatorClass == null) {
			return null;
		}

		// annotationMirror is an constraint annotation
		T validator = cacheMap.get(constraintValidatorClass);

		if (validator == null) {
			try {
				validator = constraintValidatorClass.newInstance();
				cacheMap.put(constraintValidatorClass, validator);
			} catch (Exception ex) {
				processingEnv.getMessager().printMessage(
						Diagnostic.Kind.ERROR,
						constraintValidatorClass
								+ " needs an empty constructor!");
			}
		}

		return validator;
	}

	private boolean shouldSkip(TypeElement memberAnnotationTypeElement) {
		String qualifierPrefix = memberAnnotationTypeElement.getQualifiedName()
				.toString();

		// Don't process system annotations
		return qualifierPrefix.startsWith("java.")
				|| qualifierPrefix.startsWith("javax.");
	}
}
