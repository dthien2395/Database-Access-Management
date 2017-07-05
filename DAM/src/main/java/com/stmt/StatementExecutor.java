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
import com.support.DatabaseResults;
import com.table.TableInfo;

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

	public T queryForId(DatabaseConnection databaseConnection, ID id) throws SQLException {
		if (mappedQueryForId == null) {
			throw new SQLException("Cannot query-for-id with " + dataClass + " because it doesn't have an id field");
		}
		return mappedQueryForId.execute(databaseConnection, id);
	}

	public T queryForFirst(DatabaseConnection databaseConnection, PreparedStmt<T> preparedStmt) throws SQLException {
		CompiledStatement stmt = null;
		try {
			stmt = preparedStmt.compile(databaseConnection);
			DatabaseResults results = stmt.executeQuery();
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


	public List<T> queryForAll(ConnectionSource connectionSource) throws SQLException {
		return query(connectionSource, preparedQueryForAll);
	}


	public List<T> query(ConnectionSource connectionSource, PreparedStmt<T> preparedStmt) throws SQLException {
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

	public SelectIterator<T, ID> buildIterator(BaseDaoImpl<T, ID> classDao, ConnectionSource connectionSource)
			throws SQLException {
		return buildIterator(classDao, connectionSource, preparedQueryForAll);
	}

	public SelectIterator<T, ID> buildIterator(BaseDaoImpl<T, ID> classDao, ConnectionSource connectionSource,
			PreparedStmt<T> preparedStmt) throws SQLException {
		DatabaseConnection connection = connectionSource.getReadOnlyConnection();
		return new SelectIterator<T, ID>(dataClass, classDao, preparedStmt, connectionSource, connection,
				preparedStmt.compile(connection), preparedStmt.getStatement());
	}

	public RawResults buildIterator(ConnectionSource connectionSource, String query) throws SQLException {
		DatabaseConnection connection = connectionSource.getReadOnlyConnection();
		return new RawResultsIterator(query, connectionSource, connection, connection.compileStatement(query));
	}


	public int create(DatabaseConnection databaseConnection, T data) throws SQLException {
		return mappedInsert.insert(databaseConnection, data);
	}

	public int update(DatabaseConnection databaseConnection, T data) throws SQLException {
		if (mappedUpdate == null) {
			throw new SQLException("Cannot update " + dataClass
					+ " because it doesn't have an id field defined or only has id field");
		} else {
			return mappedUpdate.update(databaseConnection, data);
		}
	}

	public int updateId(DatabaseConnection databaseConnection, T data, ID newId) throws SQLException {
		if (mappedUpdateId == null) {
			throw new SQLException("Cannot update " + dataClass + " because it doesn't have an id field defined");
		} else {
			return mappedUpdateId.execute(databaseConnection, data, newId);
		}
	}

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

	public int delete(DatabaseConnection databaseConnection, T data) throws SQLException {
		if (mappedDelete == null) {
			throw new SQLException("Cannot delete " + dataClass + " because it doesn't have an id field defined");
		} else {
			return mappedDelete.delete(databaseConnection, data);
		}
	}

	public int deleteObjects(DatabaseConnection databaseConnection, Collection<T> datas) throws SQLException {
		if (idField == null) {
			throw new SQLException("Cannot delete " + dataClass + " because it doesn't have an id field defined");
		} else {
			// have to build this on the fly because the collection has variable number of args
			return MappedDeleteCollection.deleteObjects(databaseType, tableInfo, databaseConnection, datas);
		}
	}

	public int deleteIds(DatabaseConnection databaseConnection, Collection<ID> ids) throws SQLException {
		if (idField == null) {
			throw new SQLException("Cannot delete " + dataClass + " because it doesn't have an id field defined");
		} else {
			// have to build this on the fly because the collection has variable number of args
			return MappedDeleteCollection.deleteIds(databaseType, tableInfo, databaseConnection, ids);
		}
	}

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

		public String[] mapRow(DatabaseResults rs) throws SQLException {
			String[] result = new String[columnN];
			for (int colC = 0; colC < columnN; colC++) {
				result[colC] = rs.getString(colC + 1);
			}
			return result;
		}
	}

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
