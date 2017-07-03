package com.support;

import java.sql.SQLException;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public interface ConnectionSource {

    public DatabaseConnection getReadOnlyConnection() throws SQLException;

    public DatabaseConnection getReadWriteConnection() throws SQLException;

    public void releaseConnection(DatabaseConnection connection) throws SQLException;


    public void close() throws SQLException;
}
