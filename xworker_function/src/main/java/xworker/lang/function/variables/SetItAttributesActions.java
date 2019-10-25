package xworker.lang.function.variables;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionRequest;

public class SetItAttributesActions {
	public static Object doFunction(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		
		if(request != null){
			Object it = request.getLocalVariable("It");
			for(FunctionParameter param : request.getParameters()){
				OgnlUtil.setValue(param.getName(), it, param.getValue());
			}
			
			return it;
		}else{
			Object it = actionContext.get("It");
			for(Thing child : self.getChilds()){
				String name = child.getMetadata().getName();
				OgnlUtil.setValue(name, it, actionContext.get(name));
			}
			return it;
		}
	}
}
