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
package xworker.app.model.tree.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.XMetaException;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelUtils;
import xworker.dataObject.DataObject;

public class ThingRegistTreeModelActions {
	private static final String rootId = "__TreeNodeRootId__";
	private static final String dataId = "__thing_regist_tree_model_data_id__";
	
	/**
	 * 获取根节点，每次根节点时刷新数据。
	 * 
	 * @param actionContext
	 * @return
	 * @throws IOException
	 * @throws OgnlException 
	 */
	public static Object getRoot(ActionContext actionContext) throws IOException, OgnlException{
		long start = System.currentTimeMillis();
    	Thing self = (Thing) actionContext.get("self");
    	Map<String, Group> groupMap = getAllChilds(self, getThingPath(self, actionContext), getRegistType(self, actionContext), self.getStringBlankAsNull("childThingName"), actionContext);
    	self.setData(dataId, groupMap);
    	
    	return groupMap.get(rootId).toTreeNode(self);
    }
	
	/**
	 * 获取子节点列表。
	 * 
	 * @param actionContext
	 * @return
	 * @throws IOException
	 * @throws OgnlException 
	 */
	public static Object getChilds(ActionContext actionContext) throws IOException, OgnlException{
    	Thing self = (Thing) actionContext.get("self");
    	
    	String id = (String) actionContext.get("id");
        Map<String, Group> groupMap = null;//(Map<String, Group>) self.getData(dataId);
        if(groupMap == null){
        	groupMap= getAllChilds(self, getThingPath(self, actionContext), getRegistType(self, actionContext), self.getStringBlankAsNull("childThingName"), actionContext);
        	//self.setData(dataId, groupMap);
        }
        
        Group node = groupMap.get(id);
        if(node == null){
        	node = groupMap.get(rootId);
        }
        if(node != null){
        	return node.toTreeNode(self).get("childs");
        }else{
        	//return getRoot(actionContext);
        	throw new XMetaException("ThingRegistTreeModel: id has none node, thing=" + self); 
        }    	
    }
	
	public static String getThingPath(Thing self, ActionContext actionContext) throws OgnlException{
		String id = (String) actionContext.get("id");
		String thingPath = id;
		if(TreeModel.ROOT_ID.equals(thingPath)){
			thingPath = null;
		}
		if(thingPath == null  || "".equals(thingPath) ){
			thingPath = self.getStringBlankAsNull("thingPath");
		}
		if(thingPath == null){
			String thingPathVarName = self.getStringBlankAsNull("thingPathVarName");
			if(thingPathVarName != null){
				thingPath = (String) OgnlUtil.getValue(self, "thingPathVarName", thingPathVarName, actionContext);
			}
		}
		
		return thingPath;
	}
	
	public static String getRegistType(Thing self, ActionContext actionContext) throws OgnlException{
		String registType = self.getStringBlankAsNull("registType");
		if(registType == null){
			String registTypeVarName = self.getStringBlankAsNull("registTypeVarName");
			if(registTypeVarName != null){
				registType = (String) OgnlUtil.getValue(self, "registTypeVarName", registTypeVarName, actionContext);
			}
		}
		
		return registType;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Group> getAllChilds(String thing, String registType, String childThingName, ActionContext actionContext){
		List<Thing> childs = new ArrayList<Thing>();
		World world = World.getInstance();
		
		//事物本身的子节点
		Thing parentThing = world.getThing(thing);
		if(parentThing != null){
			List<Thing> pchilds = null;
			if(childThingName != null){
				pchilds = parentThing.getAllChilds(childThingName);
			}else{
				pchilds = parentThing.getChilds();
			}
			for(Thing child : pchilds){
				childs.add(child);
			}
		}
		
		//查询注册的子事物
		Thing registThing = world.getThing("xworker.ide.db.dbindex.app.dataObject.RegistsByThing");
		List<DataObject> rchilds = (List<DataObject>) registThing.doAction(
				"query",
				actionContext,
				UtilMap.toMap(new Object[] { "thing", parentThing,
						"noDescriptor", true, "registType", registType }));
		for (DataObject rc : rchilds) {
			Thing child = world.getThing((String) rc.get("path"));
			if (child != null) {
				if(child.getBoolean("registMyChilds")){
					for(Thing cc : child.getChilds()){
						childs.add(cc);
					}
				}else{
					childs.add(child);
				}
			}
		}
		
		//分组
		Map<String, Group> groupMap = new HashMap<String, Group>();
		
		//根节点，只有一个
		Group root = new Group();
		root.path = ThingRegistTreeModelActions.rootId;
		root.name = "root";
		groupMap.put(root.path, root);
		
		for(Thing child : childs){
			String groupStr = child.getString("group");
			if(groupStr == null || "".equals(groupStr)){				
				Group group = groupMap.get(ThingRegistTreeModelActions.rootId);
				group.addThing(groupMap, child, groupStr);
			}else{
				for(String gstr : groupStr.split("[,]")){
					addPathToGroup(groupMap, gstr, child);
				}
			}
		}
		
		//排序
		for(String key : groupMap.keySet()){
			Group group = groupMap.get(key);
			Collections.sort(group.childs, new Comparator<Group>(){
				public int compare(Group o1, Group o2) {
					//目录排在最前面
					if(o1.thing == null && o2.thing != null){
						return -1;
					}else if(o1.thing != null && o2.thing == null){
						return 1;
					}
					
					return o1.name.compareTo(o2.name);
				}
				
			});
		}
				
		return groupMap;
	}
	
	public static Map<String, Group> getAllChilds(Thing treeModel, String thing, String registType, String childThingName, ActionContext actionContext) {
		ThingRegistCache cache = ThingRegistCacheManager.getCache(thing, registType, childThingName);
		if(cache != null){
			return cache.getDatas();
		}
		
		if(thing == null){
			throw new ActionException("thing is null");
		}
		Map<String, Group> groupMap = getAllChilds(thing, registType, childThingName, actionContext);
		ThingRegistCacheManager.putCache(treeModel,thing, registType, childThingName, groupMap);
		return groupMap;
	}
	
	private static void addPathToGroup(Map<String, Group> groupMap, String path, Thing thing){
		int index = path.indexOf(".");
		Group root = groupMap.get(ThingRegistTreeModelActions.rootId);
		if(index == -1){				
			if(path != null && !"".equals(path)){
				Group r = groupMap.get(path);
				if(r == null){
					root = root.addString(groupMap, path);
				}else{
					root = r;
				}
			}
			root.addThing(groupMap, thing, path);			
		}else{
			Group parent = root;
			String str = null;
			for(String name : path.split("[.]")){
				if(str == null){
					str = name;
				}else{
					str = str + "." + name;
				}
				
				Group parent_  = groupMap.get(str);
				if(parent_ != null){
					parent = parent_;
				}else{
					parent = parent.addString(groupMap, str);
				}
			}
			
			parent.addThing(groupMap, thing, path);
		}
	}
	
	public static class Group implements java.util.Comparator<Group>{
		String name;
		String path;
		Thing thing;
				
		List<Group> childs = new ArrayList<Group>();
		
		Map<String, Object> node = null;		
		
		public Map<String, Object> toTreeNode(Thing treeModel){
			if(node != null){
				return node;
			}
			node = new HashMap<String, Object>();
			node.put(TreeModel.Source, treeModel);
			TreeModelUtils.setAttributes(treeModel, path, node);
			node.put("text", name);
			if(thing == null){
				node.put("icon", "icons/folder.png");
				node.put("leaf", "false");
				node.put("tabId", "folder");
			}else{
				String icon = null;
				for(Thing descriptor : thing.getAllDescriptors()){
				    //println descriptor.getMetadata().getPath();
				    icon = descriptor.getString("icon");
				    //println icon;
				    if(icon != null && icon != ""){
				        break;
				    }
				}
				if(icon == null || "".equals(icon)){
					icon = "icons/page_white.png";
				}
				node.put("icon", icon);
				if(thing.isThingByName("ThingIndex")){
					node.put("tabId", "thingIndex");
				}else{
					node.put("tabId", "thing");
				}
				
				if(childs == null || childs.size() == 0){
					node.put("leaf", true);
				}
			}
			
			List<Map<String, Object>> childNodes = new ArrayList<Map<String, Object>>();
			for(Group child : childs){
				childNodes.add(child.toTreeNode(treeModel));
			}
			node.put("childs", childNodes);
			
			return node;
		}
		
		public Group addString(Map<String, Group> groupMap, String path){
			Group child = new Group();
			child.path = path;
			int index = path.lastIndexOf(".");
			if(index == -1){
				child.name = path;
			}else{
				child.name = path.substring(index + 1, path.length());
			}
			groupMap.put(child.path, child);
			childs.add(child);
			return child;
		}
		
		public Group addThing(Map<String, Group> groupMap, Thing thing, String path){
			Group child = new Group();
			child.name = thing.getMetadata().getLabel();
			child.path = "thing:" + thing.getMetadata().getPath();
			child.thing = thing;
			childs.add(child);
			groupMap.put(child.path, child);
			return child;
		}
		
		public int compare(Group o1, Group o2) {		
			return o1.name.compareTo(o2.name);
		}
	}
}