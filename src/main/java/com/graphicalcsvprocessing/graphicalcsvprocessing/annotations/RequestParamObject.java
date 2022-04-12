package com.graphicalcsvprocessing.graphicalcsvprocessing.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * enables the use of custom object deserializing resolver
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParamObject {

    //which part of the request is the object
    String name() default "";

    boolean required() default true;
}
