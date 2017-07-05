package com.impl;

import com.db.BaseDatabaseType;
import com.db.DatabaseType;
import com.field.FieldType;

import java.util.List;

/**
 * Created by DangThiHien on 05/07/2017.
 */
public class MySqlDatabaseType extends BaseDatabaseType implements DatabaseType {

    private final static String DATABASE_URL_PORTION = "mysql";
    private final static String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

    /**
     * Default suffix to the CREATE TABLE statement. Change with the {@link #setCreateTableSuffix} method.
     */
    public final static String DEFAULT_CREATE_TABLE_SUFFIX = "ENGINE=InnoDB";

    private String createTableSuffix = DEFAULT_CREATE_TABLE_SUFFIX;

    public String getDriverUrlPart() {
        return DATABASE_URL_PORTION;
    }

    public String getDriverClassName() {
        return DRIVER_CLASS_NAME;
    }

    /**
     * Set the string that is appended to the end of a CREATE TABLE statement.
     */
    public void setCreateTableSuffix(String createTableSuffix) {
        this.createTableSuffix = createTableSuffix;
    }

    @Override
    protected void appendDateType(StringBuilder sb, int fieldWidth) {
        /**
         * TIMESTAMP in MySQL does some funky stuff with the last-modification time. Values are 'not null' by default
         * with an automatic default of CURRENT_TIMESTAMP. Strange design decision.
         */
        sb.append("DATETIME");
    }

    @Override
    protected void appendBooleanType(StringBuilder sb) {
        sb.append("TINYINT(1)");
    }

    @Override
    protected void appendObjectType(StringBuilder sb) {
        sb.append("BLOB");
    }

    @Override
    protected void configureGeneratedId(StringBuilder sb, FieldType fieldType, List<String> statementsBefore,
                                        List<String> additionalArgs, List<String> queriesAfter) {
        sb.append("AUTO_INCREMENT ");
        configureId(sb, fieldType, statementsBefore, additionalArgs, queriesAfter);
    }

    @Override
    public void appendCreateTableSuffix(StringBuilder sb) {
        sb.append(createTableSuffix);
        sb.append(' ');
    }

    @Override
    public void appendEscapedEntityName(StringBuilder sb, String word) {
        sb.append(word);
    }
}
