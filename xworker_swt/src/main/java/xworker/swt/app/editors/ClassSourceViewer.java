package xworker.swt.app.editors;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import xworker.swt.app.IEditor;
import xworker.workbench.EditorParams;

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

	public static void onOutlineCreated(ActionContext actionContext){
		//概要
		if(actionContext.get("classViewer") != null){
			ActionContainer classViewer = actionContext.getObject("classViewer");
			classViewer.doAction("setClass", actionContext, "cls", actionContext.get("className"));
		}
	}

	public static EditorParams<Class<?>> createParams(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Object content = actionContext.getObject("content");
		Class<?> cls = null;
		if(content instanceof  String){
			try{
				cls = Class.forName((String) content);
			}catch(Exception ignore){
			}
		}else if(content instanceof Class){
			cls = (Class<?>) content;
		}

		if(cls != null) {
			final Class<?> c = cls;
			return new EditorParams<Class<?>>(self, "DemoWeb:" + cls.getName(), cls) {
				@Override
				public Map<String, Object> getParams() {
					Map<String, Object> params = new HashMap<>();
					params.put("className", c.getName());

					return params;
				}
			};
		}

		return null;
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
	
	public static Map<String, Object> createDataParams(ActionContext actionContext){
		Object data = actionContext.get("data");
		if(data instanceof Class<?>) {
			Class<?> cls = (Class<?>) data;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("className", data);
			params.put(IEditor.EDITOR_THING, World.getInstance().getThing("xworker.swt.app.editors.ClassSourceViewer"));
			params.put(IEditor.EDITOR_ID, "Class:" + cls.getName());
			return params;
		}
		
		return null;
	}
}
