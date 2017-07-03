package com.misc;

import java.sql.SQLException;

/**
 * Created by dthien on 7/3/2017.
 */
public class SqlExceptionUtil {

    private SqlExceptionUtil() {

    }

    public static SQLException create(String message, Throwable cause) {
        SQLException sqlException = new SQLException(message);
        sqlException.initCause(cause);
        return sqlException;
    }
}
