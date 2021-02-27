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
package xworker.html.design.extjs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class XWorkerCheckLoginActions {
    public static Object doAction(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        HttpServletRequest request = actionContext.getObject("request");
        HttpSession session = actionContext.getObject("session");
        
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        
        Thing securityHandler = world.getThing("_local.xworker.config.DefaultSecurityHandler");
        Object user = securityHandler.doAction("login", actionContext, 
            "name", name, "password",  password);
        if(user != null){
            session.setAttribute("xworker_thingManager", user);
            return "success";
        }else{
            return "failure";
        }
    }

}