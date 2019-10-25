package xworker.ui.function.common;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.ui.function.FunctionCallback;
import xworker.ui.function.FunctionManager;
import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionRequest;

public class AddParameterActions {
	public static void doFunction(ActionContext actionContext){
		//函数请求
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		
		String paramName = (String) actionContext.get("paramName");
		if(paramName == null || "".equals(paramName.trim())){
			throw new ActionException("paramName is null");
		}
		
		FunctionCallback callback = request.getCallback();
		if(callback != null){
			callback.getParam().setName(paramName);
			callback.getParam().setValueThing(null, null);

			Thing p = new Thing("xworker.ui.function.common.AddParameter");
			String newParamName = "_newParam_"; 
			p.put("name", newParamName);
			//p.put("description", "<p>这个参数不是固定参数，是动态加入的参数，没有相关文档。</p>");
			
			FunctionParameter newParam = callback.getRequest().addParameter(newParamName);
			newParam.setValueThing(p, null);
			//newParam.getValueRequest().setDescription("xworker.ui.function.common.AddParameter");
			
			//更新函数的UI
			//FunctionRequestUIFactory.updateRequestUI(callback.getRequest());
			FunctionManager.sendEditParameterRequest(callback.getParam());
			
			callback.cancel(request.getActionContext());
		}
	}
}
