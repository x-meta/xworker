package xworker.lang.actions.utils;

import java.io.File;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.util.UtilAction;

public class SystemUtilsActions {
	public static File getJavaHome(ActionContext actionContext){
		return SystemUtils.getJavaHome();
	}
	
	public static File getJavaIoTmpDir(ActionContext actionContext){
		return SystemUtils.getJavaIoTmpDir();
	}
	
	public static File getUserDir(ActionContext actionContext){
		return SystemUtils.getUserDir();
	}
	
	public static File getUserHome(ActionContext actionContext){
		return SystemUtils.getUserHome();
	}
	
	public static boolean isJavaAwtHeadless(ActionContext actionContext){
		return SystemUtils.isJavaAwtHeadless();
	}
	
	public static boolean isJavaVersionAtLeast(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String version = (String) self.doAction("getVersion", actionContext);
		return SystemUtils.isJavaVersionAtLeast(JavaVersion.valueOf(version));
	}
	
	public static Object isOs(ActionContext actionContext) throws IllegalAccessException{
		Thing self = actionContext.getObject("self");
		String os = (String) self.doAction("getOs", actionContext);
		return FieldUtils.readStaticField(SystemUtils.class, os);
	}
	
	public static Object switchOs(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Object result = null;
		boolean executed = false;
		for(Thing child : self.getChilds("OS")) {
			String name = child.getMetadata().getName();
			if("OTHER".equals(name)) {
				continue;
			}
			
			try {
				if(UtilData.isTrue(FieldUtils.readStaticField(SystemUtils.class, "IS_OS_" + name))) {
					executed = true;
					result = UtilAction.runChildActions(child.getChilds(), actionContext);					
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		if(!executed) {
			for(Thing child : self.getChilds("OS")) {
				String name = child.getMetadata().getName();
				if("OTHER".equals(name)) {
					result = UtilAction.runChildActions(child.getChilds(), actionContext);	
					break;
				}
			}
		}
		
		return result;
	}
	
	public static Object getSystemProperty(ActionContext actionContext) throws IllegalAccessException{
		Thing self = actionContext.getObject("self");
		String name = (String) self.doAction("getPropertyName", actionContext);
		return FieldUtils.readStaticField(SystemUtils.class, name);
	}
}
