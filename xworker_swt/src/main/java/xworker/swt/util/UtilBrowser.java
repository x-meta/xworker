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

import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.lang.actions.ActionContainer;
import xworker.lang.executor.Executor;
import xworker.swt.design.Designer;

/**
 * <p>浏览器工具类，为浏览器中的网页提供一些交互的能力，比如点击一个连接打开编辑的事物等。</p>
 * 
 * 具体文档可以参看事物xworker.swt.util.UtilBrowser。
 * 
 * <p>如果绑定了一个浏览器，且浏览器的页面引入了/js/xworker/InnerBrowserUtil.js脚本，
 * 那么就可以使用invoke('aaa:bbb?ccc=ddd&amp;eee=fff')或xw_invoke(('aaa:bbb?ccc=ddd&amp;eee=fff')的方式调用UtilBrowser了。
 *其中aaa是方法名，bbb是主参数，ccc和eee等是附加参数，ddd和fff等是对应的附加参数的值。
 * 如：thing:xworker.core.project.Project意思是打开并且编辑xworker.core.project.Project这个事物。</p>
 * 
 * @author zyx
 *
 */
public class UtilBrowser implements StatusTextListener{
	private static final String TAG = UtilBrowser.class.getName();
	
	ActionContext actionContext;
	ActionContainer actions;
	Display display;
	Browser browser;
	Thing utilBrowserThing;
	UtilBrowserListener listener;
	
	/** -1 表示还未判断，0 表示不是， 1 表示是 */ 
	private static byte isWebKit = -1;
	
	public static UtilBrowser attach(Browser browser, ActionContainer actions, Display display){
		UtilBrowser ub = new UtilBrowser(display, actions);
		ub.attach(browser);
		return ub;
	}
	
	public static UtilBrowser attach(Browser browser, ActionContext actionContext){
		UtilBrowser ub = new UtilBrowser(browser.getDisplay(), actionContext);
		ub.attach(browser);
		return ub;
	}
	
	public void setListener(UtilBrowserListener listener) {
		this.listener = listener;
	}
	
	public static synchronized boolean isWebKit(){
		if(isWebKit == -1){
			new Thread(new Runnable(){
				public void run(){
					Display display = new Display();
					Shell shell = new Shell(display);
					Browser browser = new Browser(shell, SWT.NONE);
					try{
						browser.setUrl(new File(World.getInstance().getPath() + "/webroot/js/xworker/InnerBrowserUtil.js").toURI().toString());
						Object value = browser.evaluate("var UA = navigator.userAgent.toLowerCase();return /webkit/i.test(UA);");
						if(value != null && "true".equals(value)){
							isWebKit = 1;
						}else{
							isWebKit = 0;
						}
					}catch(Exception e){
						
					}finally{						
						display.dispose();
					}
				}
			}).start();
				
				
			int count = 0;
			while(isWebKit == -1){					
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				
				count++;
				if(count > 20){
					break;
				}
			}
		}
		
		return isWebKit == 1;
	}
	
	/**
	 * 一般是编辑器(IDE)的构造方法，传入处理openThing,thing,url,tab等编辑相关的默认方法。
	 * 
	 * @param display
	 * @param actions
	 */
	public UtilBrowser(Display display, ActionContainer actions){
		this.actions = actions;
		this.display = display;
	}
	
	/**
	 * 一般的构造方法，用于非编辑器的场合。
	 * 
	 * @param display
	 * @param actionContext
	 */
	public UtilBrowser(Display display, ActionContext actionContext){
		this.actions = null;
		this.actionContext = actionContext;
		this.display = display;
	}
	
	/**
	 * 一般用于SWT创建时绑定到browser的构造方法，用于UtilBrowser事物的create方法中。
	 * 
	 * @param utilBrowserThing
	 * @param display
	 * @param actionContext
	 */
	public UtilBrowser(Thing utilBrowserThing, Display display, ActionContext actionContext){
		this.actions = null;
		this.actionContext = actionContext;
		this.display = display;
		this.utilBrowserThing = utilBrowserThing;
	}
	
	public void attach(Browser browser){		
		this.browser = browser;
		
		try{
			browser.addStatusTextListener(this);
		}catch(Throwable t){			
			//t.printStackTrace();
		}
		try{
			//swt 3.5
			UtilBrowserJavaScriptFunction.attach(this);
		}catch(Throwable t){		
			//t.printStackTrace();
		}
	}
	
	public void handle(String message){
		try{
			if(listener != null && listener.handleBrowserMessage(message)) {
				//监听器已处理
				return;
			}
			
			//Executor.info(TAG, message);
			//通过?号后可以附加参数，同url的规则
			Map<String, Object> params = new HashMap<String, Object>();
			int index = message.indexOf("?");
			if(!message.startsWith("url:") && index != -1){
				String pstr = message.substring(index + 1, message.length());
				message = message.substring(0, index);
				for(String str : pstr.split("[&]")){
					String[] ss = str.split("[=]");
					try {
						if(ss.length > 1){
							params.put(URLDecoder.decode(ss[0], "UTF-8"), URLDecoder.decode(ss[1], "UTF-8"));
						}
					} catch (Exception e) {
					}
				}
			}
			
			//params.put("browser", browser);
			params.put("utilBrowser", this);
			
			//如果链接中增加了nosecurity, 那么不判断权限校验
			boolean nosecurity = false;
			if("true".equals(params.get("nosecurity"))) {
				nosecurity = true;
			}
			
			if(message != null && !"".equals(message)){
				if(message.startsWith("openThing:")){
					//开打事物一个事物编辑
					String thingName = message.substring(10, message.length());				
					Thing thing = World.getInstance().getThing(thingName);
					params.put("thing", thing);
					if(nosecurity || xworker.security.SecurityManager.doCheck(SwtUtils.getEnviroment(),
							UtilBrowser.class.getName(), "openThing", thingName, actionContext)) {											
						runExplorerAction("openThing", params);
					}else {
						Executor.info(TAG, "SecurityManager: current session has no 'openThing' permission");
					}
				}else if(message.startsWith("createThing:")){
					//开打新建事物对话框
					String thingName = message.substring(12, message.length());
					if(nosecurity || xworker.security.SecurityManager.doCheck(SwtUtils.getEnviroment(),
							UtilBrowser.class.getName(), "createThing", thingName, actionContext)) {
						Thing createThing = new Thing("xworker.ide.worldexplorer.actions.ExplorerActions/@CreateThing");
						createThing.put("descriptor", thingName);
						createThing.getAction().run(actionContext);
					}else {
						Executor.info(TAG, "SecurityManager: current session has no 'createThing' permission");
					}
				}else if(message.startsWith("thing:")){
					//打开一个事物编辑，同openThing
					String thingName = message.substring(6, message.length());
					if(nosecurity || xworker.security.SecurityManager.doCheck(SwtUtils.getEnviroment(),
							UtilBrowser.class.getName(), "openThing", thingName, actionContext)) {	
						Thing thing = World.getInstance().getThing(thingName);
						params.put("thing", thing);
						runExplorerAction("openThing", params);
					}else {
						Executor.info(TAG, "SecurityManager: current session has no 'openThing' permission");
					}
				}else if(message.startsWith("thingTab:")){
					//打开一个事物编辑，同openThing
					String thingName = message.substring(9, message.length());				
					if(nosecurity || xworker.security.SecurityManager.doCheck(SwtUtils.getEnviroment(),
							UtilBrowser.class.getName(), "thingTab", thingName, actionContext)) {	
						Thing thing = World.getInstance().getThing(thingName);
						params.put("thing", thing);
						runExplorerAction("thingTab", params);
					}else {
						Executor.info(TAG, "SecurityManager: current session has no 'thingTab' permission");
					}
				}else if(message.startsWith("tab:")){
					//在编辑器中打开一个tab
					String tabUrl = message.substring(4, message.length());	
					if(nosecurity || xworker.security.SecurityManager.doCheck(SwtUtils.getEnviroment(),
							UtilBrowser.class.getName(), "openTab", tabUrl, actionContext)) {
						String compositeThingPath = getBaseUrl(tabUrl);			
						Thing compositeThing = World.getInstance().getThing(compositeThingPath);
						params.put("compositeThing", compositeThing);		
						params.put("path", compositeThingPath);
						params.put("title", compositeThing.getMetadata().getLabel());
						runExplorerAction("openTab", params);
					}else {
						Executor.info(TAG, "SecurityManager: current session has no 'openTab' permission");
					}
				}else if(message.startsWith("url:")){
					//在编辑器中打开一个url
					String url = message.substring(4, message.length());
					if(nosecurity || xworker.security.SecurityManager.doCheck(SwtUtils.getEnviroment(),
							UtilBrowser.class.getName(), "openUrl", url, actionContext)) {
						params.put("url", url);
						params.put("name", url);
						runExplorerAction("openUrl", params);
					}else {
						Executor.info(TAG, "SecurityManager: current session has no 'openUrl' permission");
					}
				}else if(message.startsWith("action:")){
					//在编辑器的环境中执行一个指定的动作
					String thingName = message.substring(7, message.length());
					if(nosecurity || xworker.security.SecurityManager.doCheck(SwtUtils.getEnviroment(),
							UtilBrowser.class.getName(), "action", thingName, actionContext)) {
						String[] ps = splitParams(thingName);
						thingName = ps[0];
						if(ps[1] != null){
							params.putAll(UtilString.getParams(ps[1]));
						}
						
						Thing thing = World.getInstance().getThing(thingName);
						//params.put("action", thing.getAction());
						if(thing != null){
							//runExplorerAction("doAction", params);
							try{
								thing.getAction().run(actionContext, params);
							}catch(Exception e){
								Executor.error(TAG, "runaction error, action=" + thingName, e);
							}
						}else{
							Executor.warn(TAG, "Action not exists, message=" + message);
						}
					}else {
						Executor.info(TAG, "SecurityManager: current session has no 'action' permission");
					}
				}else if(message.startsWith("actions:")){
					//在编辑器的环境中执行一个指定的动作				
					String actionName = message.substring(8, message.length());
					if(xworker.security.SecurityManager.doCheck(SwtUtils.getEnviroment(),
							UtilBrowser.class.getName(), "action", actionName, actionContext)) {
						String[] ps = splitParams(actionName);
						actionName = ps[0];
						if(ps[1] != null){
							params.putAll(UtilString.getParams(ps[1]));
						}
						
						String actionsName = null;
						String ss[] = actionName.split("[:]");
						if(ss.length == 2){
							actionsName = ss[0];
							actionName = ss[1];
						}else{
							actionsName = "actions";
							actionName = ss[0];
						}
						ActionContainer ac = actionContext.getObject(actionsName);
						if(ac != null){
							ac.doAction(actionName, actionContext, params);
						}else{
							Executor.warn(TAG, "UtilBrowser: actions not exists, message=" + message);
						}
					}else {
						Executor.info(TAG, "SecurityManager: current session has no 'action' permission");
					}
				}else if(message.startsWith("run:")){
					//在UtilBrowser绑定的环境下执行一个指定的动作，和action的差别是执行的环境不同
					String thingName = message.substring(4, message.length());		
					if(nosecurity || xworker.security.SecurityManager.doCheck(SwtUtils.getEnviroment(),
							UtilBrowser.class.getName(), "run", thingName, actionContext)) {
						Thing thing = World.getInstance().getThing(thingName);				
						if(thing != null){
							ActionContext ac = actionContext;
							if(ac == null){
								if( actions != null){
									ac = actions.getActionContext();
								}else{
									ac = new ActionContext();
								}
							}
							thing.getAction().run(ac, params);
						}
					}else {
						Executor.info(TAG, "SecurityManager: current session has no 'run' permission");
					}
				}else if(message.startsWith("runThing:")){
					//在UtilBrowser绑定的环境下执行一个指定的动作，和action的差别是执行的环境不同
					String method = "run";
					String thingName = message.substring(9, message.length());			
					if(nosecurity || xworker.security.SecurityManager.doCheck(SwtUtils.getEnviroment(),
							UtilBrowser.class.getName(), "runThing", thingName, actionContext)) {
						String ss[] = thingName.split("[:]");
						if(ss.length > 1){
							thingName = ss[0];
							method = ss[1];
						}
						Thing thing = World.getInstance().getThing(thingName);				
						if(thing != null){
							ActionContext ac = actionContext;
							if(ac == null || actions != null){
								ac = actions.getActionContext();
							}
							thing.doAction(method, ac, params);
						}else{
							Executor.warn(TAG, "UtilBroser： thing is null, path=" + thingName);
						}
					}else {
						Executor.info(TAG, "SecurityManager: current session has no 'runThing' permission");
					}
				}else if(message.startsWith("webkit:")){
					String webkit = message.substring(7, message.length());
					if("true".equals(webkit)){
						isWebKit = 1;
					}else{
						isWebKit = 0;
					}
				}else{
					//默认的情况使用xworker.swt.util.UtilBrowser负责执行
					if(nosecurity || xworker.security.SecurityManager.doCheck(SwtUtils.getEnviroment(),
							UtilBrowser.class.getName(), "other", message, actionContext)) {
						int index1 = message.indexOf(":");
						if(index1 != -1){
							String prefix = message.substring(0, index1);
							String path = message.substring(index1 + 1, message.length());
							Thing uthing = utilBrowserThing;
							if(uthing == null){
								uthing = World.getInstance().getThing("xworker.swt.util.UtilBrowser");
							}
							if(uthing != null){
								ActionContext ac = actionContext;
								if(ac == null && actions != null){
									ac = actions.getActionContext();
								}
								if(ac == null){
									ac = new ActionContext();
									ac.put("utilBrowser", this);
									ac.put("path", path);
								}						
								params.put("path", path);
								params.put("utilBrowser", this);
								uthing.doAction(prefix, ac, params);
							}
						}else{
							if(utilBrowserThing != null){
								utilBrowserThing.doAction("message", actionContext);
							}
						}
					}else {
						Executor.info(TAG, "SecurityManager: current session has no 'other' permission");
					}
				}
			}
		}catch(Exception e){
			Executor.error(TAG, "handler utilbrowser error", e);
		}
	}
	
	public void changed(StatusTextEvent event) {
		if(actions == null){
			return;
		}
		
		String message = event.text;	
		//System.out.println(message);
		handle(message);
	}	
	
	public String getBaseUrl(String str){
		int index = str.indexOf("?");
		if(index != -1){
			return str.substring(0, index);
		}else{
			return str;
		}
	}
	
	public Map<String, Object> getParameters(String str){
		Map<String, Object> params = new HashMap<String, Object>();
		
		String str1 = str;
		int index = str.indexOf("?");
		if(index != -1){
			str1 = str.substring(index + 1, str.length());
		}
		
		String[] ps = str1.split("[&]");
		for(int i=0; i<ps.length; i++){
			int index1 = ps[i].indexOf("=");
			if(index1 != -1){
				params.put(ps[i].substring(0, index1), ps[i].substring(index1 + 1, ps[i].length()));
			}else{
				params.put(ps[i], null);
			}			
		}
		
		return params;
	}
	
	public Map<String, Object> objsToMap(Object[] objs){
		Map<String, Object> m = new HashMap<String, Object>();
		for(int i=0; i<objs.length; i++){
			m.put((String) objs[i], objs[i + 1]);
			i++;
		}
		
		return m;
	}
	
	/**
	 * 执行和浏览器相关的动作。
	 * 
	 * @param name
	 * @param parameters
	 */
	public void runExplorerAction(String name, Map<String, Object> parameters){
		Display explorerDisplay = Designer.getExplorerDisplay();
		ActionContainer explorerActions = Designer.getExplorerActions();
		if(explorerDisplay != null && explorerActions != null){
			explorerDisplay.asyncExec(new Run(name, parameters, explorerActions));
		}else{
			runAction(name, parameters);
		}
	}
	
	public void runAction(String name, Map<String, Object> parameters){
		if(parameters != null){
			parameters.put("browser", browser);
		}
		if(display == null){
			actions.doAction(name, parameters);
		}else{
			display.asyncExec(new Run(name, parameters, actions));
		}
	}
	
	public String[] splitParams(String str){
		int idx = str.indexOf("?");
		if(idx != -1){
			String ps = str.substring(idx + 1, str.length());
			String p = str.substring(0, idx);
			return new String[]{p, ps};
		}else{
			return new String[]{str, null};
		}
		
	}
	public Map<String, String> getParams(String str){
		int idx = str.indexOf("?");
		if(idx != -1){
			String ps = str.substring(idx + 1, str.length());
			return UtilString.getParams(ps);
		}else{
			return null;
		}
	}
	
	public static class Run implements Runnable{
		ActionContainer actions;
		String name;
		Map<String, Object> parameters;
		Object result;
		
		public Run(String name, Map<String, Object> parameters, ActionContainer actions){
			this.name = name;
			this.parameters = parameters;
			this.actions = actions;
		}
		
		public void run(){
			if(actions == null) {
				Executor.warn(TAG, "Actions is null, action=" + name);
				return;
			}
			
			ActionContext ac = actions.getActionContext();
			Shell shell = (Shell) ac.get("shell");
			if(shell != null){
				shell.forceActive();
			}
			result = actions.doAction(name, parameters);
		}
		
		public Object getResult(){
			return result;
		}
	}
}