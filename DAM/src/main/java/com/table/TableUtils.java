package com.table;

import com.MyDatabaseConnection;
import com.db.DatabaseType;
import com.field.FieldType;
import com.misc.SqlExceptionUtil;
import com.support.CompiledStatement;
import com.support.ConnectionSource;
import com.support.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public class TableUtils {

	/**
	 * For static methods only.
	 */
	private TableUtils() {
	}

	public static <T> int createTable(DatabaseType databaseType, ConnectionSource connectionSource, Class<T> dataClass)
			throws SQLException {
		return createTable(databaseType, connectionSource, DatabaseTableConfig.fromClass(databaseType, dataClass));
	}


	public static <T> int createTable(DatabaseType databaseType, ConnectionSource connectionSource,
			DatabaseTableConfig<T> tableConfig) throws SQLException {
		TableInfo<T> tableInfo = new TableInfo<T>(databaseType, tableConfig);
		List<String> statements = new ArrayList<String>();
		List<String> queriesAfter = new ArrayList<String>();
		createTableStatements(databaseType, tableInfo, statements, queriesAfter);
		int stmtC = 0;
		DatabaseConnection connection = connectionSource.getReadWriteConnection();
		for (String statement : statements) {
			int rowC;
			CompiledStatement prepStmt = null;
			try {
				prepStmt = connection.compileStatement(statement);
				rowC = prepStmt.executeUpdate();
			} catch (SQLException e) {
				// we do this to make sure that the statement is in the exception
				throw SqlExceptionUtil.create("SQL statement failed: " + statement, e);
			} finally {
				if (prepStmt != null) {
					prepStmt.close();
				}
			}
			// sanity check
			if (rowC < 0) {
				throw new SQLException("SQL statement updated " + rowC + " rows, we were expecting >= 0: " + statement);
			} else if (rowC > 0 && databaseType.isCreateTableReturnsZero()) {
				throw new SQLException("SQL statement updated " + rowC + " rows, we were expecting == 0: " + statement);
			}

			stmtC++;
		}
		// now execute any test queries which test the newly created table
		for (String query : queriesAfter) {
			CompiledStatement prepStmt = null;
			ResultSet results = null;
			try {
				prepStmt = connection.compileStatement(query);
				results = prepStmt.executeQuery();
				int rowC = 0;
				// count the results
				while (results.next()) {
					rowC++;
				}
			} catch (SQLException e) {
				// we do this to make sure that the statement is in the exception
				throw SqlExceptionUtil.create("executing create table after-query failed: " + query, e);
			} finally {
				// result set is closed by the statement being closed
				if (prepStmt != null) {
					prepStmt.close();
				}
			}
			stmtC++;
		}
		return stmtC;
	}

	public static <T> int createTable2(DatabaseType databaseType, Class<T> tClass) throws SQLException {
		TableInfo<T> tableInfo = new TableInfo<T>(databaseType, DatabaseTableConfig.fromClass(databaseType, tClass));
		List<String> statements = new ArrayList<String>();
		List<String> queriesAfter = new ArrayList<String>();
		createTableStatements(databaseType, tableInfo, statements, queriesAfter);
		int stmtC = 0;
		Connection connection = MyDatabaseConnection.getConnection();
		for (String statement : statements) {
			int rowC;
			PreparedStatement prepStmt = null;
			try {
				prepStmt = connection.prepareStatement(statement);
				rowC = prepStmt.executeUpdate();
			} catch (SQLException e) {
				// we do this to make sure that the statement is in the exception
				throw SqlExceptionUtil.create("SQL statement failed: " + statement, e);
			} finally {
				if (prepStmt != null) {
					prepStmt.close();
				}
			}
			// sanity check
			if (rowC < 0) {
				throw new SQLException("SQL statement updated " + rowC + " rows, we were expecting >= 0: " + statement);
			} else if (rowC > 0 && databaseType.isCreateTableReturnsZero()) {
				throw new SQLException("SQL statement updated " + rowC + " rows, we were expecting == 0: " + statement);
			}

			stmtC++;
		}
		// now execute any test queries which test the newly created table
		for (String query : queriesAfter) {
			PreparedStatement prepStmt = null;
			ResultSet results = null;
			try {
				prepStmt = connection.prepareStatement(query);
				results = prepStmt.executeQuery();
				int rowC = 0;
				// count the results
				while (results.next()) {
					rowC++;
				}
			} catch (SQLException e) {
				// we do this to make sure that the statement is in the exception
				throw SqlExceptionUtil.create("executing create table after-query failed: " + query, e);
			} finally {
				// result set is closed by the statement being closed
				if (prepStmt != null) {
					prepStmt.close();
				}
			}
			stmtC++;
		}
		return stmtC;
	}

	public static <T> List<String> getCreateTableStatements(DatabaseType databaseType, Class<T> dataClass)
			throws SQLException {
		return getCreateTableStatements(databaseType, DatabaseTableConfig.fromClass(databaseType, dataClass));
	}


	public static <T> List<String> getCreateTableStatements(DatabaseType databaseType,
			DatabaseTableConfig<T> tableConfig) throws SQLException {
		TableInfo<T> tableInfo = new TableInfo<T>(databaseType, tableConfig);
		List<String> statements = new ArrayList<String>();
		List<String> queriesAfter = new ArrayList<String>();
		createTableStatements(databaseType, tableInfo, statements, queriesAfter);
		return statements;
	}

	public static <T> int dropTable(DatabaseType databaseType, ConnectionSource connectionSource, Class<T> dataClass,
			boolean ignoreErrors) throws SQLException {
		return dropTable(databaseType, connectionSource, DatabaseTableConfig.fromClass(databaseType, dataClass),
				ignoreErrors);
	}


	public static <T> int dropTable(DatabaseType databaseType, ConnectionSource connectionSource,
			DatabaseTableConfig<T> tableConfig, boolean ignoreErrors) throws SQLException {
		TableInfo<T> tableInfo = new TableInfo<T>(databaseType, tableConfig);
		Collection<String> statements = dropTableStatements(databaseType, tableInfo);
		int stmtC = 0;
		DatabaseConnection connection = connectionSource.getReadWriteConnection();
		for (String statement : statements) {
			int rowC = 0;
			CompiledStatement prepStmt = null;
			try {
				prepStmt = connection.compileStatement(statement);
				rowC = prepStmt.executeUpdate();
			} catch (SQLException e) {
				if (!ignoreErrors) {
					throw e;
				}
			} finally {
				if (prepStmt != null) {
					prepStmt.close();
				}
			}
			// sanity check
			if (rowC < 0) {
				throw new SQLException("SQL statement " + statement + " updated " + rowC
						+ " rows, we were expecting >= 0");
			}
			stmtC++;
		}
		return stmtC;
	}

	/**
	 * Generate and return the list of statements to create a database table and any associated features.
	 */
	private static <T> void createTableStatements(DatabaseType databaseType, TableInfo<T> tableInfo,
			List<String> statements, List<String> queriesAfter) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ");
		databaseType.appendEscapedEntityName(sb, tableInfo.getTableName());
		sb.append(" (");
		List<String> additionalArgs = new ArrayList<String>();
		List<String> statementsBefore = new ArrayList<String>();
		List<String> statementsAfter = new ArrayList<String>();
		// our statement will be set here later
		boolean first = true;
		for (FieldType fieldType : tableInfo.getFieldTypes()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			// we have to call back to the database type for the specific create syntax
			databaseType.appendColumnArg(sb, fieldType, additionalArgs, statementsBefore, statementsAfter, queriesAfter);
		}
		for (String arg : additionalArgs) {
			// we will have spat out one argument already so we don't have to do the first dance
			sb.append(", ").append(arg);
		}
		sb.append(") ");
		databaseType.appendCreateTableSuffix(sb);
		statements.addAll(statementsBefore);
		statements.add(sb.toString());
		statements.addAll(statementsAfter);
	}

	/**
	 * Generate and return the list of statements to drop a database table.
	 */
	private static <T> Collection<String> dropTableStatements(DatabaseType databaseType, TableInfo<T> tableInfo) {
		List<String> statementsBefore = new ArrayList<String>();
		List<String> statementsAfter = new ArrayList<String>();
		for (FieldType fieldType : tableInfo.getFieldTypes()) {
			databaseType.dropColumnArg(fieldType, statementsBefore, statementsAfter);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("DROP TABLE ");
		databaseType.appendEscapedEntityName(sb, tableInfo.getTableName());
		sb.append(' ');
		List<String> statements = new ArrayList<String>();
		statements.addAll(statementsBefore);
		statements.add(sb.toString());
		statements.addAll(statementsAfter);
		return statements;
	}
}
