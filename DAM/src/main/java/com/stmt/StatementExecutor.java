package com.stmt;

import com.dao.BaseDaoImpl;
import com.dao.CloseableIterator;
import com.dao.RawResults;
import com.db.DatabaseType;
import com.field.FieldType;
import com.stmt.map.*;
import com.support.CompiledStatement;
import com.support.ConnectionSource;
import com.support.DatabaseConnection;
import com.table.TableInfo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public class StatementExecutor<T, ID> {


	private final DatabaseType databaseType;
	private final TableInfo<T> tableInfo;
	private final Class<T> dataClass;
	private final FieldType idField;
	private final MappedQueryForId<T, ID> mappedQueryForId;
	private final PreparedStmt<T> preparedQueryForAll;
	private final MappedCreate<T> mappedInsert;
	private final MappedUpdate<T> mappedUpdate;
	private final MappedUpdateId<T, ID> mappedUpdateId;
	private final MappedDelete<T> mappedDelete;
	private final MappedRefresh<T, ID> mappedRefresh;

	/**
	 * Provides statements for various SQL operations.
	 */
	public StatementExecutor(DatabaseType databaseType, TableInfo<T> tableInfo) throws SQLException {
		this.databaseType = databaseType;
		this.tableInfo = tableInfo;
		this.dataClass = tableInfo.getDataClass();
		this.idField = tableInfo.getIdField();
		this.mappedQueryForId = MappedQueryForId.build(databaseType, tableInfo);
		this.preparedQueryForAll = new StatementBuilder<T, ID>(databaseType, tableInfo).prepareStatement();
		this.mappedInsert = MappedCreate.build(databaseType, tableInfo);
		this.mappedUpdate = MappedUpdate.build(databaseType, tableInfo);
		this.mappedUpdateId = MappedUpdateId.build(databaseType, tableInfo);
		this.mappedDelete = MappedDelete.build(databaseType, tableInfo);
		this.mappedRefresh = MappedRefresh.build(databaseType, tableInfo);
	}

	/**
	 * Return the object associated with the id or null if none. This does a SQL
	 * <tt>select col1,col2,... from ... where ... = id</tt> type query.
	 */
	public T queryForId(DatabaseConnection databaseConnection, ID id) throws SQLException {
		if (mappedQueryForId == null) {
			throw new SQLException("Cannot query-for-id with " + dataClass + " because it doesn't have an id field");
		}
		return mappedQueryForId.execute(databaseConnection, id);
	}

	/**
	 * Return the first object that matches the {@link PreparedStmt} or null if none.
	 */
	public T queryForFirst(DatabaseConnection databaseConnection, PreparedStmt<T> preparedStmt) throws SQLException {
		CompiledStatement stmt = null;
		try {
			stmt = preparedStmt.compile(databaseConnection);
			ResultSet results = stmt.executeQuery();
			if (results.next()) {
				return preparedStmt.mapRow(results);
			} else {
				return null;
			}
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}


	public List<T> queryForAll(Connection connection) throws SQLException {
		return query(connection, preparedQueryForAll);
	}


	public List<T> query(Connection connectionSource, PreparedStmt<T> preparedStmt) throws SQLException {
		SelectIterator<T, ID> iterator = null;
		try {
			iterator = buildIterator(/* no dao specified because no removes */null, connectionSource, preparedStmt);
			List<T> results = new ArrayList<T>();
			while (iterator.hasNextThrow()) {
				results.add(iterator.nextThrow());
			}
			return results;
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
	}


	public RawResults queryRaw(ConnectionSource connectionSource, String query) throws SQLException {
		SelectIterator<String[], Void> iterator = null;
		try {
			DatabaseConnection connection = connectionSource.getReadOnlyConnection();
			CompiledStatement compiledStatement = connection.compileStatement(query);
			RawResultsList results = new RawResultsList(compiledStatement);
			// statement arg is null because we don't want it to double log below
			iterator =
					new SelectIterator<String[], Void>(String[].class, null, results, connectionSource, connection,
							compiledStatement, null);
			while (iterator.hasNextThrow()) {
				results.add(iterator.nextThrow());
			}
			return results;
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
	}
	/**
	 * Create and return a SelectIterator for the class using the default mapped query for all statement.
	 */
	public SelectIterator<T, ID> buildIterator(BaseDaoImpl<T, ID> classDao, ConnectionSource connectionSource)
			throws SQLException {
		return buildIterator(classDao, connectionSource, preparedQueryForAll);
	}

	/**
	 * Create and return an {@link SelectIterator} for the class using a prepared statement.
	 */
	public SelectIterator<T, ID> buildIterator(BaseDaoImpl<T, ID> classDao, Connection connection,
			PreparedStmt<T> preparedStmt) throws SQLException {
		return new SelectIterator<T, ID>(dataClass, classDao, preparedStmt, connection,
				preparedStmt.compile(connection), preparedStmt.getStatement());
	}

	/**
	 * Return a RawResults object associated with an internal iterator that matches the query argument.
	 */
	public RawResults buildIterator(ConnectionSource connectionSource, String query) throws SQLException {
		DatabaseConnection connection = connectionSource.getReadOnlyConnection();
		return new RawResultsIterator(query, connectionSource, connection, connection.compileStatement(query));
	}

	/**
	 * Create a new entry in the database from an object.
	 */
	public int create(DatabaseConnection databaseConnection, T data) throws SQLException {
		return mappedInsert.insert(databaseConnection, data);
	}

	/**
	 * Update an object in the database.
	 */
	public int update(DatabaseConnection databaseConnection, T data) throws SQLException {
		if (mappedUpdate == null) {
			throw new SQLException("Cannot update " + dataClass
					+ " because it doesn't have an id field defined or only has id field");
		} else {
			return mappedUpdate.update(databaseConnection, data);
		}
	}

	/**
	 * Update an object in the database to change its id to the newId parameter.
	 */
	public int updateId(DatabaseConnection databaseConnection, T data, ID newId) throws SQLException {
		if (mappedUpdateId == null) {
			throw new SQLException("Cannot update " + dataClass + " because it doesn't have an id field defined");
		} else {
			return mappedUpdateId.execute(databaseConnection, data, newId);
		}
	}

	/**
	 * Does a query for the object's Id and copies in each of the field values from the database to refresh the data
	 * parameter.
	 */
	public int refresh(DatabaseConnection databaseConnection, T data) throws SQLException {
		if (mappedQueryForId == null) {
			throw new SQLException("Cannot refresh " + dataClass + " because it doesn't have an id field defined");
		} else {
			T result = mappedRefresh.execute(databaseConnection, data);
			if (result == null) {
				return 0;
			} else {
				return 1;
			}
		}
	}

	/**
	 * Delete an object from the database.
	 */
	public int delete(DatabaseConnection databaseConnection, T data) throws SQLException {
		if (mappedDelete == null) {
			throw new SQLException("Cannot delete " + dataClass + " because it doesn't have an id field defined");
		} else {
			return mappedDelete.delete(databaseConnection, data);
		}
	}

	/**
	 * Delete a collection of objects from the database.
	 */
	public int deleteObjects(DatabaseConnection databaseConnection, Collection<T> datas) throws SQLException {
		if (idField == null) {
			throw new SQLException("Cannot delete " + dataClass + " because it doesn't have an id field defined");
		} else {
			// have to build this on the fly because the collection has variable number of args
			return MappedDeleteCollection.deleteObjects(databaseType, tableInfo, databaseConnection, datas);
		}
	}

	/**
	 * Delete a collection of objects from the database.
	 */
	public int deleteIds(DatabaseConnection databaseConnection, Collection<ID> ids) throws SQLException {
		if (idField == null) {
			throw new SQLException("Cannot delete " + dataClass + " because it doesn't have an id field defined");
		} else {
			// have to build this on the fly because the collection has variable number of args
			return MappedDeleteCollection.deleteIds(databaseType, tableInfo, databaseConnection, ids);
		}
	}

	/**
	 * Base class for raw results objects. It is also a row mapper to save on another object.
	 */
	private abstract static class BaseRawResults implements RawResults, GenericRowMapper<String[]> {

		protected final int columnN;
		protected final String[] columnNames;

		protected BaseRawResults(CompiledStatement compiledStmt) throws SQLException {
			this.columnN = compiledStmt.getColumnCount();
			this.columnNames = new String[this.columnN];
			for (int colC = 0; colC < this.columnN; colC++) {
				this.columnNames[colC] = compiledStmt.getColumnName(colC + 1);
			}
		}

		public int getNumberColumns() {
			return columnN;
		}

		public String[] getColumnNames() {
			return columnNames;
		}

		/**
		 * Row mapper which handles our String[] raw results.
		 */
		public String[] mapRow(ResultSet rs) throws SQLException {
			String[] result = new String[columnN];
			for (int colC = 0; colC < columnN; colC++) {
				result[colC] = rs.getString(colC + 1);
			}
			return result;
		}
	}

	/**
	 * Raw results from a list of results.
	 */
	private static class RawResultsList extends BaseRawResults {

		private final List<String[]> results = new ArrayList<String[]>();

		public RawResultsList(CompiledStatement preparedStmt) throws SQLException {
			super(preparedStmt);
		}

		void add(String[] result) throws SQLException {
			results.add(result);
		}

		int size() {
			return results.size();
		}

		public CloseableIterator<String[]> iterator() {
			return new RawResultsListIterator();
		}

		/**
		 * Internal iterator to work on our list.
		 */
		private class RawResultsListIterator implements CloseableIterator<String[]> {

			private int resultC = 0;

			public boolean hasNext() {
				return results.size() > resultC;
			}

			public String[] next() {
				return results.get(resultC++);
			}

			public void remove() {
				// noop
			}

			public void close() {
				// noop
			}
		}
	}

	/**
	 * Raw results from an iterator.
	 */
	private static class RawResultsIterator extends BaseRawResults {

		private final CompiledStatement statement;
		private final String query;
		private final ConnectionSource connectionSource;
		private final DatabaseConnection connection;

		public RawResultsIterator(String query, ConnectionSource connectionSource, DatabaseConnection connection,
				CompiledStatement statement) throws SQLException {
			super(statement);
			this.query = query;
			this.statement = statement;
			this.connectionSource = connectionSource;
			this.connection = connection;
		}

		public CloseableIterator<String[]> iterator() {
			try {
				// we do this so we can iterate through the results multiple times
				return new SelectIterator<String[], Void>(String[].class, null, this, connectionSource, connection,
						statement, query);
			} catch (SQLException e) {
				// we have to do this because iterator can't throw Exceptions
				throw new RuntimeException(e);
			}
		}
	}
}
