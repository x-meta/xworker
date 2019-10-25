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
package xworker.lang.util;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class ThingCreatorActions {
    public static Object process(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        Thing templateNode = self.getThing("ThingTemplate@0");
        
        if(templateNode != null){    
        	String extendTemplate = templateNode.getStringBlankAsNull("extendTemplate");
            if(extendTemplate != null){
                templateNode = world.getThing(extendTemplate);
            }
            Thing thing = (Thing) templateNode.doAction("process", actionContext, "data", actionContext.get("data"));
            if(self.getBoolean("includeRoot")){
                return thing;
            }else{
                if(thing.getChilds().size()> 0){
                    return thing.getChilds().get(0);
                }else{
                    return null;
                }
            }
        }else{
            return null;
        }        
    }
    
    /*
    def change(thing, data){
        for(key in thing.getAttributes().keySet()){
            def value = thing.getAttributes().get(key);
            value = changeValue(value, data);
            thing.set(key, value);
        }
        
        for(child in thing.getChilds()){
            change(child, data);
        }
    }
    
    def changeValue(value, data){
        if(value == null) return null;
        if(!value instanceof String) return value;
        if(value.indexOf("\${") == -1) return value;
        
        for(key in data.keySet()){        
            def v = data.get(key);
            if(v == null) v = "";
                    
            value = value.replaceAll("(\\\$\\{" + key + "\\})", v);  
            //log.info(value);      
        }
        
        return value;
    }*/

}