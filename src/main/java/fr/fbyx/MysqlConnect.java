package fr.fbyx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MysqlConnect {
    // init database constants
    private static final String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";
    // private static final String DATABASE_URL = "jdbc:mysql://mysql-zrednjndsvbnklj.alwaysdata.net:3306/zrednjndsvbnklj_pnr";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/pnr";
    private static final String USERNAME = "pnr";
    private static final String PASSWORD = "mdppnr";
    private static final String MAX_POOL = "250";

    // init connection object
    private static Connection connection;
    // init properties object
    private Properties properties;

    public Properties setProperties(String user, String password) {
        properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("MaxPooledStatements", MAX_POOL);
        return this.properties;
    }

    // create properties
    private Properties getProperties() {
        return this.properties;
    }

    // connect database
    public Connection getConnexion() {
        if (connection == null) {
            try {
                Class.forName(DATABASE_DRIVER);
                connection = DriverManager.getConnection(DATABASE_URL, getProperties());
                System.out.println("Connected to the database !");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }    

    // disconnect database
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}