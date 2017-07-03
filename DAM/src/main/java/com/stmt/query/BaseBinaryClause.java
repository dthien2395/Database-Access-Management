package com.stmt.query;

import com.db.DatabaseType;
import com.stmt.SelectArg;

import java.util.List;

/**
 * Created by HienNguyen on 7/3/2017.
 */
abstract class BaseBinaryClause implements NeedsFutureClause {

	private Clause left;
	private Clause right = null;

	protected BaseBinaryClause(Clause left) {
		this.left = left;
	}

	protected BaseBinaryClause(Clause left, Clause right) {
		this.left = left;
		this.right = right;
	}

	/**
	 * Append the associated operation to the StringBuilder.
	 */
	public abstract StringBuilder appendOperation(StringBuilder sb);

	public StringBuilder appendSql(DatabaseType databaseType, StringBuilder sb, List<SelectArg> columnArgList) {
		sb.append("(");
		left.appendSql(databaseType, sb, columnArgList);
		appendOperation(sb);
		right.appendSql(databaseType, sb, columnArgList);
		sb.append(") ");
		return sb;
	}

	public void setMissingClause(Clause right) {
		if (this.right != null) {
			throw new IllegalStateException("Operation already has a right side set: " + this);
		}
		this.right = right;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(left).append(' ');
		appendOperation(sb);
		sb.append(right);
		return sb.toString();
	}
}
