package xworker.app.view.swt.widgets.editorchilds;

import org.eclipse.swt.widgets.Button;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;

public class DataObjectEditForm {
	public static Object create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		World world = World.getInstance();
		
		//创建Composite
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		ac.put("thing", self);

		Thing thing = self.detach();
		thing.setData("actionContext", ac);

		Designer.pushCreator(self);
		try {
			Thing compositeThing = world.getThing("xworker.app.view.swt.widgets.prototype.DataObjectEditFormShell/@composite");
			Object composite = compositeThing.doAction("create", ac);
	
			actionContext.getScope(0).put(self.getMetadata().getName(), thing);
			return composite;
		}finally {
			Designer.popCreator();
		}
		
	}
	
	public static void parentSelected(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		World world = World.getInstance();
		
		ActionContext ac = (ActionContext) self.getData("actionContext");

		Thing form = ac.getObject("form");
		Button saveButton = ac.getObject("saveButton");
		
		if(actionContext.get("data") == null){
		     Thing thing = world.getThing("xworker.app.view.swt.widgets.editorchilds.BlankDataObject");
		     form.doAction("setDataObject", ac, "dataObject", thing);
		     saveButton.setEnabled(false);
		}else{
		     form.doAction("setDataObject", ac, "dataObject", actionContext.get("data"));
		     ac.put("dataStore",  actionContext.get("dataStore"));
		     ac.put("data",  actionContext.get("data"));
		     saveButton.setEnabled(true);
		}
	}
}
