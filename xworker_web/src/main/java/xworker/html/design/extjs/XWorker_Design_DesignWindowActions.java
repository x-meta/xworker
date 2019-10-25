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

import javax.servlet.http.HttpSession;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class XWorker_Design_DesignWindowActions {
    public static void httpDo(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        HttpSession session = actionContext.getObject("session");
        
        Action action = world.getAction("xworker.html.extjs.xw.remote.RemoteWidget/@actions/@httpDo");
        if(session.getAttribute("xworker_thingManager") == null){
            //换成登录窗口
           Thing login = world.getThing("xworker.html.design.extjs.XWorker_Design_Login_Window");
            login.doAction("httpDo", actionContext);    
        }else{
            //还是设计器登录
            action.run(actionContext);
        }
    }

}