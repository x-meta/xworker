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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;

public class TreeModelInsActionCreator {
    @SuppressWarnings("unchecked")
	public static void fireEvent(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
    	
        //log.info("eventName=" + eventName);
        Iterable<Object> nodes = (Iterable<Object>) actionContext.get("nodes");
        Object node = actionContext.get("node");
        
        Map<String, Object> nodeMap = new HashMap<String, Object>();
        if(node != null){
            nodeMap = UtilMap.toMap(new Object[]{"id", OgnlUtil.getValue("id", node), "node", node});
        }
        List<Object> nodeMapList = null;
        if(nodes != null){
            nodeMapList = new ArrayList<Object>();
            for(Object rnode : nodes){
                nodeMapList.add(UtilMap.toMap(new Object[]{"id", OgnlUtil.getValue("id", rnode), "node", rnode}));
            }
        }
        
        for(Thing listener : (List<Thing>) self.get("listeners")){
            //log.info("tree listener=" + listener);
            listener.doAction((String) actionContext.get("eventName"), actionContext, UtilMap.toMap(new Object[]{"node", nodeMap, "nodes", nodeMapList}));
        }
    }

    public static Object getRoot(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
    	
        Object root = ((Thing) self.get("modelThing")).doAction("getRoot", actionContext, UtilMap.toMap(new Object[]{"treeModel", self}));
        if(root != null){
            Map<String, Object> nodeMap = UtilMap.toMap(new Object[]{"id", "" + OgnlUtil.getValue("id",root), "node", root});
            self.doAction("initNodeMap", actionContext, UtilMap.toMap(new Object[]{"nodeMap", nodeMap, "node",root}));
            return nodeMap;
        }else{
            return null;
        }
    }

    @SuppressWarnings("unchecked")
	public static Object getRoots(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
    	
        Iterable<Object> roots = (Iterable<Object>) ((Thing) self.get("modelThing")).doAction("getRoots", actionContext, UtilMap.toMap(new Object[]{"treeModel", self}));
        if(roots != null){
            List<Object> rs = new ArrayList<Object>();
            for(Object root : roots){
                Map<String, Object> nodeMap = UtilMap.toMap(new Object[]{"id", "" + OgnlUtil.getValue("id", root), "node", root});
                self.doAction("initNodeMap", actionContext, UtilMap.toMap(new Object[]{"nodeMap", nodeMap, "node",root}));
                rs.add(nodeMap);
            }
            
            return rs;
        }else{
            return roots;
        }
    }

    @SuppressWarnings("unchecked")
	public static Object getChilds(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
    	Thing modelThing = (Thing) self.get("modelThing");
    	
        Iterable<Object> nodes = (Iterable<Object>) modelThing.doAction("getChilds", actionContext, UtilMap.toMap(new Object[]{"treeModel", self}));
        //log.info("nodes=" + nodes);
        if(nodes != null){
            if(modelThing.getBoolean("multiSubModel")){
                Thing model = (Thing) modelThing.doAction("getTreeModel", actionContext, UtilMap.toMap(new Object[]{"treeModel", self}));
                if(model != modelThing){
                    List<Object> list = new ArrayList<Object>();
                    for(Object node : nodes){
                        Map<String, Object> nodeMap = UtilMap.toMap(new Object[]{"id", model.get("modelId") + modelThing.getString("multiSubModelSeparator") + OgnlUtil.getValue("id", node), 
                             "node", node, "modelId",  model.get("modelId")});
                        self.doAction("initNodeMap", actionContext, UtilMap.toMap(new Object[]{"nodeMap", nodeMap, "node",node, "modelId", model.get("modelId")}));
                        list.add(nodeMap);
                    }
                    return list;
                }else{
                	List<Object> list = new ArrayList<Object>();
                    for(Object node : nodes){
                        Map<String, Object> nodeMap = UtilMap.toMap(new Object[]{"id", "" + OgnlUtil.getValue("id", node), "node", node});
                        self.doAction("initNodeMap", actionContext, UtilMap.toMap(new Object[]{"nodeMap", nodeMap, "node",node}));
                        list.add(nodeMap);
                    }
                    return list;
                }
            }else{
            	List<Object> list = new ArrayList<Object>();
                for(Object node : nodes){
                    Map<String, Object> nodeMap = UtilMap.toMap(new Object[]{"id", "" + OgnlUtil.getValue("id", node), "node", node});
                    self.doAction("initNodeMap", actionContext, UtilMap.toMap(new Object[]{"nodeMap", nodeMap, "node",node}));
                    list.add(nodeMap);
                }
        
                return list;
            }
        }else{
            return null;
        }
    }

    public static void insertNode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        self.doAction("fireEvent", actionContext, 
                UtilMap.toMap(new Object[]{"eventName","onNodeInserted", "treeNodes", null, "nodes",null, "treeModel", self}));
    }

    public static void updateNode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        self.doAction("fireEvent", actionContext, 
        		UtilMap.toMap(new Object[]{"eventName","onNodeUpdate", "node",actionContext.get("node"), "nodes",null, "treeModel", self}));
    }

    public static void removeNode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        self.doAction("fireEvent", actionContext, 
        		UtilMap.toMap(new Object[]{"eventName","onNodeRemoved", "ndoe", actionContext.get("node"), "nodes",null, "treeModel", self}));
    }

    public static void insertNodes(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        self.doAction("fireEvent", actionContext, 
        		UtilMap.toMap(new Object[]{"eventName","onNodeInserted", "node",null, "nodes", actionContext.get("nodes"), "treeModel", self}));
    }

    public static void updateNodes(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        self.doAction("fireEvent", actionContext, 
        		UtilMap.toMap(new Object[]{"eventName","onNodeUpdate", "node",null, "nodes", actionContext.get("nodes"), "treeModel", self}));
    }

    public static void removeNodes(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        self.doAction("fireEvent", actionContext, 
        		UtilMap.toMap(new Object[]{"eventName","onNodeRemoved", "node",null, "nodes",actionContext.get("nodes"), "treeModel", self}));
    }

    public static void refresh(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        
        //更新一个节点
        Object node = actionContext.get("node");
        if(node != null){
            self.doAction("fireEvent", actionContext, 
            		UtilMap.toMap(new Object[]{"eventName","onNodeUpdate", "node",node, "nodes",null, "includeChilds", true, "treeModel", self}));
        }
        
        //如果没有更新根节点
        if(node == null){
            self.doAction("fireEvent", actionContext, 
            		UtilMap.toMap(new Object[]{"eventName","onNodeUpdate", "node",null, "nodes",null, "includeChilds", true, "treeModel", self}));
        }
    }

    @SuppressWarnings("unchecked")
	public static void initNodeMap(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");

    	Object node = actionContext.get("node");
        Iterable<Object> childs = null;
        try{
        	childs = (Iterable<Object>) OgnlUtil.getValue("childs", node);
        }catch(Exception e){
        }
        
        if(childs != null){
            List<Object> nodes = new ArrayList<Object>();
            for(Object child : childs){
                Map<String, Object> childNodeMap = UtilMap.toMap(new Object[]{"id", OgnlUtil.getValue("id", child), "node", child, "modelId", OgnlUtil.getValue("modelId", child)});
                self.doAction("initNodeMap", actionContext, UtilMap.toMap(new Object[]{"nodeMap", childNodeMap, "node", child, "treeModel", self}));
                nodes.add(childNodeMap);
            }
            
            ((Map<String, Object>) actionContext.get("nodeMap")).put("childs", nodes);
        }
    }

    public static void reload(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	Object parent = self.get("parent");
    	/*
    	if(parent instanceof Tree){
    		for(TreeItem item : ((Tree) parent).getItems()){
    			item.dispose();
    		}
    	}else if(parent instanceof TreeItem){
    		for(TreeItem item : ((TreeItem) parent).getItems()){
    			item.dispose();
    		}
    	}*/
        self.doAction("fireEvent", actionContext, UtilMap.toMap(new Object[]{"eventName","init", "treeModel", self, "parent", parent, "treeModel", self}));
    }

}