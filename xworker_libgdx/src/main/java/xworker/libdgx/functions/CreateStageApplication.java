package xworker.libdgx.functions;

import org.xmeta.ActionContext;

public class CreateStageApplication {
	public static Object doFunction(ActionContext actionContext){
		Number width = (Number) actionContext.get("width");
		Number height = (Number) actionContext.get("height");
		Boolean keepAspectRatio = (Boolean) actionContext.get("keepAspectRatio");
		
		return new StageApplication(width.intValue(), height.intValue(), keepAspectRatio);
	}
}
