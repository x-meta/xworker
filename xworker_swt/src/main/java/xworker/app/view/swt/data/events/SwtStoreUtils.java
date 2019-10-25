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
package xworker.app.view.swt.data.events;

import java.text.Format;

import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import xworker.app.view.swt.data.DataStoreManager;
import xworker.app.view.swt.data.DataStoreUtils;
import xworker.dataObject.DataObject;

public class SwtStoreUtils {
	private static Logger log = LoggerFactory.getLogger(SwtStoreUtils.class);
	
	public static void runSync(Thing store, Display display, Runnable runnable){
		display.syncExec(runnable);
	}
	
	public static void runAsync(Thing store, Display display, Runnable runnable){
		display.asyncExec(runnable);
	}
	
	/**
	 * 返回一个列（如Table的列）中数据对象的值对应的要显示的值。
	 * 
	 * @param dataObject
	 * @param column
	 * @return
	 */
	public static String getColumnDisplayText(DataObject record, Thing column, ActionContext actionContext) {
		String text = null;
	    Boolean __tableDataStoreToDisplayString = (Boolean) column.getData("__tableDataStoreToDisplayString");
	    if(__tableDataStoreToDisplayString != null && __tableDataStoreToDisplayString == true){
	        text = (String) column.doAction("toDisplayString", actionContext, UtilMap.toMap(new Object[]{"record", record, "value", record.get(column.getString("name"))}));
	    }else if(column.getData("__tableDataStoreFormater") != null){ 
	    	Format formater = (Format) column.getData("__tableDataStoreFormater");
	        Object value = record.get(column.getString("name"));
	        if(value != null){
	            text = formater.format(value);
	        }
	    }else{    
	        //先从显示字段中取
	        //log.info("sotre=" + column.getAttributes());
	        String disField = column.getStringBlankAsNull("displayField");
	        if(disField != null){
	            //log.info("display=" + disField);
	            String[] diss = disField.split("[.]");
	            Object v = record;
	            try{
	                for(String dis : diss){
	                	if(v instanceof DataObject){
	                		v = ((DataObject) v).get(dis);
	                	}
	                	
	                    //log.info("value=" + v);
	                    if(v == null){
	                        break;
	                    }                        
	                }
	                if(v != null){
	                    text = v.toString();
	                }
	            }catch(Exception e){
	                v = null;
	                log.warn("TableDataStoreListener: get display error," + disField, e);
	            }
	        }
	        
	        //从关联的数据仓库中取数据
	        Thing columnDataStore = null;
	        String relationDataObject = column.getStringBlankAsNull("relationDataObject");
	        String columnStore = column.getStringBlankAsNull("store");
	        if(text == null && columnStore != null){
	            //log.info("relationDataObject=" + column.relationDataObject + ",store=" + column.store );
	        	//从引用的数据仓库取
	            columnDataStore = DataStoreManager.get(columnStore);		            
	        }
	        if(columnDataStore == null && relationDataObject != null) {
	        	//直接通过数据对象创建数据仓库
	        	columnDataStore = DataStoreManager.getByDataObject(relationDataObject);
	        }
	        if(columnDataStore != null){
                text = DataStoreUtils.getLabelByValue(columnDataStore, record.get(column.getString("name")), column.getString("relationValueField"), column.getString("relationLabelField"));
            }
	        
	        //从value属性中去数据
	        String inputType = column.getString("inputtype");
	        if(text == null && UtilData.equalsOne(inputType, new String[]{"select","inputSelect","multSelect", "checkBox", "radio"})){
	        	String value = record.getString(column.getString("name"));	        	
	        	if(value != null){
	        		String[] values = new String[] {value};
	        		if("multSelect".equals(inputType) || "checkBox".equals(inputType)) {
		        		values = value.split("[,]");
		        	}
	        		for(String v : values) {
			        	for(Thing vt : column.getAllChilds("value")){ //要使用AllChilds，因为数据会继承其他属性，value有可能是继承来的
			        		if(v.equals(vt.getString("value"))){
			        			if(text == null) {
			        				text = vt.getMetadata().getLabel();
			        			}else {
			        				text = text + "," + vt.getMetadata().getLabel();
			        			}
			        		}
			        	}
	        		}
	        	}
	        }
	            
	        if(text == null){
	        	Object value = record.get(column.getString("name")); 
	            text = value == null ? "" : value.toString();
	        }
	    }
	    
	    if(text == null){
	        text = "";
	    }else{
	        text = String.valueOf(text);
	    }
	    
	    return text;
	}
}