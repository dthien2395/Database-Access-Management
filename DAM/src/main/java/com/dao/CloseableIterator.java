package com.dao;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public interface CloseableIterator<T> extends Iterator<T> {

	/**
	 * Close any underlying SQL statements.
	 */
	public void close() throws SQLException;
}
