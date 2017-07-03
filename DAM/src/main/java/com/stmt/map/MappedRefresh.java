package com.stmt.map;

import com.db.DatabaseType;
import com.field.FieldType;
import com.table.TableInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public class MappedRefresh<T, ID> extends MappedQueryForId<T, ID> {

	private MappedRefresh(TableInfo<T> tableInfo, String statement, List<FieldType> argFieldTypeList,
						  List<FieldType> resultFieldTypeList) {
		super(tableInfo, statement, argFieldTypeList, resultFieldTypeList, "refresh");
	}

	@Override
	protected ID getId(Object obj) throws SQLException {
		@SuppressWarnings("unchecked")
		T data = (T) obj;
		// this is necessary because of a 1.6 compilation error
		@SuppressWarnings("unchecked")
		ID id = (ID) idField.getConvertedFieldValue(data);
		return id;
	}

	@Override
	protected void postProcessResult(Object obj, T result) throws SQLException {
		@SuppressWarnings("unchecked")
		T data = (T) obj;
		// copy each field into the passed in object
		for (FieldType fieldType : resultsFieldTypes) {
			if (fieldType != idField) {
				fieldType.assignField(data, fieldType.getConvertedFieldValue(result));
			}
		}
	}

	public static <T, ID> MappedRefresh<T, ID> build(DatabaseType databaseType, TableInfo<T> tableInfo)
			throws SQLException {
		List<FieldType> argFieldTypeList = new ArrayList<FieldType>();
		List<FieldType> resultFieldTypeList = new ArrayList<FieldType>();
		String statement = buildStatement(databaseType, tableInfo, argFieldTypeList, resultFieldTypeList);
		if (statement == null) {
			return null;
		} else {
			return new MappedRefresh<T, ID>(tableInfo, statement, argFieldTypeList, resultFieldTypeList);
		}
	}
}
