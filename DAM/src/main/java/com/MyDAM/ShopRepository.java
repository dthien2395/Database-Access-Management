package com.MyDAM;

import com.DatabaseConnection;
import com.model.Shop;

import javax.persistence.Table;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dthien on 6/30/2017.
 */
public class ShopRepository extends MySQLRepository<Shop, Long> {

}
