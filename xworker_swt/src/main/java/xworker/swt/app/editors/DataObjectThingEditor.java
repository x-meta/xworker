package xworker.swt.app.editors;

import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilMap;

import xworker.swt.app.IEditor;
import xworker.swt.xwidgets.CodeViewer;
import xworker.util.XWorkerUtils;

public class DataObjectThingEditor {
	public static void setContent(ActionContext actionContext) {
		Map<String, Object> params = actionContext.getObject("params");

		World world = World.getInstance();
		Object dataObject = params.get("dataObject");

		if(dataObject instanceof String){
		    dataObject = world.getThing((String) dataObject);
		}

		//设置数据对象
		actionContext.g().put("dataObject", dataObject);
		Thing dataObjectEditor = actionContext.getObject("dataObjectEditor");
		dataObjectEditor.doAction("setDataObject", actionContext, "dataObject", dataObject);
		
		ActionContainer thingEditor = actionContext.getObject("thingEditor");
		thingEditor.doAction("setThing", actionContext, "thing", dataObject);
		
		String code = ((Thing) dataObject).doAction("toJavaEntityCode", actionContext);
		CodeViewer codeViewer = actionContext.getObject("codeViewer");
		codeViewer.setCode("java", "java", code);

		//设置outline，如果存在
		if(actionContext.get("outlineBrowser") != null){
		    String url = XWorkerUtils.getThingDescUrl((Thing) dataObject);
		    Browser outlineBrowser = actionContext.getObject("outlineBrowser");
		    outlineBrowser.setUrl(url);
		}
	}
	
	public static boolean isSameContent(ActionContext actionContext) {
		Map<String, Object> params = actionContext.getObject("params");

		World world = World.getInstance();
		Object dataObject = params.get("dataObject");

		if(dataObject instanceof String){
		    dataObject = world.getThing((String) dataObject);
		}
		
		return actionContext.get("dataObject") == dataObject;
	}
	
	public static Map<String, Object> createDataParams(ActionContext actionContext){
		Object data = actionContext.get("data");
		if(data instanceof Thing) {
			Thing thing = (Thing) data;
			 if(thing.isThing("xworker.dataObject.DataObject")){				 
		        return UtilMap.toMap("dataObject", data, 
		        		IEditor.EDITOR_THING, World.getInstance().getThing("xworker.swt.app.editors.DataObjectEditor"),
		        		IEditor.EDITOR_ID, "DataObject:" + thing.getMetadata().getPath());
		    }
		}
		
		return null;
	}
}
