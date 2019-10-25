package xworker.swt.xwidgets;

import java.util.Map;

import org.eclipse.swt.browser.Browser;
//import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;
import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtDialogCallback;
import xworker.swt.util.SwtTextUtils;
import xworker.swt.util.SwtUtils;

public class AutoDemoComposite {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//原始面板
		World world = World.getInstance();
		Thing compositeThing = world.getThing("xworker.swt.xwidgets.prototypes.AutoDemoShell/@topComposite");
		
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		
		Designer.pushCreator(self);
		Composite composite = null;
		try {
			composite = (Composite) compositeThing.doAction("create", ac);
		}finally {
			Designer.popCreator();
		}
		
		//创建子事物
		actionContext.peek().put("parent", composite);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		//演示事物
		ActionContainer actionContainer = (ActionContainer) ac.get("actions");
		/*
		Thing demoThing = world.getThing(self.getStringBlankAsNull("autoDemoPath"));
		if(demoThing == null){
			demoThing = self.getThing("AutoDemo@0");
		}*/
		Thing demoThing = (Thing) self.doAction("getAutoDemo", actionContext);
		if(demoThing != null){
			actionContainer.doAction("setAutoDemo", ac, UtilMap.toMap("thing", demoThing));
			AutoDemo demo = (AutoDemo) ac.get("demo");
			if(demo != null && self.getBoolean("defaultRunDemo")){
				demo.setAuto(true);
			}
		}
		
		//保存actionContainer
		actionContext.getScope(0).put(self.getMetadata().getName(), actionContainer);
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		
		//返回composite
		return composite;
	}
	
	public static void autoSelection(ActionContext actionContext){
		Button autoButton = actionContext.getObject("autoButton");
		Button preButton = actionContext.getObject("preButton");
		Button nextButton = actionContext.getObject("nextButton");
		AutoDemo demo = actionContext.getObject("demo");
		
		if(autoButton.getSelection()){
		    preButton.setEnabled(false);
		    nextButton.setEnabled(false);
		}else{
		    preButton.setEnabled(true);
		    nextButton.setEnabled(true);
		}

		demo.setAuto(autoButton.getSelection());
	}
	
	public static void selectNode(ActionContext actionContext){
		Button autoButton = actionContext.getObject("autoButton");
		final AutoDemo demo = actionContext.getObject("demo");
		World world = World.getInstance();
		
		//创建选择节点的窗口
		ActionContext ac = new ActionContext();
		ac.put("parent", autoButton.getShell());
		ac.put("demoThing", demo.getThing().getMetadata().getPath());
		Thing shellThing = world.getThing("xworker.swt.xwidgets.prototypes.AutoDemoSelectNodeShell");
		Shell newShell = (Shell) shellThing.doAction("create", ac);

		//打开窗口
		SwtDialog dialog = new SwtDialog(autoButton.getShell(), newShell, ac);
		dialog.open(new SwtDialogCallback() {

			@Override
			public void disposed(Object result) {
				//打开相应的节点
				if(result instanceof Thing){
				    demo.show((Thing) result);
				}
			}
		});
	}
	
	public static void preButton(ActionContext actionContext){
		final AutoDemo demo = actionContext.getObject("demo");
		if(SwtUtils.isRWT()) {
			demo.showPre();
		}else {
			new Thread(new Runnable(){
				public void run(){
					demo.showPre();
				}
			}).start();
		}
	}
	
	public static void showNext(ActionContext actionContext){
		final AutoDemo demo = actionContext.getObject("demo");
		
		if(SwtUtils.isRWT()) {
			demo.show();
		}else {
			new Thread(new Runnable(){
				public void run(){
					demo.show();
				}
			}).start();
		}
	}
	
	public static void setAutoDemo(ActionContext actionContext){
		Button autoButton = actionContext.getObject("autoButton");
		AutoDemo demo = actionContext.getObject("demo");
		Browser sysBrowser = actionContext.getObject("sysBrowser");
		Control inputTxt = actionContext.getObject("inputTxt");
		Thing thing = actionContext.getObject("thing");
		Composite topComposite = actionContext.getObject("topComposite");
		
		//如果原来还有demo，那么停止
		if(demo != null){
		    demo.setAuto(false);
		}

		sysBrowser.setText("");
		SwtTextUtils.setText(inputTxt, "");

		//创建新的demo
		demo = new AutoDemo(thing, topComposite, actionContext);
		actionContext.getScope(0).put("demo", demo);

		if(autoButton.getSelection()){
		    demo.setAuto(true);
		}else{
		    //显示第一个
		    demo.show();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void selectNodeTreeSelection(ActionContext actionContext){
		Event event = actionContext.getObject("event");
		Button okButton = actionContext.getObject("okButton");
		
		Bindings g = actionContext.getScope(0);
		String path = (String) ((Map<String, Object>) event.item.getData()).get("id");
		g.put("result", World.getInstance().getThing(path));
		
		okButton.setEnabled(true);
	}
}
