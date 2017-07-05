package com.support;

import com.db.BaseMySQLDatabaseType;
import com.db.DatabaseType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DangThiHien on 04/07/2017.
 */
public class DatabaseTypeUtils {
    private static List<DatabaseType> databaseTypes = new ArrayList<DatabaseType>();

    static {
        // new drivers need to be added here
        databaseTypes.add(new BaseMySQLDatabaseType());
    }

    private DatabaseTypeUtils() {
    }

    public static DatabaseType createDatabaseType(String databaseUrl) {
        String dbTypePart = extractDbType(databaseUrl);
        for (DatabaseType databaseType : databaseTypes) {
            if (databaseType.isDatabaseUrlThisType(databaseUrl, dbTypePart)) {
                return databaseType;
            }
        }
        throw new IllegalArgumentException("Unknown database-type url part '" + dbTypePart + "' in: " + databaseUrl);
    }

    private static String extractDbType(String databaseUrl) {
        if (!databaseUrl.startsWith("jdbc:")) {
            throw new IllegalArgumentException("Database URL was expected to start with jdbc: but was " + databaseUrl);
        }
        String[] urlParts = databaseUrl.split(":");
        if (urlParts.length < 2) {
            throw new IllegalArgumentException("Database URL was expected to be in the form: jdbc:db-type:... but was "
                    + databaseUrl);
        }
        return urlParts[1];
    }
}
