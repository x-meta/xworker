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
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelUtils;
import xworker.dataObject.DataObject;
import xworker.dataObject.query.UtilCondition;

public class DataObjectTreeModelCreator {
    public static Object getRoot(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        
        //数据对象
        Thing dataObject = World.getInstance().getThing(self.getString("dataObject"));
        if(dataObject == null){
            Thing dos = self.getThing("dataObjects@0");
            if(dos != null && dos.getChilds().size() > 0){
                dataObject = dos.getChilds().get(0);
            }
        }
        
        if(dataObject == null){
            return null;
        }else{
            DataObject rootData = new DataObject(dataObject);
            rootData.put(self.getString("idField"), self.getString("rootId"));
            rootData.load(actionContext);
            
            return self.doAction("dataObjectToNode", actionContext, UtilMap.toMap(new Object[]{"dataObject", rootData}));
        }
    }

    @SuppressWarnings("unchecked")
	public static Object getChilds(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	World world = World.getInstance();
        
        //数据对象
        Thing dataObject = world.getThing(self.getString("dataObject"));
        if(dataObject == null){
            Thing dos = self.getThing("dataObjects@0");
            if(dos != null && dos.getChilds().size() > 0){
                dataObject = dos.getChilds().get(0);
            }
        }
        if(dataObject == null){
            return null;
        }
        
        //查询配置
        Thing queryConfig = world.getThing(self.getString("queryConfig"));
        if(queryConfig == null){
            queryConfig = self.getThing("queryConfig@0");
        }
        if(queryConfig == null){
            queryConfig = new Thing("xworker.dataObject.query.Condition");
            queryConfig.put("attributeName", self.getString("parentIdField"));
            queryConfig.put("dataName", self.getString("idField"));            
            queryConfig.put("operator", UtilCondition.eq);
        }
        
        //查询子节点
        String idField = self.getString("idField");
        Map<String, Object> paramData = new HashMap<String, Object>();
        paramData.put(idField, actionContext.get("id"));
        //log.info("idField=" + idField + ",id=" + id);
        List<Object> datas = (List<Object>) dataObject.doAction("query", actionContext, UtilMap.toMap(new Object[]{"conditionData", paramData, "conditionConfig", queryConfig}));
        if(datas != null){
            List<Object> nodes = new ArrayList<Object>();
            for(Object data : datas){
                nodes.add(self.doAction("dataObjectToNode", actionContext, UtilMap.toMap(new Object[]{"dataObject", data})));
            }
            //log.info("nodes=" + nodes);
            return nodes;
        }else{
            return null;
        }
    }

    public static Object dataObjectToNode(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        
    	Object dataObject = actionContext.get("dataObject");
        Map<String, Object> root = new HashMap<String, Object>();
        TreeModelUtils.copyAttributesToNodeData(dataObject, root);
        
        root.put(TreeModel.Source, dataObject);
        root.put("dataObject", dataObject);
        root.put("data", dataObject);        
        TreeModelUtils.setAttributes(self, OgnlUtil.getValue(self, "idField", dataObject), root);
        root.put("text", OgnlUtil.getValue(self.getString("textField"), dataObject));
        String iconField = self.getString("iconField");
        if(iconField != null && !"".equals(iconField)){
            root.put("icon", OgnlUtil.getValue(self.getString("iconField"), dataObject));
        }
        String clsField= self.getString("clsField");
        if(clsField != null && !"".equals(clsField)){
            root.put("cls", OgnlUtil.getValue(self.getString("clsField"), dataObject));
        }
        String isLeafField = self.getStringBlankAsNull("isLeafField");
        if(isLeafField != null){
        	root.put("leaf", "" + UtilData.isTrue(OgnlUtil.getValue(self.getString("clsField"), dataObject)));
        }
        if(root.get("icon") == null){
        	//设置默认图标
        	if(root.get("leaf") == null || UtilData.isTrue(root.get("leaf"))){
        		root.put("icon", "icons/folder.gif");
        	}else{
        		root.put("icon", "icons/page_white.gif");
        	}
        }
        
        //是否有初始化动作
        String initAction = self.getStringBlankAsNull("initAction");
        if(initAction != null){
        	self.doAction(initAction, actionContext, UtilMap.toMap("dataObject", dataObject, "treeNode", root));
        }
        
        return root;
    }

    public static Object insertNode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        if(actionContext.get("node") != null){
            return self.doAction("dataObjectToNode", actionContext, UtilMap.toMap(new Object[]{"dataObject", actionContext.get("node")}));
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
                ns.add(self.doAction("dataObjectToNode", actionContext, UtilMap.toMap(new Object[]{"dataObject", node})));
            }
            return ns;
        }else{
            return null;
        }
    }

    public static Object updateNode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        if(actionContext.get("node") != null){
            return self.doAction("dataObjectToNode", actionContext, UtilMap.toMap(new Object[]{"dataObject", actionContext.get("node")}));
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
                ns.add(self.doAction("dataObjectToNode", actionContext, UtilMap.toMap(new Object[]{"dataObject", node})));
            }
            return ns;
        }else{
            return null;
        }
    }

    public static Object removeNode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        if(actionContext.get("node") != null){
            return self.doAction("dataObjectToNode", actionContext, UtilMap.toMap(new Object[]{"dataObject", actionContext.get("node")}));
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
                ns.add(self.doAction("dataObjectToNode", actionContext, UtilMap.toMap(new Object[]{"dataObject", node})));
            }
            return ns;
        }else{
            return null;
        }
    }

}