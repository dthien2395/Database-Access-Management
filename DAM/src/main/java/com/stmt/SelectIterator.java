package com.stmt;


import com.dao.BaseDaoImpl;
import com.dao.CloseableIterator;
import com.support.CompiledStatement;
import com.support.ConnectionSource;
import com.support.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public class SelectIterator<T, ID> implements CloseableIterator<T> {


	private final Class<T> dataClass;
	private BaseDaoImpl<T, ID> classDao;
	private final ConnectionSource connectionSource;
	private final DatabaseConnection connection;
	private final CompiledStatement stmt;
	private final ResultSet results;
	private final GenericRowMapper<T> rowMapper;
	private final String statement;
	private boolean closed = false;
	private T last = null;
	private int rowC = 0;


	public SelectIterator(Class<T> dataClass, BaseDaoImpl<T, ID> classDao, GenericRowMapper<T> rowMapper,
			ConnectionSource connectionSource, DatabaseConnection connection, CompiledStatement compiledStatement,
			String statement) throws SQLException {
		this.dataClass = dataClass;
		this.classDao = classDao;
		this.rowMapper = rowMapper;
		this.connectionSource = connectionSource;
		this.connection = connection;
		this.stmt = compiledStatement;
		this.results = stmt.executeQuery();
		this.statement = statement;
	}

	public boolean hasNextThrow() throws SQLException {
		if (closed) {
			return false;
		} else if (results.next()) {
			return true;
		} else {
			close();
			return false;
		}
	}

	public boolean hasNext() {
		try {
			return hasNextThrow();
		} catch (SQLException e) {
			last = null;
			try {
				close();
			} catch (SQLException e1) {
				// ignore it
			}
			// unfortunately, can't propagate back the SQLException
			throw new IllegalStateException("Errors getting more results of " + dataClass, e);
		}
	}

	public T nextThrow() throws SQLException {
		if (closed) {
			return null;
		}
		last = rowMapper.mapRow(results);
		rowC++;
		return last;
	}


	public T next() {
		try {
			return nextThrow();
		} catch (SQLException e) {
			last = null;
			try {
				close();
			} catch (SQLException e1) {
				// ignore it
			}
			// unfortunately, can't propagate back the SQLException
			throw new IllegalStateException("Errors getting more results of " + dataClass, e);
		}
	}

	public void removeThrow() throws SQLException {
		if (last == null) {
			throw new IllegalStateException("No last " + dataClass
					+ " object to remove. Must be called after a call to next.");
		}
		if (classDao == null) {
			// we may never be able to get here since it should only be null for queryForAll methods
			throw new IllegalStateException("Cannot remove " + dataClass + " object because classDao not initialized");
		}
		try {
			classDao.delete(last);
		} finally {
			// if we've try to delete it, clear the last marker
			last = null;
		}
	}

	public void remove() {
		try {
			removeThrow();
		} catch (SQLException e) {
			try {
				close();
			} catch (SQLException e1) {
				// ignore it
			}
			// unfortunately, can't propagate back the SQLException
			throw new IllegalStateException("Errors trying to delete " + dataClass + " object " + last, e);
		}
	}


	public void close() throws SQLException {
		if (!closed) {
			stmt.close();
			closed = true;
			last = null;
			connectionSource.releaseConnection(connection);
		}
	}
}
