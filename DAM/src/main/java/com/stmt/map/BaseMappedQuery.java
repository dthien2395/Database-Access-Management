package com.stmt.map;


import com.field.FieldType;
import com.stmt.GenericRowMapper;
import com.support.DatabaseResults;
import com.table.TableInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public abstract class BaseMappedQuery<T> extends BaseMappedStatement<T> implements GenericRowMapper<T> {

	protected final FieldType[] resultsFieldTypes;
	private final Map<String, Integer> columnPositions = new HashMap<String, Integer>();

	protected BaseMappedQuery(TableInfo<T> tableInfo, String statement, List<FieldType> argFieldTypeList,
							  List<FieldType> resultFieldTypeList) {
		super(tableInfo, statement, argFieldTypeList);
		this.resultsFieldTypes = resultFieldTypeList.toArray(new FieldType[resultFieldTypeList.size()]);
	}

	public T mapRow(DatabaseResults results) throws SQLException {
		T instance = tableInfo.createObject();
		for (FieldType fieldType : resultsFieldTypes) {
			Object val = fieldType.resultToJava(results, columnPositions);
			fieldType.assignField(instance, val);
		}
		return instance;
	}
}
