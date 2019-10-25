package xworker.swt.app.editors;

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.util.ActionContainer;

public class ClassSourceViewer {
	public static void setContent(ActionContext actionContext) {
		ActionContainer sourceViewer = actionContext.getObject("sourceViewer");
		Map<String, Object> params = actionContext.getObject("params");
		
		Object className = params.get("className");

		if(className instanceof Class){
		    className = ((Class<?>) className).getName();
		}

		//编辑器
		actionContext.g().put("className", className);
		sourceViewer.doAction("setClassName", actionContext, "className", className);

		//概要
		if(actionContext.get("classViewer") != null){
			ActionContainer classViewer = actionContext.getObject("classViewer");
		    classViewer.doAction("setClass", actionContext, "cls", className);
		}
	}
	
	public static String getSimpleTitle(ActionContext actionContext) {
		if(actionContext.get("className") != null){
			String className = actionContext.getObject("className");
		    int index = className.lastIndexOf(".");
		    if(index != -1){
		        return className.substring(index + 1, className.length()) + ".class";
		    }else{
		        return className + ".class";
		    }
		}else{
		    return "No class";
		}
	}
	
	public static String getTitle(ActionContext actionContext) {
		if(actionContext.get("className") != null){
			String className = actionContext.getObject("className");
		    return className + ".class";
		}else{
		    return "No class";
		}
	}
	
	public static boolean isSameContent(ActionContext actionContext) {
		if(actionContext.get("className") == null){
		    return false;
		}

		String className = actionContext.getObject("className");
		Map<String, Object> params = actionContext.getObject("params");
		Object cName = (String) params.get("className");

		if(cName instanceof Class){
		    cName = ((Class<?>) cName).getName();
		}

		return className.equals(cName);
	}
}
