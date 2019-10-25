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
package xworker.app.view.extjs.widgets.editor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class SimpleDataObjectEditorCreator {
	private static Logger log = LoggerFactory.getLogger(SimpleDataObjectEditorCreator.class);
	
    public static Object toJavaScriptCode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	World world = World.getInstance();
        
        //数据对象    	
        Thing dataObject = world.getThing(self.getString("dataObject"));
        if(dataObject == null){
            Thing dos = self.getThing("dataObjects@0");
            if(dos != null && dos.getChilds().size() > 0){
                dataObject = dos.getChilds().get(0);
            }
        }
        if(dataObject == null){
            log.warn("SimpleDataObjectEditor: dataObject is null - " + self.getString("dataObject"));
            return null;
        }
        
        Thing template = world.getThing("xworker.app.view.extjs.widgets.editor.SimpleDataObjectEditor/@template");
        ActionContext context = new ActionContext();
        context.put("dataObjectPath", dataObject.getMetadata().getPath());
        context.put("thing", self);
        
        Thing grid = (Thing) template.doAction("process", context);
        if(grid != null){
            return grid.doAction("toJavaScriptCode", actionContext);
        }else{
            return null;
        }
    }

    public static Object getExtType(ActionContext actionContext){
    	return "Ext.Panel";
    	/*
    	Thing self = (Thing) actionContext.get("self");
    	World world = World.getInstance();
    	
        //数据对象
        Thing dataObject = world.getThing(self.getString("dataObject"));
        if(dataObject == null){
            Thing dos = self.getThing("dataObjects@0");
            if(dos != null && dos.getChilds().size() > 0){
                dataObject = dos.getChilds().get(0);
            }
        }
        
        if(dataObject == null){
            return "Ext.grid.GridPanel";
        }else if(dataObject.getBoolean("gridEditable") && !dataObject.getBoolean("gridRowEditor")){
            return "Ext.grid.EditorGridPanel";
        }else{
            return "Ext.grid.GridPanel";
        }*/
    }

}