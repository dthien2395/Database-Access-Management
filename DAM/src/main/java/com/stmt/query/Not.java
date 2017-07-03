package com.stmt.query;

import com.db.DatabaseType;
import com.stmt.SelectArg;

import java.util.List;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public class Not implements Clause, NeedsFutureClause {

	private Comparison comparison = null;

	public Not() {
	}

	public Not(Clause clause) {
		setMissingClause(clause);
	}

	public void setMissingClause(Clause clause) {
		if (this.comparison != null) {
			throw new IllegalArgumentException("NOT operation already has a comparison set");
		}
		if (clause instanceof Comparison) {
			this.comparison = (Comparison) clause;
		} else {
			throw new IllegalArgumentException("NOT operation can only work with comparison SQL clauses, not " + clause);
		}
	}

	public StringBuilder appendSql(DatabaseType databaseType, StringBuilder sb, List<SelectArg> selectArgList) {
		if (this.comparison == null) {
			throw new IllegalArgumentException("Comparison has not been set in NOT operation");
		}
		// this generates: (NOT 'x' = 123 )
		sb.append("(NOT ");
		databaseType.appendEscapedEntityName(sb, comparison.getColumnName());
		sb.append(' ');
		comparison.appendOperation(sb);
		comparison.appendValue(databaseType, sb, selectArgList);
		sb.append(") ");
		return sb;
	}

	@Override
	public String toString() {
		if (comparison == null) {
			return "NOT without comparison";
		} else {
			return "NOT comparison " + comparison;
		}
	}
}
