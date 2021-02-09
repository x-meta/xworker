/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.swt.widgets;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilFile;
import org.xmeta.util.UtilString;

import xworker.lang.actions.ActionContainer;
import xworker.swt.design.Designer;
import xworker.swt.design.sync.SwtSyncer;
import xworker.swt.style.StyleSetStyleCreator;
import xworker.swt.util.PoolableControlFactory;
import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.UtilSwt;
import xworker.swt.xworker.SwtAppIde;
import xworker.util.IIde;
import xworker.util.UtilAction;
import xworker.util.XWorkerUtils;

public class ShellCreator {
	private static Logger logger = LoggerFactory.getLogger(ShellCreator.class);
	private static Thread systemExitChecker = null;
	
    public static Object create(ActionContext actionContext){
    	World world = World.getInstance();
    	//self是事物模型本身
    	Thing self = (Thing) actionContext.get("self");
		
    	//初始化style
		Action styleAction = world.getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		String selfStyle = self.getString("style");
		if(self.getBoolean("NO_TRIM"))
		    style |= SWT.NO_TRIM;
		if(self.getBoolean("CLOSE"))
		    style |= SWT.CLOSE;
		if(self.getBoolean("TITLE"))
		    style |= SWT.TITLE;
		if(self.getBoolean("MIN"))
		    style |= SWT.MIN;
		if(self.getBoolean("MAX"))
		    style |= SWT.MAX;
		if(self.getBoolean("BORDER"))
		    style |= SWT.BORDER;
		if(self.getBoolean("RESIZE"))
		    style |= SWT.RESIZE;
		if(self.getBoolean("ON_TOP"))
		    style |= SWT.ON_TOP;
		if(self.getBoolean("TOOL"))
		    style |= SWT.TOOL;
		if("MODELESS".equals(selfStyle)){
		    style |= SWT.MODELESS;
		}else if("PRIMARY_MODAL".equals(selfStyle)){
		    style |= SWT.PRIMARY_MODAL;
		}else if("APPLICATION_MODAL".equals(selfStyle)){
		    style |= SWT.APPLICATION_MODAL;
		}else if("SYSTEM_MODAL".equals(selfStyle)){
		    style |= SWT.SYSTEM_MODAL;
		}
		if(self.getBoolean("H_SCROLL"))
		    style |= SWT.H_SCROLL;
		if(self.getBoolean("V_SCROLL"))
		    style |= SWT.V_SCROLL;        
		if(self.getBoolean("NO_BACKGROUND"))
		    style |= SWT.NO_BACKGROUND;        
		if(self.getBoolean("NO_FOCUS"))
		    style |= SWT.NO_FOCUS;        
		if(self.getBoolean("NO_MERGE_PAINTS"))
		    style |= SWT.NO_MERGE_PAINTS;        
		if(self.getBoolean("NO_REDRAW_RESIZ"))
		    style |= SWT.NO_REDRAW_RESIZE;        
		if(self.getBoolean("NO_RADIO_GROUP"))
		    style |= SWT.NO_RADIO_GROUP;        
		if(self.getBoolean("EMBEDDED"))
		    style |= SWT.EMBEDDED;        
		if(self.getBoolean("DOUBLE_BUFFERED"))
		    style |= SWT.DOUBLE_BUFFERED;
		if(self.getBoolean("NO_FOCUS"))
		    style |= SWT.NO_FOCUS; 
		    
		//创建shell对象
		Shell shell = null;    
		boolean isFirst = false;
		try{
			Object parent = actionContext.get("parent");
			if(parent instanceof Display){
				shell = new Shell((Display) parent, style);
			}else if(parent instanceof Shell){
				shell = new Shell((Shell) parent, style);
			}else{
				Display display = Display.getCurrent();
				if(display == null) {
					display = Display.getDefault();
				}
			
				if(display != null){
					shell = new Shell(display, style);
				}else{
					shell = new Shell(style);
				}
				
				//系统入口，是否是设计模式
				if(self.getBoolean("design") && Designer.isEnabled() == false 
						&& World.getInstance().getThing("xworker.ide.worldexplorer.swt.SimpleExplorerRunner") != null){
					Designer.setEnabled(true);
				}
				isFirst = true;				
			}
			
			//是否是IDE
			if((self.getBoolean("isIde") || isFirst)){
				IIde old = XWorkerUtils.getIde();
				if(old == null ||  (old.getIDEShell() instanceof Shell && ((Shell) old.getIDEShell()).isDisposed())) {
					SwtAppIde ide = new SwtAppIde(self, shell, actionContext);
					XWorkerUtils.setIde(ide);
				}
				//logger.info("IDe setted");
			}
			
		}catch(Exception e){
		    shell = new Shell(style);
		}
		
		//绑定到designer，使用鼠标中键双击可以打开菜单，可以通过菜单编辑模型等
		if(!shell.isDisposed()){
		    Designer.attach(shell, self.getMetadata().getPath(), actionContext);
		}
		
		
		//更新属性和创建子节点等
		actionContext.peek().put("shell", shell);
		actionContext.peek().put("self", self);
		
		//窗口的图标
		Image image = (Image) StyleSetStyleCreator.createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            shell.setImage(image);
        }
        
		try{
			//保存变量
			actionContext.getScope(0).put(self.getString("name"), shell);
			
			update(actionContext);
		
			if(self.getBoolean("centerScreen")){
			    try{
			        SwtUtils.centerShell(shell);
			    }catch(Throwable t){
			    }
			}
			
			if(self.getBoolean("maximized")){
			    shell.setMaximized(true);
			}
			
			if(self.getBoolean("exitOnDispose")){
				shell.addListener(SWT.Dispose, new Listener(){
					@Override
					public void handleEvent(Event event) {
						Shell self = (Shell) event.widget;
						
						if(SwtUtils.isRWT()) {
							//RWT下退出
							return;
						}
						
						if(systemExitChecker != null && systemExitChecker.isAlive()) {
							//系统检查退出的线程依然存在，只要一个就够了
							return;
						}
						final Display display = self.getDisplay();
						systemExitChecker = new Thread(new SystemExitor(self, display));
						systemExitChecker.start();						
					}					
				});
			}
			
					
			//打开设计窗口
			if(isFirst && self.getBoolean("designDefaultOpen") && Designer.isEnabled()){
				Designer.showDesignDialog(shell, null);
				
			}
		}catch(Exception e){
			logger.error("Create Shell error, path=" + self.getMetadata().getPath(), e);
			
			//此时Shell已经创建，避免出现异常后Shell弹不出来，不能最终释放资源
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			box.setText("Error");
			String message = UtilAction.getCause(e).getMessage();
			if(message == null) {
				message = e.toString();
			}
			box.setMessage(message);
			SwtUtils.openMessageBox(box, null, actionContext);
			//box.open();
		}

		return shell;
		    
	}

    public static Object update(ActionContext actionContext){
    	Thing self = actionContext.getObject("self");
    	Shell shell = actionContext.getObject("shell");
    	for(Control control : shell.getChildren()){
    		control.dispose();
    	}
    	
    	World world = World.getInstance();
    	
    	//初始化公共属性
		Bindings bindings = actionContext.push(null);
		bindings.put("control", shell);
		bindings.put("self", self);
		try{
		    Action action = world.getAction("xworker.swt.widgets.Composite/@scripts/@init");
		    action.run(actionContext);
		}finally{
		    actionContext.pop();
		}
		
		//初始化控件的自有或附加属性
		if(!"-1".equals(self.getString("width"))){
			int width = UtilSwt.getInt(self, "width", -1);
			int height = UtilSwt.getInt(self, "height", -1);
			if(SwtUtils.isRWT()) {
				Rectangle displaySize = shell.getDisplay().getClientArea();
				if(width > displaySize.width) {
					width = displaySize.width;
				}
				if(height > displaySize.height) {
					height = displaySize.height;
				}
			}
					
		    shell.setSize(width, height);
		}
		String text = self.getString("text");
		if(text != null && !"".equals(text)){
			String  titleTxt = UtilString.getString(text, actionContext);
			if(titleTxt != null){
				shell.setText(titleTxt);
			}
		}
		int alpha = self.getInt("alpha", -1);
		if(alpha != -1) shell.setAlpha(alpha);
			
		boolean fullScreen = self.getBoolean("fullScreen");
		shell.setFullScreen(fullScreen);
		
		if(!SwtUtils.isRWT()) {
			String imeInputMode = self.getString("imeInputMode");
			if("NONE".equals(imeInputMode)){
				shell.setImeInputMode(SWT.NONE);
			}else if("ROMAN".equals(imeInputMode)){
				shell.setImeInputMode(SWT.ROMAN);
			}else if("DBCS".equals(imeInputMode)){
				shell.setImeInputMode(SWT.DBCS);
			}else if("PHONETIC".equals(imeInputMode)){
				shell.setImeInputMode(SWT.PHONETIC);
			}else if("NATIVE".equals(imeInputMode)){
				shell.setImeInputMode(SWT.NATIVE);
			}else if("ALPHA".equals(imeInputMode)){
				shell.setImeInputMode(SWT.ALPHA);
			}		
		}
		    
		//创建子节点
		actionContext.peek().put("parent", shell);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		} 
		actionContext.peek().remove("parent");
		
		//其他功能
		if(self.getBoolean("pack")){
		    shell.pack();
		}

		shell.layout();
    	return shell;        		
    }
    
    public static Object createDialog(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
		
		Shell newShell = (Shell) self.doAction("create", actionContext);
		return new SwtDialog((Shell) actionContext.get("parent"), newShell, actionContext);       
	}

    public static void runShellMenu(ActionContext actionContext){
		ActionContext context = new ActionContext();
		context.put("parent", Display.getCurrent().getActiveShell());
		context.put("explorerActions", actionContext.get("explorerActions"));
		context.put("explorerContext", actionContext.get("explorerContext"));
		context.put("parentContext", actionContext);
		
		Shell shell = (Shell) ((Thing) actionContext.get("currentThing")).doAction("create", context);
		Event event = actionContext.getObject("event");
		Thing menu = (Thing) event.widget.getData("menu");
		if(!"nosync".equals(menu.getString("params"))) {
			SwtSyncer.startSync(shell);
		}
		if(((Thing) actionContext.get("currentThing")).getBoolean("visible")){
        	shell.open ();
        }  
	}

    public static void runShellThreadMenu(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        Thing currentThing = (Thing) actionContext.get("currentThing");
		
		if(currentThing == null){
		    currentThing = self;
		}
		
		final ActionContext acContext = new ActionContext(actionContext);
		final Thing cThing = currentThing;
		new Thread(new Runnable(){
			public void run(){
				Display display = new Display ();
		        ActionContext context = new ActionContext();        
		        context.put("explorerActions", acContext.get("explorerActions"));
		        context.put("explorerContext", acContext.get("explorerContext"));
		        context.put("parentContext", acContext);
		        context.put("parent", display);
		        
		        Shell shell = (Shell) cThing.doAction("create", context);
		        SwtSyncer.startSync(shell);
		        if(cThing.getBoolean("visible", true)){
		        	shell.open ();
		        }
			    while (!shell.isDisposed ()) {
			        if (!display.readAndDispatch ()) display.sleep ();
			    }
			    display.dispose ();
			}
		}).start(); 
	}

    public static void runDesign(ActionContext actionContext){
		World world = World.getInstance();
		
		ActionContext bin = new ActionContext();
		bin.put("parent", Display.getCurrent());
		bin.put("treeItem", ((Tree) actionContext.get("outlineTree")).getSelection()[0]);
		bin.put("treeListener", null);
		Thing designObj = world.getThing("xworker.ide.worldexplorer.swt.designer.Designer/@shell");
		Shell shell = (Shell) designObj.doAction("create", bin);
		Thing thing = (Thing) actionContext.get("thing");
		bin.put("dataObject", thing);
		//bin.put.handleEvent(null);
		Text dataText = (Text) bin.get("dataText");
		dataText.setText(thing.getMetadata().getPath());
		
		shell.open();       
	}

    public static void run(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	Object parent = actionContext.get("parent");
    	if(parent == null || !(parent instanceof Shell)){    		
    		//parent = XWorkerUtils.getIDEShell();
    		//actionContext.peek().put("parent", parent);
    	}    
    	Shell shell = null;
    	if(parent instanceof Shell || parent instanceof Display || Display.getCurrent() != null){
    		//在已有的swt环境下
    		if(self != null){
    			shell = (Shell) self.doAction("create", actionContext);
    		}else{
    			shell = (Shell) create(actionContext);
    		}
    		 
    		shell.open();
    	}else{
    		//没有swt的环境下
    		if(self != null){
    			shell = (Shell) self.doAction("create", actionContext);
    		}else{
    			shell = (Shell) create(actionContext);
    		}
    		
    		//Shell shell = (Shell) create(actionContext);
    		shell.open();
    		
    		Display display = shell.getDisplay();
    		while (!shell.isDisposed ()) {
    			try{
    				if (!display.readAndDispatch ()) display.sleep ();
    			}catch(Exception e){
    				e.printStackTrace();
    			}
		    }
		    display.dispose ();
    		/*
			Thing runner = new Thing("xworker.swt.xworker.SwtRunner");
			runner.put("shellThingPath", self.getMetadata().getPath());
			runner.put("shellName", self.getMetadata().getLabel());
			runner.doAction("run", actionContext);
			*/
    	}
	}
    
    public static void ideOpenFile(ActionContext actionContext) throws IOException{
    	SwtAppIde ide = actionContext.getObject("ide");
    	File file = actionContext.getObject("file");
    	
    	String thingPath = UtilFile.getThingPathByFile(file);
    	if(thingPath != null){
    		Thing thing = World.getInstance().getThing(thingPath);
    		actionContext.peek().put("thing", thing);
    		ideOpenThing(actionContext);
    		
    		return;
    	}
    	
    	ActionContext ac = new ActionContext();
    	ac.put("parentContext", actionContext);
    	ac.put("parent", ide.getIDEShell());
    	ac.put("file", file);
    	ac.put("ide", ide);
    	
    	Thing dialog = World.getInstance().getThing("xworker.swt.xwidgets.app.FileEditorDialog");
    	Shell shell = (Shell) dialog.doAction("create", ac);
    	
    	ActionContainer actions = ac.getObject("actions");
    	actions.doAction("setFile", ac, "file", file);
    	
    	shell.open();
    }
    
    public static ActionContext ideOpenThing(ActionContext actionContext){
    	SwtAppIde ide = actionContext.getObject("ide");
    	Thing thing = actionContext.getObject("thing");
    	
    	ActionContext ac = new ActionContext();
    	ac.put("parentContext", actionContext);
    	ac.put("parent", ide.getIDEShell());
    	ac.put("thing", thing.getRoot());
    	ac.put("ide", ide);
    	
    	Thing dialog = World.getInstance().getThing("xworker.swt.xwidgets.app.ThingEditorDialog");
    	Shell shell = (Shell) dialog.doAction("create", ac);
    	shell.open();
    	
    	return ac;
    }
    
    public static void ideOpenThingAndSelectCodeLine(ActionContext actionContext){
    	//下打开事物
    	ideOpenThing(actionContext);
    }
    
    public static void url(ActionContext actionContext){
    	SwtAppIde ide = actionContext.getObject("ide");
    	String url = actionContext.getObject("url");
    }
    
    public static void executeIdeAction(ActionContext actionContext){
    	SwtAppIde ide = actionContext.getObject("ide");
    	Shell editorShell = (Shell) PoolableControlFactory.borrowControl((Shell) ide.getIDEShell(), "xworker.swt.xwidgets.prototypes.SimpleThingEditor", actionContext);
    }
    
    public static class SystemExitor implements Runnable{
    	Display display;
    	Shell self;
    	boolean hasMore;
    	Shell other = null;
    	
    	public SystemExitor(Shell self, Display display) {
    		this.self = self;
    		this.display = display;
    	}
    	
    	public void run() {
			while(true) {
				hasMore = false;
				other = null;
				
				if(display != null && display.isDisposed() == false) {
					display.syncExec(new Runnable() {
						public void run() {
							Shell[] shells = display.getShells();									
							
							for(Shell shell : shells) {
								if(shell != self && shell.getParent() != self) {
									hasMore = true;
									other = shell;
									break;
								}
							}
						}
					});
					
				}
				
				if(hasMore == false){
					logger.info("System exit");
					System.exit(0);
				}else{							
					//logger.info("Has other shell, system not exit, current=" + self + ", otherShell=" + other);
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
    }
}