package com.stmt.query;

import com.db.DatabaseType;
import com.stmt.SelectArg;

import java.util.List;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public class IsNull extends BaseComparison {

	public IsNull(String columnName, boolean isNumber) {
		super(columnName, isNumber, null);
	}

	@Override
	public StringBuilder appendOperation(StringBuilder sb) {
		sb.append("IS NULL ");
		return sb;
	}

	@Override
	public StringBuilder appendValue(DatabaseType databaseType, StringBuilder sb, List<SelectArg> selectArgList) {
		// there is no value
		return sb;
	}
}
