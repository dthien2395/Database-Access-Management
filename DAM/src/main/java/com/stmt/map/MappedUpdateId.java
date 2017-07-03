package com.stmt.map;

import com.db.DatabaseType;
import com.field.FieldType;
import com.misc.SqlExceptionUtil;
import com.support.DatabaseConnection;
import com.table.TableInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public class MappedUpdateId<T, ID> extends BaseMappedStatement<T> {

	private MappedUpdateId(TableInfo<T> tableInfo, String statement, List<FieldType> argFieldTypeList) {
		super(tableInfo, statement, argFieldTypeList);
	}

	/**
	 * Update the id field of the object in the database.
	 */
	public int execute(DatabaseConnection databaseConnection, T data, ID newId) throws SQLException {
		Object[] fieldObjects = getFieldObjects(argFieldTypes, data);
		try {
			// the arguments are the new-id and old-id
			Object[] args = new Object[] { newId, fieldObjects[0] };
			int rowC = databaseConnection.update(statement, args, argSqlTypes);
			if (rowC == 1) {
				// adjust the object to assign the new id
				idField.assignField(data, newId);
			}

			return rowC;
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Unable to run update-id stmt on object " + data + ": " + statement, e);
		}
	}

	public static <T, ID> MappedUpdateId<T, ID> build(DatabaseType databaseType, TableInfo<T> tableInfo) {
		FieldType idField = tableInfo.getIdField();
		if (idField == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		List<FieldType> argFieldTypeList = new ArrayList<FieldType>();
		appendTableName(databaseType, sb, "UPDATE ", tableInfo.getTableName());
		sb.append("SET ");
		appendFieldColumnName(databaseType, sb, idField, argFieldTypeList);
		sb.append("= ? ");
		appendWhereId(databaseType, idField, sb, argFieldTypeList);
		return new MappedUpdateId<T, ID>(tableInfo, sb.toString(), argFieldTypeList);
	}
}
