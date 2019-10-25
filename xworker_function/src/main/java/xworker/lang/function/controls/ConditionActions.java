package xworker.lang.function.controls;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionRequest;

public class ConditionActions {
	public static boolean equal(ActionContext actionContext){
		Object v1 = actionContext.get("v1");
		Object v2 = actionContext.get("v2");
		
		if(v1 == v2){
			return true;
		}else if(v1 == null){
			return false;
		}else{
			return v1.equals(v2);
		}		
	}
	
	public static boolean exists(ActionContext actionContext){
		return actionContext.get("v1") != null;
	}
	
	public static boolean and(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request == null){
			for(Thing child : self.getChilds()){
				if(!UtilData.isTrue(actionContext.get(child.getMetadata().getName()))){
					return false;
				}
			}
			
			return true;
		}else{
			for(FunctionParameter p : request.getParameters()){
				if(!UtilData.isTrue(actionContext.get(p.getName()))){
					return false;
				}
			}
			
			return true;
		}
	}
	
	public static boolean or(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request == null){
			for(Thing child : self.getChilds()){
				if(UtilData.isTrue(actionContext.get(child.getMetadata().getName()))){
					return true;
				}
			}
			
			return false;
		}else{
			for(FunctionParameter p : request.getParameters()){
				if(UtilData.isTrue(actionContext.get(p.getName()))){
					return true;
				}
			}
			
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static boolean greater(ActionContext actionContext){
		Object v1 = actionContext.get("v1");
		Object v2 = actionContext.get("v2");
		
		Comparable c1 = (Comparable) v1;
		return c1.compareTo(v2) > 0;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean less(ActionContext actionContext){
		Object v1 = actionContext.get("v1");
		Object v2 = actionContext.get("v2");

		Comparable c1 = (Comparable) v1;
		return c1.compareTo(v2) < 0;
	}
	
	public static boolean regex(ActionContext actionContext){
		String string = (String) actionContext.get("string");
		String regex = (String) actionContext.get("regex");
		return string.matches(regex);
	}
}
