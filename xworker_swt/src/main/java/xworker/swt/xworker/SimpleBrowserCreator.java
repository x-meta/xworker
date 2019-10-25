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

import org.eclipse.swt.browser.Browser;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class SimpleBrowserCreator {
    public static Object BrowserDisposeListenercreate(ActionContext actionContext){
		World world = World.getInstance();
		Thing self = (Thing) actionContext.get("self");
		
		Thing formThing = world.getThing("xworker.swt.xworker.SimpleBrowser/@htmlViewForm1");
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		
		Object form = formThing.doAction("create", ac);
		try{
		    actionContext.push(null).put("parent", form);    
		    for(Thing child : self.getAllChilds()){
		        child.doAction("create", actionContext);
		    }
		}finally{
		    actionContext.pop();
		}
		
		actionContext.getScope(0).put(self.getString("name"), ac.get("browser"));
		actionContext.getScope(0).put(self.getString("name") + "ViewForm", form);
		return form;       
	}
    
    public static void backToolItemSelection(ActionContext actionContext){
    	Browser browser = (Browser) actionContext.get("browser");
    	browser.back();
    }
    
    public static void forwardToolItemSelection(ActionContext actionContext){
    	Browser browser = (Browser) actionContext.get("browser");
    	browser.forward();
    }
    
    public static void stoptoolItemSelection(ActionContext actionContext){
    	Browser browser = (Browser) actionContext.get("browser");
    	browser.stop();
    }
    
    public static void refreshToolItemSelection(ActionContext actionContext){
    	Browser browser = (Browser) actionContext.get("browser");
    	browser.refresh();
    }
}