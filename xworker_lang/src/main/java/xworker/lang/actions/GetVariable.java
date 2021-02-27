package xworker.lang.actions;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class GetVariable {
	public static Object run(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Thing parentSelf = null;
		List<Thing> things = actionContext.getThings();
        if(things.size() > 1){
        	parentSelf = things.get(things.size() - 2);
        }
		
		String attributeName = self.getStringBlankAsNull("attributeName");
		if(attributeName != null){
			return parentSelf.get(attributeName);
		}
		
		String varName = self.getStringBlankAsNull("varName");
		if(varName != null){
			String var = parentSelf.getStringBlankAsNull(varName);
			if(var != null){
				return actionContext.get(var);
			}
		}
		
		String varOgnlExpression = self.getStringBlankAsNull("varOgnlExpression");
		if(varOgnlExpression != null){
			String exp = parentSelf.getStringBlankAsNull(varOgnlExpression);
			if(exp != null){
				return OgnlUtil.getValue(parentSelf, varOgnlExpression, exp, actionContext);
			}
		}
		
		String thingPath = self.getStringBlankAsNull("thingPath");
		if(thingPath != null){
			String tpath = parentSelf.getStringBlankAsNull(thingPath);
			if(tpath != null){
				Thing thing = World.getInstance().getThing(tpath);
				if(thing != null){
					String thingActionName = self.getStringBlankAsNull("thingActionName");
					if(thingActionName != null){
						return thing.doAction(thingActionName, actionContext);
					}else{
						return thing;
					}
				}
			}
		}
		
		return null;
	}

	
	public static Object getVarExpression(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		return OgnlUtil.getCachedExpression(self, "varExpression");
	}
	
	public static Object runOgnl(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Object exp = self.doAction("getVarExpression", actionContext);
		if(exp != null){
			return OgnlUtil.getValue(exp, actionContext);
		}else{
			return null;
		}
	}
	
	public static Object getVariable(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String varName = (String) self.doAction("getVarName", actionContext);
		if(varName == null || "".equals(varName)){
			throw new ActionException("varName is null, action=" + self.getMetadata().getPath());
		}
		
		return actionContext.get(varName);
	}
}
