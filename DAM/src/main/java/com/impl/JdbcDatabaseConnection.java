package com.impl;

import com.db.DatabaseType;
import com.field.SqlType;
import com.mysql.jdbc.*;
import com.stmt.GenericRowMapper;
import com.support.CompiledStatement;
import com.support.DatabaseConnection;
import com.support.DatabaseResults;
import com.support.GeneratedKeyHolder;

import java.sql.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Created by DangThiHien on 05/07/2017.
 */
public class JdbcDatabaseConnection implements DatabaseConnection{

    private static Object[] noArgs = new Object[0];
    private static SqlType[] noArgTypes = new SqlType[0];
    private static GenericRowMapper<Long> longWrapper = new OneLongWrapper();

    private final Connection connection;
    private Boolean supportsSavePoints = null;

    public JdbcDatabaseConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean isAutoCommitSupported() throws SQLException {
        return true;
    }

    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

    public Savepoint setSavePoint(String name) throws SQLException {
        if (supportsSavePoints == null) {
            DatabaseMetaData metaData = connection.getMetaData();
            supportsSavePoints = metaData.supportsSavepoints();
        }
        if (supportsSavePoints) {
            return connection.setSavepoint(name);
        } else {
            return null;
        }
    }

    public void commit(Savepoint savepoint) throws SQLException {
        if (savepoint == null) {
            connection.commit();
        } else {
            connection.releaseSavepoint(savepoint);
        }
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        if (savepoint == null) {
            connection.rollback();
        } else {
            connection.rollback(savepoint);
        }
    }

    public CompiledStatement compileStatement(String statement) throws SQLException {
        return  new JdbcCompiledStatement(connection.prepareStatement(statement, ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY));
    }

    public void close() throws SQLException {
        connection.close();
    }

    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }

    public int insert(String statement, Object[] args, SqlType[] argFieldTypes) throws SQLException {
        // it's a call to executeUpdate
        return update(statement, args, argFieldTypes);
    }

    public int insert(String statement, Object[] args, SqlType[] argFieldTypes, GeneratedKeyHolder keyHolder)
            throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
        statementSetArgs(stmt, args, argFieldTypes);
        int rowN = stmt.executeUpdate();
        JdbcDatabaseResults results = new JdbcDatabaseResults(stmt, stmt.getGeneratedKeys());
        if (results == null) {
            // may never happen but let's be careful
            throw new SQLException("No generated key results returned from update: " + statement);
        }
        int colN = results.getColumnCount();
        while (results.next()) {
            for (int colC = 1; colC <= colN; colC++) {
                // get the id column data so we can pass it back to the caller thru the keyHolder
                Number id = results.getIdColumnData(colC);
                keyHolder.addKey(id);
            }
        }
        return rowN;
    }

    public int update(String statement, Object[] args, SqlType[] argFieldTypes) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(statement);
        statementSetArgs(stmt, args, argFieldTypes);
        return stmt.executeUpdate();
    }

    public int delete(String statement, Object[] args, SqlType[] argFieldTypes) throws SQLException {
        // it's a call to executeUpdate
        return update(statement, args, argFieldTypes);
    }

    public <T> Object queryForOne(String statement, Object[] args, SqlType[] argFieldTypes,
                                  GenericRowMapper<T> rowMapper) throws SQLException {
        PreparedStatement stmt =
                connection.prepareStatement(statement, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statementSetArgs(stmt, args, argFieldTypes);
        DatabaseResults results = new JdbcDatabaseResults(stmt, stmt.executeQuery());
        if (!results.next()) {
            // no results at all
            return null;
        }
        T first = rowMapper.mapRow(results);
        if (results.next()) {
            return MORE_THAN_ONE;
        } else {
            return first;
        }
    }

    public long queryForLong(String statement) throws SQLException {
        Object result = queryForOne(statement, noArgs, noArgTypes, longWrapper);
        if (result == null) {
            throw new SQLException("No results returned in query-for-long: " + statement);
        } else if (result == MORE_THAN_ONE) {
            throw new SQLException("More than 1 result returned in query-for-long: " + statement);
        } else {
            return (Long) result;
        }
    }

    private void statementSetArgs(PreparedStatement stmt, Object[] args, SqlType[] argFieldTypes) throws SQLException {
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            int typeVal = TypeValMapper.getTypeValForSqlType(argFieldTypes[i]);
            if (arg == null) {
                stmt.setNull(i + 1, typeVal);
            } else {
                stmt.setObject(i + 1, arg, typeVal);
            }
        }
    }

    /**
     * Row mapper that handles a single long result.
     */
    private static class OneLongWrapper implements GenericRowMapper<Long> {
        public Long mapRow(DatabaseResults rs) throws SQLException {
            // maps the first column (sql #1)
            return rs.getLong(1);
        }
    }
}
