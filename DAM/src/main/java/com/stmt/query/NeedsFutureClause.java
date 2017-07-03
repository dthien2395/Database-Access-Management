package com.stmt.query;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public interface NeedsFutureClause extends Clause {

	public void setMissingClause(Clause right);
}
