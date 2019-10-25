package xworker.lang.util;

import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.task.DelayTask;
import xworker.util.UtilData;

public class Trigger {
	private static final String TAG = "Trigger";
	
	public static void run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//需要同步
		List<Map<String, Object>> requests = null;
		synchronized(self) {			
			requests = self.getData("requests");
			if(requests == null) {
				requests = new java.util.concurrent.CopyOnWriteArrayList<Map<String, Object>>();
				self.setData("requests", requests);
			}
		}

			
		//是否保存请求
		if(UtilData.isTrue(self.doAction("isSaveRequest", actionContext))) {
			Map<String, Object> request = self.doAction("getRequestValues", actionContext);
			requests.add(request);
		}
		
		if(UtilData.isTrue(self.doAction("isActive", actionContext, "requests", requests))) {
			try {
				self.doAction("doAction", actionContext, "requests", requests);
			}finally {
				requests.clear();
			}
		}
	}
	
	public static boolean isActive(ActionContext actionContext) {
		final Thing self = actionContext.getObject("self");
		
		List<Map<String, Object>> requests = actionContext.getObject("requests");
		String type = self.doAction("getType", actionContext);
		int value = self.doAction("getValue", actionContext);
		
		if("delay".equals(type)) {
			if(value <= 0) {
				return true; //立即执行
			}
			
			DelayTask task = self.getData("delayTask");
			if(task == null) {
				task = new DelayTask(value) {
					Thing thing = self;
					
					public void run() {
						try {
							List<Map<String, Object>> requests = self.getData("requests");
							try {
								self.doAction("doAction", new ActionContext(), "requests", requests);
							}finally {
								requests.clear();
							}
						}catch(Exception e) {
							Executor.error(TAG, "doAction error, thing=" + thing.getMetadata().getPath(), e);
						}
					}
				};
				self.setData("delayTask", task);
			}
			task.doTask();
			
			return false;
		}else if("count".equals(type)) {
			if(requests.size() >= value) {
				return true;
			}else {
				return false;
			}
		}else {
			//其它值，返回true
			return true;
		}
	}
}
