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

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilAction;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

public class ValueFactoryCreator {
    public static Object run(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        World world = World.getInstance();
        
        Object data = null;
        String value = self.getString("value");
        String type = self.getString("type");
        
        //根据类型创建    
        if("string".equals(type)){
            data = value;
        }else if("byte".equals(type)){
            data = Byte.parseByte(value);
        }else if("short".equals(type)){
            data = Short.parseShort(value);
        }else if("int".equals(type)){
            data = Integer.parseInt(value);
        }else if("long".equals(type)){
            data = Long.parseLong(value);
        }else if("float".equals(type)){
            data = Float.parseFloat(value);
        }else if("double".equals(type)){
            data = Double.parseDouble(value);
        }else if("boolean".equals(type)){
            if(value instanceof String && ("false".equals(value.toLowerCase()) || "0".equals(value.toLowerCase()))){
                data = false;
            }else if(value != null){
                data = true;
            }else{
                data = false;
            }
        }else if("jason".equals(type)){
            Thing jasonFormat = world.getThing("xworker.lang.text.JsonDataFormat");
            data = jasonFormat.doAction("parse", actionContext, UtilMap.toParams(new Object[]{"jsonText", value}));
    	}else if("now".equals(type)){
            data = new Date();
    	}else if("hexToByte[]".equals(type)){
            data = UtilString.hexStringToByteArray(value);
    	}else if("null".equals(type)){
            return null;
        }else if("ognl".equals(type)){
			data = OgnlUtil.getValue(self, "value", value, actionContext);
        }
        
        UtilAction.putVarByActioScope(self, self.getString("varName"), data, actionContext);
        
        return data;
    }

}