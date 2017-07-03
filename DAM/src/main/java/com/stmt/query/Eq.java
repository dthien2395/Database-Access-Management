package com.stmt.query;
public class Eq extends BaseComparison {

	public Eq(String columnName, boolean isNumber, Object value) {
		super(columnName, isNumber, value);
	}

	@Override
	public StringBuilder appendOperation(StringBuilder sb) {
		sb.append("= ");
		return sb;
	}
}
