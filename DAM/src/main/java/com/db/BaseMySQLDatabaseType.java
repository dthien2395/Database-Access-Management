package com.db;

import com.field.DataType;
import com.field.FieldType;

import java.util.List;

/**
 * Created by DangThiHien on 04/07/2017.
 */
public class BaseMySQLDatabaseType extends BaseDatabaseType  {
    @Override
    protected void configureGeneratedId(StringBuilder sb, FieldType fieldType, List<String> statementsBefore,
                                        List<String> additionalArgs, List<String> queriesAfter) {
        if (fieldType.getDataType() != DataType.INTEGER && fieldType.getDataType() != DataType.INTEGER_OBJ) {
            throw new IllegalArgumentException("Sqlite requires that auto-increment generated-id be integer types");
        }
        sb.append("PRIMARY KEY AUTO_INCREMENT ");
        // no additional call to configureId here
    }

    @Override
    public boolean isVarcharFieldWidthSupported() {
        return true;
    }

    @Override
    public boolean isCreateTableReturnsZero() {
        // 'CREATE TABLE' statements seem to return 1 for some reason
        return false;
    }

    private final static String DATABASE_URL_PORTION = "mysql";
    private final static String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
    private final static String DATABASE_NAME = "MySQL";

    /**
     * Default suffix to the CREATE TABLE statement. Change with the {@link #setCreateTableSuffix} method.
     */
    public final static String DEFAULT_CREATE_TABLE_SUFFIX = "ENGINE=InnoDB";

    private String createTableSuffix = DEFAULT_CREATE_TABLE_SUFFIX;

    @Override
    public boolean isDatabaseUrlThisType(String url, String dbTypePart) {
        return DATABASE_URL_PORTION.equals(dbTypePart);
    }

    @Override
    protected String getDriverClassName() {
        return DRIVER_CLASS_NAME;
    }

    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    public void setCreateTableSuffix(String createTableSuffix) {
        this.createTableSuffix = createTableSuffix;
    }

    @Override
    protected void appendDateType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        sb.append("DATETIME");
    }

    @Override
    protected void appendBooleanType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        sb.append("TINYINT(1)");
    }

    @Override
    protected void configureGeneratedId(String tableName, StringBuilder sb, FieldType fieldType,
                                        List<String> statementsBefore, List<String> statementsAfter, List<String> additionalArgs,
                                        List<String> queriesAfter) {
        sb.append("AUTO_INCREMENT ");
        configureId(sb, fieldType, statementsBefore, additionalArgs, queriesAfter);
    }

    @Override
    public void appendCreateTableSuffix(StringBuilder sb) {
        sb.append(createTableSuffix);
        sb.append(' ');
    }

    @Override
    public boolean isTruncateSupported() {
        return true;
    }

    @Override
    public boolean isCreateIfNotExistsSupported() {
        return true;
    }

    @Override
    public boolean isCreateIndexIfNotExistsSupported() {
        return false;
    }

    @Override
    public void appendEscapedWord(StringBuilder sb, String word) {
        sb.append('\'').append(word).append('\'');
    }

    @Override
    public void appendEscapedEntityName(StringBuilder sb, String word) {
        sb.append(word);
    }
}
