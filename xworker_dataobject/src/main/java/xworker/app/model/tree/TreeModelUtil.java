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
package xworker.app.model.tree;

import java.util.Map;

import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

public class TreeModelUtil {
	
	public static void copyValues(Map<String, Object> treeNode, Object data) {
		copyValue(treeNode, data, "text");
		copyValue(treeNode, data, "icon");
		copyValue(treeNode, data, "disabled");
		copyValue(treeNode, data, "expanded");
		copyValue(treeNode, data, "qtip");
		copyValue(treeNode, data, "tabId");
		copyValue(treeNode, data, "dataId");
		copyValue(treeNode, data, "url");
		copyValue(treeNode, data, "id");
	}
	
	public static void copyValue(Map<String, Object> treeNode, Object data, String attributeName) {
		try {
			Object value = OgnlUtil.getValue(attributeName, data);
			treeNode.put(attributeName, value);
		}catch(Exception e) {			
		}
	}
	public static void setAttributes(Thing treeModel, Object id, Map<String, Object> data){
		//是否添加标识前缀		
		String modelId= treeModel.getString("modelId");
		String multiSubModelSeparator = treeModel.getString("multiSubModelSeparator");
				
		String theId = String.valueOf(id);
		if(modelId != null && !"".equals(modelId)){
			theId = modelId + multiSubModelSeparator + theId;
		}
		data.put("id", theId);
		data.put("dataId", id);
		
		//是否添加tabId和url参数
		if(treeModel.getBoolean("addTabUrl")){
			if(!data.containsKey("tabId")){
				data.put("tabId", treeModel.get("tabId"));
			}
			if(!data.containsKey("url")){
				data.put("url", treeModel.get("url"));
			}
		}
	}
	
	public static void copyAttributesToNodeData(Object data, Map<String, Object> node){
		Thing nodeThing = World.getInstance().getThing("xworker.app.model.tree.TreeNode");
		for(Thing attr : nodeThing.getChilds("attribute")){
			try{
				String name = attr.getMetadata().getName(); 
				node.put(name, OgnlUtil.getValue(name, data));
			}catch(Exception e){				
			}
		}
	}
	
}