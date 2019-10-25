package xworker.app.userflow;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xworker.dataObject.DataObject;

public class UserFlowManager {
	private static Logger logger = LoggerFactory.getLogger(UserFlowManager.class);	
	private static List<UserFlowListener> listeners = new ArrayList<UserFlowListener>();
	
	public static void addListener(UserFlowListener listener){
		if(!listeners.contains(listener)){
			listeners.add(listener);
		}
	}
	
	public static void removeListener(UserFlowListener listener){
		listeners.remove(listener);
	}
	
	public static void fireTaskStarted(DataObject task){
		for(UserFlowListener l :listeners){
			try{
				l.started(task);
			}catch(Exception e){
				logger.error("handle fireTskStarted error, task=" + task, e);
			}
		}
	}
	
	public static void fireTaskFinished(DataObject task){
		for(UserFlowListener l :listeners){
			try{
				l.finished(task);
			}catch(Exception e){
				logger.error("handle fireTaskFinished error, task=" + task, e);
			}
		}
	}
}
