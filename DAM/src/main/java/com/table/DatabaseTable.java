package com.table;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by dthien on 7/3/2017.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface DatabaseTable {

    String tableName() default "";
}