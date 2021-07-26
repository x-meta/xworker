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
package xworker.swt.util;

import java.lang.ref.WeakReference;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.lang.actions.ActionContainer;
import xworker.lang.executor.Executor;
import xworker.swt.design.Designer;
import xworker.util.XWorkerUtils;

public class UtilBrowserCreator {
	private static final String TAG = UtilBrowserCreator.class.getName();
	
    public static void create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
    	UtilBrowser utilBrowser = null;
    	Control parent = (Control) actionContext.get("parent");
    	String utilBrowserVar = self.getString("utilBrowserVar");
		if(utilBrowserVar != null && !utilBrowserVar.equals("")){
		    utilBrowser = (UtilBrowser) OgnlUtil.getValue(utilBrowserVar, actionContext);
		    if(utilBrowser == null){
		        //log.info("UtilBrowser create error, utilBrowserVar not exists, utilBrowserVar=" + self.getString("utilBrowserVar"));
		    	utilBrowser = new UtilBrowser(self, parent.getDisplay(), actionContext);
			    actionContext.getScope(0).put(self.getString("name"), utilBrowser);
		    }
		}else{			
		    utilBrowser = new UtilBrowser(self, parent.getDisplay(), actionContext);
		    actionContext.getScope(0).put(self.getString("name"), utilBrowser);
		}
		
		if(parent instanceof Browser){
		    utilBrowser.attach((Browser) parent);
		}        
	}

    public static void shell(ActionContext actionContext){
    	String path = (String) actionContext.get("path");
    	World world = World.getInstance();
		Thing thing = world.getThing(path);
		Shell shell = Display.getCurrent().getActiveShell();
		ActionContext ac = new ActionContext(actionContext);
		ac.put("parent", shell);
		Shell newShell = (Shell) thing.doAction("create", ac);
		newShell.open();       
	}

    @SuppressWarnings("unchecked")
	public static void shellSingle(ActionContext actionContext){
        World world = World.getInstance();
		
        String key = "utilbrowser_shell_" + actionContext.get("path");
        WeakReference<Shell> shellref = (WeakReference<Shell>) world.getData(key); 
		if(shellref != null && shellref.get() != null && !shellref.get().isDisposed()){
		    final Shell shell = shellref.get();
		    
		    shell.getDisplay().asyncExec(new Runnable(){
		    	public void run(){
		    		shell.setVisible(true);
		    	}
		    });                
		    return;
		}
		    
		Thing thing = world.getThing((String) actionContext.get("path"));
		Shell shell = Display.getCurrent().getActiveShell();
		ActionContext ac = new ActionContext(actionContext);
		ac.put("parent", shell);
		Shell newShell = (Shell) thing.doAction("create", ac);
		WeakReference<Shell> wr = new WeakReference<Shell>(newShell);
		world.setData(key, wr); 
		newShell.open();        
	}

    public static void shellTh(ActionContext actionContext){
		final String path = (String) actionContext.get("path");
		final ActionContainer actions = (ActionContainer) actionContext.get("actions");
		final World world = World.getInstance();
		
		new Thread(new Runnable(){
	        public void run(){
	            ActionContext ac = new ActionContext(actions.getActionContext());
	            Display display = new Display ();
	            ac.put("parent", display);
	            Thing viewObject = world.getThing(path);
	            Shell shell = (Shell) viewObject.doAction("create", ac);
	            shell.open ();
	    	    while (!shell.isDisposed ()) {
	    	        if (!display.readAndDispatch ()) display.sleep ();
	    	    }
	    	    display.dispose ();
	        }
	    }).start();        
	}

    @SuppressWarnings("unchecked")
	public static void shellThSingle(ActionContext actionContext){
        final World world = World.getInstance();
        final String path = (String) actionContext.get("path");
        final String key = "utilbrowser_th_shell_" + path;
    	WeakReference<Shell> shellref = (WeakReference<Shell>) world.getData(key); 
		if(shellref != null && shellref.get() != null && !shellref.get().isDisposed()){
		    final Shell shell = shellref.get();
		    
		    shell.getDisplay().asyncExec(new Runnable(){
		    	public void run(){
		    		shell.setVisible(true);
		    	}
		    });                
		    return;
		}
		
		final ActionContainer actions = (ActionContainer) actionContext.get("actions");
		
		new Thread(new Runnable(){
			public void run(){
				ActionContext ac = new ActionContext(actions.getActionContext());
	            Display display = new Display ();
	            ac.put("parent", display);
	            Thing viewObject = world.getThing(path);
	            Shell shell = (Shell) viewObject.doAction("create", ac);
	            WeakReference<Shell> wr = new WeakReference<Shell>(shell);
	            world.setData(key, wr); 
	            shell.open ();
	    	    while (!shell.isDisposed ()) {
	    	        if (!display.readAndDispatch ()) display.sleep ();
	    	    }
	    	    display.dispose ();
			}
		});
	}

    public static void composite(ActionContext actionContext){    	
		Thing compositeThing = World.getInstance().getThing((String) actionContext.get("path"));
		ActionContainer actions = XWorkerUtils.getIdeActionContainer();
		actions.doAction("openTab", UtilMap.toParams(new Object[]{"compositeThing", compositeThing})); 
	}

    public static void downloadProduct(ActionContext actionContext){
        World world = World.getInstance();
		
		Thing thing = world.getThing("share.xworker.apps.versioncontrol.client.VersionControlClient/@shell");
		Shell shell = Display.getCurrent().getActiveShell();
		
		actionContext.put("parent", shell);
		Shell newShell = (Shell) thing.doAction("create", actionContext);
		Text productText = (Text) actionContext.get("productText");
		productText.setText((String) actionContext.get("path"));
		newShell.open();        
	}

    public static void webControl(ActionContext actionContext){
    	/*
    	World world = World.getInstance();
    	
    	
		Thing globalCfg = world.getThing("_local.xworker.config.GlobalConfig");
		String httpServer = null;
		if(globalCfg != null){
		    globalCfg.getString("webUrl");
		}
		
		if(httpServer == null || httpServer.equals("")){
		   httpServer = "http://localhost:9001/";
		}*/
		String url = XWorkerUtils.getWebUrl() + "do?sc=" + actionContext.get("path");
		
		//log.info("dfdfdfd");
		ActionContainer explorerActions = Designer.getExplorerActions();
		if(explorerActions != null){
		    explorerActions.doAction("openUrl", UtilMap.toParams(new Object[]{"url", url, "name", actionContext.get("path")}));
		}else{
		    Executor.warn(TAG, "UtilBrowser: can not find explorerActions from Designer");
		}       
	}

    public static void html_edit_content(ActionContext actionContext){
    	String path = (String) actionContext.get("path");
    	ActionContainer actions = (ActionContainer) actionContext.get("actions");
    	
		//取编辑器内容的值
		String content = path;
		String[] sizes = content.split(":");
		int width = Integer.parseInt(sizes[1]) + 20;
		if(width > 420){
		    width = 420;
		}
		
		ActionContext ac = actions.getActionContext();
		Shell shell = (Shell) ac.get("shell");
		shell.setSize(420, Integer.parseInt(sizes[0]) + 3);
		shell.setVisible(true);
		//browser.setFocus();
		//browser.setData("query", content);        
	}

    public static void html_edit_content1(ActionContext actionContext){
    	String path = (String) actionContext.get("path");
    	ActionContainer actions = (ActionContainer) actionContext.get("actions");
    	
		//取编辑器内容的值
		String content = path;
		String[] sizes = content.split(":");
		int width = Integer.parseInt(sizes[1]) + 20;
		if(width > 420){
		    width = 420;
		}
		
		ActionContext ac = actions.getActionContext();
		Shell shell = (Shell) ac.get("shell");
		shell.setSize(420, Integer.parseInt(sizes[0]) + 3);
				
		//设置位置，避免和鼠标重叠以及避免显示在屏幕之外
		SwtUtils.setShellRelateLocation(shell, shell.getDisplay().getCursorLocation(), new Point(20,20));
		//显示在最上层，可能是swt的bug，某个版本之后它会再主窗体之后从而显示不出来了，问题可能已经结局：2013-07-10
		shell.moveAbove(shell.getParent());
		shell.setVisible(true);		
		//shell.setActive();
		
		//shell.open();
		//shell.forceActive();		
		//browser.setFocus();
		//browser.setData("query", content);       
	}

}