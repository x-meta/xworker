package xworker.ai.aima.logic.fol;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import aima.core.logic.fol.domain.FOLDomain;

public class DomainActions {
	/**
	 * 创建论域。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object createDomain(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		FOLDomain domain = new FOLDomain();
		
		try{
			Bindings bindings = actionContext.push(null);
			bindings.put("domain", domain);
			
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		return domain;		
	}
	
	public static void createConstants(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
	}
	
	public static void createConstant(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		FOLDomain domain = (FOLDomain) actionContext.get("domain");
		domain.addConstant(self.getMetadata().getName());
	}
	
	public static void createConstantCollection(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		FOLDomain domain = (FOLDomain) actionContext.get("domain");
		String constantCollection = self.getStringBlankAsNull("constantCollection");
		if(constantCollection != null){
			for(String str : constantCollection.split("[,]")){
				str = str.trim();
				if(!"".equals(str)){
					domain.addConstant(str);
				}
			}
		}
	}
	
	public static void createPredicates(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
	}
	
	public static void createPredicate(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		FOLDomain domain = (FOLDomain) actionContext.get("domain");
		domain.addPredicate(self.getMetadata().getName());
	}
	
	public static void createPredicateCollection(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		FOLDomain domain = (FOLDomain) actionContext.get("domain");
		String constantCollection = self.getStringBlankAsNull("predicateCollection");
		if(constantCollection != null){
			for(String str : constantCollection.split("[,]")){
				str = str.trim();
				if(!"".equals(str)){
					domain.addPredicate(str);
				}
			}
		}
	}
	
	public static void createFunctions(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
	}
	
	public static void createFunction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		FOLDomain domain = (FOLDomain) actionContext.get("domain");
		domain.addFunction(self.getMetadata().getName());
	}
	
	public static void createFunctionCollection(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		FOLDomain domain = (FOLDomain) actionContext.get("domain");
		String constantCollection = self.getStringBlankAsNull("functionCollection");
		if(constantCollection != null){
			for(String str : constantCollection.split("[,]")){
				str = str.trim();
				if(!"".equals(str)){
					domain.addFunction(str);
				}
			}
		}
	}
}
