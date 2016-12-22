package apm.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import apm.mode.ClassInfo;
import apm.mode.HeapHistory;

public class DBServer {
	public static void saveHeapData(HeapHistory heapHistory){
		 String insertHeapSql = "INSERT INTO `heaphistory_table` (`time`, `totalheapbytes`, `totalpermgenbytes`, `sourcedisplayed`, `deltadisplayed`)"
				+ " VALUES (?, ?, ?, ?, ?);";
		 String insertHeapClassSql = "INSERT INTO `heapclassinfo_table` "
		 		+ "(`heaphistoryid`, `bytes`, `jvm_name`, `name`, `instances`, `pergen`) "
		 		+ "VALUES (?, ?, ?, ?, ?,?);";
		 try {
			Connection connection = ConnectorFactory.getConnection();
			connection.setAutoCommit(false);
			PreparedStatement preHeap = connection.prepareStatement(insertHeapSql,Statement.RETURN_GENERATED_KEYS);
			PreparedStatement preHeapClass = connection.prepareStatement(insertHeapClassSql);
			System.out.println("####### time #### "+heapHistory.getTime());
			preHeap.setString(1, heapHistory.getTime());
			preHeap.setLong(2,heapHistory.getTotalHeapBytes());
			preHeap.setLong(3, heapHistory.getTotalPermGenBytes());
			preHeap.setInt(4, heapHistory.isSourceDisplayed()?1:0);
			preHeap.setInt(5,heapHistory.isDeltaDisplayed()?1:0);
			preHeap.executeUpdate();
			connection.commit();
			ResultSet rs = preHeap.getGeneratedKeys();
			if(!rs.next()) return;
			int id = rs.getInt(1);
			
			if(heapHistory.getPermGenClasses()!=null){
				for(ClassInfo classInfo : heapHistory.getPermGenClasses()){
					preHeapClass.setInt(1, id);
					preHeapClass.setInt(2, classInfo.getBytes());
					preHeapClass.setString(3, classInfo.getJvmName());
					preHeapClass.setString(4,classInfo.getName());
					preHeapClass.setInt(5, classInfo.getInstances());
					preHeapClass.setInt(6,classInfo.isPerGen()?1:0);
					preHeapClass.addBatch();
				}
			}
			else{
				System.out.println("NULL NULL NULL NULL !@!@!@!@!");
			}
			
			
			if(heapHistory.getClasses()!=null){
				for(ClassInfo classInfo:heapHistory.getClasses()){
					preHeapClass.setInt(1,id);
					preHeapClass.setInt(2, classInfo.getBytes());
					preHeapClass.setString(3, classInfo.getJvmName());
					preHeapClass.setString(4,classInfo.getName());
					preHeapClass.setInt(5, classInfo.getInstances());
					preHeapClass.setInt(6,classInfo.isPerGen()?1:0);
					preHeapClass.addBatch();
				}
			}
			else{
				System.out.println("NULL NULL NULL NULL $%$%$%%$%");
			}
			
			if(heapHistory.getPermGenClasses()!=null || heapHistory.getClasses()!=null)
			{
				preHeapClass.executeUpdate();
				connection.commit();
			}
			preHeap.close();
			preHeapClass.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	
	
}
