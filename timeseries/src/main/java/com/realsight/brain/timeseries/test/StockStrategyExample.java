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
import com.realsight.brain.timeseries.api.StockStrategyAPI;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.util.data.StockData;
import com.realsight.brain.timeseries.lib.util.plot.Plot;

/**
 * @author Sun Muxin
 * 
 */ 
public class StockStrategyExample {
	/**
	 * @param args
	 * @throws Exception 
	 */
//	600094大明城: 还会继续下跌
//	600503华丽家族: 震荡走势
//	600028中国石化： 上涨（明天上涨的顶点在6.56-695）
// 	http://ichart.yahoo.com/table.csv?s=%d.SS&a=01&b=01&c=2005&d=01&e=18&f=2017&g=d
//	300017网宿科技：震荡走势(45.87-51.2)
//	300133华策影视：出现警告点
//	300315掌趣科技：。。。。。。
// 	http://ichart.yahoo.com/table.csv?s=%d.SZ&a=01&b=01&c=2005&d=01&e=18&f=2017&g=d
//	000009中国宝安：没有警告（短期可能会上涨，长期下跌）
//	002325洪涛股份：出现警告点
//	002329黄氏集团：继续下跌
//	002299圣农发展：改变趋势（现在是下跌）
//	http://ichart.yahoo.com/table.csv?s=%06d.SZ&a=01&b=01&c=2005&d=01&e=18&f=2017&g=d
	
	public void downLoadFromUrl(String urlStr,String fileName,String savePath) throws IOException{
		URL url = new URL(urlStr);  
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
        //设置超时间为10秒
		conn.setConnectTimeout(10*1000);
		//防止屏蔽程序抓取而返回403错误
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

		//得到输入流
		InputStream inputStream = conn.getInputStream();  
		//获取自己数组
		byte[] getData = readInputStream(inputStream);    

		//文件保存位置
		File saveDir = new File(savePath);
		if(!saveDir.exists()){
			saveDir.mkdir();
		}
		File file = new File(saveDir+File.separator+fileName);    
		FileOutputStream fos = new FileOutputStream(file);     
		fos.write(getData); 
		if(fos!=null){
			fos.close();
		}
		if(inputStream!=null){
			inputStream.close();
		}
		conn.disconnect();
		System.out.println("info:"+url+" download success"); 

	}

	/**
	 * 从输入流中获取字节数组
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
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
		String urlStr = "http://ichart.yahoo.com/table.csv?s=002329.SZ&a=01&b=01&c=2005&d=01&e=25&f=2017&g=d";
//		downLoadFromUrl(urlStr, "stock.csv", localPath);
		
		StockData td = new StockData(localPath+File.separator+"stock.csv");
		DoubleSeries nSeries = td.getPropertySeries("Open");
		nSeries.sort();
//		nSeries.normly();
		double minValue = nSeries.min();
		double maxValue = nSeries.max();
		StockStrategyAPI Strategy = new StockStrategyAPI(minValue, maxValue);
		
		Strategy.run(nSeries);
//		String resultDir = Util.writeCsv(new MultipleDoubleSeries(nSeries, anormalys)).toString();
//		System.out.println("anormaly result dir is : " + resultDir);
//		Plot.plot("Stock Dataset", nSeries, predicts.getColumn("anormaly"), predicts.getColumn("predict"));
	}
	
	public static void main(String[] args) throws Exception {
		new StockStrategyExample().main();
	}
}
