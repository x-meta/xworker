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
package xworker.html.bootstrap.css;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.html.HtmlUtil;

public class imgActions {
    public static Object toHtml(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String href = self.getStringBlankAsNull("href");
        String html = "<img";
        if(href != null){
            html = "<a href=\"" + href + "\">" + html;
        }
        
        html = html + HtmlUtil.getAttr(self, "src", "src");
        html = html + HtmlUtil.getAttr(self, "alt", "alt");
        html = html + HtmlUtil.getAttr(self, "width", "width");
        html = html + HtmlUtil.getAttr(self, "height", "height");
        html = html + " class=\"";
        
        if(self.getBoolean("responsive")){
            html = html + "img-responsive";
        }
        if(self.getStringBlankAsNull("shape") != null){
            html = html + " img-" + self.getString("shape");
        }
        
        html = html + "\"/>";
        if(href != null){
            html = html + "</a>";
        }
        return html;
    }

}