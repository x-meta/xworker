/*
 * Copyright 2007-2016 The xworker.org.
 *
 * Licensed to the X-Meta under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The X-Meta licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xworker.swt.xworker.attributeEditor.openWins;

import org.eclipse.swt.browser.Browser;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.util.XWorkerUtils;

public class ShellWithParamsWindowActions {
	public static void treeSelection(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        Thing thing = actionContext.getObject("thing");
        if(thing == null) {
        	return;
        }
        
        Thing thingForm = actionContext.getObject("thingForm");
        Object params = actionContext.get("params");
        Thing configThing = thing;
        if(configThing != null){
            actionContext.getScope(0).put("configThing", configThing);
            Thing paramsThing = configThing.getThing("ParamsThing@0");
            if(paramsThing != null){
                thingForm.doAction("setDescriptor", actionContext, "descriptor", paramsThing);
                
                String shellPath = actionContext.getObject("shellPath");
                if(shellPath != null && shellPath.equals(configThing.getString("shellPath"))){
                    thingForm.doAction("setValues", actionContext, "values", params);                
                }else if("xworker.swt.xworker.attributeEditor.openWins.ShellWithParams/@help".equals(configThing.getMetadata().getPath())){
                    thingForm.doAction("setValues", actionContext, "values", UtilMap.toMap("shellPath", shellPath));
                }
            }
        }   
        
        Browser browser = actionContext.getObject("browser");
        browser.setUrl(XWorkerUtils.getThingDescUrl(thing));
        /*
        Event event = actionContext.getObject("event");
        World world = World.getInstance();
        Thing thingForm = actionContext.getObject("thingForm");
        Object params = actionContext.get("params");
        
        Map<String, Object> data = (Map<String, Object>) event.item.getData();
        if(UtilData.isTrue(data.get("leaf"))){ 
            String id = (String) data.get("id");
            String configPath = id.substring(6, id.length());
            Thing configThing = world.getThing(configPath);
            if(configThing != null){
                actionContext.getScope(0).put("configThing", configThing);
                Thing paramsThing = configThing.getThing("ParamsThing@0");
                if(paramsThing != null){
                    thingForm.doAction("setDescriptor", actionContext, "descriptor", paramsThing);
                    
                    String shellPath = actionContext.getObject("shellPath");
                    if(configThing.getString("shellPath").equals(shellPath)){
                        thingForm.doAction("setValues", actionContext, "values", params);                
                    }else if("xworker.swt.xworker.attributeEditor.openWins.ShellWithParams/@help".equals(configThing.getMetadata().getPath())){
                        thingForm.doAction("setValues", actionContext, "values", UtilMap.toMap("shellPath", shellPath));
                    }
                }
            }else{
                logger.warn("Shell with params config thing is null, path=" + configPath);
            }    
        }*/
    }

}