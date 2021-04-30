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
package xworker.dataObject.store;

import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

/**
 * DataStore可以通过条件查询数据对象、缓存数据对象的记录，是界面和数据对象之间的一个桥梁。
 * 
 * @author Administrator
 * @deprecated 使用xworker.dataObject.DataStore
 */
public class DataStore {
	/** 数据对象 */
	Thing dataObject;
	
	/** 查询条件 */
	Thing condition;
	
	/** 记录 */
	List<DataObject> records;
	
	public DataStore(Thing dataObject, Thing condition){
		this.dataObject = dataObject;
		this.condition = condition;
	}
	
	@SuppressWarnings("unchecked")
	public void load(ActionContext actionContext){
        DataObject.beginThreadCache();
        try{
        	records = (List<DataObject>) dataObject.doAction("query", actionContext);
        }finally{
        	DataObject.finishThreadCache();
        }
	}
	
	public void load(Map<String, Object> params, ActionContext actionContext){
		try{
			Bindings bindings = actionContext.push();
			if(params != null){
				bindings.putAll(params);
			}
			
			load(actionContext);
		}finally{
			actionContext.pop();
		}
	}
}