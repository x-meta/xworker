package xworker.notification;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;

import xworker.task.TaskManager;

/**
 * 系统通知管理器。
 * 
 * @author zyx
 *
 */
public class NotificationManager{
	private static Logger logger = LoggerFactory.getLogger(NotificationManager.class);
	private static List<NotificationManagerListener> listeners = new CopyOnWriteArrayList<NotificationManagerListener>();
	
	private static List<Notification> events = new CopyOnWriteArrayList<Notification>();
	static{
		load();
		
		//添加到定时任务中
		TaskManager.getScheduledExecutorService().scheduleAtFixedRate(new Runnable(){
			public void run(){
				try{
					//检查事件是否过期
					synchronized(events) {
						for(Notification event : events){
							if(event.isExpired()){
								removeNotification(event);
							}else if(event.isEndWait()) {
								event.finishWait();
							}
						}
					}
				}catch(Exception e){
					logger.error("check event error", e);
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}
	
	/**
	 * 返回未阅读的事件数量。
	 * 
	 * @return
	 */
	public static int getUnViewedCount(){
		int count = 0;
		for(Notification event : events){
			if(event.isViewed() == false){
				count++;
			}
		}
		
		return count;
	}
	
	public static void removeNotification(String id) {
		for(int i=0; i<events.size(); i++) {
			Notification event = events.get(i);
			
			if(event.getMessageId().equals(id)) {
				removeNotification(event);
				break;
			}
		}		
	}
	
	public static void removeNotification(Notification event){
		synchronized(events) {
			//首先通知事件自己结束
			event.finish();
			
			//如果是存储的，移除
			if(event.isPersistent()){
				event.thing.remove();
			}
			
			//从事件列表中移除
			events.remove(event);
			
			//添加到事件列表
			for(NotificationManagerListener listener : listeners){			
				try {
					listener.removed(event);
				}catch(Exception e) {
					logger.error("Fire removed event error", e);
				}
			}
		}
	}
	
	public static void fireUpdateEvent(Notification event){
		synchronized(events) {
			for(NotificationManagerListener listener : listeners){
				try {
					listener.updated(event);
				}catch(Exception e) {
					logger.error("Fire updated event error", e);
				}
			}
		}
	}
		
	private static void load(){
		synchronized(events) {
			ThingManager thingManager = World.getInstance().getThingManager("_local");
			Iterator<Thing> iter = thingManager.iterator("_local.xworker.notifications.", false);
			while(iter.hasNext()){
				Thing thing = iter.next();
				Notification event = new Notification(thing, new ActionContext(), thing.getLong("createTime"));
				events.add(event);
			}
			
			Collections.sort(events);
		}
	}
	
	public static void addListener(NotificationManagerListener listener){
		listeners.add(listener);
	}
	
	public static void removeListener(NotificationManagerListener listener){
		listeners.remove(listener);
	}
	
	public static Notification send(Thing thing, ActionContext actionContext){		
		Notification event = null;
		String id = thing.doAction("getMessageId", actionContext);
		synchronized(events) {
			boolean have = false;
			for(Notification e : events) {
				if(e.isSameMessage(id)) {
					e.init(thing, actionContext);
					event = e;
					have = true;
					break;
				}
			}
			
			if(!have) {
				event = new Notification(thing, actionContext);
				events.add(0, event);
				
				for(NotificationManagerListener listener : listeners){
					try {
						listener.added(event);
					}catch(Exception e) {
						logger.error("Fire add event error", e);
					}
				}
			}else {
				for(NotificationManagerListener listener : listeners){
					try {
						listener.updated(event);
					}catch(Exception e) {
						logger.error("Fire add event error", e);
					}
				}
			}
			
		}
		
		//如果是同步的，那么等待处理结果，2019-05-24暂时去掉该代码
		/*
		Shell ideShell = (Shell) XWorkerUtils.getIDEShell();
		if(ideShell == null || ideShell.getDisplay().getThread() != Thread.currentThread()) {
			if(event.isSync()) {
				event.lock();
			}
			//System.out.println("waked");
		}*/
		return event;
		
	}
		
	public static List<Notification> getNotifications(){
		return events;
	}
}
