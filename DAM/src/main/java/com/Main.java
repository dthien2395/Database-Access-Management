package com;

import com.dao.Dao;
import com.db.DatabaseType;
import com.impl.CarDaoImpl;
import com.impl.JdbcConnectionSource;
import com.impl.MySqlDatabaseType;
import com.model.Car;
import com.stmt.StatementBuilder;
import com.support.ConnectionSource;
import com.table.TableInfo;

import java.sql.SQLException;

/**
 * Created by dthien on 6/30/2017.
 */
public class Main {
    public static void main(String[] params) throws SQLException, ClassNotFoundException {
        DatabaseType databaseType = new MySqlDatabaseType();
        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:mysql://localhost:3306/shop", "root", null, databaseType);
//        TableUtils.createTable(new MySqlDatabaseType(),connectionSource.getReadWriteConnection(), Person.class);

        Dao<Car, Integer> carDao = CarDaoImpl.createDao(databaseType, connectionSource, Car.class);

        System.out.println(carDao.queryForAll());

        StatementBuilder<Car, Integer> statementBuilder = new StatementBuilder<Car, Integer>(databaseType, new TableInfo<Car>(databaseType, Car.class));

        statementBuilder.prepareQuery();
    }
}
