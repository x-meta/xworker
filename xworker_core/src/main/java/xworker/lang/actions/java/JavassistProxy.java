package xworker.lang.actions.java;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.cache.ThingEntry;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

public class JavassistProxy implements MethodHandler{
	Object object;
	ThingEntry thingEntry;
	Thing thing;
	ActionContext actionContext;
	/** 重写的方法列表 */
	List<String> actionNames = null;
	
	public JavassistProxy(Thing thing, ActionContext actionContext) throws Throwable{
		this.thing = thing;
		thingEntry = new ThingEntry(thing);
		
		this.actionContext = actionContext;
		ProxyFactory factory = new ProxyFactory();
		String superclass = thing.doAction("getSuperclass", actionContext);
		String interfaces = thing.doAction("getInterfaces", actionContext);
		if(superclass != null && !"".equals(superclass)) {
			factory.setSuperclass(Class.forName(superclass));
		}
		if(interfaces != null) {
			String[] ifaces =  interfaces.split("[,]");
			Class<?>[] ifaceClasses = new Class[ifaces.length];
			for(int i=0; i<ifaces.length; i++) {
				ifaceClasses[i] = Class.forName(ifaces[i]);
			}
			factory.setInterfaces(ifaceClasses);
		}
		
		factory.setFilter(new MethodFilter() {
			@Override
			public boolean isHandled(java.lang.reflect.Method m) {
				String name = m.getName();
				for(String actionName : getActionNames()) {
					if(name.equals(actionName)) {
						return true;
					}
				}
				
				return false;
			}
		 });
		Object[] args = thing.doAction("getArgs", actionContext);
		Class<?>[] paramTypes = thing.doAction("getParamTypes", actionContext);
		object = factory.create(paramTypes, args, this);
	}
	
	public List<String> getActionNames(){
		if(thingEntry.isChanged() || actionNames == null) {
			actionNames = new ArrayList<String>();
			for(Thing overrides : thingEntry.getThing().getAllChilds("Overrides")) {
				for(Thing method : overrides.getChilds()) {
					actionNames.add(method.getMetadata().getName());
				}
			}
		}
		
		return actionNames;
	}

	@Override
	public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
		return thingEntry.getThing().doAction(thisMethod.getName(), actionContext, "args", args, "object", object, "proxy", self);
	}
	
	public static Object create(ActionContext actionContext) throws Throwable {
		Thing self = actionContext.getObject("self");
		
		JavassistProxy proxy = new JavassistProxy(self, actionContext);
		return proxy.object;
	}
}
