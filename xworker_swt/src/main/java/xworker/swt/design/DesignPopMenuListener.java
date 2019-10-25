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
package xworker.swt.design;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.actions.ActionContainer;

public class DesignPopMenuListener implements SelectionListener, DisposeListener{
	private static Logger log = LoggerFactory.getLogger(DesignPopMenuListener.class);
	private static World world = World.getInstance();
	
	String thingPath;
	ActionContext actionContext;
	Control control;
	boolean isAttribute;
	
	public DesignPopMenuListener(String thingPath, ActionContext actionContext, Control control, boolean isAttribute){
		this.thingPath = thingPath;
		this.actionContext = actionContext;
		this.control = control;
		this.isAttribute = isAttribute;
	}
	
	public void widgetDefaultSelected(SelectionEvent event) {		
	}

	public void widgetSelected(SelectionEvent event) {
		MenuItem item = (MenuItem) event.widget;		
		String type = (String) item.getData();
		
		if("open".equals(type)){
			Display explorerDisplay = Designer.getExplorerDisplay();
			final ActionContainer explorerActions = Designer.getExplorerActions();
			
			if(explorerDisplay != null && explorerActions != null){
				final Thing thing = world.getThing(thingPath);
				if(thing != null){
					explorerDisplay.asyncExec(new Runnable(){
						public void run(){
							ActionContext ac = explorerActions.getActionContext();
							Shell shell = (Shell) ac.get("shell");
							if(shell != null){
								shell.forceActive();
							}
							
							Map<String, Object> parameters = new HashMap<String, Object>();
							parameters.put("thing", thing);
							explorerActions.doAction("openThing", parameters);;
						}
					});
				}else{
					log.warn("创建控件的事物为空！");
				}
			}else{
				log.warn("没有指定打开事物的浏览器！");
			}
		}else if("refresh".equals(type)){
			Composite composite = control.getParent();
			
			Thing thing = world.getThing(thingPath);
			
			if(thing != null){
				control.dispose();
				Bindings bindings = actionContext.push();
				try{
					bindings.put("parent", composite);
					
					thing.doAction("create", actionContext);
					
					composite.layout();
				}finally{
					actionContext.pop();
				}
			}
		}else if("refreshParent".equals(type)){
			Composite composite = control.getParent();
			String parentThingPath = (String) composite.getData("_designer_thingPath");
			ActionContext parentActionContext = (ActionContext) composite.getData("_designer_actionContext");
			
			if(parentThingPath == null){
				log.info("父控件没有创建者事物的路径！");
				return;
			}
			
			Thing thing = world.getThing(parentThingPath);
			
			if(thing != null){
				for(Control control : composite.getChildren()){
					control.dispose();
				}

				Bindings bindings = actionContext.push();
				try{
					bindings.put("parent", composite);
					
					for(Thing child : thing.getAllChilds()){
						child.doAction("create", parentActionContext);
					}
					
					composite.layout();
				}finally{
					actionContext.pop();
				}
			}
		}else if("refreshChilds".equals(type)){
			Thing thing = world.getThing(thingPath);
			
			if(thing != null){
				if(control instanceof Composite){
					Composite composite = (Composite) control;
					for(Control childControl : composite.getChildren()){
						childControl.dispose();
					}
				}

				Bindings bindings = actionContext.push();
				try{
					bindings.put("parent", control);
					
					for(Thing child : thing.getAllChilds()){
						child.doAction("create", actionContext);
					}
					
					control.pack();
					if(control.getParent() != null){
						control.getParent().layout();
					}
				}finally{
					actionContext.pop();
				}
			}
		}else if("cancel".equals(type)){
			item.getParent().setVisible(false);
		}
	}

	public void widgetDisposed(DisposeEvent arg0) {
		//log.info("menuitem is disposed");
	}

}