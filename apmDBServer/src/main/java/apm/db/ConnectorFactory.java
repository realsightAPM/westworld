package apm.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectorFactory {
	private final static String url = "jdbc:mysql://10.4.55.167:3306/apmdata?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&useSSL=false";
	
	private final static String dbDriver="com.mysql.jdbc.Driver";   
	
	private static String dbUser="root";  
	private static String dbPass="root"; 
	
	static{
		try {
			Class.forName(dbDriver);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() throws SQLException{
		Connection connection = DriverManager.getConnection(url, dbUser, dbPass);
		return connection;
	}
}
