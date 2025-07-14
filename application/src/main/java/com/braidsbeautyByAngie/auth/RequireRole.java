package com.braidsbeautyByAngie.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    String[] value() default {};
    boolean requireAll() default false; // true = requiere TODOS los roles, false = requiere AL MENOS UNO
}