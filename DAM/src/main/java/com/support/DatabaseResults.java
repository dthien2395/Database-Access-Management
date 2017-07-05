package com.support;

import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created by DangThiHien on 05/07/2017.
 */
public interface DatabaseResults {

    public int getColumnCount() throws SQLException;

    public String getColumnName(int column) throws SQLException;

    public boolean next() throws SQLException;

    public int findColumn(String columnName) throws SQLException;

    public String getString(int columnIndex) throws SQLException;

    public boolean getBoolean(int columnIndex) throws SQLException;

    public byte getByte(int columnIndex) throws SQLException;

    public byte[] getBytes(int columnIndex) throws SQLException;

    public short getShort(int columnIndex) throws SQLException;

    public int getInt(int columnIndex) throws SQLException;

    public long getLong(int columnIndex) throws SQLException;

    public float getFloat(int columnIndex) throws SQLException;

    public double getDouble(int columnIndex) throws SQLException;

    public Timestamp getTimestamp(int columnIndex) throws SQLException;

    public InputStream getBlobStream(int columnIndex) throws SQLException;

    public boolean isNull(int columnIndex) throws SQLException;
}
