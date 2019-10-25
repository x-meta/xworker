package xworker.ui.function.swt;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.ui.UIRequest;
import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUIFactory;
import xworker.ui.function.FunctionRequestUtil;

public class ShowCompositeActions {
	public static void showComposite(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");		
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request == null){
			throw new ActionException("function is running at quiet mode, no ui, path=" + self.getMetadata().getPath());
		}
				
		String uiHandlerId = "xworker_session_swt_dialogComposite";
		
		Object thingObj = actionContext.get("composite");		
		Thing uiThing = null;
		if(thingObj instanceof Thing){
			uiThing = (Thing) thingObj;
		}else if(thingObj instanceof String){
			uiThing = World.getInstance().getThing((String) thingObj);
		}
				
		if(uiHandlerId == null || uiThing == null){
			throw new ActionException("ui thing or uiHandlerId is null");
		}else{
			UIRequest uiRequest = new UIRequest(uiThing, actionContext);
			//proxy应该是其他函数的被调用者
			uiRequest.setRequestMessage(request);
			uiRequest.setRequestCallback(true);
			FunctionRequestUIFactory.requestUI(request, uiHandlerId, uiRequest, actionContext);

			//显示outline的文档
			FunctionRequestUtil.initFunctionRequestHtml(request, null, actionContext);
		}
	}
}
