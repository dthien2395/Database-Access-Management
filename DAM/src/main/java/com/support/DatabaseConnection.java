package com.support;

import com.field.SqlType;
import com.stmt.GenericRowMapper;

import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public interface DatabaseConnection {

    public final static Object MORE_THAN_ONE = new Object();

    public boolean isAutoCommitSupported() throws SQLException;

    public boolean getAutoCommit() throws SQLException;

    public void setAutoCommit(boolean autoCommit) throws SQLException;

    public Savepoint setSavePoint(String name) throws SQLException;

    public void commit(Savepoint savePoint) throws SQLException;

    public void rollback(Savepoint savePoint) throws SQLException;

    public CompiledStatement compileStatement(String statement) throws SQLException;

    public int insert(String statement, Object[] args, SqlType[] argSqlTypes) throws SQLException;

    public int insert(String statement, Object[] args, SqlType[] argSqlTypes, GeneratedKeyHolder keyHolder)
            throws SQLException;

    public int update(String statement, Object[] args, SqlType[] argSqlTypes) throws SQLException;

    public int delete(String statement, Object[] args, SqlType[] argSqlTypes) throws SQLException;

    public <T> Object queryForOne(String statement, Object[] args, SqlType[] argSqlTypes, GenericRowMapper<T> rowMapper)
            throws SQLException;

    public long queryForLong(String statement) throws SQLException;


    public void close() throws SQLException;
}
