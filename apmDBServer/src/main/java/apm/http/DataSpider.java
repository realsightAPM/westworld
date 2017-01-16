package apm.http;

import java.util.HashMap;
import java.util.Map;






import apm.Process.DataProcess;
import apm.globalinfo.DataType;
import apm.globalinfo.HttpMethod;
import apm.globalinfo.Part;
import apm.globalinfo.Period;


public class DataSpider {
	private static final String URL = "http://121.42.185.24:8080/monitoring";
	DataProcess processForSystemData = null;
	DataProducer systemDataProducer = null;
	public void start(){
	/*	Map<String,String> params = new HashMap<String,String>();
		params.put("part",Part.HEAPHISTORY.getName());
		params.put("period",Period.JOUR.getName());
		params.put("format",DataType.JSON.getName());
		
		DataProcess processForHeapData = new DataProcess();
		DataProducer heapDataProducer = new DataProducer(URL,HttpMethod.GET,params,processForHeapData);
		System.out.println("heapDataProducer start ");
		heapDataProducer.start();
		*/
		processForSystemData = new DataProcess();
		Map<String,String> params1 = new HashMap<String,String>();
		params1.put("part","lastValue");
		params1.put("graph","usedMemory,cpu,httpSessions,activeThreads,activeConnections,usedConnections,gc,"
				+ "threadCount,loadedClassesCount,usedNonHeapMemory,usedPhysicalMemorySize,usedSwapSpaceSize,"
				+ "httpSessionsMeanAge,httpHitsRate,httpMeanTimes,httpSystemErrors,systemLoad,"
				+ "tomcatBusyThreads,tomcatBytesReceived,tomcatBytesSent,fileDescriptors,Free_disk_space,DeadLockThreads,"
				+ "RunningThreads,Http_Cpu_Time_Sum,Http_Durations_Sum,"
				+ "Http_Error_Hits,Http_Gobal_Hits,HTTP_SYSTEM_ERROR,Http2xxNum,Http4xxNum,Http5xxNum");
		systemDataProducer = new DataProducer(URL,HttpMethod.GET,params1 ,processForSystemData);
		//System.out.println("systemDataProducer start ");
		systemDataProducer.start();
	}
	
	
}
