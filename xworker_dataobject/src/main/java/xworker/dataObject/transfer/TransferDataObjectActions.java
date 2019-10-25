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
package xworker.dataObject.transfer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.XMetaException;
import org.xmeta.util.UtilAction;
import org.xmeta.util.UtilData;

import xworker.dataObject.DataObject;

public class TransferDataObjectActions {
	private static Logger logger = LoggerFactory.getLogger(TransferDataObjectActions.class);
	
	/**
	 * 把源数据对象转换为目标数据对象。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static DataObject transferDataObject(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		TransferableDataObject.SourceTargetDataObject st = TransferableDataObject.getSourceAndTargetDataObject(actionContext);
		if(st == null){
			throw new XMetaException("Source or target dataObject not configed, thing=" + self);
		}
		
		//源数据对象
		DataObject sourceObj = (DataObject) actionContext.get(self.getString("sourceVarName"));
		if(sourceObj == null){
			logger.info("source dataobject is null, thing=" + self);
			return null;
		}
		
		//目标数据对象
		DataObject targetObj = new DataObject(st.target);
		Thing transfers = self.getThing("AttributeTransfers@0");
		if(transfers == null){
			logger.info("AttributeTransfers is null, thing=" + self);
		}
				
		try{
			Bindings bindings = actionContext.push();
			bindings.put("sourceData", sourceObj);
			bindings.put("targetData", targetObj);
			for(Thing transfer : transfers.getAllChilds()){
				transfer.doAction("run", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		UtilAction.putVarByActioScope(self, self.getString("targetVarName"), targetObj, actionContext);
		return targetObj;
	}
	
	/**
	 * 转换数据对象列表。
	 * 
	 * @param actionContext
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<DataObject> transferDataObjectList(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		TransferableDataObject.SourceTargetDataObject st = TransferableDataObject.getSourceAndTargetDataObject(actionContext);
		if(st == null){
			throw new XMetaException("Source or target dataObject not configed, thing=" + self);
		}
		
		//源数据对象
		List<DataObject> sourceObjList = (List<DataObject>) actionContext.get(self.getString("sourceVarName"));
		if(sourceObjList == null){
			logger.info("source dataobject is null, thing=" + self);
			return Collections.emptyList();
		}
		
		//目标数据对象
		List<DataObject> targetObjList = new ArrayList<DataObject>();
		Thing transfers = self.getThing("AttributeTransfers@0");
		if(transfers == null){
			logger.info("AttributeTransfers is null, thing=" + self);
		}
				
		try{
			Bindings bindings = actionContext.push();
			for(DataObject sourceObj : sourceObjList){
				bindings.put("sourceData", sourceObj);
				DataObject targetObj = new DataObject(st.target);
				bindings.put("targetData", targetObj);
				for(Thing transfer : transfers.getAllChilds()){
					transfer.doAction("run", actionContext);
				}
				targetObjList.add(targetObj);
			}
		}finally{
			actionContext.pop();
		}
		
		//保存变量
		UtilAction.putVarByActioScope(self, self.getString("targetVarName"), targetObjList, actionContext);
		
		return targetObjList;
	}
	
	public static void transferAttribute(ActionContext actionContext) throws ParseException{
		Thing self = (Thing) actionContext.get("self");
		DataObject sourceData = (DataObject) actionContext.get("sourceData");
		DataObject targetData = (DataObject) actionContext.get("targetData");
		
		String srcName = self.getString("sourceAttributeName");
		String tagName = self.getString("targetAttributeName");
		String targetType = self.getString("targetType");
		String patternType = self.getString("patternType");
		String patternAction = self.getString("patternAction");
		String pattern = self.getString("pattern");
		
		Object tagObj = UtilData.transfer(sourceData.get(srcName), targetType, pattern, patternType, patternAction);
		targetData.put(tagName, tagObj);
	} 
}