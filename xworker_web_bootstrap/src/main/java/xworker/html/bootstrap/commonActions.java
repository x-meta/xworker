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
package xworker.html.bootstrap;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class commonActions {
    public static Object toHtml(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String html = "";
        
        html = html + execute("xworker.html.bootstrap.aria/@actions/@toHtml", actionContext);
        html = html + execute("xworker.html.bootstrap.data/@actions/@toHtml", actionContext);
        
        if(self.getStringBlankAsNull("extraAttributes") != null){
            html = html + " " + self.getString("extraAttributes");
        }
        return html;
        
       
    }
    
    public static Object  execute(String actionPath, ActionContext actionContext){
    	World world = World.getInstance();
        Thing action = world.getThing(actionPath);    
        return action.doAction("toHtml", actionContext);
    }

}