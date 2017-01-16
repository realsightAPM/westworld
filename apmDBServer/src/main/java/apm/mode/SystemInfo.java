package apm.mode;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="mysysteminfo_table")
public class SystemInfo {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	 @Column(nullable=false)
	private Timestamp nowTime;
	
	 @Column(nullable=false)
	private float usedMemory;//使用的内存
	
	 @Column(nullable=false)
	private float cpu;//cpu使用率
	
	 @Column(nullable=false)
	private float httpSessions;//当前会话个数
	
	 @Column( nullable=false)
	private float activeThreads;//活跃线程数
	
	 @Column( nullable=false)
	private float activeConnections;//活跃的JDBC链接
	
	 @Column( nullable=false)
	private float usedConnections;//使用的JDBC链接
	
	 @Column( nullable=false)
	private float gc;
	
	 @Column( nullable=false)
	private float threadCount;//线程个数
	
	 @Column( nullable=false)
	private float loadedClassesCount;//加载的类的个数
	
	 @Column( nullable=false)
	private float usedNonHeapMemory;//使用的非堆内存
	 @Column( nullable=false)
	private float usedPhysicalMemorySize;//使用的物理内存
	
	 @Column( nullable=false)
	private float usedSwapSpaceSize;//
	
	 @Column( nullable=false)
	private float httpSessionsMeanAge;//
	
	 @Column( nullable=false)
	private float httpHitsRate;//http点击频率
	
	 @Column( nullable=false)
	private float httpMeanTimes;//http持续时间
	
	 @Column( nullable=false)
	private float httpSystemErrors;//系统错误
	
	 @Column( nullable=false)
	private float systemLoad;//系统的cpu使用率
	
	 @Column( nullable=false)
	private float tomcatBusyThreads;//tomcat繁忙线程数
	
	 @Column( nullable=false)
	private float tomcatBytesReceived;//tomcat接受的字节数
	
	 @Column( nullable=false)
	private float tomcatBytesSent;//tomcat发送的字节数
	
	 @Column( nullable=false)
	private float fileDescriptors;//打开的文件个数
	
	 @Column( nullable=false)
	private float free_disk_space;//空闲硬盘空间
	
	 @Column( nullable=false)
	private float deadLockThreads;//死锁线程数
	
	 @Column( nullable=false)
	private float runningThreads;//线程个数
	
	 @Column( nullable=false)
	private float http_Cpu_Time_Sum;//http请求消耗的cpu时间
	
	 @Column( nullable=false)
	private float http_Durations_Sum;//http持续时间总和？？？
	
	 @Column( nullable=false)
	private float http_Error_Hits;//http错误点击次数
	
	 @Column( nullable=false)
	private float http_Gobal_Hits;//http全部点击次数
	
	 @Column( nullable=false)
	private float HTTP_SYSTEM_ERROR;//http系统错误次数
	
	 @Column( nullable=false)
	private float http2xxNum;
	
	 @Column( nullable=false)
	private float http4xxNum;
	
	 @Column( nullable=false)
	private float http5xxNum;
	
	 @Column( nullable=false)
	private int clients;//客户端线程数目
	
}
