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

public class dropdownActions {
    public static Object toHtml(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String html = "<div class=\"" + self.getString("type") + "\">\n";
        for(Thing child : self.getChilds()){
            String code = (String) child.doAction("toHtml", actionContext);
            if(code != null){
                html = html + "  " + HtmlUtil.getIdentString(code, "  ") + "\n";
            }
        }
        return html + "</div>";
    }

    public static Object toHtml1(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String html = "<ul class=\"" + self.getString("type") + "\"";
        html = html + HtmlUtil.getAttr(self, "aria-labelledby", "aria-labelledby");
        html = html + ">\n";
        if(self.getBoolean("useItemCode")){
            html = html + self.getString("itemCode");
        }else{
            for(Thing child : self.getChilds()){
                String code = (String) child.doAction("toHtml", actionContext);    
                if(code != null){
                    html = html + "  " + HtmlUtil.getIdentString(code, "  ") + "\n";
                }
            }
        }
        html = html + "</ul>";
        return html;
    }

    public static Object toHtml2(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String html = "<li class=\"";
        
        html = html + HtmlUtil.getAttrBoolean(self, "header", "header", "");
        html = html + HtmlUtil.getAttrBoolean(self, "divider", " divider", "");
        html = html + HtmlUtil.getAttrBoolean(self, "disabled", " disabled", "");
        if(self.getBoolean("divider")){
            html = html + " role=\"separator\"";
        }
        html = html + "\"><a href=\"" + self.getString("href") + "\">";
        html = html + self.getString("text") + "</a></li>";
        
        return html;
    }

}