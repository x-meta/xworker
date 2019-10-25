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
package xworker.html.bootstrap.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public class AccordionActions {
    public static void init(ActionContext actionContext){
        Thing data = actionContext.getObject("data");
        List<Map<String, Object>> contents = new ArrayList<Map<String, Object>>();
        for(Thing child : data.getChilds()){
            Map<String, Object> content = UtilMap.toMap("title", child.getString("title"));
            content.put("id", child.getString("id"));
            content.put("in", child.getString("in"));
            content.put("expanded", child.getString("expanded"));
            content.put("nobody", child.getString("nobody"));
            content.put("content", child.doAction("toHtml", actionContext));
            contents.add(content);
        }
        
        actionContext.getScope().put("contents", contents);
    }

    public static Object toHtml(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String html = "";
        if(self.getStringBlankAsNull("content") != null){
            html = self.getString("content");
        }
        
        for(Thing child : self.getChilds()){
            String code = (String) child.doAction("toHtml", actionContext);
            if(code != null){
                html = html + "\n" + code;
            }
        }
        
        return html;
    }

}