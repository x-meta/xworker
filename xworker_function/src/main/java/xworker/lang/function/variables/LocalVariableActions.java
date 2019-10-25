package xworker.lang.function.variables;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.ui.function.FunctionCallback;
import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionRequest;

public class LocalVariableActions {
	public static void defineLocalVariables(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request != null){
			FunctionCallback callback = request.getCallback();
			if(callback.getRequest() != null && callback.getParam() != null){
				FunctionRequest parent = callback.getRequest(); 
				if(parent.isLocalVariableContainer()){
					for(FunctionParameter p : request.getParameters()){
						parent.definedLocalVariable(p.getName(), actionContext.get(p.getName()));
					}
				}else{
					throw new ActionException("Parent function is not a variable container, path=" + self.getMetadata().getPath());
				}
			}else{
				throw new ActionException("Parent function is null, path=" + self.getMetadata().getPath());
			}
		}else{
			Bindings local = actionContext.getScope();
			for(Thing child : self.getChilds()){
				String name = child.getMetadata().getName();
				local.put(name, actionContext.get(name));
			}
		}
	}
}
