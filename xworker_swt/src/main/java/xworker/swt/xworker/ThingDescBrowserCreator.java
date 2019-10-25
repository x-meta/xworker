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

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.swt.util.UtilBrowser;
import xworker.util.XWorkerUtils;

public class ThingDescBrowserCreator {
    public static Object create(ActionContext actionContext){
        World world = World.getInstance();
        Thing self = (Thing) actionContext.get("self");
		
		int browserStyle = SWT.NONE;
		if(self.getBoolean("BORDER"))
		    browserStyle = browserStyle | SWT.BORDER;
		if(self.getBoolean("MOZILLA"))
		    browserStyle = browserStyle | SWT.MOZILLA;
		if(self.getBoolean("NO_FOCUS"))
		    browserStyle |= SWT.NO_FOCUS;
		    
		Browser browse = new Browser((Composite) actionContext.get("parent"), browserStyle);
		if(actionContext.get("utilBrowser") != null){
		    //log.info("utilBrowser=" + utilBrowser);
		    ((UtilBrowser) actionContext.get("utilBrowser")).attach(browse);
		}else{
			UtilBrowser.attach(browse, actionContext);
		}
		Thing thing = self.doAction("getThing", actionContext);//world.getThing(self.getString("thing"));
		if(thing == null){
			thing = self;
		}
		if(thing != null){
			String url = XWorkerUtils.getThingDescUrl(thing);
			browse.setUrl(url);
			/*
		    Thing globalConfig = world.getThing("_local.xworker.config.GlobalConfig");
		    if(globalConfig != null){
		    	String webUrl = globalConfig.getString("webUrl");
		    	browse.setUrl(webUrl + "do?sc=xworker.ide.worldExplorer.swt.http.ThingDoc/@desc&thing=" + thing.getMetadata().getPath() + "&nohead=" + self.getBoolean("nohead"));
		    }else{
		    	String desc = thing.getStringBlankAsNull("description");
		    	if(desc != null){
		    		browse.setText(desc);
		    	}
		    }*/
		}		
		//保存变量和创建子事物		
		actionContext.peek().put("parent", browse);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Designer.attachCreator(browse, self.getMetadata().getPath(), actionContext);
		
		//露出的动作，供外部调用设置事物
		ActionContext ac = new ActionContext();
		ac.put("browser", browse);
		Thing actionThing = world.getThing("xworker.swt.xworker.ThingDescBrowser/@actions");
		Object action = actionThing.doAction("create", ac);
		actionContext.getScope(0).put(self.getString("name") + "Action", action);
		actionContext.getScope(0).put(self.getString("name"), browse);
		browse.setData("action", action);
		
		return browse;        
	}

    public static void setThing(ActionContext actionContext){
    	//World world = World.getInstance();
    	Browser browser = (Browser) actionContext.get("browser");
    	Thing thing = (Thing) actionContext.get("thing");
    	if(thing != null){
    		String webUrl = XWorkerUtils.getThingDescUrl(thing);
       		browser.setUrl(webUrl);
    	}else{
    	    browser.setText("Thing not exists");
    	}
    }
}