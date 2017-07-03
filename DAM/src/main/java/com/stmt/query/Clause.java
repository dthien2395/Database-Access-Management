package com.stmt.query;


import com.db.DatabaseType;
import com.stmt.SelectArg;

import java.util.List;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public interface Clause {

	public StringBuilder appendSql(DatabaseType databaseType, StringBuilder sb, List<SelectArg> selectArgList);
}
