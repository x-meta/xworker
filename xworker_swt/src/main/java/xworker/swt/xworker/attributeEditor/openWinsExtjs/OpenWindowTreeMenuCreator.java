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
package xworker.swt.xworker.attributeEditor.openWinsExtjs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

public class OpenWindowTreeMenuCreator {
	private static Logger log = LoggerFactory.getLogger(OpenWindowTreeMenuCreator.class);
	
    @SuppressWarnings("unchecked")
	public static Object getChilds(ActionContext actionContext){
        World world = World.getInstance();
        
        Thing descriptor = world.getThing("xworker.swt.xworker.attributeEditor.OpenWindowSets");
        List<Thing> desChilds = new ArrayList<Thing>();
        for(Thing child : descriptor.getChilds()){
            desChilds.add(child);
        }
        
        //查找并添加注册的子事物
        try{
            Thing registThing = world.getThing("xworker.ide.db.dbindex.app.dataObject.RegistsByThing");
            List<Map<String, Object>> rchilds = (List<Map<String, Object>> ) registThing.doAction("query", actionContext, UtilMap.toParams(new Object[]{"thing",descriptor, "noDescriptor",true, "registType","child"}));
            for(Map<String, Object> rc : rchilds){
                Thing child = world.getThing((String) rc.get("path"));
                if(child != null){
                    desChilds.add(child);
                }
            }
        }catch(Exception e){
            log.info("add regist child error", e);
        }
        
        Thing currentThing = (Thing) actionContext.get("currentThing");
        List<Thing> dess = new ArrayList<Thing>();
        for(Thing des : desChilds){
            if("false".equals(des.getString("many"))){
                if(currentThing.getChilds(des.getMetadata().getName()).size() != 0){
                    continue;
                }
            }
            log.info(des.getMetadata().getPath());
            dess.add(des);
        }
        
        //分组
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("childs", new ArrayList<Thing>());
        
        for(Thing des : dess){
            String group = des.getString("group");
            if(group == null || group == ""){
                ((List<Thing>) root.get("childs")).add(des);
            }else{
                String[] gs = group.split("[.]");
                Map<String, Object> subRoot = null;
                Map<String, Object> currentRoot = root;
                for(String g : gs){
                    subRoot = (Map<String, Object>) currentRoot.get(g);
                    if(subRoot == null){
                    	subRoot = new HashMap<String, Object>();
                    	subRoot.put("childs", new ArrayList<Thing>());                        
                        subRoot.put("__name__", g);
                        currentRoot.put(g, subRoot);
                    }
                    currentRoot = subRoot;
                }
                
                ((List<Thing>) subRoot.get("childs")).add(des);
            }
        }
        //构造子节点树
        Map<String, Object> treeNodes = new HashMap<String, Object>();
        createTree(treeNodes, root);
        
        log.info("treeNodes=" + treeNodes.get("childs"));
        return treeNodes.get("childs");
    }
    
    @SuppressWarnings("unchecked")
	public static void createTree(Map<String, Object> node, Map<String, Object> aroot){
        List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
        for(String key : aroot.keySet()){
            Object v = aroot.get(key);
            if(v instanceof Map){
                groups.add((Map<String, Object>) v);
            }
        }
        
        //为group排序
        Collections.sort(groups, new Comparator(){

			@Override
			public int compare(Object o1, Object o2) {
				HashMap<String, Object> t1 = (HashMap<String, Object>) o1;
				HashMap<String, Object> t2 = (HashMap<String, Object>) o2;
				String t1Name = ((String) t1.get("__name__")).toLowerCase();
				String t2Name = ((String) t2.get("__name__")).toLowerCase();
				return t1Name.compareTo(t2Name);  
			}
        	
        });
        
        List<Map<String, Object>> childs = new ArrayList<Map<String, Object>>();
        for(Map<String, Object> g : groups){
            Map<String, Object> child = new HashMap<String, Object>();
            child.put("text" ,g.get("__name__"));
            childs.add(child);
            createTree(child, g);
        }
    
        for(Thing child : (List<Thing>) aroot.get("childs")){
            Map<String, Object> childNode = new HashMap<String, Object>();
            childNode.put("text", child.getMetadata().getLabel());
            childNode.put("leaf", "true");        
            childs.add(childNode);
        }
        
        node.put("childs", childs);
    }

}