package apm.webstress;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Stress {
	static final int  NORMAL = 100;
	static final int MAX = 600;
	public static  Queue<Client> queue = new LinkedList<Client>();
	
	private Client getClient(){
		return new Client("http://202.118.67.200:18080/","http://202.118.67.200:18080/",1);
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
		 
		 for(int i=0;i<4*60;i++){
				Thread.sleep(60*1000);//4小时
				change();
			}
			
			for(int i=0;i<4*60;i++){
				Thread.sleep(60*1000);//4小时
				add();
			}
			
			for(int i=0;i<3*60;i++){
				Thread.sleep(60*1000);//3小时
				remove();
			}
	 }
	
}
