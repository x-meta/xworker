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
package xworker.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionListener;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.ThingManagerListener;
import org.xmeta.World;

import xworker.lang.executor.Executor;
import xworker.swt.ActionContainer;

public  class Debuger implements ActionListener, ThingManagerListener{
	private static Logger log = LoggerFactory.getLogger(Debuger.class);
	private static Debuger debuger = new Debuger();
	
	public static final String BREAK_POINT_INIT = "initBreakPoint";
	public static final String BREAK_POINT_SUCCESS = "successBreakPoint";
	public static final String BREAK_POINT_EXCEPTION = "exceptionBreakPoint";
	public static final String TRACE = "trace";
	
	boolean enabled = false;
	/** 是否打印动作的执行信息 */
	boolean trace = false;
	Map<String, PerformanceRecord> performanceRecords = new HashMap<String, PerformanceRecord>();
	
	/** 最后出错的10个异常 */
	List<ExceptionRecord> lastExceptions = new ArrayList<ExceptionRecord>();
	
	/** 线程调试信息 */
	private static Map<Thread, DebugInfo> debugInfos = new WeakHashMap<Thread, DebugInfo>();
	
	/** 调试控制台的变量空间 */
	private static ActionContext debugConsoleContext = null;
	
	/** 调试控制台的线程，调试控制台不能被调试，否则会锁死 */
	private static Thread debugConsoleThread = null;
	
	private ActionRecorder actionRecorder = new ActionRecorder(this);
	
	/** 动作的断点信息 */
	private Map<String, Thing> actionDebugInfo = new HashMap<String, Thing>();
	/** 函数的断点（设计）信息 */
	private Map<String, Thing> functionDebugInfo = new HashMap<String, Thing>();
	
	private Debuger(){	
		renitDebugInfo();
	}
	
	public static Debuger getDebuger(){
		return debuger;
	}
	
	/**
	 * 返回调试信息。
	 * 
	 * @return
	 */
	public static DebugInfo getDebugInfo(Thread thread){
		DebugInfo debugInfo = debugInfos.get(thread);
		if(debugInfo == null){
			debugInfo = new DebugInfo();			
			debugInfos.put(thread, debugInfo);
		}		

		return debugInfo;
	}
	
	public static Thread getDebugConsoleThread(){
		return debugConsoleThread;
	}
	
	public static void showActionContext(ActionContext actionContext){
		
	}
	
	/** 
	 * 显示调试控制台。
	 */
	public synchronized static void showDebugConsole(){
		boolean startNewConsole = false;
		if(debugConsoleContext == null){
			startNewConsole = true;
		}else{
			final Shell shell = (Shell) debugConsoleContext.get("shell");
			if(shell == null || shell.isDisposed()){
				startNewConsole = true;
			}else{
				//如果调试窗口已经运行了
				shell.getDisplay().asyncExec(new Runnable(){
					public void run(){
						shell.forceActive();
					}
				});
			}
		}
		
		if(startNewConsole){
			//调试窗口未运行或已关闭
			Thread consoleThread = new Thread(){
				public void run(){
					Display display = new Display();
					ActionContext ac = new ActionContext();
					debugConsoleContext = ac;
					ac.put("parent", display);
					
					Thing debugThing = World.getInstance().getThing("xworker.ide.debug.swt.DebugConsole/@shell");
					Shell shell = (Shell) debugThing.doAction("create", ac);
					shell.open();
					
					while (!shell.isDisposed()) {
						try {
							if (!display.readAndDispatch())
								display.sleep();
						} catch (Exception e) {		
							log.error("exception", e);
						} catch (Error err){
							log.error("error", err);
						}
					}	
				}
			};
			
			debugConsoleThread = consoleThread;
			consoleThread.start();
		}
	}
	
	/**
	 * 显示调试控制台。
	 * 
	 * @param thread
	 * @param actionContext
	 */
	public synchronized static void showDebugConsole(final Thread thread, final ActionContext actionContext, final DebugInfo debugInfo){
		//调试控制台不能被调试，否则会锁死
		if(thread == debugConsoleThread){
			return;
		}
		
		boolean startNewConsole = false;
		if(debugConsoleContext == null){
			startNewConsole = true;
		}else{
			final Shell shell = (Shell) debugConsoleContext.get("shell");
			if(shell == null || shell.isDisposed()){
				startNewConsole = true;
			}else{
				//如果调试窗口已经运行了
				final ActionContainer ac = (ActionContainer) debugConsoleContext.get("actions");
				shell.getDisplay().asyncExec(new Runnable(){
					public void run(){
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("thread", thread);
						params.put("debugActionContext", actionContext);
						params.put("debugInfo", debugInfo);
						ac.doAction("setDebugInfo", params);
						
						shell.forceActive();
					}
				});
			}
		}
		
		if(startNewConsole){
			//调试窗口未运行或已关闭
			Thread consoleThread = new Thread(){
				public void run(){
					Display display = new Display();
					ActionContext ac = new ActionContext();
					debugConsoleContext = ac;
					ac.put("parent", display);
					ac.getScope(0).disableGloableContext = true;
					
					Thing debugThing = World.getInstance().getThing("xworker.ide.debug.swt.DebugConsole/@shell");
					Shell shell = (Shell) debugThing.doAction("create", ac);
					shell.open();
					
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("thread", thread);
					params.put("debugActionContext", actionContext);
					params.put("debugInfo", debugInfo);
					ActionContainer actions = (ActionContainer) debugConsoleContext.get("actions");
					actions.doAction("setDebugInfo", params);
					
					while (!shell.isDisposed()) {
						try {
							if (!display.readAndDispatch())
								display.sleep();
						} catch (Exception e) {		
							log.error("exception", e);
						} catch (Error err){
							log.error("error", err);
						}
					}	
				}
			};
			
			debugConsoleThread = consoleThread;
			consoleThread.start();
		}
	}
	
	/**
	 * 调试当前线程。
	 * 
	 * @param actionContext
	 */
	public static void debug(ActionContext actionContext){
		DebugInfo debugInfo = Debuger.getDebugInfo(Thread.currentThread());	
		debugInfo.status = DebugInfo.STATUS_DEBUG;
		debugInfo.acitonContext = actionContext;
		debugInfo.bindings = actionContext.getScopes();
		Debuger.showDebugConsole(Thread.currentThread(), actionContext, debugInfo);
		debugInfo.waitForDebug();
	}
	
	/**
	 * 返回最后的异常列表。
	 * 
	 * @return
	 */
	public List<ExceptionRecord> getLastExceptions(){
		return lastExceptions;
	}
	
	public void addException(ExceptionRecord actionContext){
		lastExceptions.add(0, actionContext);
		if(lastExceptions.size() > 50){
			lastExceptions.remove(lastExceptions.size() - 1);
		}
	}
	
	public void record(String actionPath, long time, boolean successed){
		PerformanceRecord record = performanceRecords.get(actionPath);
		if(record == null){
			record = new PerformanceRecord(actionPath);
			performanceRecords.put(actionPath, record);
		}
		
		record.record(time, successed);
	}
	
	
	public PerformanceRecord getPerformanceRecord(String actionPath){
		return performanceRecords.get(actionPath);
	}
	
	public List<PerformanceRecord> getPerformanceRecords(){
		List<PerformanceRecord> ls = new ArrayList<PerformanceRecord>();
		for(String key : performanceRecords.keySet()){
			ls.add(performanceRecords.get(key));
		}
		
		return ls;
	}
	
	public void setEnabled(boolean enabled){
		World world = World.getInstance();
		
		if(this.enabled != enabled){
			Thing contextThing = world.getThing("xworker.ide.debug.context.DebugContextInstance");			
			if(contextThing != null){
				if(enabled){				
					world.addGlobalActionListener(contextThing, 0);
					
					//断点信息是记录到_local下的事物中的，通过监听监控变化
					world.registThingManagerListener("_local", this);
				}else{
					world.removeGlobalContext(contextThing);
					world.unregistThingManagerListener("_local", this);
				}
			}
		}
		
		this.enabled = enabled;
	}
	
	
	public void startActionRecord(){		
		actionRecorder.start();
	}
	
	public void stopActionRecord(){		
		actionRecorder.stop();
	}
	
	public ActionRecorder getActionRecorder(){
		return actionRecorder;
	}
	
	public boolean isEnabled(){
		return this.enabled;
	}

	@Override
	public void actionExecuted(Action action, Object caller, ActionContext actionContext,
			Map<String, Object> parameters, long namoTime, boolean successed) {		
		actionRecorder.recordAction(action, caller, actionContext, parameters, namoTime, successed);
	}

	@Override
	public void loaded(ThingManager thingManager, Thing thing) {
	}

	@Override
	public void saved(ThingManager thingManager, Thing thing) {
		if("_local.xworker.debug.ActionDebugInfo".equals(thing.getMetadata().getPath()) ||
				"_local.xworker.debug.FunctionDebugInfo".equals(thing.getMetadata().getPath())){
			renitDebugInfo();
		}
	}
	
	public void renitDebugInfo(){
		actionDebugInfo.clear();
		functionDebugInfo.clear();
		
		Thing acInfo = World.getInstance().getThing("_local.xworker.debug.ActionDebugInfo");
		Thing dbInfo = World.getInstance().getThing("_local.xworker.debug.FunctionDebugInfo");
		if(acInfo != null){
			for(Thing child : acInfo.getChilds()){
				actionDebugInfo.put(child.getString("thingPath"), child);
			}
		}
		
		if(dbInfo != null){
			for(Thing child : dbInfo.getChilds()){
				functionDebugInfo.put(child.getString("thingPath"), child);
			}
		}
	}
	
	/**
	 * 事物执行行为时的监听。
	 * 
	 * @param thing 事物
	 * @param acName 动作名称
	 * @param type
	 * @param actionContext
	 * @return
	 */
	public boolean getDoActionDebugInfo(Thing thing, String acName, String type, ActionContext actionContext){
		Thing cfgThing = actionDebugInfo.get(thing.getMetadata().getPath());
		if(cfgThing != null){
			boolean have = false;
			String actionName = cfgThing.getStringBlankAsNull("actionName");
			if(actionName != null){
				for(String name : actionName.split("[,]")){
					if(name.equals(acName)){
						have = true;
						break;
					}
				}
			}
			
			if(have) {
				return cfgThing.getBoolean(type);
			}else {
				return false;
			}
		}else{
			return false;
		}
	}
	
	public boolean getActionDebugInfo(Thing action, String type, ActionContext actionContext){
		Thing thing = actionDebugInfo.get(action.getMetadata().getPath());
		if(thing != null){
			return thing.getBoolean(type);
		}else{
			return false;
		}
	}
	
	public byte getActionDebugLoggerLevel(Thing action, ActionContext actionContext) {
		Thing thing = actionDebugInfo.get(action.getMetadata().getPath());
		if(thing != null){
			return thing.getByte("loggerLevel");
		}else{
			return -1;
		}
	}
	
	public boolean getFunctionDebugInfo(Thing function, String type){
		Thing thing = functionDebugInfo.get(function.getMetadata().getPath());
		if(thing != null){
			return thing.getBoolean(type);
		}else{
			return false;
		}
	}

	@Override
	public void removed(ThingManager thingManager, Thing thing) {
	}
	
	/**
	 * 获取记录的动作列表。
	 * 
	 * @return
	 */
	public List<ActionRecord> getActionRecords(){
		return actionRecorder.getRecords();
	}

	@Override
	public void actionExecuted(Thing thing, String method,
			ActionContext actionContext, Map<String, Object> parameters,
			long namoTime, boolean successed) {
		actionRecorder.recordAction(thing, method, actionContext, parameters, namoTime, successed);
	}

	public boolean isTrace() {
		return trace;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}
	
	
}