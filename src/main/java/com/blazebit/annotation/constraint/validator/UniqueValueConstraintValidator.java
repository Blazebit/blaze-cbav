/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.annotation.constraint.validator;

import com.blazebit.annotation.apt.AnnotationProcessingUtil;
import com.blazebit.annotation.constraint.ConstraintScope;
import java.util.*;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class UniqueValueConstraintValidator extends AbstractValueConstraintValidator {

    @Override
    public void validate(ProcessingEnvironment procEnv, RoundEnvironment roundEnv, TypeElement annotationType, AnnotationMirror annotation, ExecutableElement annotationMember, Element e, Object value) {
        AnnotationValue scopeValue = AnnotationProcessingUtil.getAnnotationElementValue(procEnv, annotation, "scope");

        ConstraintScope scope = ConstraintScope.valueOf(scopeValue.getValue().toString());
        Map<Element, Set<Element>> elements = new HashMap<Element, Set<Element>>();

        switch (scope) {
            case GLOBAL:
                elements.put(e, (Set<Element>) roundEnv.getElementsAnnotatedWith(annotationType));
                break;
            case CLASS:
                switch (e.getKind()) {
                    case CONSTRUCTOR:
                    case FIELD:
                    case METHOD:
                        elements.put(e, new HashSet<Element>());
                        
                        for (Element classMemberElement : procEnv.getElementUtils().getAllMembers((TypeElement) e.getEnclosingElement())) {
                            if (AnnotationProcessingUtil.findAnnotationMirror(classMemberElement, annotationType) != null) {
                                elements.get(e).add(classMemberElement);
                            }
                        }

                        break;
                    case ENUM:
                    case INTERFACE:
                    case CLASS:
                        elements.put(e, new HashSet<Element>());
                
                        for (Element classMemberElement : procEnv.getElementUtils().getAllMembers((TypeElement) e)) {
                            if (AnnotationProcessingUtil.findAnnotationMirror(classMemberElement, annotationType) != null) {
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
                                        if (AnnotationProcessingUtil.findAnnotationMirror(procEnv, classMemberElement, annotationType) != null) {
                                            elements.get(stereotypeAnnotatedElement).add(classMemberElement);
                                        }
                                    }

                                    break;
                                case INTERFACE:
                                case CLASS:
                                case ENUM:
                                    for (Element classMemberElement : procEnv.getElementUtils().getAllMembers((TypeElement) stereotypeAnnotatedElement)) {
                                        if (AnnotationProcessingUtil.findAnnotationMirror(procEnv, classMemberElement, annotationType) != null) {
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
                elements.put(e, new HashSet<Element>(Arrays.asList(e)));
                break;
        }

        boolean unique;

        OUTER: for (Map.Entry<Element, Set<Element>> elementEntry : elements.entrySet()) {
            unique = true;
            
            for (Element lookupElement : elementEntry.getValue()) {
                AnnotationMirror referencedAnnotationMirror = AnnotationProcessingUtil.findAnnotationMirror(lookupElement, annotationType);

                if (!e.equals(lookupElement)) {
                    if (value.equals(AnnotationProcessingUtil.getAnnotationElementValue(procEnv, referencedAnnotationMirror, annotationMember.getSimpleName().toString()).getValue())) {
                        unique = false;
                        break;
                    }
                }
            }

            if (!unique) {
                procEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, (String) AnnotationProcessingUtil.getAnnotationElementValue(procEnv, annotation, "errorMessage").getValue(), e, annotation);
            }
        }
    }
}
