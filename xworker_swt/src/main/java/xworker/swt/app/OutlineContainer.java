package xworker.swt.app;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

/**
 * 概要容器。
 * 
 * @author bookroom
 *
 */
public class OutlineContainer {
	ActionContainer actions;
	ActionContext actionContext;
	
	public OutlineContainer(ActionContainer actions, ActionContext actionContext) {
		this.actions = actions;
		this.actionContext = actionContext;
	}
	
	public void setComposite(Composite composite) {
		actions.doAction("setComposite", actionContext, "composite", composite);
	}

	public void removeALl(){
		Composite parent = getParentComposite();
		if(parent != null && !parent.isDisposed()){
			for(Control child : parent.getChildren()){
				child.dispose();
			}
		}
	}
	
	public Composite getParentComposite() {
		return actions.doAction("getComposite", actionContext);
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ActionContext ac = new ActionContext();
		ac.put("parentContext", actionContext);
		ac.put("parent", actionContext.get("parent"));
		
		int style = SWT.NONE;
		if(self.getBoolean("BORDER")) {
			style = style | SWT.BORDER;
		}
		Thing thing = World.getInstance().getThing("xworker.swt.app.prototypes.OutlineShell/@outlineComposite");
		Designer.pushCreator(self);
		Composite composite = null;		
		try{
			SwtUtils.pushInitStyle();
			SwtUtils.setInitStyle("xworker.swt.app.prototypes.OutlineShell/@outlineComposite", style);
			composite =	thing.doAction("create", ac);
			
			OutlineContainer container = new OutlineContainer((ActionContainer) ac.get("actions"), ac);
			actionContext.g().put(self.getMetadata().getName(), container);
			
		}finally{
			Designer.popCreator();
			SwtUtils.popInitStyle();
		}
		
		//创建子节点
		actionContext.peek().put("parent", composite);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		return composite;
	}
}
