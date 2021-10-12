package xworker.app.model.tree.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import ognl.OgnlException;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelUtils;

public class ThingTreeModelActions {
	
	/**
	 * 获取根节点，每次根节点时刷新数据。
	 * 
	 * @param actionContext
	 * @return
	 * @throws IOException
	 * @throws OgnlException 
	 */
	public static Object getThingRoot(ActionContext actionContext) throws IOException, OgnlException{
    	Thing self = (Thing) actionContext.get("self");
    	Thing thing = self.doAction("getRootThing", actionContext);
    	if(thing == null) {
	    	String thingPath = ThingRegistTreeModelActions.getThingPath(self, actionContext);
	    	thing = World.getInstance().getThing(thingPath);
    	}
    	if(thing == null){
    		return null;
    	}else{
    		return toTreeNode(thing, self, null, null, actionContext);
    	}    	
    }
	
	/**
	 * 获取子节点列表。
	 * 
	 * @param actionContext
	 * @return
	 * @throws IOException
	 * @throws OgnlException 
	 */
	public static Object getThingChilds(ActionContext actionContext) throws IOException, OgnlException{
    	Thing self = (Thing) actionContext.get("self");
    	
    	String id = (String) actionContext.get("id");
    	if(id == null || "".equals(id) || TreeModel.ROOT_ID.equals(id)){
    		List<Object> childs = new ArrayList<Object>();
    		childs.add(getThingRoot(actionContext));
    		return childs;
    	}
    	
        Thing thing = World.getInstance().getThing(id);   	
        List<Map<String, Object>> childNodes = new ArrayList<Map<String, Object>>();
        
        List<String> includeThingNames = null;
        List<String> excludeThingNames = null;
        String ins = self.doAction("getIncludeThingNames", actionContext);
        if(ins != null && !"".equals(ins)) {
        	includeThingNames = new ArrayList<String>();
        	for(String inName : ins.split("[,]")) {
        		includeThingNames.add(inName);
        	}
        }
        if(includeThingNames == null) {
        	String exs = self.doAction("getExcludeThingNames", actionContext);
        	if(exs != null && !"".equals(exs)) {
        		excludeThingNames = new ArrayList<String>();
        		for(String ex : exs.split("[,]")) {
        			excludeThingNames.add(ex);
        		}
        	}
        }
        
        List<Thing> childs = self.doAction("getChildThings", actionContext, "thing", thing);
        if(childs != null) {
			for(Thing child : childs){
				if(isNotFiltered(child, includeThingNames, excludeThingNames)) {
					childNodes.add(toTreeNode(child, self, includeThingNames, excludeThingNames, actionContext));
				}
			}
        }
		
		return childNodes;
    }
	
	private static boolean isNotFiltered(Thing child, List<String> includeThingNames, List<String> excludeThingNames) {
		boolean ok = true;
		if(includeThingNames != null) {
			ok = false;
			for(String name : child.getThingNames()) {
				for(String inName : includeThingNames) {
					if(name.equals(inName)) {
						ok = true;
						break;
					}
				}
			}
		}
		
		if(excludeThingNames != null) {
			ok = true;
			for(String name : child.getThingNames()) {
				for(String exName : excludeThingNames) {
					if(name.equals(exName)) {
						ok = false;
						break;
					}
				}
			}
		}
		
		return ok;
	}
	
	public static Map<String, Object> toTreeNode(Thing thing, Thing treeModel, List<String> includeThingNames,
				List<String> excludeThingNames, ActionContext actionContext){
		Map<String, Object> node = new HashMap<String, Object>();		
		TreeModelUtils.setAttributes(treeModel, thing.getMetadata().getPath(), node);
		node.put("text", thing.getMetadata().getLabel());
		String icon = thing.getStringBlankAsNull("icon");
		if(icon == null){
			for(Thing descriptor : thing.getAllDescriptors()){
			    //println descriptor.getMetadata().getPath();
			    icon = descriptor.getString("icon");
			    //println icon;
			    if(icon != null && icon != ""){
			        break;
			    }
			}
		}
		if(icon == null || "".equals(icon)){
			icon = "icons/page_white.png";
		}
		node.put("icon", icon);
		
		boolean dynamic = treeModel.getBoolean("dynamic");
		if(dynamic) {
			//设置为false，这样可以在后续继续获取子节点
			node.put("leaf", false);
		}else {
			List<Thing> childs = treeModel.doAction("getChildThings", actionContext, "thing", thing);
			
			if(childs == null || childs.size() == 0){
				node.put("leaf", true);
			}else{
				node.put("leaf", false);
			
				List<Map<String, Object>> childNodes = new ArrayList<Map<String, Object>>();
				for(Thing child : childs){
					if(isNotFiltered(child, includeThingNames, excludeThingNames)) {
						childNodes.add(toTreeNode(child, treeModel, includeThingNames, excludeThingNames, actionContext));
					}
				}
				node.put("childs", childNodes);
			}
		}
		node.put(TreeModel.Source, thing);
		
		return node;
	}
	
	/**
	 * 获取子节点列表的动作实现。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static List<Thing> getChildsThing(ActionContext actionContext){
		Thing thing = actionContext.getObject("thing");
		
		return thing.getChilds();
	}
	
	public static String getThingIcon(Thing thing){
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
		
		return icon;
	}
}
