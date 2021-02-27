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

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.XMetaException;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilThing;

import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;
import xworker.lang.executor.Executor;

public class TransferableDataObject {
	private static final String TAG = TransferableDataObject.class.getName();
	
	/**
	 * 返回源和目标数据对象。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static SourceTargetDataObject getSourceAndTargetDataObject(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String key = "__TransferSourceTarget__cache__";
		SourceTargetDataObject st = (SourceTargetDataObject) self.getData(key);
		if(st == null || st.lastmodified != self.getMetadata().getLastModified()){
			if(st == null){
				st = new SourceTargetDataObject();
			}
			
			st.source = UtilThing.getThingFromAttributeOrChilds(self, "sourceDataObject", "SourceDataObject@0"); 
			st.target = UtilThing.getThingFromAttributeOrChilds(self, "targetDataObject", "TargetDataObject@0"); 
			if(st.target == null){
				st.target = self;
			}
			st.lastmodified = self.getMetadata().getLastModified();
			
			self.setData(key, st);			
		}
		

		if(st.source == null || st.target == null){
			Executor.info(TAG, "TransferableDataObject, source or target is null, path=" + self.getMetadata().getPath() + ", source=" + st.source + ",target=" + st.target);
			return null;
		}else{
			return st;
		}
	}
	
	public static Object load(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");
		SourceTargetDataObject st = getSourceAndTargetDataObject(actionContext);
		if(st != null){
			//先转换数据到源数据
			Object theData = self.doAction("toSourceData", actionContext);
			
			//源数据执行装载
			theData = st.source.doAction("load", actionContext, UtilMap.toMap(new Object[]{"theData", theData}));
			
			//再转换成目标数据
			return self.doAction("toTargetData", actionContext, UtilMap.toMap(new Object[]{"theData", theData}));
		}else{
			throw new XMetaException("SourceDataObject is null, path=" + self);
		}
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");
		SourceTargetDataObject st = getSourceAndTargetDataObject(actionContext);
		if(st != null){
			//先转换数据到源数据
			Object theData = self.doAction("toSourceData", actionContext);
			
			//源数据执行装载
			theData = st.source.doAction("create", actionContext, UtilMap.toMap(new Object[]{"theData", theData}));
			
			//再转换成目标数据
			return self.doAction("toTargetData", actionContext, UtilMap.toMap(new Object[]{"theData", theData}));
		}else{
			throw new XMetaException("SourceDataObject is null, path=" + self);
		}
	}
	
	public static Object update(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		SourceTargetDataObject st = getSourceAndTargetDataObject(actionContext);
		if(st != null){
			//先转换数据到源数据
			Object theData = self.doAction("toSourceData", actionContext);
			
			//源数据执行装载
			Object result = st.source.doAction("update", actionContext, UtilMap.toMap(new Object[]{"theData", theData}));
			
			return result;
		}else{
			throw new XMetaException("SourceDataObject is null, path=" + self);
		}
	}
	
	public static Object updateBatch(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		SourceTargetDataObject st = getSourceAndTargetDataObject(actionContext);
		if(st != null){
			//先转换数据到源数据
			Object theData = self.doAction("toSourceData", actionContext);
			
			//源数据执行装载
			return st.source.doAction("updateBatch", actionContext, UtilMap.toMap(new Object[]{"theData", theData}));
		}else{
			throw new XMetaException("SourceDataObject is null, path=" + self);
		}
	}
	
	public static Object delete(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		SourceTargetDataObject st = getSourceAndTargetDataObject(actionContext);
		if(st != null){
			//先转换数据到源数据
			Object theData = self.doAction("toSourceData", actionContext);
			
			//源数据执行装载
			return st.source.doAction("delete", actionContext, UtilMap.toMap(new Object[]{"theData", theData}));
		}else{
			throw new XMetaException("SourceDataObject is null, path=" + self);
		}
	}
	
	public static Object deleteBatch(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		SourceTargetDataObject st = getSourceAndTargetDataObject(actionContext);
		if(st != null){
			//源数据执行装载
			return st.source.doAction("deleteBatch", actionContext);
		}else{
			throw new XMetaException("SourceDataObject is null, path=" + self);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Object query(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		SourceTargetDataObject st = getSourceAndTargetDataObject(actionContext);
		if(st != null){
			//源数据执行装载
			Object datas = st.source.doAction("deleteBatch", actionContext);
			
			//再转换成目标数据
			List<DataObject> targDatas = (List<DataObject>) self.doAction("toTargetDatas", actionContext, UtilMap.toMap(new Object[]{"datas", datas}));
			
			//如果有pageInfo，同时要修改pageInfo
			Object pageInfo = actionContext.get("pageInfo");
			if(pageInfo != null){
				PageInfo pinfo = PageInfo.getPageInfo(pageInfo);
				pinfo.setDatas(targDatas);
			}
			
			return targDatas;
		}else{
			throw new XMetaException("SourceDataObject is null, path=" + self);
		}
	}
	
	public static Object toSourceData(ActionContext actionContext){
		throw new XMetaException("You should implement toSourceData method in your dataobject");
	}
	
	public static Object toTargetData(ActionContext actionContext){
		throw new XMetaException("You should implement toTargetData method in your dataobject");
	}
	
	public static Object toTargetDatas(ActionContext actionContext){
		throw new XMetaException("You should implement toTargetDatas method in your dataobject");
	}
	
	public static class SourceTargetDataObject{
		public Thing source;
		public Thing target;
		
		long lastmodified = 0;
	}
}