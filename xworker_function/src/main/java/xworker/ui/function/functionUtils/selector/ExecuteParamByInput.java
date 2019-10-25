package xworker.ui.function.functionUtils.selector;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionQuietRunner;
import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUtil;

public class ExecuteParamByInput {
	public static Object doFunction(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request != null){
			FunctionParameter lastParameter = getLastParameter(actionContext);
			
			if(request.getParameters().size() == 0){
				//没有参数直接返回
				FunctionRequestUtil.callbakMyselfOk(request, null, actionContext);
			}
			
			FunctionParameter inputParam = request.getParameter("input");
			FunctionParameter loopParam = request.getParameter("loop");
			
			String lastParamName =  lastParameter != null ? lastParameter.getName() : null;
					
			if("input".equals(lastParamName)){
				//input参数执行完了执行loop
				if(loopParam != null){
					request.executeParam(loopParam);
				}else{
					throw new ActionException("Param 'loop' not exists");
				}			
				return null;			
			}else if("loop".equals(lastParamName)){
				//loop如果执行了loop参数
				Object inputValue = inputParam.getValue();
				if(inputValue == null){
					FunctionRequestUtil.callbakMyselfOk(request, null, actionContext);
					return null;
				}else{
					String inputName = inputValue.toString();
					if(inputName == null){
						FunctionRequestUtil.callbakMyselfOk(request, null, actionContext);
						return null;
					}
					
					if("input".equals(inputName) || "loop".equals(inputName)){
						throw new ActionException("Input can not bei 'input' or 'loop', these a reserved param");
					}
					
					FunctionParameter selectedParam = null;
					for(FunctionParameter param : request.getParameters()){
						if(inputName.equals(param.getName())){
							selectedParam = param;
							break;
						}
					}
					
					if(selectedParam != null){
						request.executeParam(selectedParam);
					}else{
						FunctionRequestUtil.callbakMyselfOk(request, inputParam.getValue(), actionContext);
						return null;
					}
				}
			}else {
				if(lastParameter != null){
					request.setValue(lastParameter.getValue());
					
					if(UtilData.isTrue(loopParam.getValue())){
						request.executeParam(inputParam);
					}else{
						FunctionRequestUtil.callbakMyselfOk(request, lastParameter.getValue(), actionContext);
						return null;
					}
				}else{
					//执行参数
					if(inputParam != null){
						request.executeParam(inputParam);
					}else{
						throw new ActionException("Param 'input' not exists");
					}
				}
				return null;				
			}		
		}else{
			Thing self = (Thing) actionContext.get("self");
			
			while(true){
				Thing inputThing = getParam(self, "input");

				//先执行input
				if(inputThing != null){
					Object inputValue = FunctionQuietRunner.runFunction(inputThing, actionContext);
					if(inputValue == null){
						throw new ActionException("input value is null, path=" + self.getMetadata().getPath());
					}
					
					//根据input执行参数
					Thing paramThing = getParam(self, inputValue.toString());
					if(paramThing == null){
						return inputValue;
						//throw new ActionException("param not exists, name=" + inputValue + ",path=" + self.getMetadata().getPath());
					}
					
					Object value = FunctionQuietRunner.runFunction(paramThing, actionContext);
					
					//最后判断是否循环
					Thing loopThing = getParam(self, "loop");
					if(loopThing != null){
						Object loopValue = FunctionQuietRunner.runFunction(loopThing, actionContext);
						if(!UtilData.isTrue(loopValue)){
							return value;
						}
					}else{
						return value;
					}
					
				}else{
					throw new ActionException("input param not exists, path=" + self.getMetadata().getPath());
				}
			}
		}
		
		return null;
	}
	
	public static Thing getParam(Thing self, String name){
		for(Thing child : self.getChilds()){
			if(name.equals(child.getMetadata().getName())){
				return child;
			}
		}
		
		return null;
	}
	
	public static FunctionParameter getLastParameter(ActionContext actionContext){
		return (FunctionParameter) actionContext.get("lastParameter");
	}
}
