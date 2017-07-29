package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;

import com.realsight.westworld.bnanalysis.basic.Pair;

public class SolrResults {

	public List<Pair<String, Object>> conf_list;
	public String solr_url;
	public SolrResults() {}
	
	public SolrResults(String _solr_url) {
		solr_url = _solr_url;
		conf_list = new ArrayList<Pair<String, Object>> ();
	}
	
	public void addResult(Pair<String, Object> pair) {
		conf_list.add(pair);
	}
	
	public void write() throws SolrServerException, IOException {
		WriteSolr writer = new WriteSolr();
		writer.writeOneDoc(solr_url, conf_list);
	}
	
	private void res_idOption() throws SolrServerException, IOException {
		long start_time = 1498449060017L;
//		long start_time = 1498569780047L;
		start_time = 1499937900881L;
		long gap = (long) (1000*3600*24);
//		String rs_start = "2017-07-05T08:23:01Z";
//		start_time = TimeUtil.timeConversion2(rs_start);
		
		String res_id1 = "ff8080815ce9b31f015ce9b61d250002.f00492b8f5d2242247984099c352f13d:";
		String res_id2 = "8a8a83a95cc9eba1015cc9ff60a00002.0e47e96277ef71f02e469e33e4cd0d5a:";
		String res_id3 = "ff8080815d0dac94015d10aa99520019.7ed159cb462eced8257a4d712a8e659c:";
		String solr_url = "http://10.0.67.21:8080/solr/";
		
		SolrResults resulter = new SolrResults(solr_url + "option/");
		resulter.addResult(new Pair<String, Object> ("option_s", "bn"));
		
//		resulter.addResult(new Pair<String, Object> ("bn_name_s", "8a8a83a95cc8a3d4015cc8a985190003.19f750081da1104aa21ecb78d800a889"));
//		resulter.addResult(new Pair<String, Object> ("bn_name_s", "8a8a83a95cc9eba1015cc9ff60a00002.0e47e96277ef71f02e469e33e4cd0d5a"));
		resulter.addResult(new Pair<String, Object> ("bn_name_s", "bn_test"));
				
		resulter.addResult(new Pair<String, Object> ("solr_reader_url_s", solr_url + "nifi/"));
		resulter.addResult(new Pair<String, Object> ("solr_writer_url_s", solr_url + "rca/"));
		resulter.addResult(new Pair<String, Object> ("starttime_l", start_time));
		resulter.addResult(new Pair<String, Object> ("gap_l", gap));
		resulter.addResult(new Pair<String, Object> ("interval_l", 60000));
//		resulter.addResult(new Pair<String, Object> ("fq_s", "type_s:metricsets"));
		resulter.addResult(new Pair<String, Object> ("fq_s", "one_level_type:*"));
		
//		List<String> list = new ArrayList<String> ();
//		list.add("metricset_name_s:diskio");
//		list.add("metricset_name_s:memory");
//		list.add("metricset_name_s:cpu");
//		list.add("metricset_name_s:filesystem");
//		list.add("metricset_name_s:network");
//		list.add("metricset_name_s:load");
//		list.add("metricset_name_s:process");
//		resulter.addResult(new Pair<String, Object> ("index_ss", list));
//		
//		List<String> val_list = new ArrayList<String> ();
//		val_list.add("memory:system_memory_actual_used_bytes_f");
//		val_list.add("diskio:system_diskio_read_bytes_f");
//		val_list.add("cpu:system_cpu_system_pct_f");
//		val_list.add("filesystem:system_filesystem_used_pct_f");
//		val_list.add("network:system_network_in_packets_f");
//		val_list.add("load:metricset_rtt_f");
//		val_list.add("process:system_process_memory_share_f");
//		resulter.addResult(new Pair<String, Object> ("value_ss", val_list));
		
//		resulter.addResult(new Pair<String, Object> ("fq_s", "one_level_type:*"));
//		resulter.addResult(new Pair<String, Object> ("fq_s", "query_s:*,responsetime_f:*,timestamp_l:*"));
//		resulter.addResult(new Pair<String, Object> ("res_list_s", ""));
//		resulter.addResult(new Pair<String, Object> ("query_field_s", "query_s"));
//		resulter.addResult(new Pair<String, Object> ("value_field_s", "responsetime_f"));
		
//		resulter.addResult(new Pair<String, Object> ("res_list_s", "ff8080815ce9b31f015ce9b61d250002.f00492b8f5d2242247984099c352f13d,"
//				                                                 + "8a8a83a95cc9eba1015cc9ff60a00002.0e47e96277ef71f02e469e33e4cd0d5a,"
//																 + "ff8080815d0dac94015d10aa99520019.7ed159cb462eced8257a4d712a8e659c"));
		resulter.addResult(new Pair<String, Object> ("res_list_s", "8a84adc25d36770f015d367ef4960002.534e711defa41def64d039ce02f28dda"));
//		String list = res_id1 + "JAVAEE_Durations_sum,"
//				    + res_id1 + "JAVAEE_Memory_used,"
//				    + res_id1 + "JAVAEE_Http_global,"
//				    + res_id1 + "JAVAEE_Cpu_time_sum,"
//				    + res_id2 + "PHP_memory_cacheuse,"
//				    + res_id2 + "PHP_disk_use,"
//				    + res_id2 + "PHP_CPU_used,"
//				    + res_id2 + "PHP_disk_percent,"
//				    + res_id2 + "PHP_memory_use,"
//				    + res_id3 + "DOTNET_User_Time,"
//				    + res_id3 + "DOTNET_Disk_Time,"
//				    + res_id3 + "DOTNET_Bytes_Total_sec";
		String list = "8a84adc25d36770f015d367ef4960002.534e711defa41def64d039ce02f28dda:" + "JAVAEE_Memory_used,"
					+ "8a84adc25d36770f015d367ef4960002.534e711defa41def64d039ce02f28dda:" + "JAVAEE_PhysicalPercent,"
					+ "8a84adc25d36770f015d367ef4960002.534e711defa41def64d039ce02f28dda:" + "JAVAEE_Health";
//		String list = res_id3 + "JAVAEE_ActiveThreadsNum,"
//				    + res_id3 + "JAVAEE_ART_sql,"
//				    + res_id3 + "JAVAEE_Http_error,"
//				    + res_id3 + "JAVAEE_Durations_sum,"
//				    + res_id3 + "JAVAEE_Availability,"
//				    + res_id3 + "JAVAEE_SwapPercent,"
//				    + res_id3 + "JAVAEE_Http_2xx,"
//				    + res_id3 + "JAVAEE_Http_3xx,"
//				    + res_id3 + "JAVAEE_HeapPercent,"
//				    + res_id3 + "JAVAEE_Http_4xx,"
//				    + res_id3 + "JAVAEE_Http_5xx,"
//				    + res_id3 + "JAVAEE_OnlineUserNum_total,"
//				    + res_id3 + "JAVAEE_Memory_used,"
//				    + res_id3 + "JAVAEE_Http_global,"
//				    + res_id3 + "JAVAEE_Cpu_time_sum,"
//				    + res_id3 + "JAVAEE_Apdex,"
//				    + res_id3 + "JAVAEE_PhysicalPercent,"
//				    + res_id3 + "JAVAEE_Health,"
//				    + res_id3 + "JAVAEE_ART_http,"
//				    + res_id3 + "JAVAEE_CPU_used";
//		String list = "GET /consolefk/"
//					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/CPU_used/0.9"
//					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/Memory_used/0.9"
//					+ "^POST /detect/alert/online/254.a82d7836ffcd29fd96cb0cb734a224ff/ThroughPut/0.9"
//					+ "^GET /ReqWeb/"
//					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/ThroughPut/0.9"
//					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/ART_sql/0.9"
//					+ "^POST /detect/alert/online/254.a82d7836ffcd29fd96cb0cb734a224ff/HeapPercent/0.9"
//					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/Health/0.9"
//					+ "^GET /apm/monitoring"
//					+ "^GET /bc/monitoring"
//					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/HeapPercent/0.9"
//					+ "^class IN, type PTR, 171.55.4.10.in-addr.arpa."
//					+ "^class IN, type AAAA, developer.neusoft.com."
//					+ "^show /*!50002 global */ status"
//					+ "^POST /detect/alert/online/254.a82d7836ffcd29fd96cb0cb734a224ff/Memory_used/0.9"
//					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/ActiveThreadsNum/0.9"
//					+ "^GET /dotnet/metrics/SIS2"
//					+ "^GET /consolefk/monitoring"
//					+ "^GET /bc/"
//					+ "^class IN, type A, developer.neusoft.com."
//					+ "^POST /detect/alert/online/254.a82d7836ffcd29fd96cb0cb734a224ff/OnlineUserNum_total/0.9"
//					+ "^show variables like 'query_cache_size'"
//					+ "^GET /RealSightAPM/php_info.php"
//					+ "^class IN, type PTR, 177.55.4.10.in-addr.arpa."
//					+ "^GET /acl_users/credentials_cookie_auth/require_login"
//					+ "^GET /bc"
//					+ "^GET /"
//					+ "^SELECT 1"
//					+ "^POST /detect/alert/online/254.a82d7836ffcd29fd96cb0cb734a224ff/ART_sql/0.9"
//					+ "^show variables like 'table_open_cache'"
//					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/ART_http/0.9"
//					+ "^GET /apm/components/aclome-app-security/pages/login/login.jsp"
//					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/Apdex/0.9"
//					+ "^class IN, type PTR, 1.2.3.25.in-addr.arpa."
//					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/OnlineUserNum_total/0.9"
//					+ "^POST /detect/alert/online/254.a82d7836ffcd29fd96cb0cb734a224ff/CPU_used/0.9";
					
		resulter.addResult(new Pair<String, Object> ("indexList_s", list));
		resulter.write();
	}
	
	private void napmOption() throws SolrServerException, IOException {
		long start_time = 1498449060017L;
		start_time = 1499937900881L;
		long gap = (long) (1000*3600*24);
		String rs_start = "2017-07-05T08:23:01Z";
		start_time = TimeUtil.timeConversion2(rs_start);
		
		String solr_url = "http://10.0.67.21:8080/solr/";
		
		SolrResults resulter = new SolrResults(solr_url + "option/");
		resulter.addResult(new Pair<String, Object> ("option_s", "bn"));
		resulter.addResult(new Pair<String, Object> ("bn_name_s", "bn_napm"));
		resulter.addResult(new Pair<String, Object> ("solr_reader_url_s", solr_url + "napm/"));
		resulter.addResult(new Pair<String, Object> ("solr_writer_url_s", solr_url + "rca/"));
		resulter.addResult(new Pair<String, Object> ("starttime_l", start_time));
		resulter.addResult(new Pair<String, Object> ("gap_l", gap));
		resulter.addResult(new Pair<String, Object> ("interval_l", 60000));
		resulter.addResult(new Pair<String, Object> ("fq_s", "type_s:metricsets"));
		
		List<String> list = new ArrayList<String> ();
		list.add("metricset_name_s:diskio");
		list.add("metricset_name_s:memory");
		list.add("metricset_name_s:cpu");
		list.add("metricset_name_s:filesystem");
		list.add("metricset_name_s:network");
		list.add("metricset_name_s:load");
		list.add("metricset_name_s:process");
		resulter.addResult(new Pair<String, Object> ("index_ss", list));
		
		List<String> val_list = new ArrayList<String> ();
		val_list.add("memory:system_memory_actual_used_bytes_f");
		val_list.add("diskio:system_diskio_read_bytes_f");
		val_list.add("cpu:system_cpu_system_pct_f");
		val_list.add("filesystem:system_filesystem_used_pct_f");
		val_list.add("network:system_network_in_packets_f");
		val_list.add("load:metricset_rtt_f");
		val_list.add("process:system_process_memory_share_f");
		resulter.addResult(new Pair<String, Object> ("value_ss", val_list));
		resulter.write();
	}
	
	private void metricbeatOption() throws SolrServerException, IOException {
		long start_time = 1498449060017L;
		start_time = 1499937900881L;
		long gap = (long) (1000*3600*24);
		String rs_start = "2017-07-05T08:23:01Z";
		start_time = TimeUtil.timeConversion2(rs_start);
		
		String solr_url = "http://10.0.67.14:8080/solr/";
		
		SolrResults resulter = new SolrResults(solr_url + "option/");
		resulter.addResult(new Pair<String, Object> ("option_s", "bn"));
		resulter.addResult(new Pair<String, Object> ("bn_name_s", "bn_beat"));
		resulter.addResult(new Pair<String, Object> ("solr_reader_url_s", solr_url + "metrics/"));
		resulter.addResult(new Pair<String, Object> ("solr_writer_url_s", solr_url + "rca/"));
		resulter.addResult(new Pair<String, Object> ("starttime_l", start_time));
		resulter.addResult(new Pair<String, Object> ("gap_l", gap));
		resulter.addResult(new Pair<String, Object> ("interval_l", 60000));
		resulter.addResult(new Pair<String, Object> ("fq_s", "type_s:metricsets"));
		
		List<String> list = new ArrayList<String> ();
		list.add("metricset_name_s:diskio");
		list.add("metricset_name_s:memory");
		list.add("metricset_name_s:cpu");
		list.add("metricset_name_s:filesystem");
		list.add("metricset_name_s:network");
		list.add("metricset_name_s:load");
		list.add("metricset_name_s:process");
		resulter.addResult(new Pair<String, Object> ("index_ss", list));
		
		List<String> val_list = new ArrayList<String> ();
		val_list.add("memory:system_memory_actual_used_bytes_f");
		val_list.add("diskio:system_diskio_read_bytes_f");
		val_list.add("cpu:system_cpu_system_pct_f");
		val_list.add("filesystem:system_filesystem_used_pct_f");
		val_list.add("network:system_network_in_packets_f");
		val_list.add("load:metricset_rtt_f");
		val_list.add("process:system_process_memory_share_f");
		resulter.addResult(new Pair<String, Object> ("value_ss", val_list));
		resulter.write();
	}
	
	public static void main(String[] args) {
		
	}
}
