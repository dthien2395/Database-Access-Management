package com.stmt.map;

import com.db.DatabaseType;
import com.field.FieldType;
import com.field.SqlType;
import com.misc.SqlExceptionUtil;
import com.support.DatabaseConnection;
import com.table.TableInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public abstract class BaseMappedStatement<T> {

	protected final TableInfo<T> tableInfo;
	protected final FieldType idField;
	protected final String statement;
	protected final FieldType[] argFieldTypes;
	protected final SqlType[] argSqlTypes;

	protected BaseMappedStatement(TableInfo<T> tableInfo, String statement, List<FieldType> argFieldTypeList) {
		this.tableInfo = tableInfo;
		this.idField = tableInfo.getIdField();
		this.statement = statement;
		this.argFieldTypes = argFieldTypeList.toArray(new FieldType[argFieldTypeList.size()]);
		this.argSqlTypes = getSqlTypes(argFieldTypes);
	}

	protected int insert(DatabaseConnection databaseConnection, T data) throws SQLException {
		try {
			Object[] args = getFieldObjects(argFieldTypes, data);
			int rowC = databaseConnection.insert(statement, args, argSqlTypes);
			return rowC;
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Unable to run insert stmt on object " + data + ": " + statement, e);
		}
	}

	public int update(DatabaseConnection databaseConnection, T data) throws SQLException {
		try {
			Object[] args = getFieldObjects(argFieldTypes, data);
			int rowC = databaseConnection.update(statement, args, argSqlTypes);
			return rowC;
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Unable to run update stmt on object " + data + ": " + statement, e);
		}
	}

	public int delete(DatabaseConnection databaseConnection, T data) throws SQLException {
		try {
			Object[] args = getFieldObjects(argFieldTypes, data);
			int rowC = databaseConnection.delete(statement, args, argSqlTypes);
			return rowC;
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Unable to run delete stmt on object " + data + ": " + statement, e);
		}
	}

	protected Object[] getFieldObjects(FieldType[] fieldTypes, Object data) throws SQLException {
		Object[] objects = new Object[fieldTypes.length];
		for (int i = 0; i < fieldTypes.length; i++) {
			FieldType fieldType = fieldTypes[i];
			objects[i] = fieldType.getConvertedFieldValue(data);
			if (objects[i] == null && fieldType.getDefaultValue() != null) {
				objects[i] = fieldType.getDefaultValue();
			}
		}
		return objects;
	}

	static void appendWhereId(DatabaseType databaseType, FieldType idField, StringBuilder sb,
							  List<FieldType> fieldTypeList) {
		sb.append("WHERE ");
		appendFieldColumnName(databaseType, sb, idField, fieldTypeList);
		sb.append("= ?");
	}

	static void appendTableName(DatabaseType databaseType, StringBuilder sb, String prefix, String tableName) {
		if (prefix != null) {
			sb.append(prefix);
		}
		databaseType.appendEscapedEntityName(sb, tableName);
		sb.append(' ');
	}

	static void appendFieldColumnName(DatabaseType databaseType, StringBuilder sb, FieldType fieldType,
			List<FieldType> fieldTypeList) {
		databaseType.appendEscapedEntityName(sb, fieldType.getDbColumnName());
		if (fieldTypeList != null) {
			fieldTypeList.add(fieldType);
		}
		sb.append(' ');
	}

	private SqlType[] getSqlTypes(FieldType[] fieldTypes) {
		SqlType[] typeVals = new SqlType[fieldTypes.length];
		for (int i = 0; i < fieldTypes.length; i++) {
			typeVals[i] = fieldTypes[i].getSqlTypeVal();
		}
		return typeVals;
	}

	@Override
	public String toString() {
		return "MappedStatement: " + statement;
	}
}
