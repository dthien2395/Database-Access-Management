package com.dao;

import com.db.DatabaseType;
import com.field.FieldType;
import com.misc.SqlExceptionUtil;
import com.stmt.PreparedStmt;
import com.stmt.SelectIterator;
import com.stmt.StatementBuilder;
import com.stmt.StatementExecutor;
import com.support.ConnectionSource;
import com.support.DatabaseConnection;
import com.table.DatabaseTableConfig;
import com.table.TableInfo;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public abstract class BaseDaoImpl<T, ID> implements Dao<T, ID> {

	private DatabaseType databaseType;
	private ConnectionSource connectionSource;

	private final Class<T> dataClass;
	private DatabaseTableConfig<T> tableConfig;
	private TableInfo<T> tableInfo;
	private StatementExecutor<T, ID> statementExecutor;

	protected BaseDaoImpl(Class<T> dataClass) {
		this(null, dataClass, null);
	}

	protected BaseDaoImpl(DatabaseType databaseType, Class<T> dataClass) {
		this(databaseType, dataClass, null);
	}


	protected BaseDaoImpl(DatabaseTableConfig<T> tableConfig) {
		this(null, tableConfig.getDataClass(), tableConfig);
	}


	protected BaseDaoImpl(DatabaseType databaseType, DatabaseTableConfig<T> tableConfig) {
		this(databaseType, tableConfig.getDataClass(), tableConfig);
	}

	private BaseDaoImpl(DatabaseType databaseType, Class<T> dataClass, DatabaseTableConfig<T> tableConfig) {
		this.databaseType = databaseType;
		this.dataClass = dataClass;
		this.tableConfig = tableConfig;
	}


	public void initialize() throws SQLException {
		if (databaseType == null) {
			throw new IllegalStateException("databaseType was never set on " + getClass().getSimpleName());
		}

		if (tableConfig == null) {
			tableConfig = DatabaseTableConfig.fromClass(databaseType, dataClass);
		}
		this.tableInfo = new TableInfo<T>(databaseType, tableConfig);
		this.statementExecutor = new StatementExecutor<T, ID>(databaseType, tableInfo);
	}

	public T queryForId(ID id) throws SQLException {
		DatabaseConnection connection = connectionSource.getReadOnlyConnection();
		try {
			return statementExecutor.queryForId(connection, id);
		} finally {
			connectionSource.releaseConnection(connection);
		}
	}

	public T queryForFirst(PreparedStmt<T> preparedStmt) throws SQLException {
		DatabaseConnection connection = connectionSource.getReadOnlyConnection();
		try {
			return statementExecutor.queryForFirst(connection, preparedStmt);
		} finally {
			connectionSource.releaseConnection(connection);
		}
	}

	public List<T> queryForAll() throws SQLException {
		return statementExecutor.queryForAll(connectionSource);
	}

	public StatementBuilder<T, ID> statementBuilder() {
		return new StatementBuilder<T, ID>(databaseType, tableInfo);
	}

	public StatementBuilder<T, ID> queryBuilder() {
		return statementBuilder();
	}

	public List<T> query(PreparedStmt<T> preparedStmt) throws SQLException {
		return statementExecutor.query(connectionSource, preparedStmt);
	}

	public RawResults queryForAllRaw(String queryString) throws SQLException {
		return statementExecutor.queryRaw(connectionSource, queryString);
	}

	public int create(T data) throws SQLException {
		if (data == null) {
			return 0;
		} else {
			DatabaseConnection connection = connectionSource.getReadWriteConnection();
			try {
				return statementExecutor.create(connection, data);
			} finally {
				connectionSource.releaseConnection(connection);
			}
		}
	}

	public int update(T data) throws SQLException {
		if (data == null) {
			return 0;
		} else {
			DatabaseConnection connection = connectionSource.getReadWriteConnection();
			try {
				return statementExecutor.update(connection, data);
			} finally {
				connectionSource.releaseConnection(connection);
			}
		}
	}

	public int updateId(T data, ID newId) throws SQLException {
		if (data == null) {
			return 0;
		} else {
			DatabaseConnection connection = connectionSource.getReadWriteConnection();
			try {
				return statementExecutor.updateId(connection, data, newId);
			} finally {
				connectionSource.releaseConnection(connection);
			}
		}
	}

	public int refresh(T data) throws SQLException {
		if (data == null) {
			return 0;
		} else {
			DatabaseConnection connection = connectionSource.getReadOnlyConnection();
			try {
				return statementExecutor.refresh(connection, data);
			} finally {
				connectionSource.releaseConnection(connection);
			}
		}
	}

	public int delete(T data) throws SQLException {
		if (data == null) {
			return 0;
		} else {
			DatabaseConnection connection = connectionSource.getReadWriteConnection();
			try {
				return statementExecutor.delete(connection, data);
			} finally {
				connectionSource.releaseConnection(connection);
			}
		}
	}
	public int delete(Collection<T> datas) throws SQLException {
		if (datas == null || datas.size() == 0) {
			return 0;
		} else {
			DatabaseConnection connection = connectionSource.getReadWriteConnection();
			try {
				return statementExecutor.deleteObjects(connection, datas);
			} finally {
				connectionSource.releaseConnection(connection);
			}
		}
	}

	public int deleteIds(Collection<ID> ids) throws SQLException {
		if (ids == null || ids.size() == 0) {
			return 0;
		} else {
			DatabaseConnection connection = connectionSource.getReadWriteConnection();
			try {
				return statementExecutor.deleteIds(connection, ids);
			} finally {
				connectionSource.releaseConnection(connection);
			}
		}
	}

	public SelectIterator<T, ID> iterator() {
		try {
			return statementExecutor.buildIterator(this, connectionSource);
		} catch (Exception e) {
			throw new IllegalStateException("Could not build iterator for " + dataClass, e);
		}
	}

	public SelectIterator<T, ID> iterator(PreparedStmt<T> preparedQuery) throws SQLException {
		try {
			return statementExecutor.buildIterator(this, connectionSource, preparedQuery);
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Could not build iterator for " + dataClass, e);
		}
	}

	public RawResults iteratorRaw(String query) throws SQLException {
		try {
			return statementExecutor.buildIterator(connectionSource, query);
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Could not build iterator for " + query, e);
		}
	}

	public String objectToString(T data) {
		return tableInfo.objectToString(data);
	}

	public boolean objectsEqual(T data1, T data2) throws SQLException {
		for (FieldType fieldType : tableInfo.getFieldTypes()) {
			Object fieldObj1 = fieldType.getFieldValue(data1);
			Object fieldObj2 = fieldType.getFieldValue(data2);
			if (fieldObj1 == null) {
				if (fieldObj2 != null) {
					return false;
				}
			} else if (fieldObj2 == null) {
				return false;
			} else if (!fieldObj1.equals(fieldObj2)) {
				return false;
			}
		}
		return true;
	}

	public Class<T> getDataClass() {
		return dataClass;
	}

	public DatabaseTableConfig<T> getTableConfig() {
		return tableConfig;
	}

	public void setDatabaseType(DatabaseType databaseType) {
		this.databaseType = databaseType;
	}

	public void setConnectionSource(ConnectionSource connectionSource) {
		this.connectionSource = connectionSource;
	}

	public void setTableConfig(DatabaseTableConfig<T> tableConfig) {
		this.tableConfig = tableConfig;
	}

	public static <T, ID> Dao<T, ID> createDao(DatabaseType databaseType, ConnectionSource connectionSource,
			Class<T> clazz) throws SQLException {
		BaseDaoImpl<T, ID> dao = new BaseDaoImpl<T, ID>(databaseType, clazz) {
		};
		dao.setConnectionSource(connectionSource);
		dao.initialize();
		return dao;
	}
}
