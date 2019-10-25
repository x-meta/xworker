package xworker.lang.actions.text;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.utils.JacksonFormator;

public class JsonActions {
	public static Object parse(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		String jsonText = (String) self.doAction("getJsonText", actionContext);
		
		if(jsonText == null || "".equals(jsonText)){
			return null;
		}else{
			return JacksonFormator.parseObject(jsonText);
		}
	}
	
	public static String format(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		
		Object object = self.doAction("getObject", actionContext);
		if(object != null){
			return JacksonFormator.formatObject(object);
		}else{
			return null;
		}
	}
}
