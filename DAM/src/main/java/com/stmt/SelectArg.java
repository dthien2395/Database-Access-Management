package com.stmt;

import java.sql.SQLException;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public class SelectArg {

	private boolean hasBeenSet = false;
	private String columnName = null;
	private Object value = null;


	public String getColumnName() {
		if (columnName == null) {
			throw new IllegalArgumentException("Column name has not been set");
		} else {
			return columnName;
		}
	}


	public void setColumnName(String columnName) {
		if (this.columnName == null) {
			// not set yet
		} else if (this.columnName.equals(columnName)) {
			// set to the same value as before
		} else {
			throw new IllegalArgumentException("Column name cannot be set twice from " + this.columnName + " to "
					+ columnName);
		}
		this.columnName = columnName;
	}

	public Object getValue() throws SQLException {
		if (hasBeenSet) {
			return value;
		} else {
			throw new SQLException("Column value has not been set for " + columnName);
		}
	}

	public void setValue(Object value) {
		this.hasBeenSet = true;
		this.value = value;
	}

	@Override
	public String toString() {
		if (hasBeenSet) {
			return "set arg(" + value + ")";
		} else {
			return "unset arg()";
		}
	}
}
