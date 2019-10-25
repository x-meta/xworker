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
package xworker.http;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.ClipboardUtils;
import xworker.util.XWorkerUtils;

public class ControlSetCreator {
	public static void httpExecute(Thing currentThing, String uri, ActionContext actionContext){
        String httpServer = XWorkerUtils.getWebUrl();

        String url = httpServer + uri;
        XWorkerUtils.ideOpenUrl(url);
        /*
        ActionContext explorerContext = (ActionContext) actionContext.get("explorerContext");
        ActionContainer actions = (ActionContainer) explorerContext.get("actions");
        XWorkerUtils.ideOpenWebControl(control)
        actions.doAction("openUrl", UtilMap.toMap(new Object[]{"url",url, "name", currentThing}));
        */
	}
	
    public static void httpExecute(ActionContext actionContext){
        Thing currentThing = (Thing) actionContext.get("currentThing");
        String url = "do?sc=" + currentThing.getMetadata().getPath();
        
        httpExecute(currentThing, url, actionContext);
    }

    public static void httpExecuteUL(ActionContext actionContext){
    	Thing currentThing = (Thing) actionContext.get("currentThing");
        String url = currentThing.getMetadata().getPath().replace('.', '_') + ".do";
        
        httpExecute(currentThing, url, actionContext);
    }
    
    public static void httpExecuteR(ActionContext actionContext){
    	Thing currentThing = (Thing) actionContext.get("currentThing");
        String url = currentThing.getMetadata().getPath().replace('.', '/') + ".do";
        
        httpExecute(currentThing, url, actionContext);
    }
    
    public static void copyUrl(ActionContext actionContext){
    	Thing currentThing = (Thing) actionContext.get("currentThing");
    	String url = "do?sc=" + currentThing.getMetadata().getPath();
        
        copyUrl(url);
    }
    
    public static void copyUrlUL(ActionContext actionContext){
    	Thing currentThing = (Thing) actionContext.get("currentThing");
        String url = currentThing.getMetadata().getPath().replace('.', '_') + ".do";
        
        copyUrl(url);
    }
    
    public static void copyUrlR(ActionContext actionContext){
    	Thing currentThing = (Thing) actionContext.get("currentThing");
        String url = currentThing.getMetadata().getPath().replace('.', '/') + ".do";
        
        copyUrl(url);
    }
    
    public static void copyUrl(String uri){
        String url = uri;//httpServer + uri;
        
        ClipboardUtils.setSystemClipboard(url);
    }
}