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
	
	public void addResult(Pair<String, Object> pair) throws SolrServerException, IOException {
		conf_list.add(pair);
	}
	
	public void write() throws SolrServerException, IOException {
		WriteSolr writer = new WriteSolr();
		writer.writeOneDoc(solr_url, conf_list);
	}
	
	public static void main(String[] args) throws SolrServerException, IOException {
		
//		long start_time = 1498449060017L;
		long start_time = 1497487899000L;
		long gap = (long) (1000*3600);
		
//		String res_id1 = "8a8a83a95cc8a3d4015cc8a985190003.19f750081da1104aa21ecb78d800a889:";
//		String res_id2 = "8a8a83a95cc9eba1015cc9ff60a00002.0e47e96277ef71f02e469e33e4cd0d5a:";
		
		
		SolrResults resulter = new SolrResults("http://10.4.55.171:8983/solr/option/");
		resulter.addResult(new Pair<String, Object> ("option_s", "bn"));
		
//		resulter.addResult(new Pair<String, Object> ("bn_name_s", "8a8a83a95cc8a3d4015cc8a985190003.19f750081da1104aa21ecb78d800a889"));
//		resulter.addResult(new Pair<String, Object> ("bn_name_s", "8a8a83a95cc9eba1015cc9ff60a00002.0e47e96277ef71f02e469e33e4cd0d5a"));
		resulter.addResult(new Pair<String, Object> ("bn_name_s", "bn_test"));
				
		resulter.addResult(new Pair<String, Object> ("solr_reader_url_s", "http://10.4.55.171:8983/solr/test/"));
		resulter.addResult(new Pair<String, Object> ("solr_writer_url_s", "http://10.4.55.171:8983/solr/rca2/"));
		resulter.addResult(new Pair<String, Object> ("starttime_l", start_time));
		resulter.addResult(new Pair<String, Object> ("gap_l", gap));
		resulter.addResult(new Pair<String, Object> ("interval_l", 60000));
//		resulter.addResult(new Pair<String, Object> ("fq_s", "one_level_type:*"));
		resulter.addResult(new Pair<String, Object> ("fq_s", "query_s:*,responsetime_f:*,timestamp_l:*"));
		resulter.addResult(new Pair<String, Object> ("res_list_s", ""));
		resulter.addResult(new Pair<String, Object> ("query_field_s", "query_s"));
		resulter.addResult(new Pair<String, Object> ("value_field_s", "responsetime_f"));
		
//		resulter.addResult(new Pair<String, Object> ("res_list_s", "8a8a83a95cc8a3d4015cc8a985190003.19f750081da1104aa21ecb78d800a889,"
//				                                                 + "8a8a83a95cc9eba1015cc9ff60a00002.0e47e96277ef71f02e469e33e4cd0d5a"));
//		String list = res_id1 + "JAVAEE_ActiveThreadsNum,"
//				    + res_id1 + "JAVAEE_ART_sql,"
//				    + res_id1 + "JAVAEE_Http_error,"
//				    + res_id1 + "JAVAEE_Durations_sum,"
//				    + res_id1 + "JAVAEE_Availability,"
//				    + res_id1 + "JAVAEE_SwapPercent,"
//				    + res_id1 + "JAVAEE_Http_2xx,"
//				    + res_id1 + "JAVAEE_Http_3xx,"
//				    + res_id1 + "JAVAEE_HeapPercent,"
//				    + res_id1 + "JAVAEE_Http_4xx,"
//				    + res_id1 + "JAVAEE_Http_5xx,"
//				    + res_id1 + "JAVAEE_OnlineUserNum_total,"
//				    + res_id1 + "JAVAEE_Memory_used,"
//				    + res_id1 + "JAVAEE_Http_global,"
//				    + res_id1 + "JAVAEE_Cpu_time_sum,"
//				    + res_id1 + "JAVAEE_Apdex,"
//				    + res_id1 + "JAVAEE_PhysicalPercent,"
//				    + res_id1 + "JAVAEE_Health,"
//				    + res_id1 + "JAVAEE_ART_http,"
//				    + res_id1 + "JAVAEE_CPU_used,"
//		            + res_id2 + "PHP_load_min5,"
//				    + res_id2 + "PHP_swap_idle,"
//				    + res_id2 + "PHP_memory_cacheuse,"
//				    + res_id2 + "PHP_disk_use,"
//				    + res_id2 + "PHP_CPU_used,"
//				    + res_id2 + "PHP_Availability,"
//				    + res_id2 + "PHP_ART_http,"
//				    + res_id2 + "PHP_Cpu_time_sum,"
//				    + res_id2 + "PHP_Health,"
//				    + res_id2 + "PHP_swap_use,"
//				    + res_id2 + "PHP_memory_realuse,"
//     				+ res_id2 + "PHP_memory_cachepercent,"
//				    + res_id2 + "PHP_Http_error,"
//				    + res_id2 + "PHP_php_syscpu,"
//				    + res_id2 + "PHP_PhysicalPercent,"
//				    + res_id2 + "PHP_load_min15,"
//				    + res_id2 + "PHP_SwapPercent,"
//				    + res_id2 + "PHP_disk_idle,"
//				    + res_id2 + "PHP_User_cpu,"
//				    + res_id2 + "PHP_disk_percent,"
//				    + res_id2 + "PHP_memory_use,"
//				    + res_id2 + "PHP_load_min,"
//				    + res_id2 + "PHP_load_avg,"
//				    + res_id2 + "PHP_memory_idle,"
//     				+ res_id2 + "PHP_memory_realidle,"
//				    + res_id2 + "PHP_memory_bufferuse,"
//				    + res_id2 + "PHP_Http_4xx,"
//				    + res_id2 + "PHP_Http_global,"
//				    + res_id2 + "PHP_Http_5xx,"
//    				+ res_id2 + "PHP_HeapPercent,"
//				    + res_id2 + "PHP_Http_2xx,"
//				    + res_id2 + "PHP_ThroughPut,"
//				    + res_id2 + "PHP_Http_3xx,"
//				    + res_id2 + "PHP_Http_1xx,"
//				    + res_id2 + "PHP_Memory_used";
		String list = "GET /consolefk/"
					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/CPU_used/0.9"
					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/Memory_used/0.9"
					+ "^POST /detect/alert/online/254.a82d7836ffcd29fd96cb0cb734a224ff/ThroughPut/0.9"
					+ "^GET /ReqWeb/"
					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/ThroughPut/0.9"
					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/ART_sql/0.9"
					+ "^POST /detect/alert/online/254.a82d7836ffcd29fd96cb0cb734a224ff/HeapPercent/0.9"
					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/Health/0.9"
					+ "^GET /apm/monitoring"
					+ "^GET /bc/monitoring"
					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/HeapPercent/0.9"
					+ "^class IN, type PTR, 171.55.4.10.in-addr.arpa."
					+ "^class IN, type AAAA, developer.neusoft.com."
					+ "^show /*!50002 global */ status"
					+ "^POST /detect/alert/online/254.a82d7836ffcd29fd96cb0cb734a224ff/Memory_used/0.9"
					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/ActiveThreadsNum/0.9"
					+ "^GET /dotnet/metrics/SIS2"
					+ "^GET /consolefk/monitoring"
					+ "^GET /bc/"
					+ "^class IN, type A, developer.neusoft.com."
					+ "^POST /detect/alert/online/254.a82d7836ffcd29fd96cb0cb734a224ff/OnlineUserNum_total/0.9"
					+ "^show variables like 'query_cache_size'"
					+ "^GET /RealSightAPM/php_info.php"
					+ "^class IN, type PTR, 177.55.4.10.in-addr.arpa."
					+ "^GET /acl_users/credentials_cookie_auth/require_login"
					+ "^GET /bc"
					+ "^GET /"
					+ "^SELECT 1"
					+ "^POST /detect/alert/online/254.a82d7836ffcd29fd96cb0cb734a224ff/ART_sql/0.9"
					+ "^show variables like 'table_open_cache'"
					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/ART_http/0.9"
					+ "^GET /apm/components/aclome-app-security/pages/login/login.jsp"
					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/Apdex/0.9"
					+ "^class IN, type PTR, 1.2.3.25.in-addr.arpa."
					+ "^POST /detect/alert/online/327.a82d7836ffcd29fd96cb0cb734a224ff/OnlineUserNum_total/0.9"
					+ "^POST /detect/alert/online/254.a82d7836ffcd29fd96cb0cb734a224ff/CPU_used/0.9";
					
		resulter.addResult(new Pair<String, Object> ("indexList_s", list));
		resulter.write();
	}
}
