package com.stmt.map;

import com.db.DatabaseType;
import com.field.FieldType;
import com.field.SqlType;
import com.stmt.SelectArg;
import com.stmt.StatementBuilder.InternalQueryBuilder;
import com.support.DatabaseConnection;
import com.table.TableInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public class MappedQueryForId<T, ID> extends BaseMappedQuery<T> {

	private final String label;

	protected MappedQueryForId(TableInfo<T> tableInfo, String statement, List<FieldType> argFieldTypeList,
							   List<FieldType> resultFieldTypeList, String label) {
		super(tableInfo, statement, argFieldTypeList, resultFieldTypeList);
		this.label = label;
	}

	public T execute(DatabaseConnection databaseConnection, Object obj) throws SQLException {
		Object[] args = new Object[] { getId(obj) };
		// @SuppressWarnings("unchecked")
		Object result =
				databaseConnection.queryForOne(statement, args, new SqlType[] { idField.getSqlTypeVal() }, this);
		@SuppressWarnings("unchecked")
		T castResult = (T) result;
		postProcessResult(obj, castResult);
		return castResult;
	}

	protected ID getId(Object obj) throws SQLException {
		@SuppressWarnings("unchecked")
		ID id = (ID) obj;
		return id;
	}

	protected void postProcessResult(Object obj, T result) throws SQLException {
		// noop
	}

	public static <T, ID> MappedQueryForId<T, ID> build(DatabaseType databaseType, TableInfo<T> tableInfo)
			throws SQLException {
		List<FieldType> argFieldTypeList = new ArrayList<FieldType>();
		List<FieldType> resultFieldTypeList = new ArrayList<FieldType>();
		String statement = buildStatement(databaseType, tableInfo, argFieldTypeList, resultFieldTypeList);
		if (statement == null) {
			return null;
		} else {
			return new MappedQueryForId<T, ID>(tableInfo, statement, argFieldTypeList, resultFieldTypeList,
					"query-for-id");
		}
	}

	protected static <ID, T> String buildStatement(DatabaseType databaseType, TableInfo<T> tableInfo,
			List<FieldType> argFieldTypeList, List<FieldType> resultFieldTypeList) throws SQLException {
		FieldType idField = tableInfo.getIdField();
		if (idField == null) {
			return null;
		}
		InternalQueryBuilder<T, ID> qb = new InternalQueryBuilder<T, ID>(databaseType, tableInfo);
		// this selectArg is ignored here because we pass in the id as a fixed argument
		SelectArg idSelectArg = new SelectArg();
		qb.where().eq(idField.getDbColumnName(), idSelectArg);
		List<SelectArg> selectArgList = new ArrayList<SelectArg>();
		return qb.buildSelectString(argFieldTypeList, resultFieldTypeList, selectArgList);
	}

}
