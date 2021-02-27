package xworker.swt.xwidgets;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import ognl.OgnlException;

public class Editor {
	Thing thing;
	Composite parent;
	ActionContext actionContext;
	ActionContext ac = new ActionContext();
	Composite composite;
	
	public Editor(Thing thing, Composite parent,  ActionContext actionContext) throws OgnlException{
		this.thing = thing;
		this.parent = parent;
		this.actionContext = actionContext;
		
		ac.put("parent", parent);
		ac.put("parentContext", actionContext);
		ac.put("parentActionContext", actionContext);
		ac.put("editor", this);
				
		Thing compositeThing = UtilData.getThing(thing, "composite", "Composite@0", actionContext);
		composite = (Composite) compositeThing.doAction("create", ac);
		if(composite != null){
			composite.setData("actionContext", ac);
		}
		
		//创建子节点
		actionContext.peek().put("parent", composite);
		for(Thing child : thing.getChilds()){
			String thingName = child.getThingName();
			if("Composite".equals(thingName) || "actions".equals(thingName)){
				continue;
			}
			
			child.doAction("create", actionContext);
		}
		
		init(null);
	}
	
	
	public void init(Map<String, Object> params){
		//执行初始化
		thing.doAction("init", ac, params);
	}
	
	public void save(Map<String, Object> params){
		thing.doAction("save", ac, params);
	}
	
	public void close(Map<String, Object> params){
		thing.doAction("close", ac, params);
	}
	
	public static Object create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.getObject("self");
		Composite parent = actionContext.getObject("parent");

		Editor editor = new Editor(self, parent, actionContext);
				
		actionContext.getScope(0).put(self.getMetadata().getName(), editor);
		
		return editor.composite;
	}
	
	public static Thing getCompositeThing(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.getObject("self");
		
		return UtilData.getThing(self, "composite", "Composite@0", actionContext);
	}
}
