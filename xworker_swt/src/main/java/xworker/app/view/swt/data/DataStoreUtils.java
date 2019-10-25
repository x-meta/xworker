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
package xworker.app.view.swt.data;

import java.util.List;

import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class DataStoreUtils {
	/**
	 * 返回一个记录的标签值。
	 * 
	 * @param dataStore
	 * @param record
	 * @return
	 */
	public static String getLabel(Thing dataStore, DataObject record){
		 //首先取DataStore定义的标签字段
		 String labelField = getLabelField(dataStore);

		 String text = "no label field";
         if(labelField != null && !"".equals(labelField)){
        	 Object value = record.get(labelField);
             text = value == null ? "" : String.valueOf(value);             
         }
         
         return text; 
	}
	
	/**
	 * 通过数据仓库区标签字段，先从数据仓库的定义取，其次通过数据对象的displayName属性取。
	 * 
	 * @param dataStore
	 * @return
	 */
	public static String getLabelField(Thing dataStore){
		Thing config = (Thing) dataStore.get("config");
		Thing dataObject = (Thing) dataStore.get("dataObject");
		 
		//首先取DataStore定义的标签字段
		String labelField = config.getString("labelField");
		 
		//其次取数据对象上定义的标签字段
        if((labelField == null || "".equals(labelField)) && dataObject != null){
            labelField = dataObject.getString("displayName");
        }
        
        return labelField;
	}
	
	/**
	 * 通过数据仓库取数据的标识字段，只返回第一个数据对象关键字字段，如果数据仓库没有定义数据对象或数据对象没有关键字，
	 * 返回null。
	 * 
	 * @param dataStore
	 * @return
	 */
	public static String getIdField(Thing dataStore){
		Thing dataObject = (Thing) dataStore.get("dataObject");
		if(dataObject != null){
			for(Thing attr : dataObject.getChilds("attribute")){
				if(attr.getBoolean("key")){
					return attr.getString("name");
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 通过值从DataStore上获取要显示的标签。
	 * 
	 * @param dataStore
	 * @param value
	 * @return
	 */
	public static String getLabelByValue(Thing dataStore, Object value, String valueField, String labelField){
		List<DataObject> records = getRecords(dataStore);
		if(records == null){
			return null;
		}
		
		if(valueField == null || "".equals(valueField)){
			return null;
		}
		
		if(labelField == null || "".equals(labelField)){
			return null;
		}
		
		String strValue = String.valueOf(value);
		for(DataObject record : records){
			Object dvalue = record.get(valueField);			
			if(dvalue != null && (dvalue.equals(value) || String.valueOf(dvalue).equals(strValue))){
				Object labelValue = record.get(labelField);
	             return labelValue == null ? null : String.valueOf(labelValue);  
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static List<DataObject> getRecords(Thing dataStore){
		return (List<DataObject>) dataStore.get(DataStoreConstants.RECORDS);
	}
}