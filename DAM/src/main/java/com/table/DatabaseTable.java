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

    /**
     * The name of the column in the database. If not set then the name is taken from the class name lowercased.
     */
    String tableName() default "";
}