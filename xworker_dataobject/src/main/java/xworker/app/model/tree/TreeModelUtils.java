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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmeta.*;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilString;
import xworker.dataObject.DataObject;
import xworker.java.assist.JavaCacheItem;
import xworker.lang.executor.Executor;
import xworker.util.ThingGroup;
import xworker.util.UtilFileIcon;
import xworker.util.XWorkerUtils;

public class TreeModelUtils {
	private static final String TAG = TreeModelUtils.class.getName();

	public static TreeModelItem toItem(TreeModel treeModel, TreeModelItem parentItem, ThingManager thingManager) {
		TreeModelItem item = new TreeModelItem(treeModel, parentItem);
		item.setSource(thingManager);
		String id = treeModel.getThing().getMetadata().getPath() + "|";

		item.setTabId(thingManager.getName());
		item.setIcon("/icons/xworker/project1.gif");
		item.setId(id + "thingManager:" + thingManager.getName());
		item.setDataId("thingManager:" + thingManager.getName());

		return item;
	}

	public static TreeModelItem toItem(TreeModel treeModel, TreeModelItem parentItem, Index index){
		TreeModelItem item = new TreeModelItem(treeModel, parentItem);
		item.setSource(index);
		String id = treeModel.getThing().getMetadata().getPath() + "|";

		item.setText(index.getLabel());
		String indexId = Index.getIndexId(index);
		item.setId(id + indexId);
		item.setDataId(indexId);

		String type = index.getType();
		if (Index.TYPE_WORLD.equals(type)) {
			item.setIcon("/icons/xworker/project.gif");
		} else if ("thingManager".equals(type)) {
			item.setIcon("/icons/xworker/project1.gif");
		} else if ("category".equals(type)) {
			item.setIcon("/icons/xworker/package.gif");
		} else if ("thing".equals(type)) {
			if (index.getIndexObject() instanceof ThingIndex) {
				item.setIcon("/icons/xworker/dataObject.gif");
			} else {
				item.setIcon("xworker:core/images/swteditor/dataObjectChild.gif");
			}
		} else if (Index.TYPE_WORKING_SET.equals(type)) {
			item.setIcon("/icons/folder.gif");
		}else{
			item.setIcon("icons/page.png");
		}
		return item;
	}

	public static TreeModelItem toItem(TreeModel treeModel, TreeModelItem parentItem, Thing thing){
		TreeModelItem item = new TreeModelItem(treeModel, parentItem);
		item.setSource(thing);
		item.setText(thing.getMetadata().getLabel());
		item.setIcon(XWorkerUtils.getThingIcon(thing));
		item.setDataId(thing.getMetadata().getPath());
		item.setId(treeModel.getThing().getMetadata().getPath() + "|" + thing.getMetadata().getPath());

		return item;
	}

	public static TreeModelItem toItem(TreeModel treeModel, TreeModelItem parentItem, ThingGroup thingGroup){
		TreeModelItem item = new TreeModelItem(treeModel, parentItem);
		String id = treeModel.getThing().getMetadata().getPath() + "|";
		if(thingGroup.getThing() == null){
			//是目录
			item.setSource(thingGroup);
			item.setText(thingGroup.getGroup());
			item.setId(id + thingGroup.getGroup());
			item.setDataId(thingGroup.getGroup());
			item.setIcon("icons/folder.png");
		}else{
			Thing thing = thingGroup.getThing();
			item.setSource(thing);
			item.setText(thing.getMetadata().getLabel());
			item.setIcon(XWorkerUtils.getThingIcon(thing));
			item.setDataId(thing.getMetadata().getPath());
			item.setId(id + thing.getMetadata().getPath());
		}

		List<TreeModelItem> items = new ArrayList<>();
		for(ThingGroup childGroup : thingGroup.getChilds()){
			items.add(toItem(treeModel, item, childGroup));
		}

		item.setItems(items);

		return item;
	}

	public static TreeModelItem toItem(TreeModel treeModel, TreeModelItem parentItem, Method method){
		TreeModelItem item = new TreeModelItem(treeModel, parentItem);
		item.setSource(method);
		String id = item.getTreeModel().getThing().getMetadata().getPath() + "|";
		StringBuilder sb = new StringBuilder(method.getName());
		sb.append("(");
		Parameter[] params = method.getParameters();
		for(int i=0; i<params.length; i++){
			sb.append(params[i].getType().getSimpleName());
			if(i < params.length - 1){
				sb.append(", ");
			}
		}
		sb.append("): ");
		sb.append(method.getReturnType().getSimpleName());
		item.setText(sb.toString());
		String path = method.getDeclaringClass().getName() + "@" + method.getName();
		item.setId(id + path);
		item.setDataId(path);
		item.setLeaf(true);
		item.setIcon("icons/bullet_green.png");
		return item;
	}

	public static TreeModelItem toItem(TreeModel treeModel, TreeModelItem parentItem, Field field){
		TreeModelItem item = new TreeModelItem(treeModel, parentItem);
		item.setSource(field);
		String id = item.getTreeModel().getThing().getMetadata().getPath() + "|";
		item.setText(field.getName() + ": " + field.getDeclaringClass().getSimpleName());
		item.setIcon("icons/text_list_bullets.png");
		item.setLeaf(true);
		String path = field.getDeclaringClass().getName() + "#" + field.getName();
		item.setId(id + path);
		item.setDataId(path);

		return item;
	}

	public static TreeModelItem toItem(TreeModel treeModel, TreeModelItem parentItem, Thing self, JavaCacheItem cacheItem){
		TreeModelItem item = new TreeModelItem(treeModel, parentItem);
		item.setSource(cacheItem);
		String id = item.getTreeModel().getThing().getMetadata().getPath() + "|";
		item.setText(cacheItem.getSimpleName());
		if (cacheItem.isPackage()) {
			item.setIcon("icons/xworker/package.gif");
		} else {
			item.setIcon("icons/xworker/normalFile.gif");
		}
		item.setId(id + cacheItem.getName());
		item.setDataId(cacheItem.getName());
		return item;
	}

	public static TreeModelItem toItem(TreeModel treeModel, TreeModelItem parentItem, Thing self, File file,  ActionContext actionContext) throws IOException {
		TreeModelItem item = new TreeModelItem(treeModel, parentItem);
		String name = file.getName();
		if(name.equals(":")){
			item.setText(UtilString.getString("lang:d=计算机&en=Computer", actionContext));
			item.setIcon("icons/computer.png");
			item.setId(":");
		}else{
			if(name.trim().isEmpty()){
				item.setText(file.getPath());
			}else {
				item.setText(name);
			}
			if(file.isDirectory()){
				item.setIcon("icons/folder.gif");
			}else {
				item.setIcon(UtilFileIcon.getFileIcon(file, false));
			}
			item.setId(file.getCanonicalPath());
		}

		String path;
		try {
			path = file.getCanonicalPath();
		}catch(Exception e){
			path = file.getPath();
		}
		item.setDataId(path);
		item.setId(item.getTreeModel().getThing().getMetadata().getPath() + "|" + path);
		item.setSource(file);

		return item;
	}

	public static TreeModelItem toItem(TreeModel treeModel, TreeModelItem parentItem, Thing self, Object data){
		String id = getValue(data, self.getString("idName"));
		if(id == null){
			TreeModelItem item = new TreeModelItem(treeModel, parentItem);
			item.setText("null");
			return item;
		}

		TreeModelItem item = new TreeModelItem(treeModel, parentItem);
		item.setText(getValue(data, self.getString("textName")));
		item.setIcon(getValue(data, self.getString("iconName")));
		item.setCls(getValue(data, self.getString("clsName")));
		item.setId(treeModel.getThing().getMetadata().getPath() + "|" + id);
		item.setDataId(id);
		item.setSource(data);

		return item;
	}

	public static TreeModelItem toItem(TreeModel treeModel, TreeModelItem parentItem, Thing self, DataObject data, ActionContext actionContext) {
		TreeModelItem item = new TreeModelItem(treeModel, parentItem);
		item.setSource(data);
		String id = data.getString(self.getString("idField"));
		item.setId(treeModel.getThing().getMetadata().getPath() + "|" + id);
		item.setDataId(id);
		item.setText(data.getString(self.getString("textField")));
		item.setIcon(data.getString(self.getString("iconField")));
		item.setCls(data.getString(self.getString("clsField")));
		item.setLeaf(data.getBoolean(self.getString("isLeafField")));

		self.doAction("init", actionContext, "item", item, "data", data);

		return item;
	}
	
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
		}catch(Exception ignored) {
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

	public static String getValue(Object data, String name){
		try{
			if(name == null || "".equals(name)){
				return null;
			}

			Object v = OgnlUtil.getValue(name, data);
			if(v != null){
				return v.toString();
			}else{
				return null;
			}
		}catch(Exception e){
			Executor.warn(TAG, "DataTreeModel getRoot: getValueError, name=" + name, e);
			return null;
		}
	}
}