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
package xworker.swt.style;

import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

public class StyleSetCreator {
    public static void create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		Thing styleManager = (Thing) actionContext.get("StyleManager");
		if(styleManager == null){
		    styleManager = new Thing("xworker.swt.style.StyleSet");
		    actionContext.getScope(0).put("StyleManager", styleManager);
		}
		
		for(Thing child : self.getChilds()){
		    child.doAction("create", actionContext);
		}
		
		//作用于父的样式
		Object parent = actionContext.get("parent");
		if(parent != null && parent instanceof Widget){
		    String thingPath = (String) ((Widget) parent).getData("_designer_thingPath");
		    if(thingPath != null){
		        Thing parentThing = World.getInstance().getThing(thingPath);
		        if(parentThing != null){
		            Thing clsThing = (Thing) styleManager.get(parentThing.getString("cls"));
		            if(clsThing != null){
		                clsThing.doAction("apply", actionContext, UtilMap.toParams(new Object[]{"widget", parent}));
		            }
		        }
		    }
		}    
	}
		
    public static void apply(ActionContext actionContext){
		//Thing self = (Thing) actionContext.get("self");
		Thing StyleManager = (Thing) actionContext.get("StyleManager");
		
		//应用模板，如果存在
		String cls = (String) actionContext.get("cls");
		Thing widgetThing = (Thing) actionContext.get("widgetThing");
		if(widgetThing != null){
		    cls = widgetThing.getString("cls");
		}
		if(cls != null && !cls.equals("")){
		    for(String cl : cls.split("[,]")){ //允许同时使用多个样式
		        Thing clsThing = (Thing) StyleManager.get(cl);
		        if(clsThing != null){
		            clsThing.doAction("apply", actionContext);
		        }
		    }
		}        
	}

}