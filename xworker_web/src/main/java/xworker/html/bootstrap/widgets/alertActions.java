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

public class alertActions {
    public static Object toHtml(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String html = "<div class=\"alert";
        String style = self.getStringBlankAsNull("style");
        if(style != null){
            html = html + "  alert-" + style;
        }
        if(self.getBoolean("dismissible")){
            html = html + " dismissible";
        }
        html = html + "\">\n";
        
        for(Thing child : self.getChilds()){
            String code = (String) child.doAction("toHtml", actionContext);
            if(code != null){
                html = html + "  " + HtmlUtil.getIdentString(code, "  ") + "\n";
            }
        }
        html = html + "</div>";
        
        return html;
    }

}