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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.util.GlobalConfig;

public class HtmlDescEditor {
	public static Object create(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
		Thing codeEditor = world.getThing("xworker.swt.xworker.HtmlEditor/@htmlViewForm1");
		ActionContext newContext = new ActionContext();
		newContext.put("parent", actionContext.get("parent"));
		newContext.put("utilBrowser", actionContext.get("utilBrowser"));
		newContext.put("toolbarSet", self.getString("toolbarSet"));
		newContext.put("modifyListener", actionContext.get("modifyListener"));
		Composite composite = null;
		Designer.pushCreator(self);
		try{
			composite = codeEditor.doAction("create", newContext);
		}finally{
			Designer.popCreator();
		}
		
		//创建子节点
		actionContext.peek().put("parent", newContext.get("htmlViewForm"));
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Thing htmlThing = new Thing("xworker.swt.xworker.HtmlDescEditor");
		htmlThing.set("browser", newContext.get("browser"));
		htmlThing.set("actionContext", newContext);
		actionContext.getScope(0).put(self.getString("name"), htmlThing);
		newContext.put("htmlThing", htmlThing);
		
		Designer.attach((Control) newContext.get("browser"), self.getMetadata().getPath(), actionContext);
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		composite.setData("htmlThing", htmlThing);
		return composite;        
	}

    public static void setValue(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		final Browser browser = (Browser) self.getAttribute("browser");
		//设置浏览器应该显示的内容
		String value = (String) actionContext.get("value");
		String url = GlobalConfig.getThingDescUrl(value);
		browser.setUrl(url);
	}

    public static Object getValue(ActionContext actionContext){    	
		return null;        
	}
}