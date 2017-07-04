package com;

import com.MyDAM.MySQLRepository;
import com.MyDAM.ShopRepository;
import com.db.BaseMySQLDatabaseType;
import com.db.DatabaseType;
import com.field.DatabaseFieldConfig;
import com.model.Car;
import com.table.DatabaseTableConfig;
import com.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by dthien on 6/30/2017.
 */
public class Main {
    public static void main(String[] params) throws SQLException {
        ShopRepository shopRepository = new ShopRepository();
        System.out.println(shopRepository.findAll());
//        System.out.println(shopRepository.findById(7L));
        TableUtils.createTable2(new BaseMySQLDatabaseType(), Car.class);
    }
}
