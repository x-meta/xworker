package xworker.swt.xworker;

import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;
import xworker.util.XWorkerUtils;

public class ThingTableEditor {
	 public static Object create(ActionContext actionContext) throws OgnlException{
	    	World world  = World.getInstance();
	        Thing self = (Thing) actionContext.get("self");
			
			ActionContext ac = new ActionContext();
			ac.setLabel("ThingTableEditor");
			//ac.put("explorerActions", actionContext.get("explorerActions"));
			ac.put("explorerActions", XWorkerUtils.getIdeActionContainer());
			ac.put("parent", actionContext.get("parent"));
			ac.put("parentContext", actionContext);
			ac.put("editorThing", self);
			
			Thing editorThing = world.getThing("xworker.swt.xworker.prototype.ThingTableEditor/@mainComposite");
			Composite composite = null;
			Designer.pushCreator(self);
			try{
				composite = (Composite) editorThing.doAction("create", ac);
			}finally{
				Designer.popCreator();
			}
			composite.setData("actionContext", ac);
			
			try{
				Bindings bindings = actionContext.push();
				bindings.put("parent", composite);
				for(Thing child : self.getAllChilds()){
				    child.doAction("create", actionContext);
				}
			}finally{
				actionContext.pop();
			}
			Object obj = self.doAction("getThing", actionContext);
			Thing thing = null;
			if(obj instanceof Thing){
				thing = (Thing) obj;
			}else if(obj instanceof String){
				thing = World.getInstance().getThing((String) obj);
			}
			
			ActionContainer actions = ac.getObject("actions");
			if(thing != null){				
				actions.doAction("setThing", UtilMap.toMap("thing", thing));
			}
			
			actionContext.g().put(self.getMetadata().getName(), actions);
			
			Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
			return composite;        
		}

}
