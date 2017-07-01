package com;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by dthien on 6/30/2017.
 */
public class DatabaseConnection {
    private static Connection connection;

    public static Connection getConnection() {

        if (connection != null) {
            return connection;
        }

        InputStream input = null;
        Properties connectionProps = new Properties();

        try {
            String filename = "application.properties";
            input = DatabaseConnection.class.getClassLoader().getResourceAsStream(filename);
            if (input == null) {
                return null;
            }
            connectionProps.load(input);
            Class.forName(connectionProps.getProperty("db.driver"));
            connection = DriverManager.getConnection(
                    connectionProps.getProperty("db.url"),
                    connectionProps.getProperty("db.username"), "");

            System.out.println("Connected to database");
            return connection;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (SQLException ex) {

        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        } finally {
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void closeConnection() throws SQLException{
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
