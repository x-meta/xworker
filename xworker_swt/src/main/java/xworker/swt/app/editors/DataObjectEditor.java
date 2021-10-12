package xworker.swt.app.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.swt.app.IEditor;
import xworker.swt.util.SwtUtils;
import xworker.util.XWorkerUtils;
import xworker.workbench.EditorParams;

public class DataObjectEditor {
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

		//设置outline，如果存在
		if(actionContext.get("outlineBrowser") != null){
		    String url = XWorkerUtils.getThingDescUrl((Thing) dataObject);
		    Browser outlineBrowser = actionContext.getObject("outlineBrowser");
		    outlineBrowser.setUrl(url);
		}
	}

	public static void onOutlineCreated(ActionContext actionContext){
		Thing dataObject = actionContext.getObject("dataObject");
		if(dataObject != null && actionContext.get("outlineBrowser") != null){
			Browser outlineBrowser = actionContext.getObject("outlineBrowser");

			SwtUtils.setThingDesc(dataObject, outlineBrowser);
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

	public static EditorParams<Thing> createParams(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Object content = actionContext.getObject("content");
		Thing thing = null;
		if(content instanceof  String){
			thing = World.getInstance().getThing((String) content);
		}else if(content instanceof Thing){
			thing = (Thing) content;
		}
		if(thing != null){
			if(thing.isThing("xworker.dataObject.DataObject")){
				String path = thing.getMetadata().getPath();
				return new EditorParams<Thing>(self, "DataObject:" + path, thing) {
					@Override
					public Map<String, Object> getParams() {
						Map<String, Object> params = new HashMap<>();
						params.put("dataObject", this.getContent().getMetadata().getPath());

						return params;
					}
				};
			}
		}

		return null;
	}
}
