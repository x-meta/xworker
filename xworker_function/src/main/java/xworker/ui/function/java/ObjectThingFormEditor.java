package xworker.ui.function.java;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.ui.function.FunctionRequest;

public class ObjectThingFormEditor {
	public static void doFunction(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		Object object = actionContext.get("object");
		Thing thing = (Thing) actionContext.get("thing");
		
	}
}
