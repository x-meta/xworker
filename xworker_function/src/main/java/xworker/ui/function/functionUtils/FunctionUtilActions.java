package xworker.ui.function.functionUtils;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.ui.function.FunctionCallback;
import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionRequest;

public class FunctionUtilActions {
	/**
	 * 返回指定函数节点的保存事物。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Thing getSavedThingByName(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		String name = (String) actionContext.get("name");
		
		if(request == null){
			//静默方式后执行
			Thing self = (Thing) actionContext.get("self");			
			Thing thing = getThingByName(self, name);
			if(thing == null){
				throw new ActionException("Function Thing not exists, name=" + name);
			}else{
				return thing;
			}
		}else{
			FunctionRequest root = getCurrentRoot(request);
			return getSaveThingByName(root, name);
		}
	}
	
	public static FunctionRequest getCurrentRoot(FunctionRequest request){
		FunctionRequest current = request;
		while(current.getCallback() != null){
			FunctionCallback callback = current.getCallback();
			//排除函数的调用，之处当前栈的函数
			if(callback.getRequest() != null && callback.getParam() != null && current.getParentCall() == null){
				current = callback.getRequest();
			}else{
				return current;
			}
		}
		
		return current;
	}
	
	public static Thing getSaveThingByName(FunctionRequest request, String name){
		FunctionRequest r = getFunctionRequestByName(request, name);
		if(r == null){
			throw new ActionException("Function thing not exists, name=" + name);
		}else{
			return r.getSavedThing();
		}
	}
	
	public static FunctionRequest getFunctionRequestByName(FunctionRequest request, String name){
		for(FunctionParameter param : request.getParameters()){
			if(param.getName().equals(name)){
				return param.getValueRequest();
			}else{
				FunctionRequest valueRequest = param.getValueRequest();
				if(valueRequest != null){
					FunctionRequest prequest = getFunctionRequestByName(valueRequest, name);
					if(prequest != null){
						return prequest;
					}
				}
			}
		}
		
		return null;
	}
	
	public static Thing getThingByName(Thing thing, String name){
		if(thing.getMetadata().getName().equals(name)){
			return thing;
		}
		
		Thing childThing = null;
		for(Thing child : thing.getChilds()){
			childThing = getThingByName(child, name);
			if(childThing != null){
				return childThing;
			}
		}
		
		return null;
	}
}
