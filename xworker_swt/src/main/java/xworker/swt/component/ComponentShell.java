package xworker.swt.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class ComponentShell {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		World world = World.getInstance();
		
		//创建主控件
		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(self, actionContext);
		cc.setCompositeThing(World.getInstance().getThing("xworker.swt.component.prototypes.ComponentComposite/@mainComposite"));
		Composite composite = cc.create();
		
		//创建组件
		ActionContext ac = cc.getNewActionContext();
		Thing component = self.doAction("getComponent", actionContext);
		ac.peek().put("parent", ac.get("contentComposite"));
		if(component == null) {
			Thing noneLabel = world.getThing("xworker.swt.component.prototypes.ComponentComposite/@contentNotExsitsLabel");			
			noneLabel.doAction("create", ac);
			
			((Control) ac.get("mainCoolbar")).dispose();
		}else {
			ActionContainer actions = component.doAction("create", ac);
			ActionContext acc = actions.getActionContext();
			
			Thing menu = null;
			if(self.getBoolean("menu")) {
				menu = component.doAction("getMenu", actionContext);
			}
			
			Thing toolbar = null;
			if(self.getBoolean("toolbar")) {
				toolbar = component.doAction("getToolbar", actionContext);
			}
			if(menu != null) {
				Menu menuBar = composite.getShell().getMenuBar();
				if(menuBar == null) {
					menuBar = new Menu(composite.getShell(), SWT.BAR);
					composite.getShell().setMenuBar(menuBar);
				}
				MenuItem menuItem = new MenuItem(menuBar, SWT.CASCADE);
				menuItem.setText(component.getMetadata().getLabel());
				acc.put("parent", menuItem);
				menu.doAction("create", acc);
			}
			if(toolbar != null) {
				acc.put("parent", ac.get("toolbar"));
				for(Thing child : toolbar.getChilds()) {
					child.doAction("create", acc);
				}
			}
			
			((Control) ac.get("menuToobar")).dispose();
			((Item) ac.get("menuCoolItem")).dispose();
			((CoolBar) ac.get("mainCoolbar")).update();
			
			if(toolbar == null) {
				((Control) ac.get("mainCoolbar")).dispose();
				composite.layout();
			}			
			actionContext.g().put(self.getMetadata().getName(), actions);
		}
		
		return composite;
	}
}
