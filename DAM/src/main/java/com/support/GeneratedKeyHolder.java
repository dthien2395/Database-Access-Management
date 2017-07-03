package com.support;

import java.sql.SQLException;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public interface GeneratedKeyHolder {

    public void addKey(Number key) throws SQLException;

}
