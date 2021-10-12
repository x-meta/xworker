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

public class ListTreeModelCreator {
    @SuppressWarnings("unchecked")
	public static Object getRoot(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        
        if(self.getBoolean("virtualRoot")){
        	Map<String, Object> node = new HashMap<String, Object>();
        	TreeModelUtils.setAttributes(self, self.getString("rootIdValue"), node);
        	node.put("text", "");
        	node.put("leaf", "false");
            return node;
        }
        
        Iterable<Object> list = (Iterable<Object>) actionContext.get(self.getString("listVar"));
        if(list == null){
            return null;
        }else{
            for(Object l : list){
                Object idValue = OgnlUtil.getValue(self.getString("idField"), l);
                if(idValue != null && idValue.toString().equals(self.getString("rootIdValue"))){
                    return self.doAction("dataToNode", actionContext, UtilMap.toMap(new Object[]{"data", l}));
                }
            }
            
            return null;
        }
    }

    @SuppressWarnings("unchecked")
	public static Object getChilds(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        
        Iterable<Object> list = (Iterable<Object>) actionContext.get(self.getString("listVar"));
        if(list == null){
            return null;
        }else{
            //log.info("id=" + actionContext.get("id"));
            if(actionContext.get("id") == null){
                return self.doAction("getRoots", actionContext);
            }
        
            List<Object> childs = new ArrayList<Object>();
            for(Object l : list){
                Object idValue = OgnlUtil.getValue(self.getString("parentIdField"), l);
                if(idValue != null && idValue.toString().equals(actionContext.get("id").toString())){
                    Object node = self.doAction("dataToNode", actionContext, UtilMap.toMap(new Object[]{"data", l}));
                    if(node != null){
                        childs.add(node);
                    }  
                }
            }
            
            //log.info("childs=" + childs);
            return childs;
        }
    }

    public static Object dataToNode(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        Object data = actionContext.get("data");
    	Map<String, Object> node = new HashMap<String, Object>();
        TreeModelUtils.setAttributes(self, OgnlUtil.getValue(self, "idField",data), node);
        node.put("text", OgnlUtil.getValue(self.getString("textField"), data));
        TreeModelUtils.copyAttributesToNodeData(data, node);
        node.put("data", data);
        node.put(TreeModel.Source, data);
        
        return node;
    }

    public static Object insertNode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        if(actionContext.get("node") != null){
            return self.doAction("dataToNode", actionContext, UtilMap.toMap(new Object[]{"dataObject", actionContext.get("node")}));
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
                ns.add(self.doAction("dataToNode", actionContext, UtilMap.toMap(new Object[]{"dataObject", node})));
            }
            return ns;
        }else{
            return null;
        }     
    }

    public static Object updateNode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        if(actionContext.get("node") != null){
            return self.doAction("dataToNode", actionContext, UtilMap.toMap(new Object[]{"dataObject", actionContext.get("node")}));
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
                ns.add(self.doAction("dataToNode", actionContext, UtilMap.toMap(new Object[]{"dataObject", node})));
            }
            return ns;
        }else{
            return null;
        }
    }

    public static Object removeNode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        if(actionContext.get("node") != null){
            return self.doAction("dataToNode", actionContext, UtilMap.toMap(new Object[]{"dataObject", actionContext.get("node")}));
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
                ns.add(self.doAction("dataToNode", actionContext, UtilMap.toMap(new Object[]{"dataObject", node})));
            }
            return ns;
        }else{
            return null;
        }
    }

}