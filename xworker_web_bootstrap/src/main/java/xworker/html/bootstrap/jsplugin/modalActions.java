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
package xworker.html.bootstrap.jsplugin;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.html.HtmlUtil;

public class modalActions {
    public static Object toHtml(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        
        String html = "<div class=\"modal";
        
        //class
        if(self.getStringBlankAsNull("style") != null){
            html = html + " " + self.getString("style");
        }
        
        if(self.getBoolean("dropdown-toggle")){
            html = html + " dropdown-toggle";
        }
        
        if(self.getStringBlankAsNull("size") != null){
            html = html + " btn-" + self.getString("size");
        }
        
        if(self.getBoolean("active")){
            html = html + " active";
        }
        
        if(self.getBoolean("navbar-btn")){
            html = html + " navbar-btn";
        }
        
        html = html + "\"";
        if(self.getStringBlankAsNull("type") != null){
             html = html + " type=\"" + self.getString("type") + "\"";
        }
        if(self.getStringBlankAsNull("id") != null){
             html = html + " id=\"" + self.getString("id") + "\"";
        }
        
        Action commonAction = world.getAction("xworker.html.bootstrap.common/@actions/@toHtml");
        html = html + commonAction.run(actionContext);
        
        html = html + HtmlUtil.getAttr(self, "data-toggle", "data-toggle");
        
        if(self.getBoolean("disabled")){
            html = html + " disabled=\"disabled\"";
        }
        
        html = html + ">\n";
        if(self.getBoolean("sr-only") == false){
           html = html  + self.getString("text");
        }
        if(self.getBoolean("caret")){
           html = html + "  <span class=\"caret\"></span>\n";
        }
        if(self.getBoolean("sr-only")){
           html = html + "  <span class=\"sr-only\">" + self.getString("text") + "</span>\n";
        }
        
        for(Thing child : self.getChilds()){
            String code = (String) child.doAction("toHtml", actionContext);
            if(code != null){
                html = html + "  " + HtmlUtil.getIdentString(code, "  ") + "\n";
            }
        }
        html = html + "</button>";
        
        return html;
    }

}