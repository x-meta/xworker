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
package xworker.http.controls;

import java.io.IOException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import freemarker.template.TemplateException;
import xworker.util.UtilTemplate;

public class SimpleControlRegionCreator {
    public static void control(ActionContext actionContext) throws IOException, TemplateException{
    	Thing self = (Thing) actionContext.get("self");
        String controlPath = self.getString("value");
        if(controlPath.indexOf("${") != -1){
            controlPath = UtilTemplate.processString(actionContext, self.getMetadata().getPath() + "value", controlPath);
        }    
        
        Thing controlThing = World.getInstance().getThing(controlPath);
        
        if(controlThing != null){
            controlThing.doAction("httpDo", actionContext);
        }
    }

    public static void script(ActionContext actionContext) throws IOException, TemplateException{
    	Thing self = (Thing) actionContext.get("self");        
    	String actionPath = self.getString("value");
        if(actionPath.indexOf("${") != -1){
            actionPath = UtilTemplate.processString(actionContext, self.getMetadata().getPath() + "value", actionPath);
        }    
        
        Thing actionThing = World.getInstance().getThing(actionPath);
        
        if(actionThing != null){
            actionThing.getAction().run(actionContext);
        }
    }

}