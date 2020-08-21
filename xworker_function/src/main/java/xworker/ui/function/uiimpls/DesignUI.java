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
package xworker.ui.function.uiimpls;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.lang.actions.ActionContainer;
import xworker.swt.app.IEditor;
import xworker.swt.design.Designer;
import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUI;
import xworker.util.XWorkerUtils;

/**
 * 在事物管理器上打开一个设计函数流程的UI。
 * 
 * @author Administrator
 *
 */
public class DesignUI extends FunctionRequestUI{
	private static Logger log = LoggerFactory.getLogger(DesignUI.class);
	private CTabItem tabItem;
	private ActionContext actionContext;
	private FunctionRequest functionRequest;
	
	@Override
	public void createUI(final FunctionRequest functionRequest) {
		this.functionRequest = functionRequest;
		
		openTablItem();
	}

	private void openTablItem(){
		//IDE动作
		final ActionContainer actions = Designer.getExplorerActions();
		if(actions == null){
			log.info("designer exlplorer actions is null");
		    return;
		}

		Thing editor = World.getInstance().getThing("xworker.ui.function.FunctionEditor");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("request", functionRequest);
		params.put("handlers", this.handlers);
		String editorId = "functionRequest_" + String.valueOf(functionRequest.hashCode());
		
		IEditor ieditor = (IEditor) XWorkerUtils.openEditor(editorId, editor, params);
		this.actionContext = ieditor.getActionContext();
		
		/*
		
		//要打开的事物
		String thingPath = "xworker.ui.function.FunctionRequestShell/@composite";
		final String compositeId = "functionRequest_" + String.valueOf(functionRequest.hashCode());
		final String title = functionRequest.getThing().getMetadata().getLabel();
		final Thing thing = World.getInstance().getThing(thingPath);
		
		if(thing == null){
		    log.info("thing not exists, thingPath=" + thingPath);
		    return;
		}

		Display display = Designer.getExplorerDisplay();
		Runnable runnable = new Runnable(){
			public void run(){
				ActionContext ac = actions.getActionContext();
				Shell shell = (Shell) ac.get("shell");
				if(shell != null){
					shell.forceActive();
				}
			
				//创建UI
				CTabItem citem = (CTabItem) actions.doAction("openCompoisteAtTab", UtilMap.toMap(new Object[]{"compositeThing", thing, "title", title, "path", compositeId}));
				if(citem != tabItem){					
					tabItem = citem;
					
					//第一次初始化
					actionContext = (ActionContext) tabItem.getData("actionContext");
					tabItem.addDisposeListener(new DisposeListener(){
						@Override
						public void widgetDisposed(DisposeEvent arg0) {
							FunctionRequestUIFactory.removeUI(functionRequest);
						}
						
					});
					
					//注册Handler
					ActionContainer actions = (ActionContainer) actionContext.get("actions");
					actions.doAction("initHandlers", actionContext, UtilMap.toMap(new Object[]{"handlers", handlers}));
					actions.doAction("initFunctionRequest", actionContext, UtilMap.toMap(new Object[]{"request", functionRequest}));
				}
			}
		};
		
		if(!display.isDisposed()){
			display.syncExec(runnable);
		}*/
	}
	
	@Override
	public void forceActive() {
		openTablItem();
	}

	@Override
	public void updateRequestUI(final FunctionRequest functionRequest) {
		openTablItem();
		
		final ActionContainer actions = (ActionContainer) actionContext.get("actions");
		Display display = Designer.getExplorerDisplay();
		Runnable runnable = new Runnable(){
			public void run(){
				actions.doAction("initFunctionRequest", actionContext, UtilMap.toMap(new Object[]{"request", functionRequest}));
			}
		};
		display.asyncExec(runnable);
	}

	@Override
	public void close() {
		tabItem.dispose();
	}

}