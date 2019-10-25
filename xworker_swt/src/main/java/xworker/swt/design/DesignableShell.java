package xworker.swt.design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.World;

public class DesignableShell {
	public static Object create(ActionContext actionContext){
		//Thing self = actionContext.getObject("self");
		
		//先调用Shell的创建方法创建Shell
		Action shellCreate = World.getInstance().getAction("xworker.swt.widgets.Shell/@scripts/@create");
		Shell shell = (Shell) shellCreate.run(actionContext);
		shell.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				//默认打开布局设置
				Designer.showDesignDialog((Control) event.widget, 
						World.getInstance().getThing("xworker.swt.design.DesignToolBase/@layout"));
				
			}
			
		});
		/*
		shell.setLayout(new FillLayout());
		
		//通过模板创建Compoiste的子节点
		
		shell.setData("parentContext", actionContext);
		shell.setData("thing", self);
		actionContext.peek().put("parent", shell);
		
		//创建InteractiveUI		
		Thing uiThing = World.getInstance().getThing("xworker.swt.design.prototype.InjectableCompositeUI");
		uiThing = uiThing.detach();
		uiThing.set("label", self.getMetadata().getLabel());
		InjectableComposite.copyAttributes(self, uiThing);
		InteractiveUI ui = new InteractiveUI(uiThing, Collections.EMPTY_MAP, actionContext);
		shell.setData(InteractiveUI.DATA_KEY, ui);
				
		//如果已设置子节点
		Thing control = self.getThing("Control@0");
		if(control != null && control.getChilds().size() > 0){
			//System.out.println("design shell : create control");
			control = control.getChilds().get(0);
			control.doAction("create", actionContext);
		}else{
			//System.out.println("design shell : create button");
			
			//创建设置按钮
			ActionContext ac = new ActionContext();
			ac.put("parent", shell);
			Thing thing = World.getInstance().getThing("xworker.swt.design.prototype.InjectableComposite/@setButton");	
			thing.doAction("create", ac);
		}
		
		Thing menu = self.getThing("Menu@0");
		if(menu != null){
			for(Thing child : menu.getChilds()){
				child.doAction("create", actionContext);	
				break;
			}
		}
		
		//执行初始化脚本
		for(Thing init : self.getChilds("Init")){
			for(Thing child : init.getChilds()){
				child.getAction().run(actionContext);
			}
		}
		
		//创建动作容器
		Thing acContainer = self.getThing("ActionContainer@0");
		if(acContainer != null){
			acContainer.doAction("create", actionContext);
		}*/
		return shell;
	}
}
