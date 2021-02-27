package xworker.lang.updator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.lang.executor.Executor;
import xworker.task.TaskManager;

public class ObjectUpdator implements Runnable{
	private static final String TAG = ObjectUpdator.class.getName();
	
	Thing self;
	ActionContext actionContext;
	AttributeSetter attributeSetter;
	ScheduledFuture<?> taskFuture;
	Map<String, Object> dataMap = null;
	
	public ObjectUpdator(Thing self, ActionContext actionContext) {
		this.self = self;
		this.actionContext = actionContext;
		
		this.attributeSetter = self.doAction("getAttributeSetter", actionContext);
		if(this.attributeSetter == null) {
			this.attributeSetter = new OgnlAttributeSetter();
		}
	}
	
	public void setData(String key, Object value) {
		if(dataMap == null) {
			dataMap = new HashMap<String, Object>();
		}
		
		dataMap.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getData(String key) {
		if(dataMap == null) {
			return null;
		}
		
		return (T) dataMap.get(key);
	}
	
	@SuppressWarnings("rawtypes")
	public void run() {
		if(UtilData.isTrue(self.doAction("isStoped", actionContext, "updator", this, "attributeSetter", attributeSetter))) {
			//已经停止了
			if(taskFuture != null) {
				taskFuture.cancel(true);
			}
			
			return;
		}
		
		Object object = self.doAction("getObject", actionContext, "updator", this, "attributeSetter", attributeSetter);
		if(object == null) {
			return;
		}
		
		try {
			for(Thing valueActions : self.getChilds("AttributeValues")) {
				for(Thing action : valueActions.getChilds()) {
					String name = action.getMetadata().getName();
					Object value = action.getAction().run(actionContext, "object", object, "updator", this, "attributeSetter", attributeSetter);
					
					attributeSetter.setAttribute(object, name, value);
				}
			}
			
			for(Thing valueActions : self.getChilds("AttributeMaps")) {
				for(Thing action : valueActions.getChilds()) {
					Object obj = action.getAction().run(actionContext, "object", object, "updator", this, "attributeSetter", attributeSetter);
					if(obj instanceof Map) {
						Map map = (Map) obj;
						for(Object key : map.keySet()) {
							if(key instanceof String) {
								String name = (String) key;
								Object value = map.get(key);
								attributeSetter.setAttribute(object, name, value);
							}
						}
					}
				}
			}
		}catch(Exception e) {
			Executor.error(TAG, "Set object attributes error, path=" + self.getMetadata().getPath(), e);
		}
	}
	
	public void start()  {
		if(UtilData.isTrue(self.doAction("isSchedule", actionContext))) {
			long interval = self.doAction("getInterval", actionContext);
			long initialDelay = self.doAction("getInitialDelay", actionContext);
			taskFuture = TaskManager.getScheduledExecutorService().scheduleWithFixedDelay(this, initialDelay, interval, TimeUnit.MILLISECONDS);			
		}else {
			run();
		}
	}
	
	public static Object getAttributeSetter(ActionContext actionContext) {
		return new OgnlAttributeSetter();
	}
	
	public static boolean isStoped(ActionContext actionContext) {
		return false;
	}
	
	public static void execute(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		ObjectUpdator updator = new ObjectUpdator(self, actionContext);
		updator.start();
	}
}
