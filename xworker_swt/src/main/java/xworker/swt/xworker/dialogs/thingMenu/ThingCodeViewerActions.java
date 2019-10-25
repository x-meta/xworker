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
package xworker.swt.xworker.dialogs.thingMenu;

import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.actions.ActionContainer;

public class ThingCodeViewerActions {
    public static Object create(ActionContext actionContext){
        Thing currentThing = actionContext.getObject("currentThing");
        World world = World.getInstance();        
        
        //参数
        Map<String, Object> params = actionContext.getObject("params");
        
        //生成代码
        String code = (String) currentThing.doAction((String) params.get("actionName"), actionContext);
        
        //生成查看代码的窗口
        Thing codeViewerThing = world.getThing("xworker.swt.xworker.dialogs.CodeViewer");
        Shell shell = (Shell) codeViewerThing.doAction("create", actionContext);
        ActionContainer actions = actionContext.getObject("actions");
        
        //设置代码, acitons为codeViewerThing执行create之后产生的
        actions.doAction("setCode", actionContext, "code", code, "codeType", params.get("codeType"));
        
        return shell;
    }

}