package com.realsight.brain.timeseries.lib.util.data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.realsight.brain.timeseries.lib.csv.CsvReader;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.StringSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries;

@SuppressWarnings("deprecation")
public class StockData {
	
	private Character delimiter = null;
	private Charset charset = null;
	private String filepath = null;
	
	
	public StockData(){
		String root = new File(System.getProperty("user.dir")).getPath();
		this.delimiter = ',';
		this.charset = Charset.forName("ISO-8859-1");
		this.filepath = root + File.separator + "target" + 
							File.separator + "stockid" + 
							File.separator + "ids.csv";
	}
	
	public StringSeries stockidset(){
		StringSeries res = new StringSeries("stock id");
		try {
			CsvReader cr = new CsvReader(filepath, delimiter, charset);
			cr.readHeaders();
			if(cr.getIndex("id") == -1)
				throw new IOException("File not exists id.");
			while(cr.readRecord()){
				String value = cr.get("id").substring(2);
				res.add(new TimeSeries.Entry<String>(value, -1L));
			}
			cr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;
	}
	
	public DoubleSeries downLoadFromUrl(String url) throws IOException{
		
		DoubleSeries res = new DoubleSeries(url);
		CloseableHttpClient client =  HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(url);
		CloseableHttpResponse resp = client.execute(get);
		if (resp.getStatusLine().getStatusCode() != 200)
			return res;
		HttpEntity entity = resp.getEntity();
		String str = EntityUtils.toString(entity);
		Scanner sin = new Scanner(str);
//		HttpEntity entity = resp.getEntity();
		entity.getContent();
		while(sin.hasNext()) {
			String[] req = sin.nextLine().split(",");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (req[0].contains("/")){
				sdf = new SimpleDateFormat("yyyy/MM/dd");
			} else if (req[0].trim().equals("Date")) {
				continue;
			}
			try {
				Date date = sdf.parse(req[0].trim());
				Long timestamp = date.getTime();
				Double value = Double.parseDouble(req[6].trim());
				res.add(new TimeSeries.Entry<Double>(value, timestamp));
			} catch (ParseException e) {
				System.out.println(req[0].trim());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sin.close();
		res.sort();
		res.reverse();
		client.close();
		System.out.println(url+" download success"); 
		return res;
	}

	public DoubleSeries history_data(String id) throws IOException {
		String url = "http://ichart.yahoo.com/table.csv?s=";
		if (id.equals("000001")){
			return downLoadFromUrl(url + "000001.ss");
		}
		if (id.charAt(0) == '6') {
			return downLoadFromUrl(url + id + ".ss");
		}
		return downLoadFromUrl(url + id + ".sz");
	}
	
	public static void main(String[] args) throws IOException {
		StringSeries res = new StockData().stockidset();
		for (int i = 0; i < res.size(); i++) {
			System.out.println(res.get(i).getItem());
			new StockData().history_data(res.get(i).getItem());
		}
	}
}
