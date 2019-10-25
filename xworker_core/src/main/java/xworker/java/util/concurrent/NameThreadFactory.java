package xworker.java.util.concurrent;

import java.util.concurrent.ThreadFactory;

public class NameThreadFactory implements ThreadFactory{
	String name;
	int count = 0;
	
	public NameThreadFactory(String name){
		this.name = name;
	}
	
	@Override
	public Thread newThread(Runnable r) {
		count++;
		return new Thread(r, name + "-" + count);
	}

}
