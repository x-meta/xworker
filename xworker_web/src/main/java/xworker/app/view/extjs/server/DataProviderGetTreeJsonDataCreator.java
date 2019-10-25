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
package xworker.app.view.extjs.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;
import xworker.security.PermissionConstants;
import xworker.security.SecurityManager;

public class DataProviderGetTreeJsonDataCreator {
	private static Logger log = LoggerFactory.getLogger(DataProviderGetTreeJsonDataCreator.class);
	
    @SuppressWarnings("unchecked")
	public static void doAction(ActionContext actionContext) throws IOException{
        World world = World.getInstance();
        
        HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
        HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
        
        String path = request.getParameter("path");
        if(!SecurityManager.doCheck("WEB", PermissionConstants.XWORKER_DATAPROVIDER, "read", path, actionContext)){
        	return;
        }
        
        Object thing = world.get(path);
        response.setContentType("text/plain; charset=utf-8");
        String type = request.getParameter("type");
        if("data".equals(type)){
        	List<Object> data = null;
            //直接是事物数据
            if(thing == null){
                log.info("DataProvider.getTreeJsonData: thing path data is null, path=" + request.getParameter("path"));
                data = new ArrayList<Object>();
            }
            if(!(thing instanceof List)){
            	data = new ArrayList<Object>();
            	data.add(thing);
            }else {
            	data = (List<Object>) thing;
            }
            Thing jsonFactory = world.getThing("xworker.text.JsonDataFormat");
            String code = (String) jsonFactory.doAction("getData", actionContext, UtilMap.toMap(new Object[]{"data",data}));
            response.getWriter().println(code);
        }else if("treeModel".equals(type)){
            //通过getChilds方法取树节点
        	actionContext.peek().put("id", request.getParameter("id"));
            Object nodes = ((Thing) thing).doAction("getChilds", actionContext);
            if(nodes == null){
                nodes = new ArrayList<Object>();
            }
            if(!(nodes instanceof List)){
            	List<Object> ns = new ArrayList<Object>();
            	ns.add(nodes);
            	nodes = ns;
            }
            Object data = nodes;
            List<Object> ns = (List<Object>) nodes;
            
            if(ns.size() > 0 && !(ns.get(0) instanceof Thing || ns.get(0) instanceof DataObject)){
                //不是事物，转换为TreeNode事物
                Thing descriptor = world.getThing("xworker.app.model.tree.TreeNode");
                List<Object> dataTmp = new ArrayList<Object>();
                for(Object nodeData : ns){
                    Thing node = new Thing("xworker.app.model.tree.TreeNode");
                    convertToTreeNode(nodeData, node, descriptor);
                    dataTmp.add(node);
                }
                
                data = dataTmp;
            }
            Thing jsonFactory = world.getThing("xworker.text.JsonDataFormat");
            String code = (String) jsonFactory.doAction("getData", actionContext, UtilMap.toMap(new Object[]{"data", data}));
            
            response.getWriter().println(code);
        }else{
            //其他，应该不存在这种情况，以前保留的代码，也许有用
            Object data = ((Thing) thing).doAction("getTreeModel", actionContext);
            Thing jsonFactory = world.getThing("xworker.text.JsonDataFormat");
            String code = (String) jsonFactory.doAction("getData", actionContext, UtilMap.toMap(new Object[]{"data", data}));
            response.getWriter().println(code);
        }
    }

  //把非Thing的对象转化为树节点对象
    @SuppressWarnings("unchecked")
	public static void  convertToTreeNode(Object data, Thing node, Thing descriptor){
        for(Thing attr : (List<Thing>) descriptor.get("attribute@")){
            try{
                //log.info("data=" + data);
            	String name = attr.getString("name");
                node.put(name, OgnlUtil.getValue(name, data));
            }catch(Exception e){
                //log.info("DataProvider.getTreeJsonData: conver to TreeNode attribute", e);
            }
        }
        
        try{
            Iterable<Object> childs = (Iterable<Object>) OgnlUtil.getValue("childs", data);
            if(childs != null){
                for(Object child : childs){
                    Thing childNode = new Thing("xworker.app.model.tree.TreeNode/@children");
                    convertToTreeNode(child, childNode, descriptor);
                    node.addChild(childNode);
                }
            }
        }catch(Exception e){
            //log.info("DataProvider.getTreeJsonData: conver to TreeNode childs", e);
        }
    }
}