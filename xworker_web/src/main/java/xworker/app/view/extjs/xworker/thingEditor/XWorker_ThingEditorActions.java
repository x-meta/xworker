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
package xworker.app.view.extjs.xworker.thingEditor;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.http.HttpRequestBean;

public class XWorker_ThingEditorActions {
    public static Object doAction(ActionContext actionContext){
    	HttpRequestBean requestBean = actionContext.getObject("requestBean");
    	World world = World.getInstance();
        //获取事物
        Thing thing = world.getThing((String) requestBean.get("thingPath"));
        
        actionContext.getScope(0).put("thing", thing);
        actionContext.getScope(0).put("crrenttime", "" + System.currentTimeMillis());
        
        return "success";
    }

    public static Object httpDo(ActionContext actionContext){
    	HttpRequestBean requestBean = actionContext.getObject("requestBean");
    	World world = World.getInstance();
    	
        //获取事物
        Thing thing = world.getThing((String) requestBean.get("thingPath"));
        
        Thing descriptor = world.getThing((String) requestBean.get("descriptor"));
        
        actionContext.getScope(0).put("thing", thing);
        actionContext.getScope(0).put("descriptor", descriptor);
        actionContext.getScope(0).put("crrenttime", "" + System.currentTimeMillis());
        
        Action action = world.getAction("xworker.html.extjs.ExtJs/@actions/@httpDo");
        return action.run(actionContext);
    }

}