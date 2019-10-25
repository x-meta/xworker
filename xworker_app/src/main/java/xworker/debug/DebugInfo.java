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

import java.util.List;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

/**
 * 和线程绑定的调试信息，线程中动作的单步执行、中断等从这里控制。
 * 
 * @author zyx
 *
 */
public class DebugInfo {
	/** 运行状态 */
	public static final int STATUS_RUNNING = 0;
	/** 调试状态 */
	public static final int STATUS_DEBUG = 1;
	
	public static final byte STEP_OVER = 1;
	public static final byte STEP_INTO = 2;
	public static final byte STEP_RETURN = 3;
	
	public static final byte ACTION_STATUS_INIT = 1;
	public static final byte ACTION_STATUS_SUCCESS = 2;
	public static final byte ACTION_STATUS_EXCEPTION = 3;	
	
	/** 状态 */
	public int status = STATUS_RUNNING;
	
	/** 用于暂停线程的监控对象 */
	public Object monitorObj = new Object();
	
	/** 需要调试的线程当前动作上下文 ，已不用，调试控制器的线程不一致取出的栈不同*/
	public ActionContext acitonContext = null;
	
	private boolean waitting = false;
	
	/** 需要调试的线程的变量列表  */
	public List<Bindings> bindings = null;
	
	/** 下一步的操作类型，是STEP_OVER、STEP_INTO或STEP_RETURN */
	public byte nextAction;
	
	/** 当前的堆栈层数，用于跟进、跟出或下一步 */
	public int currentStackSize;
	
	/** 动作的状态  */
	public byte actionStatus;
	
	/** 上次调试时的事物 */
	public Thing lastThing;
	
	/** 是否打印trace */
	private boolean trace;
	
	public void startDebug(ActionContext actionContext, byte actionStatus){
		List<Bindings> bindings = actionContext.getScopes();
		if(nextAction != 0){
			switch(nextAction){
			case STEP_OVER:
				if(bindings.size() > currentStackSize){		
					//如果调试的事物和上次的事物是同一个根事物，那么也认为是STEP_OVER
					if(!isLastThing(getLastThing(bindings))){
						return;
					}
				}
				break;
			case STEP_INTO:
				//跟进，任何时候都符合
				break;
			case STEP_RETURN:
				//跟出
				if(bindings.size() >= currentStackSize){
					return;
				}
				break;
			}
		}
		
		nextAction = 0;
		this.actionStatus = actionStatus;
		status = DebugInfo.STATUS_DEBUG;
		acitonContext = actionContext;
		this.bindings = bindings;		
		currentStackSize = bindings.size();
		
		Debuger.showDebugConsole(Thread.currentThread(), actionContext, this);		
		waitForDebug();
	}
	
	/**
	 * 等待。
	 */
	public void waitForDebug(){
		if(Thread.currentThread() != Debuger.getDebugConsoleThread()){
			synchronized(monitorObj){
				try {
					waitting = true;
					monitorObj.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean isLastThing(Thing thing){
		if(thing != null && lastThing != null){
			return thing.getRoot() == lastThing.getRoot();
		}else{
			return false;
		}
	}
	
	public Thing getLastThing(List<Bindings> bindings){
		if(bindings == null){
			return null;
		}
		
		for(int i=bindings.size() -1 ; i>=0; i--){
			Bindings binding = bindings.get(i);					
			Object caller = binding.getCaller() ;
			if(caller!= null){
				if(caller instanceof Thing){
					return (Thing) caller;
				}else if(caller instanceof Action){
					return ((Action) caller).getThing();
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 结束等待。
	 */
	public void finish(){
		if(STEP_OVER == this.nextAction){
			lastThing = getLastThing(bindings);
		}else{
			lastThing = null;
		}
		
		synchronized(monitorObj){
			waitting = false;			
			monitorObj.notify();			
		}
	}

	public boolean isWaitting() {
		return waitting;
	}

	public void setWaitting(boolean waitting) {
		this.waitting = waitting;
	}

	public boolean isTrace() {
		return trace;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}
	
	
}