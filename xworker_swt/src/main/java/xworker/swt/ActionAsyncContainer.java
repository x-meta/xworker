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
package xworker.swt;

import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.Executor;

/**
 * 和ActionContainer相同，只是这里的任何动作都会使用Display.asyncExec方法调用，
 * 这样方便其他线程调用。
 * 
 * @author zhangyuxiang
 *
 */
public class ActionAsyncContainer {
	private static final String TAG = ActionAsyncContainer.class.getName();
	static World world = World.getInstance();
	
	private Display display;
	private ActionContainer actionContainer;
	
	public ActionAsyncContainer(ActionContainer actionContainer){
		this.actionContainer = actionContainer;
		this.display = Display.getCurrent();		
	}
	
	public ActionAsyncContainer async(){
		return this;
	}
	
	public Thing getThing(){
		return actionContainer.getThing();
	}
	
	public void doAction(final String name){
		display.asyncExec(new Runnable(){
			public void run(){
				try{
					actionContainer.doAction(name);
				}catch(Throwable e){
					Executor.error(TAG, "Container do action " + name, e);
				}
			}
		});
		
	}
	
	public void doAction(final String name, final ActionContext context){
		display.asyncExec(new Runnable(){
			public void run(){
				try{
					actionContainer.doAction(name, context);
				}catch(Throwable e){
					Executor.error(TAG, "Container do action " + name, e);
				}
			}
		});
		
	}
	
	public void doAction(String name, Map<String, Object> parameters){
		display.asyncExec(new Runnable(){
			public void run(){
				try{
					actionContainer.doAction(name, parameters);
				}catch(Exception e){
					Executor.error(TAG, "Container do action " + name, e);
				}
			}
		});
	}
	
	public void doAction(String name, ActionContext context, Map<String, Object> parameters){
		display.asyncExec(new Runnable(){
			public void run(){
				try{
					actionContainer.doAction(name, context, parameters);
				}catch(Throwable e){
					Executor.error(TAG, "Container do action " + name, e);
				}
			}
		});
	}
	
	public Thing getActionThing(String name){
		return actionContainer.getActionThing(name);
	}
	
	public ActionContext getActionContext(){
		return actionContainer.getActionContext();
	}
}