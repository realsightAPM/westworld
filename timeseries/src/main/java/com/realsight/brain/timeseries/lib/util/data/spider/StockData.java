package com.realsight.brain.timeseries.lib.util.data.spider;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;

import com.realsight.brain.timeseries.lib.util.HttpUtils;

public class StockData {
	public static void main(String[] args) {
	    String host = "https://ali-stock.showapi.com";
	    String path = "/hk-stock-history";
	    String method = "GET";
	    Map<String, String> headers = new HashMap<String, String>();
	    //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
	    headers.put("Authorization", "APPCODE a18ab545f7eb4f8eb7dab6e7db799fe6");
	    Map<String, String> querys = new HashMap<String, String>();
	    querys.put("begin", "2014-09-01");
	    querys.put("code", "00001");
	    querys.put("end", "2015-09-02");


	    try {
	    	/**
	    	* 重要提示如下:
	    	* HttpUtils请从
	    	* https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
	    	* 下载
	    	*
	    	* 相应的依赖请参照
	    	* https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
	    	*/
	    	HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
	    	System.out.println(response.toString());
	    	//获取response的body
	    	//System.out.println(EntityUtils.toString(response.getEntity()));
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
}
