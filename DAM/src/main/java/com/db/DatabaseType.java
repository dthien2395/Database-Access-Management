package com.db;

import com.field.FieldConverter;
import com.field.FieldType;

import java.sql.Driver;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * Created by dthien on 7/3/2017.
 */
public interface DatabaseType {

    public void setDriver(Driver driver);

    public void loadDriver() throws ClassNotFoundException;

    public void appendColumnArg(StringBuilder sb, FieldType fieldType, List<String> additionalArgs,
                                List<String> statementsBefore, List<String> statementsAfter, List<String> queriesAfter);
    public String convertColumnName(String columnName);

    public void dropColumnArg(FieldType fieldType, List<String> statementsBefore, List<String> statementsAfter);

    public void appendEscapedEntityName(StringBuilder sb, String word);

    public void appendEscapedWord(StringBuilder sb, String word);

    public String generateIdSequenceName(String tableName, FieldType idFieldType);

    public String getCommentLinePrefix();

    public boolean isIdSequenceNeeded();

    public FieldConverter getFieldConverter(FieldType fieldType);

    public boolean isVarcharFieldWidthSupported();

    public boolean isLimitSqlSupported();

    public boolean isLimitAfterSelect();

    public void appendLimitValue(StringBuilder sb, int limit);

    public void appendSelectNextValFromSequence(StringBuilder sb, String sequenceName);

    public void appendCreateTableSuffix(StringBuilder sb);

    public boolean isCreateTableReturnsZero();

    public boolean isEntityNamesMustBeUpCase();

    public boolean isDatabaseUrlThisType(String url, String dbTypePart);

    public String getDatabaseName();

    public boolean isTruncateSupported();

    public boolean isCreateIfNotExistsSupported();

    public boolean isCreateIndexIfNotExistsSupported();

}
