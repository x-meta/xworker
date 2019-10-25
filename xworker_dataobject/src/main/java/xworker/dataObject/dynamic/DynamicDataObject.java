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
package xworker.dataObject.dynamic;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.XMetaException;

import xworker.dataObject.PageInfo;

public class DynamicDataObject {
	public static Object doLoad(ActionContext actionContext){
		return doAction(actionContext, "doLoad");
	}
	
	public static Object doCreate(ActionContext actionContext){
		return doAction(actionContext, "doCreate");
	}
	
	public static Object doUpdate(ActionContext actionContext){
		return doAction(actionContext, "doUpdate");
	}
	
	public static Object updateBatch(ActionContext actionContext){
		return doAction(actionContext, "updateBatch");
	}
	
	public static Object doDelete(ActionContext actionContext){
		return doAction(actionContext, "doDelete");
	}
	
	public static Object deleteBatch(ActionContext actionContext){
		return doAction(actionContext, "deleteBatch");
	}
	
	public static Object doQuery(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Thing dataObject = (Thing) self.doAction("getDataObject", actionContext);
		if(dataObject == null){
			throw new XMetaException("DynamicDataObject dataObject from getDataObject is null");
		}
		
		Object result = dataObject.doAction("doQuery", actionContext);
		PageInfo pageInfo = PageInfo.getPageInfo(actionContext);
		if(pageInfo != null){
			pageInfo.setDataObject(dataObject);
			pageInfo.put("sourceDataObject", self);
		}
		
		return result;
	}
	
	public static Object doAction(ActionContext actionContext, String actionName){
		Thing self = (Thing) actionContext.get("self");
		
		Thing dataObject = (Thing) self.doAction("getDataObject", actionContext);
		if(dataObject == null){
			throw new XMetaException("DynamicDataObject dataObject from getDataObject is null");
		}
		
		return dataObject.doAction(actionName, actionContext);
	}

}