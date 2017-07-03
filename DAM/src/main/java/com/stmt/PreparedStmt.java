package com.stmt;

import com.support.CompiledStatement;
import com.support.DatabaseConnection;

import java.sql.SQLException;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public interface PreparedStmt<T> extends GenericRowMapper<T> {


	public CompiledStatement compile(DatabaseConnection databaseConnection) throws SQLException;

	public String getStatement() throws SQLException;
}
