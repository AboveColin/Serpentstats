package dev.cdevries.SQLConnection;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLConnection {
    // Static variable reference of single_instance
    // of type Singleton
    private static SQLConnection connection = null;
 
    // Declaring a variable of type String
    public Connection conn;
 
    // Constructor
    // Here we will be creating private constructor
    // restricted to this class itself
    private SQLConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://*IP*:*PORT*/*DB*", "USERNAME", "PASSWORD");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
 
    // Static method
    // Static method to create instance of Singleton class
    public static SQLConnection getConnection() {
        if (connection == null) {
            connection = new SQLConnection();
        }
 
        return connection;
    }
}