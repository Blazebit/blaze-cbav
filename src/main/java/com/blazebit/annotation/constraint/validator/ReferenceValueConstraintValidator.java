/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.annotation.constraint.validator;

import com.blazebit.annotation.apt.AnnotationProcessingUtil;
import com.blazebit.annotation.constraint.ConstraintScope;
import com.blazebit.annotation.constraint.NullClass;
import java.util.*;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class ReferenceValueConstraintValidator extends AbstractValueConstraintValidator {

    @Override
    public void validate(ProcessingEnvironment procEnv, RoundEnvironment roundEnv, TypeElement annotationType, AnnotationMirror annotation, ExecutableElement annotationMember, Element e, Object value) {
        AnnotationValue referencedAnnotationClassValue = AnnotationProcessingUtil.getAnnotationElementValue(procEnv, annotation, "referencedAnnotationClass");
        AnnotationValue referencedAnnotationMemberValue = AnnotationProcessingUtil.getAnnotationElementValue(procEnv, annotation, "referencedAnnotationMember");
        AnnotationValue nullableValue = AnnotationProcessingUtil.getAnnotationElementValue(procEnv, annotation, "nullable");
        AnnotationValue scopeValue = AnnotationProcessingUtil.getAnnotationElementValue(procEnv, annotation, "scope");

        DeclaredType referencedAnnotationClass = (DeclaredType) referencedAnnotationClassValue.getValue();
        TypeElement referencedAnnotationTypeElement = (TypeElement) referencedAnnotationClass.asElement();
        ConstraintScope scope = ConstraintScope.valueOf(scopeValue.getValue().toString());
        Boolean nullable = Boolean.valueOf(nullableValue.getValue().toString());
        Map<Element, Set<Element>> elements = new HashMap<Element, Set<Element>>();

        switch (scope) {
            case GLOBAL:
                elements.put(e, (Set<Element>) roundEnv.getElementsAnnotatedWith(referencedAnnotationTypeElement));
                break;
            case CLASS:
                switch (e.getKind()) {
                    case CONSTRUCTOR:
                    case FIELD:
                    case METHOD:
                        elements.put(e, new HashSet<Element>());
                        
                        for (Element classMemberElement : procEnv.getElementUtils().getAllMembers((TypeElement) e.getEnclosingElement())) {
                            if (AnnotationProcessingUtil.findAnnotationMirror(procEnv, classMemberElement, referencedAnnotationTypeElement) != null) {
                                elements.get(e).add(classMemberElement);
                            }
                        }

                        break;
                    case ENUM:
                    case INTERFACE:
                    case CLASS:
                        elements.put(e, new HashSet<Element>());
                        
                        for (Element classMemberElement : procEnv.getElementUtils().getAllMembers((TypeElement) e)) {
                            if (AnnotationProcessingUtil.findAnnotationMirror(procEnv, classMemberElement, referencedAnnotationTypeElement) != null) {
                                elements.get(e).add(classMemberElement);
                            }
                        }

                        break;
                    case ANNOTATION_TYPE:
                        AnnotationMirror stereotypeAnnotationMirror = AnnotationProcessingUtil.findAnnotationMirror(procEnv, e, "javax.enterprise.inject.Stereotype");
                        
                        if(stereotypeAnnotationMirror == null){
                            procEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Annotation targets other than stereotyped annotation, enum, interface, class, constructor, field and methods are not supported", e, annotation);
                            break;
                        }
                        
                        Set<Element> stereotypeAnnotatedElements = (Set<Element>) roundEnv.getElementsAnnotatedWith((TypeElement)e);
                        
                        for(Element stereotypeAnnotatedElement : stereotypeAnnotatedElements){
                            
                            // Always add the element to the map, otherwise it won't be validated
                            elements.put(stereotypeAnnotatedElement, new HashSet<Element>());
                            
                            switch (stereotypeAnnotatedElement.getKind()) {
                                case CONSTRUCTOR:
                                case FIELD:
                                case METHOD:
                                    for (Element classMemberElement : procEnv.getElementUtils().getAllMembers((TypeElement) stereotypeAnnotatedElement.getEnclosingElement())) {
                                        if (AnnotationProcessingUtil.findAnnotationMirror(procEnv, classMemberElement, referencedAnnotationTypeElement) != null) {
                                            elements.get(stereotypeAnnotatedElement).add(classMemberElement);
                                        }
                                    }

                                    break;
                                case INTERFACE:
                                case CLASS:
                                    // Maybe add here support for subtype checking, so that abstract classes or interfaces must not explicitly fullfill a
                                    // referencing constraint, but also a subtype can do
                                case ENUM:
                                    for (Element classMemberElement : procEnv.getElementUtils().getAllMembers((TypeElement) stereotypeAnnotatedElement)) {
                                        if (AnnotationProcessingUtil.findAnnotationMirror(procEnv, classMemberElement, referencedAnnotationTypeElement) != null) {
                                            elements.get(stereotypeAnnotatedElement).add(classMemberElement);
                                        }
                                    }

                                    break;
                                default:
                                    procEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Feature not yet implemented", stereotypeAnnotatedElement, annotation);
                                    break;
                            }
                        }
                        
                        break;
                    default:
                        procEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Annotation targets other than stereotyped annotation, enum, interface, class, constructor, field and methods are not supported", e, annotation);
                        break;
                }
                break;
            case ELEMENT:
                if (AnnotationProcessingUtil.findAnnotationMirror(procEnv, e, referencedAnnotationTypeElement) != null) {
                    elements.put(e, new HashSet<Element>(Arrays.asList(e)));
                }

                break;
        }

        OUTER: for (Map.Entry<Element, Set<Element>> elementEntry : elements.entrySet()) {
            for (Element lookupElement : elementEntry.getValue()) {
                AnnotationMirror referencedAnnotationMirror = AnnotationProcessingUtil.findAnnotationMirror(procEnv, lookupElement, referencedAnnotationTypeElement);
                Object memberName = null;

                // Should never be null here
                assert (referencedAnnotationMirror != null);

                AnnotationValue referencedAnnotationValue = AnnotationProcessingUtil.getAnnotationElementValue(procEnv, referencedAnnotationMirror, referencedAnnotationMemberValue.getValue().toString());

                if (referencedAnnotationValue != null) {
                    memberName = referencedAnnotationValue.getValue();
                }

                if (memberName == null) {
                    procEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Cannot find member name '" + referencedAnnotationMemberValue.getValue().toString() + "' in annotation class '" + referencedAnnotationTypeElement.getQualifiedName() + "'", annotationType, annotation, referencedAnnotationMemberValue);
                } else {
                    if (value.equals(memberName)) {
                        continue OUTER;
                    }
                }
            }

            if (nullable){
                if(annotationMember.getDefaultValue() == null && value.toString().equals(NullClass.class.getName())) {
                    // If no default value is given, but the constraint may be nullable we check for equallity of the value with NullClass
                    continue OUTER;
                } else if(annotationMember.getDefaultValue() != null && value.equals(annotationMember.getDefaultValue().getValue())) {
                    // constraint defines that it may be nullable and the default value matches the actual value, then no error message will be printed
                    continue OUTER;
                }
            }

            procEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, (String) AnnotationProcessingUtil.getAnnotationElementValue(procEnv, annotation, "errorMessage").getValue(), elementEntry.getKey(), annotation);
        }
    }
}
