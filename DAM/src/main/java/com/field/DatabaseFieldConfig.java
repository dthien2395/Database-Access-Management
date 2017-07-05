package com.field;

import com.db.DatabaseType;
import com.misc.JavaxPersistence;
import com.table.DatabaseTableConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * Created by dthien on 7/3/2017.
 */
public class DatabaseFieldConfig {
    private String fieldName;
    private String columnName;
    private DataType dataType = DataType.UNKNOWN;
    private String defaultValue;
    private int width;
    private boolean canBeNull;
    private boolean id;
    private boolean generatedId;
    private String generatedIdSequence;
    private boolean foreign;
    private DatabaseTableConfig<?> foreignTableConfig;
    private boolean useGetSet;
    private Enum<?> unknownEnumvalue;
    private boolean throwIfNull;
    private String format;

    public DatabaseFieldConfig() {
        // for spring
    }

    public DatabaseFieldConfig(String fieldName, String columnName, DataType dataType, String defaultValue, int width,
                               boolean canBeNull, boolean id, boolean generatedId, String generatedIdSequence, boolean foreign,
                               DatabaseTableConfig<?> foreignTableConfig, boolean useGetSet, Enum<?> unknownEnumValue,
                               boolean throwIfNull, String format) {
        this.fieldName = fieldName;
        this.columnName = columnName;
        this.dataType = dataType;
        this.defaultValue = defaultValue;
        this.width = width;
        this.canBeNull = canBeNull;
        this.id = id;
        this.generatedId = generatedId;
        this.generatedIdSequence = generatedIdSequence;
        this.foreign = foreign;
        this.foreignTableConfig = foreignTableConfig;
        this.useGetSet = useGetSet;
        this.unknownEnumvalue = unknownEnumValue;
        this.throwIfNull = throwIfNull;
        this.format = format;
    }

    public DatabaseFieldConfig(String fieldName, String columnName, DataType dataType, String defaultValue, int width,
                               boolean canBeNull, boolean id, boolean generatedId, String generatedIdSequence, boolean foreign,
                               DatabaseTableConfig<?> foreignTableConfig, boolean useGetSet, Enum<?> unknownEnumValue, boolean throwIfNull) {
        this(fieldName, columnName, dataType, defaultValue, width, canBeNull, id, generatedId, generatedIdSequence,
                foreign, foreignTableConfig, useGetSet, unknownEnumValue, throwIfNull, null);
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isCanBeNull() {
        return canBeNull;
    }

    public void setCanBeNull(boolean canBeNull) {
        this.canBeNull = canBeNull;
    }

    public boolean isId() {
        return id;
    }

    public void setId(boolean id) {
        this.id = id;
    }

    public boolean isGeneratedId() {
        return generatedId;
    }

    public void setGeneratedId(boolean generatedId) {
        this.generatedId = generatedId;
    }

    public String getGeneratedIdSequence() {
        return generatedIdSequence;
    }

    public void setGeneratedIdSequence(String generatedIdSequence) {
        this.generatedIdSequence = generatedIdSequence;
    }

    public boolean isForeign() {
        return foreign;
    }

    public void setForeign(boolean foreign) {
        this.foreign = foreign;
    }

    public DatabaseTableConfig<?> getForeignTableConfig() {
        return foreignTableConfig;
    }

    public void setForeignTableConfig(DatabaseTableConfig<?> foreignTableConfig) {
        this.foreignTableConfig = foreignTableConfig;
    }

    public boolean isUseGetSet() {
        return useGetSet;
    }

    public void setUseGetSet(boolean useGetSet) {
        this.useGetSet = useGetSet;
    }

    public Enum<?> getUnknownEnumvalue() {
        return unknownEnumvalue;
    }

    public void setUnknownEnumvalue(Enum<?> unknownEnumvalue) {
        this.unknownEnumvalue = unknownEnumvalue;
    }

    public boolean isThrowIfNull() {
        return throwIfNull;
    }

    public void setThrowIfNull(boolean throwIfNull) {
        this.throwIfNull = throwIfNull;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public static DatabaseFieldConfig fromField(DatabaseType databaseType, Field field) throws SQLException {
        // first we lookup the DatabaseField annotation
        DatabaseField databaseField = field.getAnnotation(DatabaseField.class);
        if (databaseField != null) {
            if (databaseField.persisted()) {
                return fromDatabaseField(databaseType, field, databaseField);
            } else {
                return null;
            }
        }

		/*
		 * NOTE: to remove javax.persistence usage, comment the following lines out
		 */
        DatabaseFieldConfig config = JavaxPersistence.createFieldConfig(databaseType, field);
        if (config != null) {
            return config;
        }

        return null;
    }

    public static Method findGetMethod(Field field, boolean throwExceptions) {
        String methodName = methodFromField(field, "get");
        Method fieldGetMethod;
        try {
            fieldGetMethod = field.getDeclaringClass().getMethod(methodName);
        } catch (Exception e) {
            if (throwExceptions) {
                throw new IllegalArgumentException("Could not find appropriate get method for " + field);
            } else {
                return null;
            }
        }
        if (fieldGetMethod.getReturnType() != field.getType()) {
            if (throwExceptions) {
                throw new IllegalArgumentException("Return type of get method " + methodName + " does not return "
                        + field.getType());
            } else {
                return null;
            }
        }
        return fieldGetMethod;
    }

    public static Method findSetMethod(Field field, boolean throwExceptions) {
        String methodName = methodFromField(field, "set");
        Method fieldSetMethod;
        try {
            fieldSetMethod = field.getDeclaringClass().getMethod(methodName, field.getType());
        } catch (Exception e) {
            if (throwExceptions) {
                throw new IllegalArgumentException("Could not find appropriate set method for " + field);
            } else {
                return null;
            }
        }
        if (fieldSetMethod.getReturnType() != void.class) {
            if (throwExceptions) {
                throw new IllegalArgumentException("Return type of set method " + methodName + " returns "
                        + fieldSetMethod.getReturnType() + " instead of void");
            } else {
                return null;
            }
        }
        return fieldSetMethod;
    }

    private static DatabaseFieldConfig fromDatabaseField(DatabaseType databaseType, Field field,
                                                         DatabaseField databaseField) {
        DatabaseFieldConfig config = new DatabaseFieldConfig();
        config.fieldName = field.getName();
        if (databaseType.isEntityNamesMustBeUpCase()) {
            config.fieldName = config.fieldName.toUpperCase();
        }
        if (databaseField.columnName().length() > 0) {
            config.columnName = databaseField.columnName();
        } else {
            config.columnName = null;
        }
        config.dataType = databaseField.dataType();
        if (databaseField.defaultValue().length() > 0) {
            config.defaultValue = databaseField.defaultValue();
        } else {
            config.defaultValue = null;
        }
        config.width = databaseField.width();
        config.canBeNull = databaseField.canBeNull();
        config.id = databaseField.id();
        config.generatedId = databaseField.generatedId();
        if (databaseField.generatedIdSequence().length() > 0) {
            config.generatedIdSequence = databaseField.generatedIdSequence();
        } else {
            config.generatedIdSequence = null;
        }
        config.foreign = databaseField.foreign();
        config.useGetSet = databaseField.useGetSet();
        if (databaseField.unknownEnumName().length() > 0) {
            config.unknownEnumvalue = findMatchingEnumVal(field, databaseField.unknownEnumName());
        } else {
            config.unknownEnumvalue = null;
        }
        config.throwIfNull = databaseField.throwIfNull();
        if (databaseField.format().length() > 0) {
            config.format = databaseField.format();
        } else {
            config.format = null;
        }
        return config;
    }

    private static String methodFromField(Field field, String prefix) {
        return prefix + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
    }

    private static Enum<?> findMatchingEnumVal(Field field, String unknownEnumName) {
        for (Enum<?> enumVal : (Enum<?>[]) field.getType().getEnumConstants()) {
            if (enumVal.name().equals(unknownEnumName)) {
                return enumVal;
            }
        }
        throw new IllegalArgumentException("Unknwown enum unknown name " + unknownEnumName + " for field " + field);
    }
}
