package xworker.project.index;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.Category;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;

/**
 * 索引可能会消耗一定的时间，所以放在后台运行。
 * 
 * @author Administrator
 * @deprecated
 *
 */
public class ProjectThingIndexThread {
	private static final String TAG = ProjectThingIndexThread.class.getName();
	
	/** 用于锁定的对象 */
	private static Object lockObj = new Object();
	
	private List<Runnable> runList = new ArrayList<Runnable>();
	
	private static ProjectThingIndexThread instance = new ProjectThingIndexThread();
	
	private Thread thread = new Thread(new Runnable(){
		public void run(){
			while(true){
				try{
					synchronized(lockObj){
						lockObj.wait();
					}
					
					while(runList.size() > 0){
						Runnable r = runList.remove(0);
						if(r != null) {
							r.run();
						}
					}
				}catch(Exception e){
					Executor.error(TAG, "Index porject thing error", e);
				}
			}
		}
	}, "ProjectThingIndexTread");
	
	private ProjectThingIndexThread(){		
		thread.start();
	}
	
	public static void updateIndex(Thing thing){
		instance.runList.add(new UpdateTask(thing));
		synchronized(lockObj){
			lockObj.notify();
		}
	}
	
	public static void removeIndex(Thing thing){
		instance.runList.add(new RemoveCacheTask(thing));
		synchronized(lockObj){
			lockObj.notify();
		}
	}
	
	public static void startIndex(Category category){
		instance.runList.add(new IndexAllTask(category));
		synchronized(lockObj){
			lockObj.notify();
		}
	}
	
	public static class UpdateTask implements Runnable{
		Thing thing;
		
		public UpdateTask(Thing thing){
			this.thing = thing;
		}
		
		public void run(){
			ProjectThingIndexManager.indexThing(thing);
		}
	}
	
	public static class RemoveCacheTask implements Runnable{
		Thing thing;
		
		public RemoveCacheTask(Thing thing){
			this.thing = thing;
		}
		
		public void run(){
			ProjectThingIndexManager.removeIndex(thing);
		}
	}
	
	public static class IndexAllTask implements Runnable{
		Category category;
		
		public IndexAllTask(Category category){
			this.category = category;
		}
		
		public void indexCategory(Category cat){
			for(Thing thing : cat.getThings()){
				ProjectThingIndexManager.indexThing(thing);
			}
			
			for(Category childCategory : cat.getCategorys()){
				indexCategory(childCategory);
			}
		}
		
		public void run(){
			indexCategory(category);
		}
	}
}
