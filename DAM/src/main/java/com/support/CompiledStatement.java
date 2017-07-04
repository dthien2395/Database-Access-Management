package com.support;

import com.field.SqlType;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public interface CompiledStatement {

    public int getColumnCount() throws SQLException;

    public String getColumnName(int column) throws SQLException;

    public int executeUpdate() throws SQLException;

    public ResultSet executeQuery() throws SQLException;

    public ResultSet getGeneratedKeys() throws SQLException;

    public void close() throws SQLException;

    public void setNull(int parameterIndex, SqlType sqlType) throws SQLException;


    public void setObject(int parameterIndex, Object obj, SqlType sqlType) throws SQLException;


    public void setMaxRows(int max) throws SQLException;
}
