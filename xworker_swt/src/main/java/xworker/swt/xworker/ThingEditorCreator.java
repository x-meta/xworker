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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;
import xworker.util.XWorkerUtils;

public class ThingEditorCreator {
    public static Object create(ActionContext actionContext) throws OgnlException{
    	//long start = System.currentTimeMillis();
    	World world  = World.getInstance();
        Thing self = (Thing) actionContext.get("self");
		
		ActionContext ac = new ActionContext();
		//ac.peek().setContextThing(new Thing("xworker.lang.context.DebugContext"));
		ac.setLabel("ThingEditor");
		//ac.put("explorerActions", actionContext.get("explorerActions"));
		ac.put("explorerActions", XWorkerUtils.getIdeActionContainer());
		ac.put("parent", actionContext.get("parent"));
		ac.put("parentContext", actionContext);
		ac.put("editorThing", self);
		ac.put("editorThingUseRootThing", self.doAction("isUseRootThing", actionContext));				
		ac.put("variablesActionContext", self.doAction("getVariablesActionContext", actionContext));
		
		
		Thing editorThing = null;
		if(SwtUtils.isRWT()) {
			//RWT下为了安全起见进行权限校验
			if(xworker.security.SecurityManager.doCheck("RAP",
				"XWorker_Thing_Manager", null, null, actionContext)) {
				editorThing = world.getThing("xworker.ide.worldexplorer.ThingEditor/@shell/@mainComposite");
			}
		}else {
			editorThing = world.getThing("xworker.ide.worldexplorer.ThingEditor/@shell/@mainComposite");
		}

		boolean isThingViewer = false;
		if(editorThing == null){			
			editorThing = world.getThing("xworker.swt.xworker.prototype.RapThingViewer");
			/*
			new Thing("xworker.swt.xworker.ThingViewer");
			editorThing.put("name", "editor");
			*/
			Thing editorActions = world.getThing("xworker.swt.xworker.ThingViewer/@ActionContainer");
			editorActions.doAction("create", ac);
			isThingViewer = true;
		}
		//System.out.println("ThingEditor create actons time: " + (System.currentTimeMillis() - start));
		//start = System.currentTimeMillis();
		
		Composite composite = null;
		Designer.pushCreator(self);
		try{
			composite = (Composite) editorThing.doAction("create", ac);
		}finally{
			Designer.popCreator();
		}
		
		//System.out.println("ThingEditor create time: " + (System.currentTimeMillis() - start));
		//start = System.currentTimeMillis();
		
		composite.setData("actionContext", ac);
		
		//创建概要，如果outlineBrowser存在且是Composite
		Object outlineBrowser = actionContext.get("outlineBrowser");
		if(outlineBrowser != null && outlineBrowser.getClass().getName().equals("org.eclipse.swt.widgets.Composite")) {
			Thing outlineThing = null;
			if(isThingViewer){
				outlineThing = new Thing("xworker.swt.browser.Browser");
				outlineThing.set("name", "outlineBrowser");
			}else {
				outlineThing = world.getThing("xworker.ide.worldexplorer.swt.dataExplorerParts.ThingEditor/@outlineBrowser");
			}
			ActionContext thingContext = (ActionContext) ac.get("thingContext");
			thingContext.peek().put("parent", outlineBrowser);

			outlineThing.doAction("create", thingContext);
			((Composite) outlineBrowser).layout();
		}
		//System.out.println("ThingEditor outline create time: " + (System.currentTimeMillis() - start));
		//start = System.currentTimeMillis();
		
		try{
			Bindings bindings = actionContext.push();
			bindings.put("parent", composite);
			for(Thing child : self.getAllChilds()){
			    child.doAction("create", actionContext);
			    
			    //System.out.println("ThingEditor child " + child.getMetadata().getName() + " create time: " + (System.currentTimeMillis() - start));
				//start = System.currentTimeMillis();
			}
		}finally{
			actionContext.pop();
		}
		
		//System.out.println("ThingEditor child create time: " + (System.currentTimeMillis() - start));
		//start = System.currentTimeMillis();
		Object obj = self.doAction("getThing", actionContext);//UtilData.getData(self, "thingPath", actionContext);
		Thing thing = null;
		if(obj instanceof Thing){
			thing = (Thing) obj;
		}else if(obj instanceof String){
			thing = World.getInstance().getThing((String) obj);
		}
		if(thing != null){
			if(UtilData.isTrue(self.doAction("isUseRootThing", actionContext))){
				final ActionContainer acs = (ActionContainer) ac.get("editorActions");
				acs.doAction("setThing", UtilMap.toMap("thing", thing.getRoot()));
				final Thing selectThing = thing;
				composite.getDisplay().asyncExec(new Runnable() {
					public void run() {
						try {
							acs.doAction("selectThing", UtilMap.toMap("thing", selectThing));
						}catch(Exception e) {							
						}
					}
				});
				
			}else{
				((ActionContainer) ac.get("editorActions")).doAction("setThing", UtilMap.toMap("thing", thing));
			}
		}
		
		if(self.getString("title") != null && self.getBoolean("title") == false){
			ActionContext thingContext = (ActionContext) ac.get("thingContext");
			if(thingContext != null){
				((Widget) thingContext.get("titleComposite")).dispose();
			}
		}
		
		if(self.getBoolean("saveActionContainer")){
			actionContext.getScope(0).put(self.getString("name"), ac.get("editorActions"));
		}else{
			actionContext.getScope(0).put(self.getString("name"), ac);
		}
		
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		
		//System.out.println("ThingEditor finish create time: " + (System.currentTimeMillis() - start));
		//start = System.currentTimeMillis();
		return composite;        
	}

}