package xworker.lang;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.cache.ThingEntry;

public class BeanFactory {
	private static String KEY = "__BeanFactory__";
	
	ThingEntry thingEntry;
	Thing thing;
	ActionContext actionContext;
	Map<String, FactoryBean> beans = null;
	
	public BeanFactory(Thing thing) {
		this.thing = thing;
		thingEntry = new ThingEntry(thing);
		this.actionContext = new ActionContext();
		
		init();
	}
	
	private void init() {
		synchronized(this) {
			if(beans == null || thingEntry.isChanged()) {
				if(beans == null) {
					beans = new HashMap<String, FactoryBean>();
				}
				
				Thing thing = thingEntry.getThing();
				if(thing == null) {
					return;
				}
				
				for(Thing child : thing.getChilds()) {
					String name = child.getMetadata().getName();
					FactoryBean fb = beans.get(name);
					if(fb == null || fb.getThing() != child) {
						fb = new FactoryBean(child);
						beans.put(name, fb);
					}
				}
			}
		}
	}
	
	/**
	 * 查询是否定义了一个变量。
	 * 
	 * @param name
	 * @return
	 */
	public boolean exists(String name) {
		init();
		
		return beans.containsKey(name);
	}
	
	public Thing getThing() {
		return thing;
	}
	
	public ActionContext getActionContext() {
		return actionContext;
	}
	
	public static BeanFactory getBeanFactory(Thing thing) {
		BeanFactory beanFactory = thing.getStaticData(KEY);
		if(beanFactory == null) {
			beanFactory = new BeanFactory(thing);
			thing.setStaticData(KEY, beanFactory);
		}
		
		return beanFactory;
	}
	
	public static class FactoryBean {
		ThingEntry thingEntry = null;
		Object object = null;
		boolean removed = false;
		
		public FactoryBean(Thing thing) {
			thingEntry = new ThingEntry(thing);
		}
		
		public Thing getThing() {
			return thingEntry.getThing();
		}
		
		public Object create(ActionContext actionContext) {
			if(removed) {
				return null;
			}
			
			if(object == null || thingEntry.isChanged()) {
				Thing thing = thingEntry.getThing();
				if(thing != null) {
					object = thing.doAction("create", actionContext);
				}else {
					removed = true;
				}
			}
			
			return object;
		}
		
		public void close(ActionContext actionContext) {
			Thing thing = thingEntry.getThing();
			if(object != null && thing != null) {
				thing.doAction("close", actionContext, "object", object);
			}
		}
	}
}
