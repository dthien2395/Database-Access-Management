package com.stmt.query;


import com.db.DatabaseType;
import com.stmt.SelectArg;

import java.util.List;

/**
 * Created by HienNguyen on 7/3/2017.
 */
interface Comparison extends Clause {

	public String getColumnName();

	public StringBuilder appendOperation(StringBuilder sb);

	public StringBuilder appendValue(DatabaseType databaseType, StringBuilder sb, List<SelectArg> selectArgList);
}
