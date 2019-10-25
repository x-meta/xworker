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
package xworker.app.view.swt.widgets.form;

import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class DataObjectEditDialogActions {
    @SuppressWarnings("unchecked")
	public static void okButtonAction(ActionContext actionContext){
        Thing form = actionContext.getObject("form");
        Shell shell = actionContext.getObject("shell");
        
        Map<String, Object> vs = (Map<String, Object>) form.doAction("getValues", actionContext);
        Map<String, Object> values = (Map<String, Object>)  form.getData("values");
        if(values != null && vs != null){
            for(String key : vs.keySet()){
                values.put(key, vs.get(key));
            }
        }else{
            values = vs;
        }
        
        actionContext.getScope(0).put("result", values);
        shell.dispose();
    }

    public static void cancelButtonSelection(ActionContext actionContext){
    	Shell shell = actionContext.getObject("shell");
        actionContext.getScope(0).put("result", null);
        shell.dispose();
    }

    public static void setValues(ActionContext actionContext){
    	Thing form = actionContext.getObject("form");
    	ActionContext ac = actionContext.getObject("ac");
    	
        //取数据
        Object dataObject = actionContext.get("dataObject");
        if(dataObject == null){
            dataObject = actionContext.get("values");
        }
        
        form.doAction("setDataObject", ac, "dataObject", dataObject);
    }

    public static Object getValues(ActionContext actionContext){
        return null;    
    }

}