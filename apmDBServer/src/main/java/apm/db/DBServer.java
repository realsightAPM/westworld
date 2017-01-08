package apm.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import apm.globalinfo.Save;
import apm.mode.ClassInfo;
import apm.mode.HeapHistory;
import apm.mode.SystemInfo;
import apm.webstress.Stress;

public class DBServer {
	public static void saveHeapData(HeapHistory heapHistory){
		try {
			int id = saveHeapInfo(heapHistory);
			if(id>0){
				if(heapHistory.getPermGenClasses()!=null)
				{
					saveClassInfoList(heapHistory.getPermGenClasses(),id);
				}
				if(heapHistory.getClasses()!=null){
					saveClassInfoList(heapHistory.getClasses(),id);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	public static void saveSystemInfo(SystemInfo systemInfo) throws SQLException{
		String insertSystemInfo = "INSERT INTO `systeminfo_table` "
				+ "(`times`, `cpu`, `http_times`, `session_count`, `thread_count`,`used_memory`,`client`) "
				+ "VALUES (?, ?, ?, ?, ?, ?,?);";
		 Connection connection = ConnectorFactory.getConnection();
		 PreparedStatement preStatement = connection.prepareStatement(insertSystemInfo);
		// Timestamp nowTime = new Timestamp(System.currentTimeMillis());
		 preStatement.setTimestamp(1, systemInfo.getNowTime());
		 preStatement.setFloat(2, systemInfo.getCpu());
		 preStatement.setFloat(3, systemInfo.getHttpTime());
		 preStatement.setInt(4,systemInfo.getSessionCount());
		 //preStatement.setInt(5,systemInfo.getHttpCount());
		 preStatement.setInt(5,systemInfo.getThreadCount());
		 preStatement.setInt(6, systemInfo.getUsedMemory());
		 preStatement.setInt(7,Stress.queue.size());
		 preStatement.executeUpdate();
		 preStatement.close();
		 connection.close();
	}
	
	
	private static int saveHeapInfo(HeapHistory heapHistory) throws SQLException{
		 String insertHeapSql = "INSERT INTO `heaphistory_table` (`time`, `totalheapbytes`, `totalpermgenbytes`, `sourcedisplayed`, `deltadisplayed`)"
					+ " VALUES (?, ?, ?, ?, ?);";
		 Connection connection = ConnectorFactory.getConnection();
		 PreparedStatement preHeap = connection.prepareStatement(insertHeapSql,Statement.RETURN_GENERATED_KEYS);
		 preHeap.setString(1, heapHistory.getTime());
		 preHeap.setLong(2,heapHistory.getTotalHeapBytes());
		 preHeap.setLong(3, heapHistory.getTotalPermGenBytes());
		 preHeap.setInt(4, heapHistory.isSourceDisplayed()?1:0);
		 preHeap.setInt(5,heapHistory.isDeltaDisplayed()?1:0);
		 preHeap.executeUpdate();
		 ResultSet rs = preHeap.getGeneratedKeys();
		 if(!rs.next()) return -1;
		 int id = rs.getInt(1);
		 preHeap.close();
		 connection.close();
		 return id;
	}
	
	private static void saveClassInfoList(List<ClassInfo> list,int fk) throws SQLException{
		 String insertHeapClassSql = "INSERT INTO `heapclassinfo_table` "
			 		+ "(`heaphistoryid`, `bytes`, `jvm_name`, `name`, `instances`, `pergen`) "
			 		+ "VALUES (?, ?, ?, ?, ?,?);";
		 Connection connection  =  ConnectorFactory.getConnection();
		 connection.setAutoCommit(false);
		 PreparedStatement preHeapClass = connection.prepareStatement(insertHeapClassSql);
		 int size = list.size();
		 int index=0;
		 for(ClassInfo classInfo : list)
		 {
			 	if(Filter.filter(classInfo).equals(Save.SAVE)){
			 		preHeapClass.setInt(1,fk);
					preHeapClass.setInt(2, classInfo.getBytes());
					preHeapClass.setString(3, classInfo.getJvmName());
					preHeapClass.setString(4,classInfo.getName());
					preHeapClass.setInt(5, classInfo.getInstances());
					preHeapClass.setInt(6,classInfo.isPerGen()?1:0);
					preHeapClass.addBatch();
					index++;
			 	}
				
				if(index==100){
					 preHeapClass.executeBatch();
					size-=100;
					index=0;
					connection.commit();
					preHeapClass.clearBatch();
				}

		 }
		 if(size>0)
		 {
			// int row = preHeapClass.executeUpdate();
			 preHeapClass.executeBatch();
			 connection.commit();
			 //System.out.println("row3 "+row);
		 }
		
		 preHeapClass.close();
		 connection.close();
	}
	
	
	
}
