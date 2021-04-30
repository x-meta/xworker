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

import xworker.html.HtmlUtil;

public class dataActions {
    public static Object toHtml(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String html = "";
        html = html + HtmlUtil.getAttr(self, "data-togglel", "data-toggle");
        html = html + HtmlUtil.getAttr(self, "data-target", "data-target");
        html = html + HtmlUtil.getAttr(self, "data-dismiss", "data-dismiss");
        html = html + HtmlUtil.getAttr(self, "title", "title");
        html = html + HtmlUtil.getAttr(self, "data-container", "data-container");
        html = html + HtmlUtil.getAttr(self, "data-placement", "data-placement");
        html = html + HtmlUtil.getAttr(self, "data-trigger", "data-trigger");
        html = html + HtmlUtil.getAttr(self, "data-content", "data-content");
        
        return html;
    }

}