package com.dao;


import com.stmt.PreparedStmt;
import com.stmt.StatementBuilder;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public interface Dao<T, ID> extends CloseableIterable<T> {


	public T queryForId(ID id) throws SQLException;


	public T queryForFirst(PreparedStmt<T> preparedQuery) throws SQLException;


	public List<T> queryForAll() throws SQLException;


	public RawResults queryForAllRaw(String query) throws SQLException;


	public StatementBuilder<T, ID> statementBuilder();

	public StatementBuilder<T, ID> queryBuilder();


	public List<T> query(PreparedStmt<T> preparedQuery) throws SQLException;


	public int create(T data) throws SQLException;


	public int update(T data) throws SQLException;


	public int updateId(T data, ID newId) throws SQLException;


	public int refresh(T data) throws SQLException;


	public int delete(T data) throws SQLException;


	public int delete(Collection<T> datas) throws SQLException;


	public int deleteIds(Collection<ID> ids) throws SQLException;


	public CloseableIterator<T> iterator();


	public CloseableIterator<T> iterator(PreparedStmt<T> preparedQuery) throws SQLException;


	public RawResults iteratorRaw(String query) throws SQLException;


	public String objectToString(T data);


	public boolean objectsEqual(T data1, T data2) throws SQLException;
}
