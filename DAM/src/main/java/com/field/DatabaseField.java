package com.field;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by dthien on 7/3/2017.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface DatabaseField {

    String columnName() default "";

    DataType dataType() default DataType.UNKNOWN;

    @Deprecated
    DataType jdbcType() default DataType.UNKNOWN;

    String defaultValue() default "";

    int width() default 0;

    boolean canBeNull() default true;

    boolean id() default false;

    boolean generatedId() default false;

    String generatedIdSequence() default "";

    boolean foreign() default false;

    boolean useGetSet() default false;

    String unknownEnumName() default "";

    boolean throwIfNull() default false;

    boolean persisted() default true;

    String format() default "";
}
