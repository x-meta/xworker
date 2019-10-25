package xworker.ui.function.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

public class FunctionContext {
	private static final String contextKey = "__xworker_function__context__key__201505191939__"; 
	
	@SuppressWarnings("unchecked")
	public static List<Thing> getVars(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing vars = self.getThing("Vars@0");
		if(vars != null){
			return vars.getChilds();
		}else{
			return Collections.EMPTY_LIST;
		}
	}
	
	public static Thing getVarThing(Thing self, String varName){
		Thing vars = self.getThing("Vars@0");
		if(vars != null){
			for(Thing var : vars.getChilds()){
				if(var.getMetadata().getName().equals(varName)){
					return var;
				}
			}
		}
		
		throw new ActionException("Var not exists, name=" + varName + ",path=" + self.getMetadata().getPath());
	}
	
	public static String getVarInputFunction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String name = (String) actionContext.get("name");
		
		Thing var = getVarThing(self, name);
		return var.getString("inputFunction");
	}
	
	/**
	 * 获取要设置值的变量列表。
	 * 
	 * @param actionContext
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Thing> getNeedInputVars(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Thing thingVars = self.getThing("Vars@0");
		if(thingVars == null || thingVars.getChilds().size() == 0){
			throw new ActionException("Vars not defined, path=" + self.getMetadata().getPath());
		}
		
		List<String> vars = (List<String>) actionContext.get("vars");
		List<Thing> varList = new ArrayList<Thing>();
		
		Map<String, Object> values = getValues(actionContext, self);
		//变量不存在
		for(String var : vars){
			Thing varThing = null;
			for(Thing v : thingVars.getChilds()){
				if(v.getMetadata().getName().equals(var)){
					varThing = v;
					break;
				}
			}
			
			if(varThing != null){
				if(values.get(var) != null || varThing.getBoolean("optional")){						
				}else{
					varList.add(varThing);
				}
			}
		}
		
		return varList;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getValues(ActionContext actionContext, Thing self){
		Map<String, Map<String, Object>> values = (Map<String, Map<String, Object>>) actionContext.getScope(0).get(contextKey);
		if(values == null){
			values = new HashMap<String, Map<String, Object>>();
			actionContext.getScope(0).put(contextKey, values);
		}
		
		String path = self.getMetadata().getPath();
		Map<String, Object> vs = values.get(path);
		if(vs == null){
			vs = new HashMap<String, Object>();
			values.put(path, vs);
		}
		
		return vs;
		
	}
	
	public static void setVar(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String name = (String) actionContext.get("name");
		Object value = actionContext.get("value");
		
		Map<String, Object> values = getValues(actionContext, self);
		values.put(name, value);
	}
	
	public static Object getVar(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String name = (String) actionContext.get("name");
		
		Map<String, Object> values = getValues(actionContext, self);
		return values.get(name);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Thing> getFunctions(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing vars = self.getThing("Functions@0");
		if(vars != null){
			return vars.getChilds();
		}else{
			return Collections.EMPTY_LIST;
		}
	}
	
	public static Thing addVar(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String name = (String) actionContext.get("name");
		
		Thing vars = self.getThing("Vars@0");
		if(vars != null){
			for(Thing var : vars.getChilds()){
				if(var.getMetadata().getName().equals(name)){
					throw new ActionException("Var has exists, name=" + name);
				}
			}
		}
		
		if(vars == null){
			vars = new Thing("xworker.ui.function.context.Context/@Vars");
			self.addChild(vars);
		}
		
		Thing var = new Thing("xworker.ui.function.context.Context/@Vars/@Var");
		var.put("name", name);
		vars.addChild(var);
		self.save();
		
		return var;
	}
	
	public static Thing addFunction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String name = (String) actionContext.get("name");
		
		Thing vars = self.getThing("Functions@0");
		if(vars != null){
			for(Thing var : vars.getChilds()){
				if(var.getMetadata().getName().equals(name)){
					throw new ActionException("Function has exists, name=" + name);
				}
			}
		}
		
		if(vars == null){
			vars = new Thing("xworker.ui.function.context.Context/@Functions");
			self.addChild(vars);
		}
		
		Thing var = new Thing("xworker.ui.function.context.Context/@Functions/@Function");
		var.put("name", name);
		vars.addChild(var);
		self.save();
		
		return var;
	}
	
	public static void removeVar(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String name = (String) actionContext.get("name");
		
		Thing vars = self.getThing("Vars@0");
		if(vars != null){
			for(Thing var : vars.getChilds()){
				if(var.getMetadata().getName().equals(name)){
					vars.removeChild(var);
					self.save();
					
					return;
				}
			}
		}
	}
	
	public static void removeFunction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String name = (String) actionContext.get("name");
		
		Thing vars = self.getThing("Functions@0");
		if(vars != null){
			for(Thing var : vars.getChilds()){
				if(var.getMetadata().getName().equals(name)){
					vars.removeChild(var);
					self.save();
					
					return;
				}
			}
		}
	}
}
