/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.lang.actions;

import java.util.Date;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import ognl.OgnlException;
import xworker.lang.executor.Executor;

public class PropertySetterCreator {
	private static final String TAG = PropertySetterCreator.class.getName();
	
    public static void run(ActionContext actionContext) throws OgnlException{
        Thing self = (Thing) actionContext.get("self");
        
        //log.info("name="+ self.propertyName);
        Object value = null;
        //log.info("data=" + data);
        String valueSource = self.getString("valueSource"); 
        if("get".equals(valueSource)){
        	String svalue = self.getString("value");
            if(svalue == null || "".equals(svalue)){
                return;
            }
            value = OgnlUtil.getValue(svalue, actionContext);
            //类型转换
            //根据类型创建    
            String type = self.getString("type");
            
            if("string".equals(type)){
             
            }else if("byte".equals(type)){
                value = UtilData.getByte(value, (byte) 0);
            }else if("short".equals(type)){
                value = UtilData.getShort(value, (short) 0);
            }else if("int".equals(type)){
                value = UtilData.getInt(value, 0);
            }else if("long".equals(type)){
                value = UtilData.getLong(value, 0);
            }else if("float".equals(type)){
                value = UtilData.getFloat(value, 0);
            }else if("double".equals(type)){
                value = UtilData.getDouble(value, 0);
            }else if("boolean".equals(type)){
                value = UtilData.getBoolean(value, false);
            }else if("now".equals(type)){
                value = new Date();
            }else if("hexToByte[]".equals(type)){
                value = UtilString.hexStringToByteArray((String) value);
            }else if("null".equals(type)){
                value = null;             
            }
        }else if("action".equals(valueSource)){
        	value = self.doAction("getValue", actionContext);
        } else{
             Action valueFactory = World.getInstance().getAction("xworker.lang.actions.ValueFactory/@actions/@run");
             value = valueFactory.run(actionContext);
        }
        
        String dataSource= self.getString("dataSource");
        if(dataSource != null &&!"".equals(dataSource)){
            Object data = OgnlUtil.getValue(dataSource, actionContext);
            if(data != null){
            	String propertyName = self.getString("propertyName"); 
            	Executor.info(TAG, "propertyName: " + propertyName + " , value=" + value);
            	OgnlUtil.setValue(propertyName, data, value);
            }else{
            	Executor.info(TAG, "dataSource is null, dataSource=" + dataSource + ", thing=" + self);
            }            
        }else{
            Bindings bindings = (Bindings) self.doAction("getVarScope", actionContext);
            if(bindings != null){
                bindings.put(self.getString("propertyName"), value);
            }
        }
    }

}