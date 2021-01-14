package xworker.swt.xworker.codeassist.variableproviders;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.VariableDesc;
import xworker.swt.xworker.codeassist.VariableProvider;

public class ActionVariableProvider implements VariableProvider{

	@Override
	public List<VariableDesc> getVariables(String code, int offset,  List<String> statements, Thing thing, ActionContext actionContext) {
		return getActionVariables(code, offset, statements, thing, actionContext);
	}
	
	public static List<VariableDesc> getActionVariables(String code, int offset,  List<String> statements, Thing thing, ActionContext actionContext) {
		 List<VariableDesc> vars = VariableDesc.getActionInputParams(thing, actionContext);
		 
		Thing parent = thing.getParent();
		if(parent == null) {
			return vars;
		}
		
		//查看是否是动作行为
		String thingName = parent.getThingName();
		if(!"actions".equals(thingName)) {
			return vars;
		}
		
		//动作行为所属的对象
		parent = parent.getParent();
				
		String name = thing.getMetadata().getName();
		List<Thing> actionThings = parent.getActionThings(name);
		for(Thing actionThing : actionThings) {
			if(actionThing != null) {
				List<VariableDesc> vs = VariableDesc.getActionInputParams(actionThing, actionContext);
				for(VariableDesc v : vs) {
					boolean have = false;
					for(VariableDesc var : vars) {
						if(v.getName().equals(var.getName())) {
							have = true;
							break;
						}
					}
					
					if(!have) {
						vars.add(v);
					}
				}
			}
		}
		
		return vars;
	}
	
	
}
