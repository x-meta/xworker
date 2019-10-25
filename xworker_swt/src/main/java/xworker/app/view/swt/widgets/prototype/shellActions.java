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
package xworker.app.view.swt.widgets.prototype;

import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class shellActions {
    @SuppressWarnings("unchecked")
	public static void okGroovy(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        Thing form = actionContext.getObject("form");
        Thing dataStore = actionContext.getObject("dataStore");
        Shell shell = actionContext.getObject("shell");
        ActionContext parentContext = actionContext.getObject("parentContext");
        
        Map<String, Object> values = (Map<String, Object>) form.doAction("getValues", actionContext);
        Map<String, Object> queryValues = (Map<String, Object>)  parentContext.get("queryValues");
        if(queryValues == null){
            queryValues = values;    
        }else{
            queryValues.putAll(values);
        }
        parentContext.g().put("queryValues", queryValues);
        dataStore.doAction("load", parentContext, "params", queryValues);
        
        shell.dispose();
    }

}