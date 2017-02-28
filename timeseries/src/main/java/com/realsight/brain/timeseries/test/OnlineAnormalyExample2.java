package com.realsight.brain.timeseries.test;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;

import com.realsight.brain.timeseries.api.OnlineAnormalyDetectionAPI;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.util.Util;
import com.realsight.brain.timeseries.lib.util.data.ArtificialData;
import com.realsight.brain.timeseries.lib.util.data.StockData;
import com.realsight.brain.timeseries.lib.util.plot.Plot;

/**
 * @author Sun Muxin
 * 
 */ 
public class OnlineAnormalyExample2 {
	/**
	 * @param args
	 * @throws Exception 
	 */
//	600094大明城: 不改变趋势 
//	600503华丽家族: 不改变趋势
//	600028中国石化： 改变趋势（现在是上涨）
// 	http://ichart.yahoo.com/table.csv?s=%d.SS&a=01&b=01&c=2005&d=01&e=18&f=2017&g=d
//	300017网宿科技：改变趋势（现在是下跌）
//	300013华策影视：不改变趋势
//	300315掌趣科技：不改变趋势
// 	http://ichart.yahoo.com/table.csv?s=%d.SZ&a=01&b=01&c=2005&d=01&e=18&f=2017&g=d
//	000009中国宝安：改变趋势（现在是下跌）
//	002325洪涛股份：不改变趋势
//	002329黄氏集团：不改变趋势
//	002299圣农发展：改变趋势（现在是下跌）
//	http://ichart.yahoo.com/table.csv?s=%06d.SZ&a=01&b=01&c=2005&d=01&e=18&f=2017&g=d
	
	public byte[] readInputStream(InputStream inputStream) throws IOException {  
		byte[] buffer = new byte[1024];  
		int len = 0;  
		ByteArrayOutputStream bos = new ByteArrayOutputStream();  
		while((len = inputStream.read(buffer)) != -1) {  
			bos.write(buffer, 0, len);  
		}  
		bos.close();  
		return bos.toByteArray();  
	}  

	public void main() throws Exception {
		String root = new File(System.getProperty("user.dir")).getPath();
		String localPath = Paths.get(root, "target", "data").toString();
		String urlStr = "http://ichart.yahoo.com/table.csv?s=300017.SZ&a=01&b=01&c=2012&d=01&e=19&f=2017&g=d";
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setConnectTimeout(30*1000);
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		InputStream inputStream = conn.getInputStream();  
		byte[] getData = readInputStream(inputStream);    

		//文件保存位置
		File saveDir = new File(localPath);
		if(!saveDir.exists()){
			saveDir.mkdir();
		}
		File file = new File(saveDir+File.separator+"stock.csv");    
		FileOutputStream fos = new FileOutputStream(file);     
		fos.write(getData); 
		if(fos!=null){
			fos.close();  
		}
		if(inputStream!=null){
			inputStream.close();
		}
		System.out.println("info:"+url+" download success"); 
		
		StockData td = new StockData(saveDir+File.separator+"stock.csv");
		DoubleSeries nSeries = td.getPropertySeries("Open");
		nSeries.sort();
		nSeries.normly();
		double minValue = nSeries.min();
		double maxValue = nSeries.max();
		OnlineAnormalyDetectionAPI detection = new OnlineAnormalyDetectionAPI(minValue, maxValue);
		
		DoubleSeries anormalys = detection.detectorSeries(nSeries, 0.2);
//		String resultDir = Util.writeCsv(new MultipleDoubleSeries(nSeries, anormalys)).toString();
//		System.out.println("anormaly result dir is : " + resultDir);
		Plot.plot("Stock Dataset", anormalys, nSeries);
	}
	
	public static void main(String[] args) throws Exception {
		new OnlineAnormalyExample2().main();
	}
}
