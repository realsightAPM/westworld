package com.realsight.brain.rca.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.realsight.brain.rca.util.Settings;

public class AppDataManager {

	private static AppDataManager ins = null;
	private static final Logger logger = LogManager.getLogger(AppDataManager.class.getName());
	
	private String jdbcDriver = null;
	private String dbUrl = null;
	private String dbUsername = null;
	private String dbPassword = null;
	private String testTable = "";
	private int initialConnections = 10;
	private int incrementalConnections = 5;
	private int maxConnections = 50;
	private Vector<PooledConnection> connections = null;

	private AppDataManager() {

		this.jdbcDriver = "com.mysql.jdbc.Driver";
		this.dbUrl =  "jdbc:mysql://" + 
				Settings.getInstance().get("rca.host") + ":" + 
				Settings.getInstance().get("rca.port") + "/" +
				Settings.getInstance().get("rca.dbname") + 
				"?characterEncoding=utf8&useSSL=false";
		this.dbUsername = Settings.getInstance().get("rca.user");
		this.dbPassword = Settings.getInstance().get("rca.pwd");
		try {
			createPool();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static AppDataManager getInstance() {
		if (ins == null) {
			ins = new AppDataManager();
		}
		return ins;
	}

	public void execute(String sql) {
		try {
			PooledConnection conn = AppDataManager.getInstance().getPoolConnection();
			conn.getConnection().setAutoCommit(false);
			PreparedStatement pstmt = conn.getConnection().prepareStatement(sql);
			pstmt.execute();
			conn.getConnection().commit();
			AppDataManager.getInstance().releasePoolConnection(conn);
			logger.info("[" + sql + "]" + " execute success.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("[" + sql + "]" + " execute error.");
			e.printStackTrace();
		}
    }
	
	public int getInitialConnections() {
		return this.initialConnections;
	}

	public void setInitialConnections(int initialConnections) {
		this.initialConnections = initialConnections;

	}

	public int getIncrementalConnections() {
		return this.incrementalConnections;
	}

	public void setIncrementalConnections(int incrementalConnections) {
		this.incrementalConnections = incrementalConnections;

	}

	public int getMaxConnections() {
		return this.maxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public String getTestTable() {
		return this.testTable;
	}

	public void setTestTable(String testTable) {
		this.testTable = testTable;
	}

	public synchronized void createPool() throws Exception {
		if (connections != null) {
			return; 
		}
		Driver driver = (Driver) (Class.forName(this.jdbcDriver).newInstance());
		DriverManager.registerDriver(driver);
		connections = new Vector<PooledConnection>();
		createConnections(this.initialConnections);
		logger.info(" 数据库连接池创建成功！ ");
	}

	private void createConnections(int numConnections) throws SQLException {
		for (int x = 0; x < numConnections; x++) {
			if (this.maxConnections > 0
					&& this.connections.size() >= this.maxConnections) {
				break;
			}
			try {
				connections.addElement(new PooledConnection(newConnection()));
			} catch (SQLException e) {
				throw new SQLException();
			}
			logger.info(" 数据库连接己创建 ......");
		}

	}

	private Connection newConnection() throws SQLException {
		Connection conn = DriverManager.getConnection(dbUrl, dbUsername,
				dbPassword);
		if (connections.size() == 0) {
			DatabaseMetaData metaData = conn.getMetaData();
			int driverMaxConnections = metaData.getMaxConnections();
			if (driverMaxConnections > 0
					&& this.maxConnections > driverMaxConnections) {
				this.maxConnections = driverMaxConnections;
			}
		}
		return conn; 
	}

	public synchronized PooledConnection getPoolConnection() throws SQLException {
		if (connections == null) {
			return null;
		}
		PooledConnection conn = getFreePoolConnection();
		while (conn == null) {
			wait(250);
			conn = getFreePoolConnection(); 
		}
		return conn;
	}
	
	public synchronized void releasePoolConnection(PooledConnection conn) throws SQLException {
		if (connections == null) {
			return ;
		}
		conn.setBusy(false);
		return ;
	}
	
	private PooledConnection getFreePoolConnection() throws SQLException {
		PooledConnection pconn = findFreePoolConnection();
		if (pconn == null) {
			createConnections(incrementalConnections);
			pconn = findFreePoolConnection();
			if (pconn == null) {
				return null;
			}
		}
		return pconn;
	}

	private PooledConnection findFreePoolConnection() throws SQLException {
		PooledConnection pConn = null;
		Enumeration<PooledConnection> enumerate = connections.elements();
		while (enumerate.hasMoreElements()) {
			pConn = (PooledConnection) enumerate.nextElement();
			if (!pConn.isBusy()) {
				Connection conn = pConn.getConnection();
				pConn.setBusy(true);
				if (!testConnection(conn)) {
					try {
						conn = newConnection();
					} catch (SQLException e) {
						logger.error(" 创建数据库连接失败！ " + e.getMessage());
						return null;
					}
					pConn.setConnection(conn);
				}
				break; 
			}

		}
		return pConn;
	}

	private boolean testConnection(Connection conn) {
		try {
			if (testTable.equals("")) {
				conn.setAutoCommit(true);
			} else {
				Statement stmt = conn.createStatement();
				stmt.execute("select count(*) from " + testTable);
			}
		} catch (SQLException e) {
			closeConnection(conn);
			return false;
		}
		return true;
	}

	public void returnConnection(Connection conn) {
		if (connections == null) {
			logger.info(" 连接池不存在，无法返回此连接到连接池中 !");
			return;
		}
		PooledConnection pConn = null;
		Enumeration<PooledConnection> enumerate = connections.elements();
		while (enumerate.hasMoreElements()) {
			pConn = (PooledConnection) enumerate.nextElement();
			if (conn == pConn.getConnection()) {
				pConn.setBusy(false);
				break;
			}
		}
	}

	public synchronized void refreshConnections() throws SQLException {
		if (connections == null) {
			logger.info(" 连接池不存在，无法刷新 !");
			return;
		}
		PooledConnection pConn = null;
		Enumeration<PooledConnection> enumerate = connections.elements();
		while (enumerate.hasMoreElements()) {
			pConn = (PooledConnection) enumerate.nextElement();
			if (pConn.isBusy()) {
				wait(5000);
			}
			closeConnection(pConn.getConnection());
			pConn.setConnection(newConnection());
			pConn.setBusy(false);
		}
	}

	public synchronized void closeConnectionPool() throws SQLException {
		if (connections == null) {
			logger.info(" 连接池不存在，无法关闭 !");
			return;
		}
		PooledConnection pConn = null;
		Enumeration<PooledConnection> enumerate = connections.elements();
		while (enumerate.hasMoreElements()) {
			pConn = (PooledConnection) enumerate.nextElement();
			if (pConn.isBusy()) {
				wait(5000); 
			}
			closeConnection(pConn.getConnection());
			connections.removeElement(pConn);
		}
		connections = null;
	}
	
	private void closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			logger.error(" 关闭数据库连接出错： " + e.getMessage());
		}
	}

	private void wait(int mSeconds) {
		try {
			Thread.sleep(mSeconds);
		} catch (InterruptedException e) {
		}
	}
	
	class PooledConnection {
		Connection connection = null;
		boolean busy = false; 
		public PooledConnection(Connection connection) {
			this.connection = connection;
		}
		
		public Connection getConnection() {
			return connection;
		}

		public void setConnection(Connection connection) {
			this.connection = connection;
		}

		public boolean isBusy() {
			return busy;
		}

		public void setBusy(boolean busy) {
			this.busy = busy;
		}
	}
	
	public static void main(String[] args) throws SQLException {
		String create_log_table = String.format("" +
                "CREATE TABLE IF NOT EXISTS log ( " +
                "id int NOT NULL AUTO_INCREMENT, " +
                "ts bigint, " +
                "http varchar(240), " +
                "message_type varchar(10), " +
                "message_class varchar(240), " +
                "message_text varchar(240), " +
                "PRIMARY KEY (id), " + 
                "UNIQUE (ts, http, message_type, message_class) );"
		);
		AppDataManager.getInstance().execute(create_log_table);
		String create_cpu_table = String.format("" +
                "CREATE TABLE IF NOT EXISTS cpu ( " +
                "ts bigint, " +
                "value double, " +
                "PRIMARY KEY (ts) );"
		);
		AppDataManager.getInstance().execute(create_cpu_table);
		String create_mem_table = String.format("" +
                "CREATE TABLE IF NOT EXISTS mem ( " +
                "ts bigint, " +
                "value double, " +
                "PRIMARY KEY (ts) );"
		);
		AppDataManager.getInstance().execute(create_mem_table);
	} 
}

