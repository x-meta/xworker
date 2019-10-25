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

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.http.HttpRequestBean;

public class DocControlsActions {
    public static void doAction(ActionContext actionContext) throws IOException{
        //Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        HttpRequestBean requestBean = actionContext.getObject("requestBean");
        HttpServletResponse response = actionContext.getObject("response");
        
        Thing thing = world.getThing((String) requestBean.get("thingPath"));
        String descriptor = thing.getDescriptor().getMetadata().getPath();
        
        response.sendRedirect("do?sc=xworker.ide.worldExplorer.swt.http.ThingDoc/@desc&thing=" + descriptor);
    }

    public static void doAction1(ActionContext actionContext) throws IOException{
    	 World world = World.getInstance();
         HttpRequestBean requestBean = actionContext.getObject("requestBean");
         HttpServletResponse response = actionContext.getObject("response");
         
        Thing thing = world.getThing((String) requestBean.get("thing"));
        String  descriptor = thing.getString("descriptor");        
        response.sendRedirect("do?sc=xworker.ide.worldExplorer.swt.http.ThingDoc/@desc&thing=" + descriptor);
    }

}