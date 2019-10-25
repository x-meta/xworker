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

import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;

public class ExtendWidgetCreator {
    public static Object create(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
		Thing widgetThing = self.doAction("getExtendWidget", actionContext);
		if(widgetThing != null){
			Object acObj = self.doAction("getActionContext", actionContext);
			ActionContext ac = null;
			if(acObj instanceof String){
				acObj = actionContext.get(acObj.toString());
			}
			if(acObj == null || !(acObj instanceof ActionContext)){
				ac = null;
			}else{
				ac = (ActionContext) acObj;
			}
			
			if(ac == null){
				String actionContextName = self.getStringBlankAsNull("actionContext");
				if(actionContextName != null){
					Boolean createActionContextIfNotExists = self.doAction("isCreateActionContextIfNotExists", actionContext);
					if(createActionContextIfNotExists != null && createActionContextIfNotExists){
						ac = new ActionContext();
				        ac.put("parentContext", actionContext);
				        ac.put("parent", actionContext.get("parent"));
				        actionContext.getScope(0).put(actionContextName, ac);
					}		
				}else{
					//旧的已过时的属性
					if(self.getBoolean("newActionContext")){
				        ac = new ActionContext();
				        ac.put("parentContext", actionContext);
				        ac.put("parent", actionContext.get("parent"));
				       
				        String newActionContextName = self.getStringBlankAsNull("newActionContextName");
				        if(newActionContextName != null){
				        	actionContext.getScope(0).put(newActionContextName, ac);
				        }		
				    }else{
				    	ac = actionContext; 
				    }		
				}
			}
			
			ac.peek().put("parent", actionContext.getObject("parent"));
			Object control = null;
			Designer.pushCreator(self);
			try{
				control = widgetThing.doAction("create", ac);
			}finally{
				Designer.popCreator();
			}
		    Bindings bindings = actionContext.push();
		    bindings.put("parent", control);
		    try{
		    	for(Thing child : self.getChilds()){
		    		child.doAction("create", actionContext);
		    	}
		    }finally{
		    	actionContext.pop();
		    }
		    
		    if(control instanceof Control) {
		    	Designer.attachCreator((Control) control, self.getMetadata().getPath(), actionContext);
		    }
		    actionContext.getScope(0).put(self.getMetadata().getName(), control);
		    return control;
		} else {
			return null;
		}      
	}

}