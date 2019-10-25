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
package xworker.html.bootstrap.widgets;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.html.HtmlUtil;

public class progressActions {
    public static Object toHtml(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String html = "<div class=\"progress\">";
        
        for(Thing child : self.getChilds()){
            String code = (String) child.doAction("toHtml", actionContext);
            if(code != null){
                html = html + "  " + HtmlUtil.getIdentString(code, "  ") + "\n";
            }
        }
        html = html + "</div>";
        
        return html;
    }

    public static Object toHtml1(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String html = "<div class=\"progress-bar";
        String style = self.getStringBlankAsNull("style");
        if(style != null){
            html = html + " progress-bar-" + style;
        }
        if(self.getBoolean("striped")){
            html = html + " progress-bar-striped";
        }
        if(self.getBoolean("active")){
            html = html + " active";
        }
        html = html + "\"";
        
        html = html + HtmlUtil.getAttr(self, "valuenow", "aria-valuenow");
        html = html + HtmlUtil.getAttr(self, "valuemin", "aria-valuemin");
        html = html + HtmlUtil.getAttr(self, "valuemax", "aria-valuemax");
        html = html + " style=\"width: " + self.getString("progress") + "\"";
        html = html + ">\n";
        if(self.getBoolean("showText")){
            html = html + "  " + self.getString("text") + "\n";
        }else{
            html = html + "  <span class=\"sr-only\">" + self.getString("text") + "</span>\n";
        }
        
        html = html + "</div>";
        
        return html;
    }

}