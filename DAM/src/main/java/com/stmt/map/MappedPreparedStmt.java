package com.stmt.map;


import com.field.FieldType;
import com.stmt.PreparedStmt;
import com.stmt.SelectArg;
import com.support.CompiledStatement;
import com.support.DatabaseConnection;
import com.table.TableInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public class MappedPreparedStmt<T> extends BaseMappedQuery<T> implements PreparedStmt<T> {

	private final SelectArg[] selectArgs;
	private final Integer limit;

	public MappedPreparedStmt(TableInfo<T> tableInfo, String statement, List<FieldType> argFieldTypeList,
							  List<FieldType> resultFieldTypeList, List<SelectArg> selectArgList, Integer limit) {
		super(tableInfo, statement, argFieldTypeList, resultFieldTypeList);
		this.selectArgs = selectArgList.toArray(new SelectArg[selectArgList.size()]);
		// select args should match the field-type list
		if (argSqlTypes == null || selectArgs.length != argSqlTypes.length) {
			throw new IllegalArgumentException("Should be the same number of SelectArg and field-types in the arrays");
		}
		this.limit = limit;
	}

	public PreparedStatement compile(Connection connection) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(statement);
		if (limit != null) {
			stmt.setMaxRows(limit);
		}
		for (int i = 0; i < selectArgs.length; i++) {
			Object arg = selectArgs[i].getValue();
			// sql statement arguments start at 1
			Types
			if (arg == null) {
				stmt.setNull(i + 1, argSqlTypes[i]);
			} else {
				stmt.setObject(i + 1, arg, argSqlTypes[i]);
			}
		}
		return stmt;
	}

	public String getStatement() {
		return statement;
	}
}
