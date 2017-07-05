package com.stmt;

import com.support.DatabaseResults;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public interface GenericRowMapper<T> {

    public T mapRow(DatabaseResults results) throws SQLException;

}
