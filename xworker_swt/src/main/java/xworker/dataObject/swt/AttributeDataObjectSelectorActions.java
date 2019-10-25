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
package xworker.dataObject.swt;

import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.dataObject.DataObject;

public class AttributeDataObjectSelectorActions {
    @SuppressWarnings("unchecked")
	public static void selection(ActionContext actionContext) throws OgnlException{
        //Thing self = actionContext.getObject("self");
        Table dataTable= actionContext.getObject("dataTable");
        String attributeName= actionContext.getObject("attributeName");
        String valueMapping= actionContext.getObject("valueMapping");
        String relationName= actionContext.getObject("relationName");
        Shell shell= actionContext.getObject("shell");
        ActionContext textContext = actionContext.getObject("textContext");
        ActionContext parentContext = actionContext.getObject("parentContext");
        Thing editModel = actionContext.getObject("editModel");
        
        
        //设置输入框的值
        Object data = dataTable.getSelection()[0].getData();
        Object value = OgnlUtil.getValue(attributeName, data);
        if(value != null){
            ((Text) textContext.get("text")).setText(value.toString());
        }
        
        //设置其他映射的参数
        if(valueMapping != null && valueMapping != ""){
            Map<String, Object> values = ( Map<String, Object>) editModel.doAction("getValue", parentContext);
            for(String vm : valueMapping.split("[,]")){
                String[] vs = vm.split("[:]");
                if(vs.length >= 2){
                    values.put(vs[1], OgnlUtil.getValue(vs[0], data));
                }
            }
            //log.info("values=" + values);
            //log.info("dataSource=" + editModel.dataSource);
            editModel.doAction("setValue", parentContext, "thingAttributes", values);
            //log.info("xxxxxxxxxxxxxxxxxxxx");
        }
        
        if(relationName != null && relationName != "" && parentContext.get("__dataObjectForm") != null){
            //log.info("dfd");
            //数据对象的表单
            Thing form = (Thing) parentContext.get("__dataObjectForm");
            DataObject dataObject = (DataObject) form.doAction("getDataObject", parentContext);
            dataObject.put(relationName, data);
            
            //log.info("dataObject=" + dataObject);
            
            //回填数据
            form.doAction("setValues", parentContext, "values",  dataObject);
        }
        
        shell.dispose();
    }

}