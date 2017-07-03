package com.stmt.map;

import com.db.DatabaseType;
import com.field.FieldType;
import com.table.TableInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public class MappedDelete<T> extends BaseMappedStatement<T> {

	private MappedDelete(TableInfo<T> tableInfo, String statement, List<FieldType> argFieldTypeList) {
		super(tableInfo, statement, argFieldTypeList);
	}

	public static <T> MappedDelete<T> build(DatabaseType databaseType, TableInfo<T> tableInfo) {
		FieldType idField = tableInfo.getIdField();
		if (idField == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		List<FieldType> argFieldTypeList = new ArrayList<FieldType>();
		appendTableName(databaseType, sb, "DELETE FROM ", tableInfo.getTableName());
		appendWhereId(databaseType, idField, sb, argFieldTypeList);
		return new MappedDelete<T>(tableInfo, sb.toString(), argFieldTypeList);
	}
}
