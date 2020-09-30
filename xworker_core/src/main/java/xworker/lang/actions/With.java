package xworker.lang.actions;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.util.UtilAction;

public class With {
	public static Object run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		List<Object> resList = new ArrayList<Object>();
		try {
			Bindings bindings = actionContext.peek();
			bindings.setVarScopeFlag();
			for(Thing resources : self.getChilds("Resources")) {
				for(Thing resource : resources.getChilds()) {
					Object res = resource.getAction().run(actionContext);
					resList.add(res);
					bindings.put(resource.getMetadata().getName(), res);
				}
			}
			
			Thing doac = self.getThing("Do@0");
			if(doac != null) {
				return UtilAction.runChildActions(doac.getChilds(), actionContext);
			}else {
				return null;
			}
		}finally {
			for(int i=resList.size() -1 ; i >= 0; i--) {
				Object res = resList.get(i);
				try {
					close(res, actionContext);
				}catch(Exception e) {					
				}
			}
		}		
	}
	
	public static void close(Object obj, ActionContext actionContext) throws Exception {
		if(obj == null) {
			return;
		}
		
		if(obj instanceof AutoCloseable) {
			AutoCloseable closeable = (AutoCloseable) obj;
			closeable.close();
		}else if(obj instanceof Closeable){
			Closeable closeable = (Closeable) obj;
			closeable.close();
		}else if(obj instanceof InputStream) {
			((InputStream) obj).close();
		}else if(obj instanceof OutputStream) {
			((OutputStream) obj).close();
		}else if(obj instanceof Connection) {
			((Connection) obj).close();
		}else if(obj instanceof ActionContainer){
			((ActionContainer) obj).doAction("close", actionContext);
		}else if(obj instanceof Thing){
			((Thing) obj).doAction("close", actionContext);
		}else {
			Method closeMethod = obj.getClass().getDeclaredMethod("close", new Class<?>[0]);
			if(closeMethod != null) {
				closeMethod.invoke(obj, new Object[0]);
			}
					
		}
	}
}
