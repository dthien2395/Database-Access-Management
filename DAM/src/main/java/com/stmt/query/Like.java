package com.stmt.query;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public class Like extends BaseComparison {

	public Like(String columnName, boolean isNumber, Object value) {
		super(columnName, isNumber, value);
	}

	@Override
	public StringBuilder appendOperation(StringBuilder sb) {
		sb.append("LIKE ");
		return sb;
	}
}
