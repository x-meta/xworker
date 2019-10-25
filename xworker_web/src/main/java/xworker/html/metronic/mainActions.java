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
package xworker.html.metronic;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class mainActions {
    public static void init(ActionContext actionContext){
        Thing data = actionContext.getObject("data");
        //获取本地变量范围
        Bindings sc = actionContext.getScope();
        
        //头部左边菜单
        Thing topBar = data.getThing("TopNavbar@0");
        if(topBar != null){
            Object topBarHtml = topBar.doAction("toHtml", actionContext);
            sc.put("topNavbar", topBarHtml);
        }
        
        //头部右边菜单
        Thing rightBar = data.getThing("RightNavbar@0");
        if(rightBar != null){
        	Object rightBarHtml = rightBar.doAction("toHtml", actionContext);
            sc.put("rightNavbar", rightBarHtml);
        }
        
        //主内容
        Thing content = data.getThing("Content@0");
        if(content != null){
        	Object contentHtml = content.doAction("toHtml", actionContext);
            sc.put("mainPage", contentHtml);
        }
    }

}