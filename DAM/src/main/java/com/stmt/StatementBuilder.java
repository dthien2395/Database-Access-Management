package com.stmt;

import com.db.DatabaseType;
import com.field.FieldType;
import com.stmt.map.MappedPreparedStmt;
import com.stmt.query.OrderBy;
import com.table.TableInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public class StatementBuilder<T, ID> {


	private TableInfo<T> tableInfo;
	private DatabaseType databaseType;
	private final FieldType idField;

	private boolean distinct = false;
	private boolean selectIdColumn = true;
	private List<String> columnList = null;
	private final List<OrderBy> orderByList = new ArrayList<OrderBy>();
	private final List<String> groupByList = new ArrayList<String>();
	private Where where = null;
	private Integer limit = null;


	public StatementBuilder(DatabaseType databaseType, TableInfo<T> tableInfo) {
		this.databaseType = databaseType;
		this.tableInfo = tableInfo;
		this.idField = tableInfo.getIdField();
	}

	public StatementBuilder<T, ID> columns(String... columns) {
		if (columnList == null) {
			columnList = new ArrayList<String>();
		}
		for (String column : columns) {
			addColumnToList(column);
		}
		return this;
	}

	public StatementBuilder<T, ID> columns(Iterable<String> columns) {
		if (columnList == null) {
			columnList = new ArrayList<String>();
		}
		for (String column : columns) {
			addColumnToList(column);
		}
		return this;
	}

	public StatementBuilder<T, ID> groupBy(String columnName) {
		verifyColumnName(columnName);
		groupByList.add(columnName);
		selectIdColumn = false;
		return this;
	}

	public StatementBuilder<T, ID> orderBy(String columnName, boolean ascending) {
		verifyColumnName(columnName);
		orderByList.add(new OrderBy(columnName, ascending));
		return this;
	}

	public StatementBuilder<T, ID> distinct() {
		distinct = true;
		selectIdColumn = false;
		return this;
	}

	public StatementBuilder<T, ID> limit(Integer maxRows) {
		limit = maxRows;
		return this;
	}

	public Where where() {
		where = new Where(tableInfo);
		return where;
	}


	public void setWhere(Where where) {
		this.where = where;
	}


	public PreparedStmt<T> prepareStatement() {
		List<FieldType> argFieldTypeList = new ArrayList<FieldType>();
		List<FieldType> resultFieldTypeList = new ArrayList<FieldType>();
		List<SelectArg> selectArgList = new ArrayList<SelectArg>();
		String statement = buildSelectString(argFieldTypeList, resultFieldTypeList, selectArgList);
		return new MappedPreparedStmt<T>(tableInfo, statement, argFieldTypeList, resultFieldTypeList, selectArgList,
				(databaseType.isLimitSqlSupported() ? null : limit));
	}

	public PreparedStmt<T> prepareQuery() {
		return prepareStatement();
	}


	public String prepareStatementString() {
		List<FieldType> argFieldTypeList = new ArrayList<FieldType>();
		List<FieldType> resultFieldTypeList = new ArrayList<FieldType>();
		List<SelectArg> selectArgList = new ArrayList<SelectArg>();
		return buildSelectString(argFieldTypeList, resultFieldTypeList, selectArgList);
	}

	private String buildSelectString(List<FieldType> argFieldTypeList, List<FieldType> resultFieldTypeList,
			List<SelectArg> selectArgList) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		if (databaseType.isLimitAfterSelect()) {
			appendLimit(sb);
		}
		if (distinct) {
			sb.append("DISTINCT ");
		}
		appendColumns(sb, resultFieldTypeList);
		sb.append("FROM ");
		databaseType.appendEscapedEntityName(sb, tableInfo.getTableName());
		sb.append(' ');
		if (where != null) {
			sb.append("WHERE ");
			where.appendSql(databaseType, sb, selectArgList);
		}
		for (SelectArg selectArg : selectArgList) {
			FieldType fieldType = tableInfo.getFieldTypeByName(selectArg.getColumnName());
			argFieldTypeList.add(fieldType);
		}
		// 'group by' comes before 'order by'
		appendGroupBys(sb);
		appendOrderBys(sb);
		if (!databaseType.isLimitAfterSelect()) {
			appendLimit(sb);
		}
		String statement = sb.toString();
		return statement;
	}

	private void addColumnToList(String column) {
		verifyColumnName(column);
		columnList.add(column);
	}

	private void verifyColumnName(String columnName) {
		if (tableInfo.getFieldTypeByName(columnName) == null) {
			throw new IllegalArgumentException("Unknown column-name " + columnName);
		}
	}

	private void appendColumns(StringBuilder sb, List<FieldType> fieldTypeList) {
		// if no columns were specified then * is the default
		if (columnList == null) {
			sb.append("* ");
			// add all of the field types
			for (FieldType fieldType : tableInfo.getFieldTypes()) {
				fieldTypeList.add(fieldType);
			}
			return;
		}

		boolean first = true;
		boolean hasId = false;
		for (String columnName : columnList) {
			if (first) {
				first = false;
			} else {
				sb.append(',');
			}
			FieldType fieldType = tableInfo.getFieldTypeByName(columnName);
			appendFieldColumnName(sb, fieldType, fieldTypeList);
			if (fieldType == idField) {
				hasId = true;
			}
		}

		// we have to add the idField even if it isn't in the columnNameSet
		if (!hasId && selectIdColumn) {
			if (!first) {
				sb.append(',');
			}
			appendFieldColumnName(sb, idField, fieldTypeList);
		}
		sb.append(' ');
	}

	private void appendFieldColumnName(StringBuilder sb, FieldType fieldType, List<FieldType> fieldTypeList) {
		databaseType.appendEscapedEntityName(sb, fieldType.getDbColumnName());
		if (fieldTypeList != null) {
			fieldTypeList.add(fieldType);
		}
	}

	private void appendGroupBys(StringBuilder sb) {
		if (groupByList.size() == 0) {
			return;
		}

		sb.append("GROUP BY ");
		boolean first = true;
		for (String columnName : groupByList) {
			if (first) {
				first = false;
			} else {
				sb.append(',');
			}
			databaseType.appendEscapedEntityName(sb, columnName);
		}
		sb.append(' ');
	}

	private void appendOrderBys(StringBuilder sb) {
		if (orderByList.size() == 0) {
			return;
		}

		sb.append("ORDER BY ");
		boolean first = true;
		for (OrderBy orderBy : orderByList) {
			if (first) {
				first = false;
			} else {
				sb.append(',');
			}
			String columnName = orderBy.getColumnName();
			verifyColumnName(columnName);
			databaseType.appendEscapedEntityName(sb, columnName);
			if (orderBy.isAscending()) {
				// sb.append(" ASC");
			} else {
				sb.append(" DESC");
			}
		}
		sb.append(' ');
	}

	private void appendLimit(StringBuilder sb) {
		if (limit != null && databaseType.isLimitSqlSupported()) {
			databaseType.appendLimitValue(sb, limit);
		}
	}


	public static class InternalQueryBuilder<T, ID> extends StatementBuilder<T, ID> {

		public InternalQueryBuilder(DatabaseType databaseType, TableInfo<T> tableInfo) {
			super(databaseType, tableInfo);
		}


		public String buildSelectString(List<FieldType> argFieldTypeList, List<FieldType> resultFieldTypeList,
				List<SelectArg> selectArgList) {
			return super.buildSelectString(argFieldTypeList, resultFieldTypeList, selectArgList);
		}
	}
}
