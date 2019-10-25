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
package xworker.html.base.container;

import java.io.IOException;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import freemarker.template.TemplateException;
import xworker.util.UtilTemplate;

public class FreeLayoutActions {
    public static Object toHtml(ActionContext actionContext) throws IOException, TemplateException{
        Thing self = actionContext.getObject("self");
        //World world = World.getInstance();
        
        //先遍历执行子节点的toHtml的方法，并保存子节点的HTML
        Bindings l = actionContext.peek();
        for(Thing child : self.getChilds()){
            l.put(child.getMetadata().getName(), child.doAction("toHtml", actionContext));
        }
        
        //把code当做Freemarker模板，生成最终的页面
        return UtilTemplate.processString(actionContext, self.getString("code"));
    }

}