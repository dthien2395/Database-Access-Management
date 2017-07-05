package com.dao;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public interface CloseableIterator<T> extends Iterator<T> {

	public void close() throws SQLException;
}
