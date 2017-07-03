package com.db;

import com.field.FieldConverter;
import com.field.FieldType;
import com.field.SqlType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by DangThiHien on 04/07/2017.
 */
public abstract class BaseDatabaseType implements DatabaseType{

    protected static int DEFAULT_VARCHAR_WIDTH = 255;
    protected static int DEFAULT_DATE_STRING_WIDTH = 50;
    protected static String DEFAULT_SEQUENCE_SUFFIX = "_id_seq";

    protected final static FieldConverter booleanConverter = new BooleanNumberFieldConverter();

    protected abstract String getDriverClassName();

    public boolean isDatabaseUrlThisType(String url, String dbTypePart) {
        return false;
    }

    public void loadDriver() throws ClassNotFoundException {
        String className = getDriverClassName();
        if (className != null) {
            // this instantiates the driver class which wires in the JDBC glue
            Class.forName(className);
        }
    }

    public void appendColumnArg(StringBuilder sb, FieldType fieldType, List<String> additionalArgs,
                                List<String> statementsBefore, List<String> statementsAfter, List<String> queriesAfter) {
        appendEscapedEntityName(sb, fieldType.getDbColumnName());
        sb.append(' ');
        switch (fieldType.getDataType()) {

            case STRING :
                int fieldWidth = fieldType.getWidth();
                if (fieldWidth == 0) {
                    fieldWidth = getDefaultVarcharWidth();
                }
                appendStringType(sb, fieldWidth);
                break;

            case BOOLEAN :
            case BOOLEAN_OBJ :
                appendBooleanType(sb);
                break;

            case JAVA_DATE :
                fieldWidth = fieldType.getWidth();
                if (fieldWidth == 0) {
                    fieldWidth = DEFAULT_DATE_STRING_WIDTH;
                }
                appendDateType(sb, fieldWidth);
                break;

            case JAVA_DATE_LONG :
                appendDateLongType(sb);
                break;

            case JAVA_DATE_STRING :
                fieldWidth = fieldType.getWidth();
                if (fieldWidth == 0) {
                    fieldWidth = DEFAULT_DATE_STRING_WIDTH;
                }
                appendDateStringType(sb, fieldWidth);
                break;

            case BYTE :
            case BYTE_OBJ :
                appendByteType(sb);
                break;

            case SHORT :
            case SHORT_OBJ :
                appendShortType(sb);
                break;

            case INTEGER :
            case INTEGER_OBJ :
                appendIntegerType(sb);
                break;

            case LONG :
            case LONG_OBJ :
                appendLongType(sb);
                break;

            case FLOAT :
            case FLOAT_OBJ :
                appendFloatType(sb);
                break;

            case DOUBLE :
            case DOUBLE_OBJ :
                appendDoubleType(sb);
                break;

            case SERIALIZABLE :
                appendObjectType(sb);
                break;

            case ENUM_STRING :
                appendEnumStringType(sb, fieldType);
                break;

            case ENUM_INTEGER :
                appendEnumIntType(sb, fieldType);
                break;

            default :
                // shouldn't be able to get here unless we have a missing case
                throw new IllegalArgumentException("Unknown field type " + fieldType.getDataType());
        }
        sb.append(' ');
		/*
		 * NOTE: the configure id methods must be in this order since isGeneratedIdSequence is also isGeneratedId and
		 * isId. isGeneratedId is also isId.
		 */
        if (fieldType.isGeneratedIdSequence()) {
            configureGeneratedIdSequence(sb, fieldType, statementsBefore, additionalArgs, queriesAfter);
        } else if (fieldType.isGeneratedId()) {
            configureGeneratedId(sb, fieldType, statementsBefore, additionalArgs, queriesAfter);
        } else if (fieldType.isId()) {
            configureId(sb, fieldType, statementsBefore, additionalArgs, queriesAfter);
        }
        // if we have a generated-id then neither the not-null nor the default make sense and cause syntax errors
        if (!fieldType.isGeneratedId()) {
            Object defaultValue = fieldType.getDefaultValue();
            if (defaultValue != null) {
                sb.append("DEFAULT ");
                appendDefaultValue(sb, fieldType, defaultValue);
                sb.append(' ');
            }
            if (fieldType.isCanBeNull()) {
                appendCanBeNull(sb, fieldType);
            } else {
                sb.append("NOT NULL ");
            }
        }
    }

    public String convertColumnName(String columnName) {
        // default is a no-op
        return columnName;
    }

    protected void appendStringType(StringBuilder sb, int fieldWidth) {
        if (isVarcharFieldWidthSupported()) {
            sb.append("VARCHAR(").append(fieldWidth).append(")");
        } else {
            sb.append("VARCHAR");
        }
    }

    protected void appendDateType(StringBuilder sb, int fieldWidth) {
        sb.append("TIMESTAMP");
    }

    protected void appendDateType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        sb.append("TIMESTAMP");
    }

    protected void appendBooleanType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        sb.append("BOOLEAN");
    }

    protected void appendDateLongType(StringBuilder sb) {
        appendLongType(sb);
    }

    protected void appendDateStringType(StringBuilder sb, int fieldWidth) {
        appendStringType(sb, fieldWidth);
    }

    protected void appendBooleanType(StringBuilder sb) {
        sb.append("BOOLEAN");
    }

    protected void appendByteType(StringBuilder sb) {
        sb.append("TINYINT");
    }

    protected void appendShortType(StringBuilder sb) {
        sb.append("SMALLINT");
    }

    protected void appendIntegerType(StringBuilder sb) {
        sb.append("INTEGER");
    }

    protected void appendLongType(StringBuilder sb) {
        sb.append("BIGINT");
    }

    protected void appendFloatType(StringBuilder sb) {
        sb.append("FLOAT");
    }

    protected void appendDoubleType(StringBuilder sb) {
        sb.append("DOUBLE PRECISION");
    }

    protected void appendObjectType(StringBuilder sb) {
        sb.append("VARBINARY");
    }

    protected void appendEnumStringType(StringBuilder sb, FieldType fieldType) {
        // delegate to a string
        appendStringType(sb, DEFAULT_VARCHAR_WIDTH);
    }

    protected void appendEnumIntType(StringBuilder sb, FieldType fieldType) {
        // delegate to an integer
        appendIntegerType(sb);
    }

    protected void appendDefaultValue(StringBuilder sb, FieldType fieldType, Object defaultValue) {
        if (fieldType.isEscapeDefaultValue()) {
            appendEscapedWord(sb, defaultValue.toString());
        } else {
            sb.append(defaultValue);
        }
    }

    protected void configureGeneratedIdSequence(StringBuilder sb, FieldType fieldType, List<String> statementsBefore,
                                                List<String> additionalArgs, List<String> queriesAfter) {
        throw new IllegalStateException("GeneratedIdSequence is not supported by this database type for " + fieldType);
    }

    protected void configureGeneratedId(StringBuilder sb, FieldType fieldType, List<String> statementsBefore,
                                        List<String> additionalArgs, List<String> queriesAfter) {
        throw new IllegalStateException("GeneratedId is not supported by this database type for " + fieldType);
    }

    protected void configureGeneratedId(String tableName, StringBuilder sb, FieldType fieldType,
                                        List<String> statementsBefore, List<String> statementsAfter, List<String> additionalArgs,
                                        List<String> queriesAfter) {
        throw new IllegalStateException(
                "GeneratedId is not supported by database " + getDatabaseName() + " for field " + fieldType);
    }

    protected void configureId(StringBuilder sb, FieldType fieldType, List<String> statementsBefore,
                               List<String> additionalArgs, List<String> queriesAfter) {
        StringBuilder primaryKeySb = new StringBuilder();
        primaryKeySb.append("PRIMARY KEY (");
        appendEscapedEntityName(primaryKeySb, fieldType.getDbColumnName());
        primaryKeySb.append(") ");
        additionalArgs.add(primaryKeySb.toString());
    }

    public void dropColumnArg(FieldType fieldType, List<String> statementsBefore, List<String> statementsAfter) {
        // by default this is a noop
    }

    public void appendEscapedWord(StringBuilder sb, String word) {
        sb.append('\'').append(word).append('\'');
    }

    public void appendEscapedEntityName(StringBuilder sb, String word) {
        sb.append('`').append(word).append('`');
    }

    public String generateIdSequenceName(String tableName, FieldType idFieldType) {
        String name = tableName + DEFAULT_SEQUENCE_SUFFIX;
        if (isEntityNamesMustBeUpCase()) {
            return name.toUpperCase();
        } else {
            return name;
        }
    }

    public String getCommentLinePrefix() {
        return "-- ";
    }

    public FieldConverter getFieldConverter(FieldType fieldType) {
        // default is none
        return null;
    }

    public boolean isIdSequenceNeeded() {
        return false;
    }

    public boolean isVarcharFieldWidthSupported() {
        return true;
    }

    public boolean isLimitSqlSupported() {
        return true;
    }

    public boolean isLimitAfterSelect() {
        return false;
    }

    public void appendLimitValue(StringBuilder sb, int limit) {
        sb.append("LIMIT ").append(limit).append(' ');
    }

    protected int getDefaultVarcharWidth() {
        return DEFAULT_VARCHAR_WIDTH;
    }

    public void appendSelectNextValFromSequence(StringBuilder sb, String sequenceName) {
        // noop by default.
    }

    public void appendCreateTableSuffix(StringBuilder sb) {
        // noop by default.
    }

    public boolean isCreateTableReturnsZero() {
        return true;
    }

    public boolean isEntityNamesMustBeUpCase() {
        return false;
    }

    protected void appendCanBeNull(StringBuilder sb, FieldType fieldType) {
        // default is a noop
    }

    protected static class BooleanNumberFieldConverter implements FieldConverter {
        public SqlType getSqlType() {
            return SqlType.BOOLEAN;
        }
        public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
            boolean bool = (boolean) Boolean.parseBoolean(defaultStr);
            return (bool ? new Byte((byte) 1) : new Byte((byte) 0));
        }
        public Object javaToArg(FieldType fieldType, Object obj) throws SQLException {
            Boolean bool = (Boolean) obj;
            return (bool ? new Byte((byte) 1) : new Byte((byte) 0));
        }
        public Object resultToJava(FieldType fieldType, ResultSet results, int columnPos) throws SQLException {
            byte result = results.getByte(columnPos);
            return (result == 1 ? (Boolean) true : (Boolean) false);
        }
        public boolean isStreamType() {
            return false;
        }
    }

    public boolean isTruncateSupported() {
        return false;
    }

    public boolean isCreateIfNotExistsSupported() {
        return false;
    }

    public boolean isCreateIndexIfNotExistsSupported() {
        return isCreateIfNotExistsSupported();
    }

}
