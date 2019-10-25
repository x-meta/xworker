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
package xworker.swt.xworker.attributeEditor.openWins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import xworker.swt.util.XWorkerTreeUtil;

public class SelectThingOpenWindowCreator {
	private static Logger logger = LoggerFactory.getLogger(SelectThingOpenWindowCreator.class);
	
	@SuppressWarnings("unchecked")
	public static void query(ActionContext actionContext){
		Thing queryForm = (Thing) actionContext.get("queryForm");
		
		Map<String, Object> params = (Map<String, Object>) queryForm.doAction("getValues", actionContext);
		String path = (String) params.get("thingScopePath");
		if(path != null){
			int lastDot = path.lastIndexOf(".");
			if(lastDot != -1){
				path = path.substring(0, lastDot);
				params.put("thingScopePath", path);
				queryForm.doAction("setValues", actionContext, UtilMap.toMap(new Object[]{"values", params}));
			}
		}
		//log.info("params=" + params);
		((Thing) actionContext.get("dataStore")).doAction("load", actionContext, UtilMap.toMap(new Object[]{"params", params}));
	}
	
	public static void okButtonSelection(ActionContext actionContext) throws Exception{
		Tree dataTree = (Tree) actionContext.get("dataTree");
		Object data = (Object) dataTree.getSelection()[0].getData();	
		if(!(data instanceof Thing)){
			return;
		}
		
		Thing thing = (Thing) data;
		String returnType = (String) actionContext.get("returnType");
		String result = null;
		if("attribute".equals(returnType)){			
			if(thing != null){
				String value = thing.getString((String) actionContext.get("attributeName"));
				if(value == null){
					value = "";
				}
				
				
				result = value;
			}
		}else{
			if("name".equals(returnType)){
				result = thing.getMetadata().getName();
			}else if("label".equals(returnType)){
				result = thing.getMetadata().getLabel();
			}else if("path".equals(returnType)){
				result = thing.getMetadata().getPath();
			}			
		}
		
						
		if(result != null){
			//属性模板
			String attributePattern = (String) actionContext.get("attributePattern");
			if(attributePattern != null && !"".equals(attributePattern)){			
				result = attributePattern.replace("{0}", result);
			}	
			
			//前缀
			String prefix = actionContext.getObject("prefix");
			if(prefix != null){
				result = prefix + result;
			}
			String surfix = actionContext.getObject("surfix");
			if(surfix != null) {
				result = result + surfix;
			}
			
			//是否是追加
			if("true".equals(actionContext.get("append"))){
				Text text = (Text) actionContext.get("text");
				if(text != null){
					String value = text.getText();
					if(value.indexOf(result) == -1){
						String seperator = (String) actionContext.get("seperator");
						if(!"".equals(value)){
							result = value + seperator + result;
						}
					}
					
				}
			}
			
			
			actionContext.getScope(0).put("result", result);
		}
		((Shell) actionContext.get("shell")).dispose();
	}
	
	@SuppressWarnings("unchecked")
	public static void init(ActionContext actionContext){
		String param = (String) actionContext.get("param");
		
		ActionContext parentContext = (ActionContext) actionContext.get("parentContext");
		//参数列表
		Map<String, String> params = UtilString.getParams(param, ",");
		Thing editThing = (Thing) ((ActionContext) parentContext.get("parentContext")).get("currentThing");
		if(editThing == null){
			return;
		}
		
		Thing rootThing = editThing.getRoot();
		String path = rootThing.getMetadata().getPath();

		params.put("thingScopePath", path);
		actionContext.getScope(0).putAll(params);
		/*
		actionContext.getScope(0).put("returnType", params.get("returnType"));
		actionContext.getScope(0).put("attributeName", params.get("attributeName"));
		actionContext.getScope(0).put("attributePattern", params.get("attributePattern"));
		actionContext.getScope(0).put("append", params.get("append"));
		actionContext.getScope(0).put("seperator", params.get("seperator"));		
		*/

		//初始化查询表单中的范围下拉列表的数据
		//def formActionContext = queryForm.doAction("getActionContext", actionContext);
		//formActionContext.thingScopePathDataStore.doAction("load", formActionContext, ["params".["pathListScopePath".editThing.metadata.path]]);

		//设置查询表单的数据
		Thing queryForm = (Thing) actionContext.get("queryForm");
		queryForm.doAction("setValues", actionContext, UtilMap.toMap(new Object[]{"values", params}));
		
		Map<String, Object> formParams = (Map<String, Object>) queryForm.doAction("getValues", actionContext);
		actionContext.g().put("params", params);

		//log.info("params=" + params);
		((Thing) actionContext.get("dataStore")).doAction("load", actionContext, UtilMap.toMap(new Object[]{"params", formParams}));
		
		if(params.get("registThing") != null){
			((Button) actionContext.getObject("openRegistButton")).setEnabled(true);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void initDataTree(ActionContext actionContext){
		//
		Thing store = (Thing) actionContext.get("store");
		List<Map<String, Object>> records = (List<Map<String, Object>>) store.get("records");

		Thing queryForm = (Thing) actionContext.get("queryForm");		
		Map<String, Object> params = (Map<String, Object>) queryForm.doAction("getValues", actionContext);
		String path = (String) params.get("thingScopePath");

		Tree dataTree = (Tree) actionContext.get("dataTree");
		for(TreeItem item : dataTree.getItems()){
			item.dispose();
		}
			
		List<String> paths = new ArrayList<String>();
		for(Map<String, Object> r : records){
			String thingPath = (String) r.get("path");
			paths.add(thingPath);
		}
		
		XWorkerTreeUtil.initTree(dataTree, paths, path, actionContext);
		
		for(String p : paths){
			Object obj = World.getInstance().get(p);
			if(obj instanceof Thing){
				Thing thing = (Thing) obj;
				boolean ok = false;
	            for(Thing desc : thing.getAllDescriptors()){
	                if(desc.getBoolean("isDescriptorForOpenWindow")){
	                    ok = true;
	                }
	            }
	  
				if(ok){
					try{
						List<Thing> things = (List<Thing>) thing.doAction("getThingForOpenWindow", actionContext, params);
						for(TreeItem item : dataTree.getItems()){
							TreeItem childItem = getItemByThing(item, thing);
							if(childItem != null){
								for(Thing gthing : things){
									TreeItem thingItem = new TreeItem(childItem, SWT.NONE);
									thingItem.setData(gthing);									
									XWorkerTreeUtil.initItem(thingItem, gthing, actionContext);
								}
								
								childItem.setExpanded(true);
								break;
							}
						}
					}catch(Exception e){
						logger.error("getThingForOpenWindow error", e);
					}
				}
			}
		}
	}
	
	private static TreeItem getItemByThing(TreeItem item, Thing thing){
		if(item.getData() == thing){
			return item;
		}
		
		for(TreeItem child : item.getItems()){
			TreeItem childItem = getItemByThing(child, thing);
			if(childItem != null){
				return childItem;
			}
		}
		
		return null;
	}
	
}