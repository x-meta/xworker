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

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThumbnailsActions {
    public static void init(ActionContext actionContext){
        Thing data = actionContext.getObject("data");
        if(data.getStringBlankAsNull("listVar") == null){
            List<String> items = new ArrayList<String>();
            for(Thing child : data.getChilds()){
                String code = (String) child.doAction("toHtml", actionContext);
                if(code != null){
                    items.add(code);
                }
            }
            
            actionContext.getScope().put("items", items);
        }
    }

}