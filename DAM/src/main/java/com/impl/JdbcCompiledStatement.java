package com.impl;

import com.field.SqlType;
import com.support.CompiledStatement;
import com.support.DatabaseResults;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by DangThiHien on 05/07/2017.
 */
public class JdbcCompiledStatement implements CompiledStatement {

    private final PreparedStatement preparedStatement;
    private ResultSetMetaData metaData = null;

    public JdbcCompiledStatement(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    public int getColumnCount() throws SQLException {
        if (metaData == null) {
            metaData = preparedStatement.getMetaData();
        }
        return metaData.getColumnCount();
    }

    public String getColumnName(int column) throws SQLException {
        if (metaData == null) {
            metaData = preparedStatement.getMetaData();
        }
        return metaData.getColumnName(column);
    }

    public int executeUpdate() throws SQLException {
        return preparedStatement.executeUpdate();
    }

    public DatabaseResults executeQuery() throws SQLException {
        // this can be a UPDATE, DELETE, or ... just not a SELECT
        return new JdbcDatabaseResults(preparedStatement, preparedStatement.executeQuery());
    }

    public DatabaseResults getGeneratedKeys() throws SQLException {
        return new JdbcDatabaseResults(preparedStatement, preparedStatement.getGeneratedKeys());
    }

    public void close() throws SQLException {
        preparedStatement.close();
    }

    public void setNull(int parameterIndex, SqlType sqlType) throws SQLException {
        preparedStatement.setNull(parameterIndex, TypeValMapper.getTypeValForSqlType(sqlType));
    }

    public void setObject(int parameterIndex, Object obj, SqlType sqlType) throws SQLException {
        preparedStatement.setObject(parameterIndex, obj, TypeValMapper.getTypeValForSqlType(sqlType));
    }

    public void setMaxRows(int max) throws SQLException {
        preparedStatement.setMaxRows(max);
    }

    /**
     * Called by {@link JdbcDatabaseResults#next()} to get more results into the existing ResultSet.
     */
    boolean getMoreResults() throws SQLException {
        return preparedStatement.getMoreResults();
    }
}
