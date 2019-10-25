package xworker.swt.xwidgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class DemoSWT {
	private static Logger logger = LoggerFactory.getLogger(DemoSWT.class);
	
	public static Object create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//要演示的事物
		//Object com = UtilData.getData(self, "composite", actionContext);
		Thing comThing = (Thing) self.doAction("getComposite", actionContext);
		/*
		if(com instanceof Thing){
			comThing = (Thing) com;
		}else if(com instanceof String){
			comThing = World.getInstance().getThing((String) com);
		}
		if(comThing == null){
			comThing = self.getThing("Composite@0");
		}*/		
		
		//创建演示的TabFolder
		Thing tabThing = World.getInstance().getThing("xworker.swt.xwidgets.DemoSWT/@demoTabFolder");
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		Object tabFolder = null;
		Designer.pushCreator(self);
		try{
			tabFolder =	tabThing.doAction("create", ac);
		}finally{
			Designer.popCreator();
		}
		//创建子节点
		actionContext.peek().put("parent", tabFolder);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
				
		//演示的动作集
		ActionContainer actions = (ActionContainer) ac.get("actions");
		if(comThing != null){
			actions.doAction("setComposite", ac, UtilMap.toMap("composite", comThing));
		}
		
		Designer.attachCreator((Control) tabFolder, self.getMetadata().getPath(), actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), actions);
		return tabFolder;
	}
	
	public static void updateButtonSelection(ActionContext actionContext){
		ActionContext thingEditor = (ActionContext) actionContext.get("thingEditor");
		Composite demoComposite = (Composite) actionContext.get("demoComposite");
		Thing compositeThing = (Thing) actionContext.get("compositeThing");
		TabItem demoTabItem = (TabItem) actionContext.get("demoTabItem");
		TabFolder demoTabFolder = (TabFolder) actionContext.get("demoTabFolder");
		
		//先保存事物
		ActionContainer editorActions = (ActionContainer) thingEditor.get("editorActions"); 
		editorActions.doAction("save");

		//先清除原有的控件		
		for(Control child : demoComposite.getChildren()){
		    child.dispose();
		}

		//创建Composite
		ActionContext ac = new ActionContext();
		ac.put("parent", demoComposite);
		compositeThing.doAction("create", ac);

		demoTabItem.setText(compositeThing.getMetadata().getLabel());

		demoComposite.layout();
		demoComposite.getParent().layout();

		demoTabFolder.setSelection(0);
	}
	
	public static void setComposite(ActionContext actionContext){
		World world = World.getInstance();
		Object composite = actionContext.get("composite");
		Composite demoComposite = (Composite) actionContext.get("demoComposite");
		TabItem demoTabItem = (TabItem) actionContext.get("demoTabItem");
		ActionContext thingEditor = (ActionContext) actionContext.get("thingEditor");
		
		Thing thing = null;		
		if(composite instanceof String){
		    thing = world.getThing((String) composite);
		}else if(composite instanceof Thing){
			thing = (Thing) composite;
		}

		if(thing == null){
		    logger.warn("DemoSWT: composite thing is null, path=" + composite);
		    return;
		}

		//先清除原有的控件
		for(Control child  : demoComposite.getChildren()){
			try {
				child.dispose();
			}catch(Throwable t) {			
				//在Eclipse的RWT下有时候会出错
			}
		}

		//创建Composite
		ActionContext ac = new ActionContext();
		ac.put("parent", demoComposite);
		thing.doAction("create", ac);
		SwtUtils.checkLayoutError(demoComposite);

		demoTabItem.setText(thing.getMetadata().getLabel());


		//在编辑器中编辑事物
		ActionContainer editorActions = (ActionContainer) thingEditor.get("editorActions");
		editorActions.doAction("setThing", UtilMap.toMap("thing",  thing));

		actionContext.getScope(0).put("compositeThing", thing);

		demoComposite.layout();
		demoComposite.getParent().layout();
	}
}
