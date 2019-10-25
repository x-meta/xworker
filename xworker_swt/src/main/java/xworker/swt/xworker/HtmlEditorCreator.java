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
package xworker.swt.xworker;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import xworker.swt.ActionContainer;
import xworker.swt.browser.BrowserCallback;
import xworker.swt.design.Designer;
import xworker.swt.util.PoolableControlFactory;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.UtilBrowser;
import xworker.util.XWorkerUtils;

public class HtmlEditorCreator {
	private static Logger logger = LoggerFactory.getLogger(HtmlEditorCreator.class);
	
    public static Object create(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
		Thing codeEditor = world.getThing("xworker.swt.xworker.HtmlEditor/@htmlViewForm1");
		ActionContext newContext = new ActionContext();
		newContext.put("parent", actionContext.get("parent"));
		newContext.put("utilBrowser", actionContext.get("utilBrowser"));
		newContext.put("toolbarSet", self.getString("toolbarSet"));
		newContext.put("modifyListener", actionContext.get("modifyListener"));
		newContext.put("__editThing__", actionContext.get("__editThing__"));
		Composite composite = null;
		Designer.pushCreator(self);
		try{
			composite = codeEditor.doAction("create", newContext);
		}finally{
			Designer.popCreator();
		}
		
		//创建子节点
		actionContext.peek().put("parent", newContext.get("htmlViewForm"));
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Thing htmlThing = new Thing("xworker.swt.xworker.HtmlEditor");
		htmlThing.set("browser", newContext.get("browser"));
		htmlThing.set("modifyListener", actionContext.get("modifyListener"));
		htmlThing.set("actionContext", newContext);
		htmlThing.set("composite", composite);
		
		actionContext.getScope(0).put(self.getString("name"), htmlThing);
		newContext.put("htmlThing", htmlThing);
		
		String code = UtilString.getString(self, "code", actionContext);
		if(code != null){
			htmlThing.doAction("setValue", actionContext, UtilMap.toMap("value", code));			
		}
		Designer.attach((Control) newContext.get("browser"), self.getMetadata().getPath(), actionContext);
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		composite.setData("htmlThing", htmlThing);
		return composite;        
	}
    
    public static void outerToolItemSelection(ActionContext actionContext) throws MalformedURLException, IOException, URISyntaxException{
    	Thing editThing = actionContext.getObject("__editThing__");
    	Desktop.getDesktop().browse(new URL(XWorkerUtils.getWebUrl() + "do?sc=xworker.ide.worldExplorer.swt.tools.EditDesc&thingPath=" + editThing.getMetadata().getPath()).toURI());
    }

    public static void setValue(final ActionContext actionContext){
		World world = World.getInstance();
		Thing self = (Thing) actionContext.get("self");
		
		if(self.getAttribute("modifyListener") != null){
		    ((ModifyListener) self.getAttribute("modifyListener")).modifyText(null);
		}
		
		//取全局设定中的Web地址
		String webUrl = XWorkerUtils.getWebUrl();
		
		final Browser browser = (Browser) self.getAttribute("browser");
		//设置浏览器应该显示的内容
		String value = (String) actionContext.get("value");
		if(value == null) value = "";
		ProgressListener lis = (ProgressListener) browser.getData("listener");
		if(lis != null){
		    browser.removeProgressListener(lis);
		}
		
		final String v = value;
		ProgressListener progressListener = new ProgressListener(){
			boolean finished = false;
			
			@Override
			public void changed(ProgressEvent arg0) {
			}

			@Override
			public void completed(ProgressEvent arg0) {
				if(!finished){
		            finished = true;
		            String html = v.replaceAll("(\")", "&_quot_;");
		            html = html.replaceAll("(\n)", "&_n_;");
		            html = html.replaceAll("(\r)", "&_r_;");		            
		            //browser.execute();
		            //logger.info("HtmlEditor executeScript: " + html);
		            //Thread.dumpStack();
		            executeScript(browser, "if(setHtmlContent&&typeof(setHtmlContent)==\"function\"){ setHtmlContent(\"" + html + "\");}", actionContext);
		        }
			}			
		};
		
		//logger.info("webUrl=" + webUrl);		
		//Thread.dumpStack();
		browser.addProgressListener(progressListener);
		browser.setData("listener", progressListener);
		if(webUrl != null){
			browser.setUrl(webUrl + "do?sc=xworker.swt.xworker.HtmlEditorHttp/@showHtml");
		}else{
			Thing desc = world.getThing(value);
			if(desc != null){
				String description = desc.getStringBlankAsNull("description");
				if(description != null){
					browser.setText(description);
				}
			}
		}
		browser.setData(value);       
	}

    public static Object getValue(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		Browser browser = (Browser) self.getAttribute("browser");
		Object value = browser.getData();
		return value;        
	}
    
    public static void htmlToolBarSelection(ActionContext actionContext){
    	if(!xworker.security.SecurityManager.doCheck(SwtUtils.getEnviroment(),
				UtilBrowser.class.getName(), "openThing", "abcd", actionContext)) {
    		return;
    	}
    	
    	
    	Browser browser = (Browser) actionContext.get("browser");

    	//取ViewForm数据对象的定义，要编辑的数据对象放置在ViewForm的数据对象中
    	Object value = browser.getData();

    	ActionContext ctx = new ActionContext();
    	ctx.put("value", value);
    	ctx.put("toolbarSet", actionContext.get("toolbarSet"));
    	ctx.put("htmlThing", actionContext.get("htmlThing"));

    	Shell newShell = (Shell) PoolableControlFactory.borrowControl(browser.getShell(), "xworker.swt.xworker.HtmlEditor/@shell", ctx);
    	//虽然newShell创建时传入的parent是browser.getShell()，但是 newShell.getParent()还是空，所以设置到data中
    	newShell.setData("shell_parent", browser.getShell());
    	ActionContext newContext = (ActionContext) newShell.getData("_poolActionContext");
    	newContext.put("value", value);
    	newContext.put("toolbarSet", actionContext.get("toolbarSet"));
    	newContext.put("htmlThing", actionContext.get("htmlThing"));

    	((ActionContainer) newContext.get("actions")).doAction("init");
    	newShell.setVisible(true);
    }
    
    public static void homeToolItemSelection(ActionContext actionContext){
    	Browser browser = (Browser) actionContext.get("browser");
    
    	((Thing) actionContext.get("htmlThing")).doAction("setValue", actionContext, UtilMap.toParams(new Object[]{"value", browser.getData()}));
    }
    
    public static void backToolItemSelection(ActionContext actionContext){
    	Browser browser = (Browser) actionContext.get("browser");
    	browser.back();

    	if(!browser.isBackEnabled()){
    		((Thing) actionContext.get("htmlThing")).doAction("setValue", actionContext, UtilMap.toParams(new Object[]{"value", browser.getData()}));
    	}
    }
    
    public static void forwardToolItemSelection(ActionContext actionContext){
    	Browser browser = (Browser) actionContext.get("browser");
    	browser.forward();
    }
    
    public static void stoptoolItemSelection(ActionContext actionContext){
    	Browser browser = (Browser) actionContext.get("browser");
    	browser.stop();
    }
    
    public static void refreshToolItemSelection(ActionContext actionContext){
    	Browser browser = (Browser) actionContext.get("browser");
    	browser.refresh();
    }
    
    public static void init(ActionContext actionContext){
    	final Browser browser = (Browser) actionContext.get("browser");
    	final ToolItem backToolItem = (ToolItem) actionContext.get("backToolItem");
    	final ToolItem forwardToolItem = (ToolItem) actionContext.get("forwardToolItem");
    	
    	ProgressListener progressListener = new ProgressListener(){

			@Override
			public void changed(ProgressEvent arg0) {
			}

			@Override
			public void completed(ProgressEvent arg0) {
				if(SwtUtils.isRWT()) {
		    		backToolItem.setEnabled(true);
		        	forwardToolItem.setEnabled(true);
		    	}else {
			    	backToolItem.setEnabled(browser.isBackEnabled());
			    	forwardToolItem.setEnabled(browser.isForwardEnabled());
		    	}
			}
    	};
  
    	browser.addProgressListener(progressListener);

    	if(SwtUtils.isRWT()) {
    		backToolItem.setEnabled(true);
        	forwardToolItem.setEnabled(true);
    	}else {
	    	backToolItem.setEnabled(browser.isBackEnabled());
	    	forwardToolItem.setEnabled(browser.isForwardEnabled());
    	}

    	//添加utilBrowser如果存在
    	UtilBrowser utilBrowser = (UtilBrowser) actionContext.get("utilBrowser");
    	if(utilBrowser != null && utilBrowser instanceof StatusTextListener){
    	    utilBrowser.attach(browser);
    	    //browser.addStatusTextListener(utilBrowser);
    	}
    }
    
    public static void okButtonSelection(final ActionContext actionContext){
    	Browser browser = (Browser) actionContext.get("browser");
    	if(SwtUtils.isRWT()) {
    		String script = "return CKEDITOR.instances.data.getData()";
    		SwtUtils.evaluateBrowserScript(browser, script, new BrowserCallback() {
				@Override
				public void evaluationFailed(Exception exception) {
					logger.error("ok button exception", exception);
				}

				@Override
				public void evaluationSucceeded(Object result) {
					((Thing) actionContext.get("htmlThing")).doAction("setValue", actionContext, UtilMap.toParams(new Object[]{"value", result}));
					
			    	Shell shell = (Shell) actionContext.get("shell");
			    	shell.setVisible(false);
			    	PoolableControlFactory.returnControl((Control) actionContext.get("parent"), "xworker.swt.xworker.HtmlEditor/@shell", shell);
				}
    			
    		}, actionContext);
    	}else {
	    	Object value = null;
	    	try{
	    	    //SWT 3.5or3.6
	    	    value = browser.evaluate("return CKEDITOR.instances.data.getData()");
	    	}catch(Throwable e){   
	    	    //SWT 3.5以下版本
	    	    browser.execute("window.status=getContents();");
	    	    value =  browser.getData("query");
	    	}
	
	    	((Thing) actionContext.get("htmlThing")).doAction("setValue", actionContext, UtilMap.toParams(new Object[]{"value", value}));
	
	    	Shell shell = (Shell) actionContext.get("shell");
	    	shell.setVisible(false);
	    	PoolableControlFactory.returnControl((Control) actionContext.get("parent"), "xworker.swt.xworker.HtmlEditor/@shell", shell);
    	}
    }
    
    public static void openOutterButton(ActionContext actionContext){
    	Thing htmlThing = actionContext.getObject("htmlThing");
    	System.out.println(htmlThing);
    }
    
    public static void cancelButtonSelection(ActionContext actionContext){
    	Shell shell = (Shell) actionContext.get("shell");

    	shell.setVisible(false);
    	PoolableControlFactory.returnControl((Control) actionContext.get("parent"), "xworker.swt.xworker.HtmlEditor/@shell", shell);
    }
    
    public static void executeScript(Browser browser, String script, ActionContext actionContext) {
    	if(SwtUtils.isRWT()) {
    		Thing swt = World.getInstance().getThing("xworker.swt.SWT");
    		swt.doAction("rwtBrowserEvaluate", actionContext, "browser", browser, "script", script, "callback", null);
    	}else {
    		browser.execute(script);
    	}
    }
    
    public static void initCode(final ActionContext actionContext){
    	//World world = World.getInstance();
    	final Browser browser = (Browser) actionContext.get("browser");
    	
    	//取全局设定中的Web地址
    	String webUrl = XWorkerUtils.getWebUrl();

    	//设置浏览器应该显示的内容
    	final String v = (String) actionContext.get("value");
    	boolean finished = false;
    	actionContext.getScope(0).put("finished", finished);

    	ProgressListener progressListener = new ProgressListener(){
    		boolean f = false;
			@Override
			public void changed(ProgressEvent arg0) {
			}

			@Override
			public void completed(ProgressEvent arg0) {
				if(!f){
    	            f = true;
    	            
    	            String value = v;
    	            if(value == null) value = "";
    	            String html = value.replaceAll("(\")", "&_quot_;");
    	            html = html.replaceAll("(\n)", "&_n_;");
    	            html = html.replaceAll("(\r)", "&_r_;");
    	            //browser.execute("setContentsFirst('" + html + "');");
    	            executeScript(browser, "setContentsFirst('" + html + "');", actionContext);
    	        }
			}
    		
    	};

    	try{
    	    browser.setJavascriptEnabled(true);
    	}catch(Throwable t){
    	}

    	browser.addProgressListener(progressListener);
    	browser.setUrl(webUrl + "do?sc=xworker.swt.xworker.HtmlEditorHttp/@showEditorHtml&init=true");

    	if(!SwtUtils.isRWT()) {
	    	StatusTextListener statusListener = new StatusTextListener(){
	
				@Override
				public void changed(StatusTextEvent event) {
					//if(event instanceof LocationEvent){
	   	            // return;
	   	         	//}
	   	         
		   	         Browser browser = (Browser) event.getSource();
		   	         //System.out.println(event.text);
		   	    	 if(event.text.startsWith("html_edit_content")){
		   	    	 	 //取编辑器内容的值
		   	    		 String content = event.text.substring(17, event.text.length());
		   	    		 //System.out.println(content);
		   	             browser.setData("query", content);
		   	    	 }    
				}
				
	    		
	    	};
	    	browser.addStatusTextListener(statusListener);
    	}

    	//加入shell监听，当shell关闭时不释放shell资源
    	((Shell) actionContext.get("shell")).addShellListener(new ShellListener(){

			@Override
			public void shellActivated(ShellEvent arg0) {
			}

			@Override
			public void shellClosed(ShellEvent event) {
				Shell shell = (Shell) event.widget;
				shell.setVisible(false);
				Control parent = (Control) shell.getData("shell_parent");
    	        PoolableControlFactory.returnControl(parent, "xworker.swt.xworker.HtmlEditor/@shell", shell);
    	        event.doit = false;
			}

			@Override
			public void shellDeactivated(ShellEvent arg0) {
			}

			@Override
			public void shellDeiconified(ShellEvent arg0) {
			}

			@Override
			public void shellIconified(ShellEvent arg0) {
			}
    		
    	});
    	   
    }
    
    public static void insertButtonSelection(ActionContext actionContext){
    	ActionContext ac = new ActionContext();
    	ac.put("parent", actionContext.get("shell"));
    	ac.put("browser", actionContext.get("browser"));

    	Thing thing = World.getInstance().getThing("xworker.swt.xworker.dialogs.HtmlEditorInsertDialog");
    	Shell shell = (Shell) thing.doAction("create", ac);
    	shell.open();
    }
}