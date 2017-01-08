package apm.webstress;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Client {
	private String startUrl;
	private String hostName;
	private int threadCount;
	private ExecutorService threadPool;
	private LinkedBlockingQueue<String> urlqueue;
	private static final int MAX_SIZE = 100000; 
	public Client(String url,String hostName,int threadCount){
		this.startUrl = url;
		this.threadCount = threadCount;
		this.hostName = hostName;
	}
	
	public void start(){
		if(threadCount <= 0) return;
		threadPool = Executors.newFixedThreadPool(threadCount);
		
		urlqueue = new LinkedBlockingQueue<String>(MAX_SIZE);
		urlqueue.add(startUrl);
		for(int i=0;i<threadCount;i++){
			threadPool.execute(new Work());
		}
	}
	
	public void stopNow(){
		threadPool.shutdownNow();
	}
	
	class Work implements Runnable{
		
		
		
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				String url = urlqueue.poll();
				if(url!=null){
					try {
						//System.out.println("url: "+url);
						Thread.sleep(1000);//1s
						URI uri = URI.create(url);
						Document doc = Jsoup.connect(uri.toASCIIString()).get();
						Elements elements = doc.select("a");
						for(Element element:elements){
							String href = element.attr("abs:href");
							if(href!=null && href.startsWith(hostName)){
								try{
								if(urlqueue.size()<MAX_SIZE)
									urlqueue.add(href);
								}catch(Exception e){
									
								}
							}
						}
					}catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
	}
}
