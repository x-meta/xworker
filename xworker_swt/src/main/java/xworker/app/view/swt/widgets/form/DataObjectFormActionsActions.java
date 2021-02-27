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

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;

public class DataObjectFormActionsActions {
	private static final String TAG = DataObjectFormActionsActions.class.getName();
	
    public static void run(ActionContext actionContext) throws OgnlException{
        Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        
        //表单对象
        Thing form = (Thing) OgnlUtil.getValue(self.getString("formName"), actionContext);
        if(form == null){
            Executor.warn(TAG, "DataObjectForm: form not exists, name=" + self.getString("formName"));
            return;
        }
        
        //数据对象
        Object dataObject = null;
        if(self.getStringBlankAsNull("dataObject") == null){
            Executor.warn(TAG, "DataObjectForm: need specify dataobject");
            return;
        }else{    
            try{
                dataObject = OgnlUtil.getValue(self.getStringBlankAsNull("dataObject"), actionContext);
            }catch(Exception e){
            }
            if(dataObject == null){
                dataObject = world.getThing(self.getStringBlankAsNull("dataObject"));
            }
            
            if(dataObject == null){
                Executor.warn(TAG, "DataObjectForm: dataObject not exists, dataObject=" + self.getStringBlankAsNull("dataObject"));
                return;
            }
        }
        
        //设置数据对象
        form.doAction("setDataObject", actionContext, "dataObject", dataObject);
    }

    public static Object run1(ActionContext actionContext) throws OgnlException{
        Thing self = actionContext.getObject("self");
        
        //表单对象
        Thing form = (Thing) OgnlUtil.getValue(self.getString("formName"), actionContext);
        if(form == null){
            Executor.warn(TAG, "DataObjectForm: form not exists, name=" + self.getString("formName"));
            return "failure";
        }
        
        //表单的值
        Object values = form.doAction("getDataObject", actionContext);
        
        //放置值
        Bindings binding = (Bindings) self.doAction("getVarScope", actionContext);
        
        if(binding != null){
        	OgnlUtil.setValue(self.getString("variable"), binding, values);
            return "success";
        }else{
            return "failure";
        }
    }

    public static void run2(ActionContext actionContext) throws OgnlException{
        Thing self = actionContext.getObject("self");
        
        //表单对象
        Thing form = (Thing) OgnlUtil.getValue(self.getString("formName"), actionContext);
        if(form == null){
            Executor.warn(TAG, "DataObjectForm: form not exists, name=" + self.getString("formName"));
            return;
        }
         
        //数据值
        Object values = null;
        String valueVariable = self.getStringBlankAsNull("valueVariable");
        if(valueVariable != null){
            try{
                values = OgnlUtil.getValue(valueVariable, actionContext);
            }catch(Exception e){
                Executor.warn(TAG, "DataObjectForm: get values error", e);
            }
        }
        
        //设置值
        if(values == null){
            Executor.warn(TAG, "DataObjectForm: value not exists, can not set values");
            return;
        }else{
            if(self.getBoolean("partialValue")){
                form.doAction("setPartialValues", actionContext, "values", values);
            }else{
                form.doAction("setValues", actionContext, "values", values);
            }
        }
    }

    public static Object run3(ActionContext actionContext) throws OgnlException{
        Thing self = actionContext.getObject("self");
        
        //表单对象
        Thing form = (Thing) OgnlUtil.getValue(self.getString("formName"), actionContext);
        if(form == null){
            Executor.warn(TAG, "DataObjectForm: form not exists, name=" + self.getString("formName"));
            return null;
        }
        
        //表单的值
        Object values = form.doAction("getValues", actionContext);
        
        //放置值
        org.xmeta.util.UtilAction.putVarByActioScope(self, self.getString("variable"), values, actionContext);
        
        return values;
    }

    public static void run4(ActionContext actionContext) throws OgnlException{
        Thing self = actionContext.getObject("self");
        
        //表单对象
        Thing form = (Thing) OgnlUtil.getValue(self.getString("formName"), actionContext);
        if(form == null){
            Executor.warn(TAG, "DataObjectForm: form not exists, name=" + self.getString("formName"));
            return;
        }
        
        //取数据对象
        DataObject dataObject = (DataObject) form.doAction("getDataObject", actionContext);
        
        //创建数据对象
        if(dataObject != null){
            dataObject.doAction("create", actionContext);
        }
        
        //回填数据
        form.doAction("setValues", actionContext, "values", dataObject);
        
        //设置数据对象
        form.doAction("setDataObject", actionContext, "dataObject", dataObject);
    }

    public static void run5(ActionContext actionContext) throws OgnlException{
        Thing self = actionContext.getObject("self");
        
        //表单对象
        Thing form = (Thing) OgnlUtil.getValue(self.getString("formName"), actionContext);
        if(form == null){
            Executor.warn(TAG, "DataObjectForm: form not exists, name=" + self.getString("formName"));
            return;
        }
        
        //取数据对象
        DataObject dataObject = (DataObject) form.doAction("getDataObject", actionContext);
        
        //创建数据对象
        if(dataObject != null){
            dataObject.doAction("update", actionContext);
        }
        
        //回填数据
        form.doAction("setValues", actionContext, "values", dataObject);
        
        //设置数据对象
        form.doAction("setDataObject", actionContext, "dataObject", dataObject);
    }

}