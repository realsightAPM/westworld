package apm.webstress;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class Stress {
	static final int  NORMAL = 100;
	static final int MAX = 600;
	public static  Queue<Client> queue = new LinkedList<Client>();
	
	public static AtomicLong timeSum = new AtomicLong(0);
	
	public static AtomicLong timesSum = new AtomicLong(0);
	private Client getClient(){
		return new Client("http://121.42.185.24:8080/","http://121.42.185.24:8080/",1);
	}
	
	 public void change() throws Exception{
	    	Random random = new Random();
	    	boolean flag = random.nextBoolean();
	    	int count = random.nextInt(5);
	    	
	    	if((queue.isEmpty())||(flag && queue.size()<NORMAL)){
	    		for(int i=0;i<count;i++){
	    			queue.add(getClient());
	    		}
	    	}
	    	else{
	    		for(int i=0;i<count && !queue.isEmpty();i++){
	    			Client client = queue.poll();
	    			client.stopNow();
	    		}
	    	}
	 }
	 
	 public  void add() throws Exception{
			for(int i=0;i<10&&queue.size()<MAX;i++){
				queue.add(getClient());
			}
		}
		
		public  void remove(){
			for(int i=0;i<10&&!queue.isEmpty();i++){
				Client client = queue.poll();
				client.stopNow();
			}
		}
	 
	 public void run() throws Exception{
		 for(int i=0;i<10;i++)
			{
				queue.add(getClient());
			}
		 
		 for(int i=0;i<3*60;i++){
			
				Thread.sleep(60*1000);//3小时
				change();
			}
			
			for(int i=0;i<4*60;i++){
				Thread.sleep(60*1000);//3小时
				add();
			}
			
			for(int i=0;i<3*60;i++){
				Thread.sleep(60*1000);//2小时
				remove();
			}
	 }
	
}
