package xworker.lang.function.controls;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionQuietRunner;
import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUtil;

public class WithActions {
	public static Object doFunction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return doFunction(self, "It", "it", actionContext);
	}
	
	public static Object doWithThisFunction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return doFunction(self, "This", "This", actionContext);
	}
	
	public static Object doWithThatFunction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return doFunction(self, "That", "That", actionContext);
	}
	
	public static Object doWithThoseFunction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return doFunction(self, "Those", "Those", actionContext);
	}
	
	public static Object doWithTheseFunction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return doFunction(self, "These", "These", actionContext);
	}
	
	public static Object doFunction(Thing self, String varName, String paramName, ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request == null){
			//安静执行时的do
			Thing itThing = null;
			Thing doThing = null;
			for(Thing child : self.getChilds()){
				if(paramName.equals(child.getMetadata().getName())){
					itThing = child;
				}else if("do".equals(child.getMetadata().getName())){
					doThing = child;
				}
			}
			
			Object itObject = FunctionQuietRunner.runFunction(itThing, actionContext);
			try{
				Bindings bindings = actionContext.push();
				bindings.put(varName, itObject);				
				
				return FunctionQuietRunner.runFunction(doThing, actionContext);
			}finally{
				actionContext.pop();
			}
		}else{
			FunctionParameter lastParameter = ControlActions.getLastParameter(actionContext);			
			FunctionParameter itParameter = request.getParameter(paramName);
			FunctionParameter doParameter = request.getParameter("do");
			
			if(lastParameter == null){
				//执行IT					
				request.executeParam(itParameter);
				
				return null;
			}else if(lastParameter == itParameter){
				//执行do
				request.putLocalVariable(varName, itParameter.getValue(), actionContext);
				request.executeParam(doParameter);
			}else{
				//callback
				FunctionRequestUtil.callbakMyselfOk(request, itParameter.getValue(), actionContext);
			}			
		}
		
		return null;
	}
}
