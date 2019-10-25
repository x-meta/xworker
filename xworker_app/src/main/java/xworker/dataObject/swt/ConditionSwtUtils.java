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
package xworker.dataObject.swt;

import java.text.ParseException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.swt.util.SwtUtils;

public class ConditionSwtUtils {
	private static Logger log = LoggerFactory.getLogger(ConditionSwtUtils.class);
	
	 @SuppressWarnings("unchecked")
	public static void initParamsSwt(ActionContext actionContext) throws ParseException{
	        Thing self = (Thing) actionContext.get("self");
	        
	        String dataName = self.getString("dataName");
	        if(dataName == null || "".equals(dataName)){
	            dataName = self.getString("attributeName");
	        }
	        
	        Map<String, Object> params = (Map<String, Object>) actionContext.get("params");
	        
	        if(actionContext.get("params") == null || params.get(dataName) != null){
	            return;
	        }
	            
	        String valueWidget = self.getString("valueWidget");
	        if(valueWidget != null && !"".equals(valueWidget)){
	            String[] vss = valueWidget.split("[:]");
	            
	            if("store".equals(vss[0])){
	                if(vss.length == 3){
	                    Thing store = (Thing) actionContext.get(vss[1]);            
	                    if(store != null && store.get("currentRecord") != null){
	                        params.put(dataName, ((DataObject) store.get("currentRecord")).get(vss[3]));
	                    }else{
	                        log.info("Store or store's currentRecord is not exists, valueWidget=" + valueWidget);  
	                    }
	                }else{
	                    log.info("Condition valueWidget format is not right, valueWidget=" + valueWidget);
	                }
	            }else if(vss[0] == "form"){
	                if(vss.length == 3){
	                     Thing form = (Thing) actionContext.get(vss[1]);
	                     if(form != null){
	                         Map<String, Object> values = (Map<String, Object>) form.doAction("getValues", actionContext);
	                         if(values != null){
	                             params.put(dataName, values.get(vss[3]));
	                         }
	                     }else{
	                         log.info("Form is not exists, valueWidget=" + valueWidget);
	                     }
	                }else{
	                     log.info("Condition valueWidget format is not right, valueWidget=" + valueWidget);
	                }
	            }else if(vss[0] == "com"){
	                if(vss.length == 2){
	                     Object control = actionContext.get(vss[1]);
	                     if(control != null){
	                         if(control instanceof Thing){
	                             Object value = ((Thing) control).doAction("getValue", actionContext);
	                             params.put(dataName, value);
	                         }else{
	                             Object value = SwtUtils.getValue(control, null, null);
	                             params.put(dataName, value);
	                         }
	                     }else{
	                         log.info("Control is not exists, valueWidget=" + valueWidget);
	                     }
	                }else{
	                     log.info("Condition valueWidget format is not right, valueWidget=" + valueWidget);
	                }
	            }
	        }
	    }
}