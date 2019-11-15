package xworker.swt.app.editors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.app.IEditor;

public class ThingEditor {
	public static Map<String, Object> createDataParams(ActionContext actionContext) throws IOException{
		Object data = actionContext.get("data");
		if(data instanceof Thing) {
			Thing thing = (Thing) data;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(IEditor.EDITOR_THING, World.getInstance().getThing("xworker.swt.app.editors.ThingEditor"));
			params.put("thing", data);
			params.put(IEditor.EDITOR_ID, "Thing:" + thing.getMetadata().getPath());
			return params;			
		}
		
		return null;
	}	
}
