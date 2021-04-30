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

public class button_groupActions {
    public static Object toHtml(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String html = "<div class=\"";
        
        String type = self.getString("type");
        String size = self.getStringBlankAsNull("size");
        
        if("group" == type){
            html = html + "btn-group";
        }else if("toolbar" == type){
            html = html + "btn-toolbar";
        }else if("vertical" == type){
            html = html + "btn-group-vertical";
        }else if("justified" == type){
            html = html + "btn-group btn-group-justified";
        }else{
            html = html + "btn-group";
        }
        if(self.getBoolean("dropup")){
            html = html + " dropup";
        }
        if(size != null){
            html = html + " btn-group-" + size + "\"";
        }else{
            html = html + "\"";
        }
        
        if("group" == type){
            html = html + " role=\"group\"";
        }else if("toolbar" == type){
            html = html + " role=\"toolbar\"";
        }else if("vertical" == type){
            html = html + " role=\"group\"";
        }else if("justified" == type){
            html = html + " role=\"group\"";
        }
        
        html = html + HtmlUtil.getAttr(self, "aria-label", "aria-label");
        html = html + ">\n";
        for(Thing child :self.getChilds()){
            String code = (String) child.doAction("toHtml", actionContext);
            if(code != null){
                html = html + "  " + HtmlUtil.getIdentString(code, "  ") + "\n";
            }
        }
        html = html + "</div>";
        
        return html;
    }

}