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
package xworker.html;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class HtmlActionsActions {
    public static Object run(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        List<Thing> things = actionContext.getThings();
        Thing parentThing = null;
        if(things.size() > 1){
            parentThing = things.get(things.size() - 2);
        }
        
        String html = "<div";
        if(self.getStringBlankAsNull("attributes") != null){
            html = html + " " + self.getString("attributes");
        }
        html = html + ">\n";
        if(parentThing != null){
            for(Thing child : parentThing.getChilds()){
                String code = (String) child.doAction("toHtml", actionContext);
                if(code != null){
                    html = html + "  " + HtmlUtil.getIdentString(code, "  ") + "\n";
                }
            }
        }
        html = html + "</div>";
        
        return html;
    }

}