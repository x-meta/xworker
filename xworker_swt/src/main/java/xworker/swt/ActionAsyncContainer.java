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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

/**
 * 和ActionContainer相同，只是这里的任何动作都会使用Display.asyncExec方法调用，
 * 这样方便其他线程调用。
 * 
 * @author zhangyuxiang
 *
 */
public class ActionAsyncContainer {
	private static Logger log = LoggerFactory.getLogger(ActionAsyncContainer.class);
	static World world = World.getInstance();
	
	private Thing actions;
	private ActionContext actionContext;	
	private Display display;
	
	public ActionAsyncContainer(Thing actions, ActionContext actionContext){
		this.actionContext = actionContext;
		this.actions = actions;
		this.display = Display.getCurrent();		
	}
	
	public ActionAsyncContainer async(){
		return this;
	}
	
	public Thing getThing(){
		return actions;
	}
	
	public void doAction(final String name){
		display.asyncExec(new Runnable(){
			public void run(){
				try{
					Thing actionThing = getActionThing(name);
					if(actionThing != null){
						Action action = world.getAction(actionThing.getMetadata().getPath());
						action.run(actionContext);
					}
				}catch(Throwable e){
					log.error("Container do action " + name, e);
				}
			}
		});
		
	}
	
	public void doAction(final String name, final ActionContext context){
		display.asyncExec(new Runnable(){
			public void run(){
				try{
					Thing actionThing = getActionThing(name);
					if(actionThing != null){
						Action action = world.getAction(actionThing.getMetadata().getPath());
						action.run(context);
					}
				}catch(Throwable e){
					log.error("Container do action " + name, e);
				}
			}
		});
		
	}
	
	public void doAction(String name, Map<String, Object> parameters){
		display.asyncExec(new Runnable(){
			public void run(){
				
			}
		});
		try{
			Thing actionThing = getActionThing(name);
			if(actionThing != null){
				Action action = world.getAction(actionThing.getMetadata().getPath());
				action.run(actionContext, parameters);
			}
		}catch(Throwable e){
			log.error("Container do action " + name, e);
		}
	}
	
	public void doAction(String name, ActionContext context, Map<String, Object> parameters){
		display.asyncExec(new Runnable(){
			public void run(){
				
			}
		});
		try{
			Thing actionThing = getActionThing(name);
			if(actionThing != null){
				Action action = world.getAction(actionThing.getMetadata().getPath());
				action.run(context, parameters);
			}
		}catch(Throwable e){
			log.error("Container do action " + name, e);
		}
	}
	
	public Thing getActionThing(String name){
		display.asyncExec(new Runnable(){
			public void run(){
				
			}
		});
		for(Thing child : actions.getAllChilds()){
			if(child.getMetadata().getName().equals(name)){
				return child;
			}
		}
		
		log.warn("action is not found : " + actions.getMetadata().getPath() + "/@" + name);
		return null;
	}
	
	public ActionContext getActionContext(){
		return actionContext;
	}
}