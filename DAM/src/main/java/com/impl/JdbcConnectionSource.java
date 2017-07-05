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

    /**
     * Constructor for Spring type wiring if you are using the set methods. If you are using Spring then your should
     * use: init-method="initialize"
     */
    public JdbcConnectionSource() {
        // for spring type wiring
    }

    /**
     * Create a data source for a particular database URL.
     *
     * @param url
     *            The database URL which should start jdbc:...
     * @throws SQLException
     *             If the driver associated with the database driver is not found in the classpath.
     */
    public JdbcConnectionSource(String url) throws SQLException, ClassNotFoundException {
        this(url, null, null, null);
    }

    /**
     * Create a data source for a particular database URL. The databaseType is usually determined from the databaseUrl
     * so most users should call {@link #JdbcConnectionSource(String)} instead. If, however, you need to force the class
     * to use a specific DatabaseType then this constructor should be used.
     *
     * @param url
     *            The database URL which should start jdbc:...
     * @param databaseType
     *            Database to associate with this connection source.
     * @throws SQLException
     *             If the driver associated with the database driver is not found in the classpath.
     */
    public JdbcConnectionSource(String url, DatabaseType databaseType) throws SQLException, ClassNotFoundException {
        this(url, null, null, databaseType);
    }

    /**
     * Create a data source for a particular database URL with username and password permissions.
     *
     * @param url
     *            The database URL which should start jdbc:...
     * @param username
     *            Username for permissions on the database.
     * @param password
     *            Password for permissions on the database.
     * @throws SQLException
     *             If the driver associated with the database driver is not found in the classpath.
     */
    public JdbcConnectionSource(String url, String username, String password) throws SQLException, ClassNotFoundException {
        this(url, username, password, null);
    }

    /**
     * Create a data source for a particular database URL with username and password permissions. The databaseType is
     * usually determined from the databaseUrl so most users should call
     * {@link #JdbcConnectionSource(String, String, String)} instead. If, however, you need to force the class to use a
     * specific DatabaseType then this constructor should be used.
     *
     * @param url
     *            The database URL which should start jdbc:...
     * @param username
     *            Username for permissions on the database.
     * @param password
     *            Password for permissions on the database.
     * @param databaseType
     *            Database to associate with this connection source.
     * @throws SQLException
     *             If the driver associated with the database driver is not found in the classpath.
     */
    public JdbcConnectionSource(String url, String username, String password, DatabaseType databaseType)
            throws SQLException, ClassNotFoundException {
        this.url = url;
        this.username = username;
        this.password = password;
        this.databaseType = databaseType;
        initialize();
    }

    /**
     * Initialize the class after the setters have been called. If you are using the no-arg constructor and Spring type
     * wiring, this should be called after all of the set methods.
     *
     * @throws SQLException
     *             If the driver associated with the database URL is not found in the classpath.
     */
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
        // noop since this is a single connection source
        return true;
    }

    public void clearSpecialConnection(DatabaseConnection connection) {
        // noop since this is a single connection source
    }

    public DatabaseType getDatabaseType() {
        if (!initialized) {
            throw new IllegalStateException(getClass().getSimpleName() + " was not initialized properly");
        }
        return databaseType;
    }

    // not required
    public void setUsername(String username) {
        this.username = username;
    }

    // not required
    public void setPassword(String password) {
        this.password = password;
    }

    // not required
    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    /**
     * Make a connection to the database.
     */
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
