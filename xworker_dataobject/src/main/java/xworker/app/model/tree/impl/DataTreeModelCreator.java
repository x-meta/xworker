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
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelUtils;
import xworker.lang.executor.Executor;

public class DataTreeModelCreator {
	private static final String TAG =  DataTreeModelCreator.class.getName();
	
    public static Object getRoot(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        
        Object data = actionContext.get(self.getString("varName"));
        if(data == null){
            return null;
        }else{
            return self.doAction("dataToNode", actionContext, UtilMap.toMap(new Object[]{"data", data}));
        }
    }

    @SuppressWarnings("unchecked")
	public static Object getChilds(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        
        if(actionContext.get("parentNode") != null){
        	Map<String, Object> parentNode = (Map<String, Object>) actionContext.get("parentNode");
            //如果有parentNode，那么直接从缓存中找子节点
            Object parentData = parentNode.get("data");
            //log.info("parentData=" + parentData);
            if(parentData != null){
                Object childs = getChilds(parentData, self, actionContext);
                //log.info("childs=" + childs);
                return childs;
            }
        }
        
        //通过遍历子节点找到父节点
        if(actionContext.get("id") == null){
            return self.doAction("getRoots", actionContext);
        }
        
        Object data = actionContext.get(self.getString("varName"));
        if(data == null){
            return null;
        }else{
            Object parentData = getNodeById(data, String.valueOf(actionContext.get("id")), self);
            if(parentData != null){
                return getChilds(parentData, self, actionContext);
            }
            
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
	public static Object getNodeById(Object data, Object id, Thing self) throws OgnlException{
        Object dataId = getValue(data, self.getString("idName"));
        if(dataId != null && dataId.equals(id)){
            return data;
        }
        
        Iterable<Object> childDatas = (Iterable<Object>) OgnlUtil.getValue(self.getString("childsName"), data);
        if(childDatas != null){
           for(Object childData : childDatas){
               Object theData = getNodeById(childData, id, self);
               if(theData != null){
                   return theData;
               }
           }
        }
        
        return null;
    }
    
    @SuppressWarnings("unchecked")
	public static Object getChilds(Object parentData, Thing self, ActionContext actionContext) throws OgnlException{
        List<Object> childs = new ArrayList<Object>();
        Iterable<Object> childDatas = (Iterable<Object>) OgnlUtil.getValue(self.getString("childsName"), parentData);
        //log.info("childs=" + childDatas + ",childsName=" + self.childsName + ",parentData=" + parentData);
        if(childDatas != null){
            for(Object childData : childDatas){
                Object node = self.doAction("dataToNode", actionContext, UtilMap.toMap(new Object[]{"data", childData}));
                if(node != null){
                    childs.add(node);
                }            
            }
        }
        //log.info("childs=" + childs);
        return childs;
    }

    public static Object dataToNode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        Object data = actionContext.get("data");
        
        String id = getValue(data, self.getString("idName"));
        if(id == null){
            return null;
        }
        
        Map<String, Object> node = new HashMap<String, Object>();
        node.put("text", getValue(data, self.getString("textName")));
        TreeModelUtils.setAttributes(self, id, node);
        node.put("icon", getValue(data, self.getString("iconName")));
        node.put("cls", getValue(data, self.getString("clsName")));
        node.put(TreeModel.Source, data);
        return node;
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

    public static Object insertNode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        if(actionContext.get("node") != null){
            return self.doAction("dataToNode", actionContext, UtilMap.toMap(new Object[]{"data", actionContext.get("node")}));
        }else{
            return null;
        }
    }

    @SuppressWarnings("unchecked")
	public static Object insertNodes(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        if(actionContext.get("nodes") != null){
            List<Object> ns = new ArrayList<Object>();
            Iterable<Object> nodes = (Iterable<Object>) actionContext.get("nodes");
            for(Object node : nodes){
                Object nd = self.doAction("dataToNode", actionContext, UtilMap.toMap(new Object[]{"data", node}));
                if(nd != null){
                    ns.add(nd);
                }
            }
            return ns;
        }else{
            return null;
        }
    }

    public static Object updateNode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        if(actionContext.get("node") != null){
            return self.doAction("dataToNode", actionContext, UtilMap.toMap(new Object[]{"data", actionContext.get("node")}));
        }else{
            return null;
        }
    }

    @SuppressWarnings("unchecked")
	public static Object updateNodes(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        if(actionContext.get("nodes") != null){
            List<Object> ns = new ArrayList<Object>();
            Iterable<Object> nodes = (Iterable<Object>) actionContext.get("nodes");
            for(Object node : nodes){
                Object nd = self.doAction("dataToNode", actionContext, UtilMap.toMap(new Object[]{"data", node}));
                if(nd != null){
                    ns.add(nd);
                }
            }
            return ns;
        }else{
            return null;
        }
    }

    public static Object removeNode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        if(actionContext.get("node") != null){
            return self.doAction("dataToNode", actionContext, UtilMap.toMap(new Object[]{"data", actionContext.get("node")}));
        }else{
            return null;
        }
    }

    @SuppressWarnings("unchecked")
	public static Object removeNodes(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        if(actionContext.get("nodes") != null){
            List<Object> ns = new ArrayList<Object>();
            Iterable<Object> nodes = (Iterable<Object>) actionContext.get("nodes");
            for(Object node : nodes){
                Object nd = self.doAction("dataToNode", actionContext, UtilMap.toMap(new Object[]{"data", node}));
                if(nd != null){
                    ns.add(nd);
                }
            }
            return ns;
        }else{
            return null;
        }
    }

}