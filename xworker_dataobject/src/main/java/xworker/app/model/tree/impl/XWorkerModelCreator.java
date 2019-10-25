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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Index;
import org.xmeta.Thing;
import org.xmeta.ThingIndex;

import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelUtil;

public class XWorkerModelCreator {
    public static Object getRoot(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        
        Index index = null;
        Thing treeModel = (Thing) actionContext.get("treeModel"); //实例
        String indexPath= treeModel.getString("indexPath"); 
        if(indexPath == null){
        	indexPath = self.getString("indexPath");
        }
        if(indexPath != null && !"".equals(indexPath)){
            index = Index.getIndexById(indexPath);            
        }else{
            index = Index.getInstance();
        }
        
        Map<String, Object> node = new HashMap<String, Object>();
        TreeModelUtil.setAttributes(self, Index.getIndexId(index), node);
        node.put("text", index.getLabel());
        node.put("icon",  getIcon(index));
        node.put("index", index);
        node.put("dataId", index.getPath());
        node.put("tabId", index.getType());
        
        return node;
    }

    public static Object getChilds(ActionContext actionContext){
        //log.info("id=" + id);
    	Thing self = (Thing) actionContext.get("self");
    	
    	Index parentIndex =  null;
    	String id = (String) actionContext.get("id");
    	if(id == null || "".equals(id) || TreeModel.ROOT_ID.equals(id)){    		
    		String indexPath = self.getString("indexPath");
	        if(indexPath != null && !"".equals(indexPath)){
	        	parentIndex = Index.getIndexById(indexPath);            
	        }else{
	        	parentIndex = Index.getInstance();
	        }
    	}else{
    		parentIndex = Index.getIndexById((String) actionContext.get("id"));
    	}
    	
    	if(self.getBoolean("refreshOnGetChilds")){
    		parentIndex.refresh();
    	}
    	if(parentIndex == null){    	
    		return null;
    	}
        List<Index> indexs = parentIndex.getChilds();
        
        List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
        for(Index index : indexs){
        	//过滤级别
        	String displayLevel = self.getString("displayLevel");
        	if(displayLevel == null || "".equals(displayLevel)){
        		displayLevel = Index.TYPE_CATEGORY;
        	}
        	if(Index.TYPE_THINGMANAGER.equals(displayLevel)){
	        	if(Index.TYPE_THING.equals(index.getType()) || Index.TYPE_CATEGORY.equals(index.getType())){
	        		continue;
	        	}
        	}else if(Index.TYPE_CATEGORY.equals(displayLevel)){
        		if(Index.TYPE_THING.equals(index.getType())){
        			continue;
        		}
        	}else if(Index.TYPE_THING.equals(displayLevel)){
        		if(Index.TYPE_THING.equals(index.getParent().getType())){
        			continue;
        		}
        	}
        	
        	 Map<String, Object> node = new HashMap<String, Object>();             
             TreeModelUtil.setAttributes(self, Index.getIndexId(index), node);
             node.put("text", index.getLabel());
             node.put("icon",  getIcon(index));
             node.put("index", index);
             node.put("data", index);
             node.put("dataId", index.getPath());
             node.put("tabId", index.getType());
            // node.put("id", value)
            
            nodes.add(node);
        }
        return nodes;
    }
    
    public static String getIcon(Index index){
    	String type = index.getType();
    	if(Index.TYPE_WORLD.equals(type)){
            return "/icons/xworker/project.gif";
    	}else if("thingManager".equals(type)){
            return "/icons/xworker/project1.gif";
    	}else if("category".equals(type)){
            return "/icons/xworker/package.gif";
    	}else if("thing".equals(type)){
            if(index.getIndexObject() instanceof ThingIndex){
                return "/icons/xworker/dataObject.gif";              
            }else{
                return "xworker:core/images/swteditor/dataObjectChild.gif"; 
            }        
    	}else if(Index.TYPE_WORKING_SET.equals(type)){
    		return "/icons/folder.gif";
    	}
    	
    	return null;
    }

}