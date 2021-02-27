package xworker.task;

import java.util.concurrent.ThreadFactory;

public class NameThreadFactory implements ThreadFactory{
	private static boolean deamon = true;
	public static void setDeamon(boolean deamon){
		NameThreadFactory.deamon = deamon;
	}
	
	String name;
	int count = 0;
	
	public NameThreadFactory(String name){
		this.name = name;
	}
	
	@Override
	public Thread newThread(Runnable r) {
		count++;
		Thread th = new Thread(r, name + "-" + count);
		if(deamon){
			th.setDaemon(true);
		}
		
		return th;
	}

}
