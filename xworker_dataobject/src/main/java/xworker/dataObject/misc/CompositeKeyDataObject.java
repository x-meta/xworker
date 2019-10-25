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
package xworker.dataObject.misc;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.XMetaException;

import xworker.dataObject.DataObject;
import xworker.dataObject.transfer.TransferableDataObject;

public class CompositeKeyDataObject {
	public static Object toSourceData(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");
		
		TransferableDataObject.SourceTargetDataObject st = TransferableDataObject.getSourceAndTargetDataObject(actionContext);
		if(st == null){
			throw new XMetaException("Source dataObject thing is null, path=" + self);
		}
		
		DataObject targetDataObject = (DataObject) actionContext.get("theData");
		if(targetDataObject == null){
			throw new XMetaException("Cann't convert to source dataOjbect, theData is null, path=" + self);
		}
		
		//源数据对象
		DataObject sourceDataObject = new DataObject(st.source);
		
		//拷贝属性
		sourceDataObject.putAll(targetDataObject);
		
		//设置关键字
		Object keyValue = targetDataObject.get(self.getString("idAttributeName"));
		
		if(keyValue != null){
			String key = String.valueOf(keyValue);
			String splitChar = self.getString("idSplitChar");
			if(splitChar == null || "".equals(splitChar)){
				splitChar = "-";
			}
			String keys[] = key.split("[" + splitChar + "]");
			String sourceIdAttributes = self.getString("sourceIdAttributes");
			if(sourceIdAttributes != null && !"".equals(sourceIdAttributes)){
				String sids[] = sourceIdAttributes.split("[,]");
				if(sids.length > keys.length){
					throw new XMetaException("Need more keys, keys=" + keys + ",sourceIds=" + sids + ",path=" + self);
				}
				for(int i=0; i<sids.length; i++){
					sourceDataObject.put(sids[i], keys[i]);
				}
			}
		}
		
		return sourceDataObject;
	}
	
	public static Object toTargetData(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		DataObject sourceDataObject = (DataObject) actionContext.get("theData");
		return sourceToTarget(self, sourceDataObject);
	}
	
	@SuppressWarnings("unchecked")
	public static Object toTargetDatas(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<DataObject> datas = ((List<DataObject>) actionContext.get("datas"));
		List<DataObject> targets = new ArrayList<DataObject>();
		for(DataObject sourceDataObject : datas){
			targets.add(sourceToTarget(self, sourceDataObject));
		}
		
		return targets;
	}
	
	public static DataObject sourceToTarget(Thing self, DataObject sourceDataObject){
		DataObject target = new DataObject(self);
		for(Thing attr : target.getMetadata().getAttributes()){
			String name = attr.getString("name");
			target.put(name, sourceDataObject.get(name));
		}
		target.put(self.getString("idAttributeName"), getTargetKey(self, sourceDataObject));
		return target;
	}
	
	public static String getTargetKey(Thing self, DataObject sourceDataObject){
		String key = "";
		String splitChar = self.getString("idSplitChar");
		if(splitChar == null || "".equals(splitChar)){
			splitChar = "-";
		}
		
		String sourceIdAttributes = self.getString("sourceIdAttributes");
		if(sourceIdAttributes != null && !"".equals(sourceIdAttributes)){
			String sids[] = sourceIdAttributes.split("[,]");
			for(String sid : sids){
				if("".equals(key)){
					key = sid;
				}else{
					key = key + splitChar + String.valueOf(sourceDataObject.get(sid));
				}
			}			
		}
		
		return key;
	}
}