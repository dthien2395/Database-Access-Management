package com.stmt.query;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public class OrderBy {

	private final String columnName;
	private final boolean ascending;

	public OrderBy(String columnName, boolean ascending) {
		this.columnName = columnName;
		this.ascending = ascending;
	}
	public String getColumnName() {
		return columnName;
	}

	public boolean isAscending() {
		return ascending;
	}
}
