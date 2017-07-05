package com.impl;

import com.db.DatabaseType;
import com.support.ConnectionSource;
import com.support.DatabaseConnection;
import com.support.DatabaseTypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by DangThiHien on 05/07/2017.
 */
public class JdbcConnectionSource implements ConnectionSource {

    private static Logger logger = LoggerFactory.getLogger(JdbcConnectionSource.class);

    private String url;
    private String username;
    private String password;
    private JdbcDatabaseConnection connection;
    private DatabaseType databaseType;
    protected boolean initialized = false;

    public JdbcConnectionSource() {
        // for spring type wiring
    }


    public JdbcConnectionSource(String url) throws SQLException, ClassNotFoundException {
        this(url, null, null, null);
    }

    public JdbcConnectionSource(String url, DatabaseType databaseType) throws SQLException, ClassNotFoundException {
        this(url, null, null, databaseType);
    }


    public JdbcConnectionSource(String url, String username, String password) throws SQLException, ClassNotFoundException {
        this(url, username, password, null);
    }

    public JdbcConnectionSource(String url, String username, String password, DatabaseType databaseType)
            throws SQLException, ClassNotFoundException {
        this.url = url;
        this.username = username;
        this.password = password;
        this.databaseType = databaseType;
        initialize();
    }

    public void initialize() throws SQLException, ClassNotFoundException {
        if (initialized) {
            return;
        }
        if (url == null) {
            throw new SQLException("url was never set on " + getClass().getSimpleName());
        }
        if (databaseType == null) {
            databaseType = DatabaseTypeUtils.createDatabaseType(url);
        }
        databaseType.loadDriver();
        initialized = true;
    }

    public void close() throws SQLException {
        if (!initialized) {
            throw new SQLException(getClass().getSimpleName() + " was not initialized properly");
        }
        if (connection != null) {
            connection.close();
            logger.debug("closed connection {}", connection);
            connection = null;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DatabaseConnection getReadOnlyConnection() throws SQLException {
        if (!initialized) {
            throw new SQLException(getClass().getSimpleName() + " was not initialized properly");
        }
        return getReadWriteConnection();
    }

    public DatabaseConnection getReadWriteConnection() throws SQLException {
        if (!initialized) {
            throw new SQLException(getClass().getSimpleName() + " was not initialized properly");
        }
        if (connection != null) {
            if (connection.isClosed()) {
                throw new SQLException("Connection has already been closed");
            } else {
                return connection;
            }
        }
        connection = makeConnection(logger);
        return connection;
    }

    public void releaseConnection(DatabaseConnection connection) throws SQLException {
        if (!initialized) {
            throw new SQLException(getClass().getSimpleName() + " was not initialized properly");
        }
        // noop right now
    }

    public boolean saveSpecialConnection(DatabaseConnection connection) {
        return true;
    }

    public void clearSpecialConnection(DatabaseConnection connection) {
    }

    public DatabaseType getDatabaseType() {
        if (!initialized) {
            throw new IllegalStateException(getClass().getSimpleName() + " was not initialized properly");
        }
        return databaseType;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    protected JdbcDatabaseConnection makeConnection(Logger logger) throws SQLException {
        Properties properties = new Properties();
        if (username != null) {
            properties.setProperty("user", username);
        }
        if (password != null) {
            properties.setProperty("password", password);
        }
        JdbcDatabaseConnection connection = new JdbcDatabaseConnection(DriverManager.getConnection(url, properties));
        logger.debug("opened connection to {} got #{}", url, connection.hashCode());
        return connection;
    }
}
