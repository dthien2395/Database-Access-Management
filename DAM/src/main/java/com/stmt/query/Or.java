package com.stmt.query;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public class Or extends BaseBinaryClause {

	public Or(Clause left) {
		super(left);
	}

	public Or(Clause left, Clause right) {
		super(left, right);
	}

	@Override
	public StringBuilder appendOperation(StringBuilder sb) {
		sb.append("OR ");
		return sb;
	}
}
