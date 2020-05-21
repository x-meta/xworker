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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;

public class TreeModelCreator {
	private static Logger logger = LoggerFactory.getLogger(TreeModelCreator.class);
	
    public static Object getRoot(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        //TreeModel自身即可作根节点，所以默认返回自身
    	Thing modelThing = (Thing) self.get("modelThing");
    	Thing root = null;
        if(modelThing != null){
            //在SWT下通过实例引用了原始的TreeModel
            root =  (Thing) modelThing.get("Root@0");  
        }else{
            //直接使用TreeModel的情况下，自身也是节点
            root = (Thing) self.get("Root@0");
        }
        
        if(root == null) {
        	root = new Thing("xworker.app.model.tree.TreeModel/@Root");
        	root.set("id", self.get("rootNodeId"));
        	root.set("text", "default root");
        }
        
        return root;
    }

    public static Object getRoots(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        
        Object root = self.doAction("getRoot", actionContext);
        if(root != null){
            if(self.getBoolean("rootVisiable")){
            	List<Object> roots = new ArrayList<Object>();
            	roots.add(root);
                return roots;
            }else{
                Object rootId = OgnlUtil.getValue("id", root);
                if(rootId != null && !"".equals(rootId)){
                    return self.doAction("getChilds", actionContext, UtilMap.toMap(new Object[]{"id", rootId}));
                }else{
                    Object childs = null;
                    try{
                        if(root instanceof Thing){
                            childs = ((Thing) root).getChilds();
                        }else{
                            childs = OgnlUtil.getValue("childs", root);
                        }
                    }catch(Exception e){
                    }
                    return childs;
                }
            }
        }else{
            return null;
        }
    }

    public static Object getChilds(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        
        String id = (String) actionContext.get("id");
        String rootId = self.getString("rootNodeId");
        //extjs下如果根节点没有设置标识，那么默认为__TreeNodeRootId__
        if(id == null || id == "" || id.equals(rootId) || "__TreeNodeRootId__".equals(id)){
            return self.doAction("getRoots", actionContext);
        }else{
            Thing model = self;
            if(self.getBoolean("multiSubModel")){
                model = (Thing) self.doAction("getTreeModel", actionContext);
            }
            if(model != self){
                if(model == null){
                    return null;
                }
        
                String separator = self.getString("multiSubModelSeparator");
            
                int index = id.indexOf(separator);
                String nodeId = id.substring(index + separator.length(), id.length());
                Object childNodes = model.doAction("getChilds", actionContext, UtilMap.toMap(new Object[]{"id", nodeId}));
                return childNodes;
            }else{
                Object root = model.doAction("getRoot", actionContext);
                Object node = getNode(root, id);
                if(node != null){
                    return getChilds(node);
                }else{
                    return null;
                }
            }
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Iterable<Object> getChilds(Object node){
    	if(node instanceof Thing){
    		List thingChilds = ((Thing) node).getChilds();
    		return (Iterable<Object>) thingChilds;
    	}else{
    		try{
    			return (Iterable<Object>) OgnlUtil.getValue("childs", node);
    		}catch(Exception e){
    			logger.warn("TreeModel get childs node error", e);
    			return null;
    		}
    	}
    }
    
    public static Object getNode(Object node, String id) throws OgnlException{
        if(id.equals(OgnlUtil.getValue("id", node))){
            return node;
        }
        
        Iterable<Object> childs = getChilds(node);
        
        if(childs != null){
            for(Object child : childs){
                Object obj = getNode(child, id);
                if(obj != null){
                    return obj;
                }
            }
        }
        
        return null;
    }

    public static Object getTreeModel(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        if(self.getBoolean("multiSubModel") == false){
            return self;
        }else{
        	
            String separator = self.getString("multiSubModelSeparator");
            if(separator == null || separator == ""){
                return self;
            }
            
            if(actionContext.get("id") == null){
                return self;
            }
            
            Object id = actionContext.get("id");
            if(!(id instanceof String)){
                return self;
            }
            
            String sid = (String) id;
            int index = sid.indexOf(separator);
            if(index == -1){
                return self;
            }
            String modelId = sid.substring(0, index);
            String nodeId = sid.substring(index + separator.length(), sid.length());
            Thing subModes = (Thing) self.getThing("SubModels@0");
            //log.info("subModes=" + subModes);
            if(subModes == null){
                return self;
            }
            for(Thing child : subModes.getChilds()){
                if(child.getString("modelId").equals(modelId)){
                    child.setData("__TreeModel_NODEID__", nodeId);
                    return child;
                }
            }
            
            return self;
        }
    }

}