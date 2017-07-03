package com.stmt.query;


/**
 * Created by HienNguyen on 7/3/2017.
 */
public class And extends BaseBinaryClause {

	public And(Clause left) {
		super(left);
	}

	public And(Clause left, Clause right) {
		super(left, right);
	}

	@Override
	public StringBuilder appendOperation(StringBuilder sb) {
		sb.append("AND ");
		return sb;
	}
}
