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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class NavtabsActions {
    public static void init(ActionContext actionContext){
        Thing data = actionContext.getObject("data");
        List<Map<String, Object>> items = null;
        if(data.getStringBlankAsNull("tabVar") == null){
            items =new ArrayList<Map<String, Object>>();
            List<Thing> things = data.getChilds("TabItem");
            for(Thing thing : things){
                items.add(createItem(thing, actionContext));
            }
             actionContext.getScope().put("items", items);
        }else{
            actionContext.getScope().put("items", null);
        }
        
        
    }
    
    public static Map<String, Object> createItem(Thing thing, ActionContext actionContext){
        Map<String, Object> item = new HashMap<String, Object>();
        item.putAll(thing.getAttributes());
        if(thing.getStringBlankAsNull("content") == null){
            String code = "";
            for(Thing child : thing.getChilds()){
                if(child.isThing("TabItem")){
                    continue;
                }
                String html = (String) child.doAction("toHtml", actionContext);
                if(html != null){
                    code = code + html;
                }
            }
            item.put("content", code);
        }
        
        if(thing.getStringBlankAsNull("href") == null){
            if(item.get("ontent") == null || "".equals(item.get("content"))){
                item.put("href", "#");
            }else{
                item.put("href", "#" + thing.getString("id"));
            }
        }
        
        List<Map<String, Object>> childs = new ArrayList<Map<String, Object>>();
        for(Thing child : thing.getChilds("TabItem")){
            childs.add(createItem(child, actionContext));
        }
        item.put("childs", childs);
        
        return item;
    }

}