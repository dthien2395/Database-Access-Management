package com.stmt.map;

import com.db.DatabaseType;
import com.field.FieldType;
import com.table.TableInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public class MappedUpdate<T> extends BaseMappedStatement<T> {

	private MappedUpdate(TableInfo<T> tableInfo, String statement, List<FieldType> argFieldTypeList) {
		super(tableInfo, statement, argFieldTypeList);
	}

	public static <T> MappedUpdate<T> build(DatabaseType databaseType, TableInfo<T> tableInfo) {
		FieldType idField = tableInfo.getIdField();
		if (idField == null) {
			return null;
		}
		if (tableInfo.getFieldTypes().length == 1) {
			// can't update because there is nothing to set
			return null;
		}
		StringBuilder sb = new StringBuilder();
		List<FieldType> argFieldTypeList = new ArrayList<FieldType>();
		appendTableName(databaseType, sb, "UPDATE ", tableInfo.getTableName());
		boolean first = true;
		for (FieldType fieldType : tableInfo.getFieldTypes()) {
			// we never update the idField
			if (fieldType == idField) {
				continue;
			}
			if (first) {
				sb.append("SET ");
				first = false;
			} else {
				sb.append(", ");
			}
			appendFieldColumnName(databaseType, sb, fieldType, argFieldTypeList);
			sb.append("= ?");
		}
		sb.append(' ');
		appendWhereId(databaseType, idField, sb, argFieldTypeList);
		return new MappedUpdate<T>(tableInfo, sb.toString(), argFieldTypeList);
	}
}
