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

public class modal_dialogActions {
    public static Object toHtml(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        
        //-----------------div 标签-------------
        String html = "<div class=\"modal";
        if(self.getBoolean("fade")){
            html = html + " fade";
        }
        html = html + "\" role=\"dialog\"";
        
        if(self.getBoolean("show")){
            html = html + " tabindex=\"-1\"";
        }
        
        html = html + HtmlUtil.getAttr(self, "id", "id");
        
        //其他属性
        Action commonAction = world.getAction("xworker.html.bootstrap.common/@actions/@toHtml");
        html = html + commonAction.run(actionContext);
        html = html + ">\n";
        
        //------------------model标签-----------
        html = html + "  <div class=\"modal-dialog";
        if(self.getStringBlankAsNull("size") != null){
            html = html + " modal-" + self.getString("size");
        }
        html = html + "\" role=\"document\">\n";
        
        //-----------------content 标签----------
        html = html + "    <div class=\"modal-content\">\n";
        
        for(Thing child : self.getChilds()){
            String code = (String) child.doAction("toHtml", actionContext);
            if(code != null){
                html = html + "      " + HtmlUtil.getIdentString(code, "      ") + "\n";
            }
        }
        html = html + "    </div>\n  </div>\n</div>";
        
        return html;
    }

    public static Object toHtml1(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String html = "<div class=\"modal-header\">\r\n";
        html = html + "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span></button>\r\n";
        html = html + "<h4 class=\"modal-title\" id=\"myModalLabel\">" + self.get("title") + "</h4>\r\n";
        html = html + "</div>";
        
        return html;
    }

}