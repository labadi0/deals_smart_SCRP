package persistence.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

public class JdbcConnection {
	private static String host = "localhost";
	private static String user = "root";
	private static String password = "";
	private static String url = "jdbc:mysql://" + host + "?characterEncoding=utf8&useConfigs=maxPerformance&allowMultiQueries=true";

	private static Connection connection;
	
	public static Connection getConnection(){
		if (connection == null) {
			try {
				connection = DriverManager.getConnection(url, user, password);
				System.out.println("Connected to MySQL DB!");
			} catch (Exception e) {
				System.err.println("Connection failed: " + e.getMessage());
			}
		}
		return connection; 
	}
}