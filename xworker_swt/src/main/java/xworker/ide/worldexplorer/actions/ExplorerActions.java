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
package xworker.ide.worldexplorer.actions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Category;
import org.xmeta.Index;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;

import ognl.OgnlException;
import xworker.ide.worldexplorer.dialogs.NewThingDialog;
import xworker.lang.actions.ActionContainer;
import xworker.swt.design.Designer;
import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtDialogCallback;
import xworker.util.IIde;
import xworker.util.XWorkerUtils;

public class ExplorerActions {
	private static Logger log = LoggerFactory.getLogger(ExplorerActions.class);
	
	public static Thing getThing(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		//要打开的事物
		Thing forOpen = null;
		String thingVarName = self.getStringBlankAsNull("thingVarName");
		if(thingVarName != null){
			forOpen = (Thing) OgnlUtil.getValue(self, "thingVarName", thingVarName, actionContext);
		}
		
		if(forOpen == null){
			String thingPathVarName = self.getStringBlankAsNull("thingPathVarName");
			if(thingPathVarName != null){
				String thingPath = (String) OgnlUtil.getValue(self, "thingPathVarName", thingPathVarName, actionContext);
				if(thingPath != null){
					forOpen = World.getInstance().getThing(thingPath);
				}
			}
		}
		
		if(forOpen == null){
			String thingPath = UtilData.getString(self, "thingPath" , actionContext);
			if(thingPath != null && !"".equals(thingPath)){
				forOpen = World.getInstance().getThing(thingPath);
				
				if(forOpen == null &&  self.getBoolean("createIfNotExists")){
					ThingManager thingManager = World.getInstance().getThingManager(self.getString("thingManager"));
					if(thingManager != null){
						forOpen = new Thing(self.getString("descriptor"));
						int index = thingPath.lastIndexOf(".");
						if(index != -1){
							forOpen.put("name", thingPath.substring(index +1 , thingPath.length()));
						}else{
							forOpen.put("name", thingPath);
						}
						forOpen.saveAs(thingManager.getName(), thingPath);
					}
				}
			}
		}
		
		return forOpen;
	}
	
	public static void openThing(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//IDE动作
		/*final ActionContainer actions = Designer.getExplorerActions();
		if(actions == null){
		    log.info("OpentThing：designer exlplorer actions is null");
		    return;
		}*/

		Thing forOpen = (Thing) self.doAction("getThing", actionContext);
		if(forOpen == null){
		    log.info("OpenThing: thing not exists, thingPath=" + self.getMetadata().getPath());
		}

		openThing(forOpen);
	}
	
	public static void openThing(final Thing thing){
		IIde ide = XWorkerUtils.getIde();
		if(ide != null){
			ide.ideOpenThing(thing);
		}
		
	}
	
	/**
	 * 打开事物并选择事物的代码的某一行
	 * 
	 * @param thing
	 * @param codeAttrName
	 * @param line
	 */
	public static void openThingAndSelectCodeLine(final Thing thing, final String codeAttrName, final int line){
		IIde ide = XWorkerUtils.getIde();
		if(ide != null){
			ide.ideOpenThingAndSelectCodeLine(thing, codeAttrName, line);
		}
	}
	
	public static Map<String, Object> getNewThingAttributes(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Map<String, Object> values = new HashMap<String, Object>();
		String attributes = self.getStringBlankAsNull("attributes");
		if(attributes != null){
			for(String attr : attributes.split("[\n]")){
				attr = attr.trim();
				String[] as = attr.split("[=]");
				if(as.length >= 2){
					values.put(as[0], as[1]);
				}
			}
		}
		return values;
	}
	
	/**
	 * 创建新事物，打开创建新事物的对话框，同openCreateThingDialog，但不要求目录树已选择等。
	 * 
	 * @param actionContext
	 */
	public static void createNewThing(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		World world = World.getInstance();
		
		//新事物的窗口
		Thing dialogObject = world.getThing("xworker.ide.worldexplorer.swt.dialogs.NewThingDialog/@shell");		
		Shell parentShell = self.doAction("getShell", actionContext);
		if(parentShell == null) {
			parentShell = (Shell) XWorkerUtils.getIDEShell();
		}

		ActionContext newContext = new ActionContext();
		newContext.put("explorerContxt", Designer.getExplorerActions().getActionContext());
		newContext.put("explorerActions", Designer.getExplorerActions());
		newContext.put("parent", parentShell);
		newContext.put("thingInitValues", self.doAction("getAttributes", actionContext));
		Category category = self.doAction("getCategory", actionContext);
		if(category != null) {			
			newContext.put("index", category);
			newContext.put("categoryPath", category.getName());
		}

		Shell newShell = (Shell) dialogObject.doAction("create", newContext);
		//设置描述者
		Object descriptor = (Object) self.doAction("getDescriptor", actionContext);
		if(descriptor != null){
			String desc = null;
			if(descriptor instanceof Thing){
				desc = ((Thing) descriptor).getMetadata().getPath();
			}else{
				desc = String.valueOf(descriptor);
			}
			((Text) newContext.get("descriptorText")).setText(desc);
		}
		
		//设置新事物的名字，如果存在
		String thingName = (String) self.doAction("getThingName", actionContext);
		if(thingName != null){
			((Text) newContext.get("dataObjectNameText")).setText(thingName);
		}
		
		SwtDialog dialog = new SwtDialog(newShell, newContext);
		dialog.open(new SwtDialogCallback() {
			@Override
			public void disposed(Object result) {
				if(result != null) {
					self.doAction("ok", actionContext, "thing", result);
				}else {
					self.doAction("cancel", actionContext, "thing", result);
				}
			}
		});
	}
	
	public static void createNewThingByExampleOrTemplate(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		World world = World.getInstance();
		
		//新事物的窗口
		Thing dialogObject = world.getThing("xworker.ide.worldexplorer.swt.dialogs.NewThingByTemplateDialog");		
		Shell parentShell = self.doAction("getShell", actionContext);
		if(parentShell == null) {
			parentShell = (Shell) XWorkerUtils.getIDEShell();
		}

		ActionContext newContext = new ActionContext();
		newContext.put("explorerContxt", Designer.getExplorerActions().getActionContext());
		newContext.put("explorerActions", Designer.getExplorerActions());
		newContext.put("parent", parentShell);
		newContext.put("thingInitValues", self.doAction("getAttributes", actionContext));
		Category category = self.doAction("getCategory", actionContext);
		if(category != null) {			
			newContext.put("index", category);
			newContext.put("categoryPath", category.getName());
		}

		Shell newShell = (Shell) dialogObject.doAction("create", newContext);
		
		//设置新事物的名字，如果存在
		String thingName = (String) self.doAction("getThingName", actionContext);
		if(thingName != null){
			((Text) newContext.get("dataObjectNameText")).setText(thingName);
		}
		
		Thing exampleThing = self.doAction("getExampleThing", actionContext);
		ActionContainer actions = newContext.getObject("actions");
		if(exampleThing != null){
			actions.doAction("setThing", newContext, "thing", exampleThing);
		}else {
			((Button) newContext.get("copyExampleButton")).setEnabled(false);
		}
		
		Thing templateThing = self.doAction("getTemplateThing", actionContext);
		if(templateThing != null){
			actions.doAction("setTemplate", newContext, "thing", templateThing);
		}else {
			((Button) newContext.get("okButton")).setEnabled(false);
		}
		
		SwtDialog dialog = new SwtDialog(newShell, newContext);
		dialog.open(new SwtDialogCallback() {
			@Override
			public void disposed(Object result) {
				if(result != null) {
					self.doAction("ok", actionContext, "thing", result);
				}else {
					self.doAction("cancel", actionContext, "thing", result);
				}
			}
		});
	}
	
	public static void openCreateThingDialog(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		final World world = World.getInstance();
		
		ActionContext explorerActionContext = Designer.getExplorerActions().getActionContext();
		Tree projectTree = (Tree) explorerActionContext.get("projectTree");
		if(projectTree == null || projectTree.isDisposed()){
			XWorkerUtils.ideShowMessageBox("错误", "事物项目导航没有打开！", SWT.ICON_WARNING | SWT.OK);
			return;
		}
		TreeItem treeItem = projectTree.getSelection()[0];
		Index index = (Index) treeItem.getData();
		if(!index.getType().equals(Index.TYPE_CATEGORY)){
			XWorkerUtils.ideShowMessageBox("创建事物", "请在项目树中选择一个目录！", SWT.ICON_WARNING | SWT.OK);
			return;
		}
		
		Thing dialogObject = world.getThing("xworker.ide.worldexplorer.swt.dialogs.NewThingDialog/@shell");		
		Shell parentShell = treeItem.getParent().getShell();

		ActionContext newContext = new ActionContext();
		newContext.put("treeItem", treeItem);
		newContext.put("explorerContxt", explorerActionContext);
		newContext.put("explorerActions", Designer.getExplorerActions());
		newContext.put("categoryPath", ((Index) treeItem.getData()).getPath());
		newContext.put("parent", parentShell);

		Shell newShell = (Shell) dialogObject.doAction("create", newContext);
		if(self.getStringBlankAsNull("descriptor") != null){
			((Text) newContext.get("descriptorText")).setText(self.getStringBlankAsNull("descriptor"));
		}
		world.setData("__xworker_ide_newThingDialog__", newContext);
		SwtDialog dialog = new SwtDialog(parentShell, newShell, newContext);
		dialog.open(new SwtDialogCallback() {

			@Override
			public void disposed(Object result) {				
			}			
		});
		//dialog.open();
	}
	
	public static Shell getNewThingDialog(ActionContext actionContext) {
		if( NewThingDialog.instance != null &&  NewThingDialog.instance.shell.isDisposed() == false) {
			return  NewThingDialog.instance.shell;
		}else {
			return null;			
		}
	}
	
	public static void startBackgroudThread(ActionContext actionContext){
		final World world = World.getInstance();
		Category taskCategory = (Category) world.get("xworker.ide.worldexplorer.background.tasks");
		if(taskCategory == null){
			return;
		}
        //log.info("background running " + taskCategory);
        
        Iterator<Thing> tasks = taskCategory.iterator(true);
        while(tasks.hasNext()){
            Thing task = tasks.next();
            try{
                if(!task.getBoolean("enable")){
                    continue;
                }
                task.doAction("run", actionContext);
            }catch(Throwable t){
                t.printStackTrace();
                log.error("run background task error", t);
            }
        }
		/*
		final World world = World.getInstance();

		ActionContext aac = (ActionContext) world.getData("xw_background_thread");
		if(aac != null && "running".equals(aac.get("status"))){
		    //线程正在运行中
		    return aac;
		}

		final ActionContext ac = new ActionContext();
		final Map<String, Object> times = new HashMap<String, Object>();
		ac.put("times", ac);
		Runnable runable = new Runnable(){
		    public void run(){		    	
		    	try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
				}
		    	
		        world.setData("xw_background_thread", ac);
		        while(true){
		            Category taskCategory = (Category) world.get("xworker.ide.worldexplorer.background.tasks");
		            //log.info("background running " + taskCategory);
		            
		            Iterator<Thing> tasks = taskCategory.iterator(true);
		            while(tasks.hasNext()){
		                Thing task = tasks.next();
		                try{
		                    if(!task.getBoolean("enable")){
		                        continue;
		                    }
		                    //log.info("run task : " + task.metadata.path);
		                    Long lastTime = (Long) times.get(task.getMetadata().getPath());
		                    if(lastTime == null){
		                        lastTime = 0l;
		                    }
		                    
		                    //log.info("lastTime=" + lastTime);
		                    //log.info("interval=" + task.getInt("interval"));
		                    if((System.currentTimeMillis() - lastTime) > task.getInt("interval")){
		                        times.put(task.getMetadata().getPath(), System.currentTimeMillis());
		                        task.doAction("run", ac);	    
		                    }
		                    if("true".equals(ac.get("stop"))){
		                        break;
		                    }
		                }catch(Throwable t){
		                    t.printStackTrace();
		                    log.error("run background task error", t);
		                }
		            }
		            if(ac.get("stop") == "true"){
		                break;
		            }
		            //println "loop";       
		            
		            try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}    
		        }
		        
		        log.info("xwroker background stoped");
		        ac.put("status", "stoped");
		    }
		};

		Thread th = new Thread(runable, "xworker_backgroud");
		th.setDaemon(true);
		th.start();

		return ac;
		*/
	}
}