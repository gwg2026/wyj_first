package nxu.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Simple database connection test
 */
public class SimpleConnectionTest {
    public static void main(String[] args) {
        System.out.println("Testing direct database connection...");
        
        // Load MySQL JDBC driver explicitly
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            e.printStackTrace();
            return;
        }
        
        String url = "jdbc:mysql://localhost:3306/ssm_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "123456";
        
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Database connection SUCCESSFUL!");
            System.out.println("Connected to: " + url);
            System.out.println("Database: ssm_db");
            System.out.println("Connection is valid: " + !connection.isClosed());
            
            connection.close();
            System.out.println("Connection closed.");
            
        } catch (SQLException e) {
            System.err.println("❌ Database connection FAILED!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}