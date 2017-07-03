package com.stmt.query;


import com.db.DatabaseType;
import com.stmt.SelectArg;

import java.util.List;

/**
 * Created by HienNguyen on 7/3/2017.
 */
abstract class BaseComparison implements Comparison {

	protected final String columnName;
	protected final boolean isNumber;
	private final Object value;

	protected BaseComparison(String columnName, boolean isNumber, Object value) {
		this.columnName = columnName;
		this.isNumber = isNumber;
		this.value = value;
	}

	public abstract StringBuilder appendOperation(StringBuilder sb);

	public StringBuilder appendSql(DatabaseType databaseType, StringBuilder sb, List<SelectArg> selectArgList) {
		databaseType.appendEscapedEntityName(sb, columnName);
		sb.append(' ');
		appendOperation(sb);
		// this needs to call appendValue (not appendArgOrValue) because it may be overridden
		appendValue(databaseType, sb, selectArgList);
		return sb;
	}

	public String getColumnName() {
		return columnName;
	}

	public StringBuilder appendValue(DatabaseType databaseType, StringBuilder sb, List<SelectArg> selectArgList) {
		appendArgOrValue(databaseType, sb, selectArgList, value);
		return sb;
	}

	protected void appendArgOrValue(DatabaseType databaseType, StringBuilder sb, List<SelectArg> selectArgList,
			Object argOrValue) {
		if (argOrValue == null) {
			throw new IllegalArgumentException("argument to comparison of '" + columnName + "' is null");
		} else if (argOrValue instanceof SelectArg) {
			sb.append('?');
			SelectArg selectArg = (SelectArg) argOrValue;
			selectArg.setColumnName(columnName);
			selectArgList.add(selectArg);
		} else if (isNumber) {
			// numbers can't have quotes around them in derby
			sb.append(argOrValue.toString());
		} else {
			databaseType.appendEscapedWord(sb, argOrValue.toString());
		}
		sb.append(' ');
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(columnName).append(' ');
		appendOperation(sb).append(' ');
		sb.append(value);
		return sb.toString();
	}
}
