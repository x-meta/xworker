package xworker.lang.text;

import org.xmeta.ActionContext;

public class JsonDataFormatActions {
	public static Object format(ActionContext actionContext) throws Exception{
        return JsonFormator.format(actionContext.get("data"));
    }
	
    public static Object parse(ActionContext actionContext) throws Exception{
    	return JsonFormator.parse((String) actionContext.get("jsonText"), actionContext);
    }
    
    public static Object getData(ActionContext actionContext) {
    	return null;
    }
    
    public static Object parseDataObject(ActionContext actionContext) {
    	return null;
    }
    
    public static Object getArrayData(ActionContext actionContext) {
    	return null;
    }
}
