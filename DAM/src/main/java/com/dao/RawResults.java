package com.dao;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public interface RawResults extends CloseableIterable<String[]> {

	public int getNumberColumns();

	public String[] getColumnNames();
}
