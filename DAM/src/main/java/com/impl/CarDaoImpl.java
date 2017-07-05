package com.impl;

import com.dao.BaseDaoImpl;
import com.db.DatabaseType;
import com.table.DatabaseTableConfig;

/**
 * Created by DangThiHien on 05/07/2017.
 */
public class CarDaoImpl<Car, Integer> extends BaseDaoImpl {

    public CarDaoImpl(Class dataClass) {
        super(dataClass);
    }

    public CarDaoImpl(DatabaseType databaseType, Class dataClass) {
        super(databaseType, dataClass);
    }

    public CarDaoImpl(DatabaseTableConfig tableConfig) {
        super(tableConfig);
    }

    public CarDaoImpl(DatabaseType databaseType, DatabaseTableConfig tableConfig) {
        super(databaseType, tableConfig);
    }
}
