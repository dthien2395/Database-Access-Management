package com.dao;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public interface RawResults extends CloseableIterable<String[]> {

	/**
	 * Return the number of columns in each result row.
	 */
	public int getNumberColumns();

	/**
	 * Return the array of column names for each result row.
	 */
	public String[] getColumnNames();
}
