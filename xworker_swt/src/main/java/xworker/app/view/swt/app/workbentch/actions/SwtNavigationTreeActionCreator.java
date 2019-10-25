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
package xworker.app.view.swt.app.workbentch.actions;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.swt.ActionContainer;

public class SwtNavigationTreeActionCreator {
	private static Logger logger = LoggerFactory.getLogger(SwtNavigationTreeActionCreator.class);
	
    public static void run(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        
    	Event event = (Event) actionContext.get("event");
        TreeItem treeItem = (TreeItem) event.item;
        Object data = treeItem.getData();
        
        for(Thing child : self.getChilds()){
            String type = null;
            if("itemData".equals(child.getString("typeSource"))){
                type = (String) treeItem.getData(child.getString("typeName"));
            }else{
                type = (String) OgnlUtil.getValue(child.getString("typeName"), data);
            }
        
            if(type != null && type.equals(child.getString("name"))){
                child.doAction("run", actionContext, UtilMap.toMap(new Object[]{"data",data, "params",treeItem, "treeModel",treeItem.getData("_treeModel")}));
            }
            
            //log.info("type=" + type + ",name=" + child.name);
        }
        
        //对TreeModel的支持
        try{
        	ActionContainer workbentchActions = (ActionContainer) actionContext.get("workbentchActions"); 
        
        	
            if("openEditor".equals(self.getString("method"))){
                workbentchActions.doAction("openEditor", UtilMap.toMap(new Object[]{"name", self.get("value"), "data", data, "params", UtilMap.toMap(new Object[]{"item",event.item})}));
            }else{
            	String ac = self.getStringBlankAsNull("value");
            	if(ac != null){
	                Action action = World.getInstance().getAction(ac);
	                if(action != null){
	                    action.run(actionContext, UtilMap.toMap(new Object[]{"data", data}));
	                }else{
	                    logger.info("NavigationTreeAction-TypeAciton: action not exits, action=" + self.get("value"));
	                }
            	}
            }
            
        	String url = (String) OgnlUtil.getValue("url", data);
        	if(url != null){
        		if(url.startsWith("editor:")){
        			String eidtorName = url.substring(7, url.length());
        			workbentchActions.doAction("openEditor", UtilMap.toMap("name", eidtorName, "data", data, "params", UtilMap.toMap("item",event.item)));
        		}else if(url.startsWith("view:")){
        			String eidtorName = url.substring(5, url.length());
        			workbentchActions.doAction("openView", UtilMap.toMap("name", eidtorName, "data", data, "params", UtilMap.toMap("item",event.item)));
        		}else{
        			Action action = World.getInstance().getAction(url);
        			if(action != null){
        				action.run(workbentchActions.getActionContext());
        			}else{
        				logger.warn("SWTNavigaitonTreeAction: action is null, url= " + url);
        			}
        		}
        	}
        }catch(Exception e){        	
        }
    }

}