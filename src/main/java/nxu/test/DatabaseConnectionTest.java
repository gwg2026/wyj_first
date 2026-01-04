package nxu.test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database connection test class
 */
public class DatabaseConnectionTest {
    
    public static void main(String[] args) {
        System.out.println("Starting database connection test...");
        
        // Read configuration file
        Properties properties = new Properties();
        String driver = null;
        String url = null;
        String username = null;
        String password = null;
        
        try {
            // Load database.properties file from classpath
            properties.load(DatabaseConnectionTest.class.getClassLoader().getResourceAsStream("database.properties"));
            
            driver = properties.getProperty("jdbc.driver");
            url = properties.getProperty("jdbc.url");
            username = properties.getProperty("jdbc.username");
            password = properties.getProperty("jdbc.password");
            
            System.out.println("Configuration file loaded successfully!");
            System.out.println("Driver class: " + driver);
            System.out.println("Connection URL: " + url);
            System.out.println("Username: " + username);
            
            // Check if password is configured
            if ("your_password".equals(password) || password == null || password.trim().isEmpty()) {
                System.err.println("Warning: Password not configured correctly!");
                System.err.println("Please set the correct MySQL password in database.properties");
                System.out.println("Connection test terminated.");
                return;
            } else {
                System.out.println("Password: Configured (hidden for security)");
            }
            
        } catch (IOException e) {
            System.err.println("Error: Cannot read database.properties file!");
            System.err.println("Please ensure the file exists and is readable");
            e.printStackTrace();
            return;
        }
         
        Connection connection = null;
         
        try {
            // Establish connection (MySQL JDBC driver 4.0+ doesn't need manual driver loading)
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connection successful!");
            
            // Test if connection is valid
            if (connection != null && !connection.isClosed()) {
                System.out.println("Connection status: Active");
                System.out.println("Database URL: " + url);
                System.out.println("Username: " + username);
                System.out.println("Connection test PASSED!");
            }
            
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            System.err.println("Error message: " + e.getMessage());
            
            // Provide detailed error analysis and solutions
            if (e.getMessage() != null) {
                if (e.getMessage().contains("Access denied")) {
                    System.err.println("Possible cause: Wrong username or password");
                    System.err.println("Please check username and password in database.properties");
                } else if (e.getMessage().contains("Unknown database")) {
                    System.err.println("Possible cause: Database 'ssm_db' does not exist");
                    System.err.println("Please execute database.sql script to create the database");
                } else if (e.getMessage().contains("Connection refused")) {
                    System.err.println("Possible cause: MySQL service not started or wrong port");
                    System.err.println("Please confirm MySQL service is running on port 3306");
                } else if (e.getMessage().contains("Communications link failure")) {
                    System.err.println("Possible cause: Network connection problem");
                    System.err.println("Please check if MySQL server is accessible");
                } else if (e.getMessage().contains("No suitable driver")) {
                    System.err.println("Possible cause: MySQL JDBC driver not found");
                    System.err.println("Please check if mysql-connector-java dependency is in pom.xml");
                }
            }
            e.printStackTrace();
        } finally {
            // Close connection
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("Connection closed");
                } catch (SQLException e) {
                    System.err.println("Error closing connection:");
                    e.printStackTrace();
                }
            }
        }
        
        System.out.println("Database connection test completed.");
    }
}