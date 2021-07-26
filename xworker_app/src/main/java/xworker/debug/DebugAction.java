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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.Executor;
import xworker.lang.executor.ExecutorService;

public class DebugAction {
	private static Logger logger = LoggerFactory.getLogger("Debug");	
	private static final String START_TIME = "__debugation_startTime__";
	
	private static void pirntTrace(String str) {
		logger.info(str);
	}
	
	/**
	 * onDoActoin是在事物执行动作时触发的，而init、success和exception是动作执行时触发的。
	 * 
	 * @see org.xmeta.Thing#run(String, ActionContext, java.util.Map, boolean, boolean)
	 * 
	 * @param actionContext
	 */
	public static void onDoAction(ActionContext actionContext){
		//获取事物以及相应要执行的方法名称
		Thing thing = actionContext.getObject("thing");
		String actionName = actionContext.getObject("actionName");
		//Map<String, Object> params = actionContext.getObject("params");
		//ActionContext acContext = (ActionContext) actionContext.get("acContext");
		
		DebugInfo debugInfo = Debuger.getDebugInfo(Thread.currentThread());
		Debuger debuger = Debuger.getDebuger();
		
		//添加TraceContext到变量上下文中，以便子动作等也能打印
		boolean actionTrace = debuger.getActionDebugInfo(thing, Debuger.TRACE, actionContext); 		
		if(debuger.isTrace()  || debugInfo.isTrace() || actionTrace){
			printThingActionTrace(thing, actionName, actionContext);
			
			if(actionTrace) {
				Thing traceThing = World.getInstance().getThing("xworker.ide.debug.context.TraceContext");
				boolean have = false;
				for(Bindings bindings : actionContext.getScopes()){
					if(bindings.getContextThing() == traceThing){
						have = true;
						break;
					}
				}
				
				if(!have){
					actionContext.getScope(actionContext.getScopesSize() - 3).setContextThing(traceThing);
				}
			}
		}
				
		if(debuger.getDoActionDebugInfo(thing, actionName, Debuger.BREAK_POINT_INIT, actionContext)){
			//onDoaction调试时不显示onDoAction自己，但启动调试状态
			debugInfo.nextAction = DebugInfo.STEP_OVER;
			debugInfo.actionStatus = DebugInfo.ACTION_STATUS_SUCCESS;
			List<Bindings> bindings = actionContext.getScopes();
			
			debugInfo.status = DebugInfo.STATUS_DEBUG;
			debugInfo.acitonContext = actionContext;
			debugInfo.bindings = bindings;		
			debugInfo.currentStackSize = bindings.size() - 2;	
			debugInfo.lastThing = thing;
			debugInfo.startDebug(actionContext, DebugInfo.ACTION_STATUS_INIT);
		}
	}
	
	private static boolean isOndoAction(Action action) {
		return action.getThing() == World.getInstance().getThing("xworker.ide.debug.context.DebugContext/@actions/@onDoAction");
	}
	
	private static void appendIdent(StringBuffer buf, ActionContext actionContext) {
		for(Bindings bindings : actionContext.getScopes()){		
			if(bindings.getCaller() instanceof Action){
				buf.append("    ");
			}
		}
		
		//List<Action> things = actionContext.getActions();
		
		//for(int i=0; i<things.size() - 1;i++) {
		//	buf.append("   ");
		//}
	}
	
	private static void printTraceInit(Thing action, String actionName, ActionContext actionContext) {
		StringBuffer buf = new StringBuffer();
		appendIdent(buf, actionContext);
		buf.append(actionName);
		buf.append(", action: ");
		buf.append(action.getMetadata().getPath());
		
		pirntTrace(buf.toString());
	}
	
	private static void printThingActionTrace(Thing thing, String actionName, ActionContext actionContext) {
		StringBuffer buf = new StringBuffer();
		appendIdent(buf, actionContext);
		buf.append(actionName);
		buf.append(", thing: ");
		buf.append(thing.getMetadata().getPath());
		
		pirntTrace(buf.toString());
	}
	
	private static void printTraceSuccess(Thing action, String actionName, ActionContext actionContext, long time) {
		StringBuffer buf = new StringBuffer();
		appendIdent(buf, actionContext);
		buf.append("/");
		buf.append(actionName);
		buf.append(", time: ");
		DecimalFormat df = new DecimalFormat("#.####");
		buf.append(df.format(1d * time / 1000000));
		buf.append(", action: ");
		buf.append(action.getMetadata().getPath());
		
		pirntTrace(buf.toString());
	}
		
	private static void printTraceException(Thing action, String actionName, ActionContext actionContext, long time, Throwable t) {
		StringBuffer buf = new StringBuffer();
		appendIdent(buf, actionContext);
		buf.append("/");
		buf.append(actionName);
		buf.append(", time: ");
		DecimalFormat df = new DecimalFormat("#.####");
		buf.append(df.format(1d * time / 1000000));
		buf.append(", exception: ");
		buf.append(t);
		buf.append(", action: ");
		buf.append(action.getMetadata().getPath());
		
		pirntTrace(buf.toString());
	}
	
	public static void init(ActionContext actionContext){
		//要执行的动作上下文
		ActionContext acContext = (ActionContext) actionContext.get(Action.str_parentContext);		
		//取最后一个Action
		List<Action> actions = acContext.getActions();
		Action action = actions.get(actions.size() - 1);
		
		//本事物的ondoAction需要排除在外
		if(isOndoAction(action)) {
			return;
		}
				
		//上下文所在动作上下文
		Thing context = (Thing) actionContext.get("self");
		if(!context.getBoolean("enabled")) return;
		
		//context.put("startTime", System.nanoTime());
		actionContext.g().put(DebugAction.START_TIME, System.nanoTime());
		

		//是否有trace，如果有初始化trace的动作上下文
		Debuger debuger = Debuger.getDebuger();
		
		//日志级别
		byte loggerLevel = debuger.getActionDebugLoggerLevel(action.getThing(), actionContext);
		if(loggerLevel > 0) {
			byte currentLoggerLevel = Executor.getLogLevel();
			ExecutorService executorService = Executor.getLogExecutorService();
			executorService.setLogLevel(loggerLevel);
			actionContext.g().put("loggerLevel", currentLoggerLevel);
			actionContext.g().put("executorService", executorService);
		}
		
		DebugInfo debugInfo = Debuger.getDebugInfo(Thread.currentThread());	
		boolean actionTrace = debuger.getActionDebugInfo(action.getThing(), Debuger.TRACE, actionContext); 
		if(debuger.isTrace() || debugInfo.isTrace() || actionTrace){
			//initTrace(acContext);
			printTraceInit(action.getThing(), action.getThing().getMetadata().getLabel(), acContext);
			
			if(actionTrace) {
				initTrace(acContext);
			}
		}		
			
		if(debugInfo.status == DebugInfo.STATUS_DEBUG || debuger.getActionDebugInfo(action.getThing(),Debuger.BREAK_POINT_INIT, actionContext)){
			debugInfo.startDebug(acContext, DebugInfo.ACTION_STATUS_INIT);
		}		
		
	}
		
	private static void initTrace(ActionContext acContext){
		Thing traceThing = World.getInstance().getThing("xworker.ide.debug.context.TraceContext");
		boolean have = false;
		//只要在变量上下文中有一个就可以了
		for(Bindings bindings : acContext.getScopes()){
			if(bindings.getContextThing() == traceThing){
				have = true;
				break;
			}
		}
		
		if(!have){
			acContext.peek().setContextThing(traceThing);
		}
	}
	/*
	private static void removeTrace(ActionContext actionContext){
		Thing traceThing = World.getInstance().getThing("xworker.ide.debug.context.TraceContext");
		for(Bindings bindings : actionContext.getScopes()){
			if(bindings.getContextThing() == traceThing){
				bindings.setContextThing(null);
				break;
			}
		}
		
	}*/
	
	public static void success(ActionContext actionContext){
		//要执行的动作上下文
		ActionContext acContext = (ActionContext) actionContext.get(Action.str_parentContext);		
		//取最后一个Action
		List<Action> actions = acContext.getActions();
		Action action = actions.get(actions.size() - 1);
		
		//本事物的ondoAction需要排除在外
		if(isOndoAction(action)) {
			return;
		}
		
		//恢复日志级别
		Byte loggerLevel = actionContext.getObject("loggerLevel");
		if(loggerLevel != null) {
			ExecutorService executorService = actionContext.getObject("executorService");
			if(executorService != null) {
				executorService.setLogLevel(loggerLevel);
			}			
		}
				
		long endTime = System.nanoTime();
		
		//上下文所在动作上下文
		Thing context = (Thing) actionContext.get("self");
		if(!context.getBoolean("enabled")) return;
		
		Long startTime = (Long) actionContext.get(DebugAction.START_TIME);
		long time = endTime - startTime.longValue();
		//System.out.println("time:" + time + ": " + startTime);
		Debuger debuger = Debuger.getDebuger();
		DebugInfo debugInfo = Debuger.getDebugInfo(Thread.currentThread());
		
		//记录，如果启动了记录
		debuger.record(action.getThing().getMetadata().getPath(), time, true);
		
		//打印Trace
		if(debuger.isTrace() || debugInfo.isTrace() 
				|| debuger.getActionDebugInfo(action.getThing(), Debuger.TRACE, actionContext)){
			//initTrace(acContext);
			printTraceSuccess(action.getThing(), action.getThing().getMetadata().getLabel(), acContext, time);
		}
		
		//调试，如果有success的断点				
		if(debugInfo.status == DebugInfo.STATUS_DEBUG || debuger.getActionDebugInfo(action.getThing(), Debuger.BREAK_POINT_SUCCESS, actionContext)){
			if("xworker.ide.debug.context.DebugContext/@actions/@onDoAction".equals(action.getThing().getMetadata().getPath())){
				//onDoaction调试时不显示onDoAction自己
				//List<Bindings> bindings = acContext.getScopes();
				//debugInfo.currentStackSize = bindings.size();	
				return;
			}
			debugInfo.startDebug(acContext, DebugInfo.ACTION_STATUS_SUCCESS);
		}		
	}
	
	public static void exception(ActionContext actionContext){
		//要执行的动作上下文
		ActionContext acContext = (ActionContext) actionContext.get(Action.str_parentContext);		
		//取最后一个Action
		List<Action> actions = acContext.getActions();
		Action action = actions.get(actions.size() - 1);
		
		//本事物的ondoAction需要排除在外
		if(isOndoAction(action)) {
			return;
		}
				
		long endTime = System.nanoTime();
		
		//恢复日志级别
		Byte loggerLevel = actionContext.getObject("loggerLevel");
		if(loggerLevel != null) {
			ExecutorService executorService = actionContext.getObject("executorService");
			if(executorService != null) {
				executorService.setLogLevel(loggerLevel);
			}			
		}
		
		//上下文所在动作上下文
		Thing context = (Thing) actionContext.get("self");
		if(!context.getBoolean("enabled")) return;
		
		long time = endTime - (Long) actionContext.get(DebugAction.START_TIME);
		
		Debuger debuger = Debuger.getDebuger();
		//录制动作，如果启动了
		debuger.record(action.getThing().getMetadata().getPath(), time, false);
		
		//记录到错误列表中
		Throwable t =  (Throwable) actionContext.get("exception");
		List<Object> callers = new ArrayList<Object>();
		for(int i=acContext.getScopesSize() - 1; i>=0; i--){
			callers.add(acContext.getScope(i).getCaller());
		}
		Debuger.getDebuger().addException(new ExceptionRecord(callers, t));
		
		//打印Trace
		if(debuger.isTrace() || debuger.getActionDebugInfo(action.getThing(), Debuger.TRACE, actionContext)){
			//initTrace(acContext);
			printTraceException(action.getThing(), action.getThing().getMetadata().getLabel(), acContext, time, t);
		}
				
		//启动调试
		DebugInfo debugInfo = Debuger.getDebugInfo(Thread.currentThread());			
		if(debugInfo.status == DebugInfo.STATUS_DEBUG || debuger.getActionDebugInfo(action.getThing(), Debuger.BREAK_POINT_EXCEPTION, actionContext)){
			debugInfo.startDebug(acContext, DebugInfo.ACTION_STATUS_EXCEPTION);
		}
	}
	
	public static Object inherit(ActionContext actionContext){
		return null;
	}
}