package com.impl;

import com.field.DataType;
import com.support.DatabaseResults;

import java.io.InputStream;
import java.sql.*;

/**
 * Created by DangThiHien on 05/07/2017.
 */
public class JdbcDatabaseResults implements DatabaseResults{
    private final PreparedStatement preparedStmt;
    private final ResultSet resultSet;
    private ResultSetMetaData metaData = null;

    public JdbcDatabaseResults(PreparedStatement preparedStmt, ResultSet resultSet) {
        this.preparedStmt = preparedStmt;
        this.resultSet = resultSet;
    }

    public int getColumnCount() throws SQLException {
        if (metaData == null) {
            metaData = resultSet.getMetaData();
        }
        return metaData.getColumnCount();
    }

    public String getColumnName(int columnIndex) throws SQLException {
        if (metaData == null) {
            metaData = resultSet.getMetaData();
        }
        return metaData.getColumnName(columnIndex);
    }

    Number getIdColumnData(int columnIndex) throws SQLException {
        if (metaData == null) {
            metaData = resultSet.getMetaData();
        }
        int typeVal = metaData.getColumnType(columnIndex);
        DataType dataType = TypeValMapper.getDataTypeForIdTypeVal(typeVal);
        if (dataType == null) {
            throw new SQLException("Unknown DataType for typeVal " + typeVal + " in column " + columnIndex);
        }
        Number id = dataType.resultToId(this, columnIndex);
        if (id == null) {
            // may never happen but let's be careful
            String colName = "unknown";
            try {
                colName = getColumnName(columnIndex);
            } catch (SQLException e) {
                // ignore it
            }
            throw new SQLException("Id column " + colName + " (#" + columnIndex + ") is invalid type " + dataType);
        }
        return id;
    }

    public int findColumn(String columnName) throws SQLException {
        return resultSet.findColumn(columnName);
    }

    public InputStream getBlobStream(int columnIndex) throws SQLException {
        Blob blob = resultSet.getBlob(columnIndex);
        if (blob == null) {
            return null;
        } else {
            return blob.getBinaryStream();
        }
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        return resultSet.getBoolean(columnIndex);
    }

    public byte getByte(int columnIndex) throws SQLException {
        return resultSet.getByte(columnIndex);
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        return resultSet.getBytes(columnIndex);
    }

    public double getDouble(int columnIndex) throws SQLException {
        return resultSet.getDouble(columnIndex);
    }

    public float getFloat(int columnIndex) throws SQLException {
        return resultSet.getFloat(columnIndex);
    }

    public int getInt(int columnIndex) throws SQLException {
        return resultSet.getInt(columnIndex);
    }

    public long getLong(int columnIndex) throws SQLException {
        return resultSet.getLong(columnIndex);
    }

    public short getShort(int columnIndex) throws SQLException {
        return resultSet.getShort(columnIndex);
    }

    public String getString(int columnIndex) throws SQLException {
        return resultSet.getString(columnIndex);
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return resultSet.getTimestamp(columnIndex);
    }

    public boolean next() throws SQLException {
        // NOTE: we should not auto-close here, even if there are no more results
        if (resultSet.next()) {
            return true;
        } else if (!preparedStmt.getMoreResults()) {
            return false;
        } else {
            return resultSet.next();
        }
    }

    public boolean isNull(int columnIndex) throws SQLException {
        return (resultSet.getObject(columnIndex) == null);
    }
}
