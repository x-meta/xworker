package xworker.app.model.tree.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.app.model.tree.SimpleTreeNode;

public class StringListTreeModel {
	@SuppressWarnings("unchecked")
	public static List<String> getStringList(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String stringListVar = self.getStringBlankAsNull("stringListVar");
		if(stringListVar == null){
			throw new ActionException("String list var is null, path=" + self.getMetadata().getPath());
		}
		
		return (List<String>) actionContext.get(stringListVar);
	}
	
	@SuppressWarnings("unchecked")
	public static Object getRoot(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<String> list = (List<String>) self.doAction("getStringList", actionContext);
		
		return getRoot(list, self, actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public static Object getChilds(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<String> list = (List<String>) self.doAction("getStringList", actionContext);
		
		SimpleTreeNode root = getRoot(list, self, actionContext);
		Object id = actionContext.get("id");
		if("__TreeNodeRootId__".equals(id)){
			return root.getChilds();
		}else{
			return root.getChilds();
		}
	}
	
	public static String getIcon(ActionContext actionContext){
		//String str = (String) actionContext.get("str");
		boolean isLeaf = (Boolean) actionContext.get("isLeaf");
		
		if(isLeaf){
			return "icons/page.png";
		}else{
			return "icons/folder.gif";
		}
	}
	
	public static SimpleTreeNode getRoot(List<String> list, Thing self, ActionContext actionContext){
		Collections.sort(list);
		
		Map<String, SimpleTreeNode> cache = new HashMap<String, SimpleTreeNode>();
		String splitRegex = self.getStringBlankAsNull("splitRegex");
		if(splitRegex == null){
			throw new ActionException("splitRegex is null, path=" + self.getMetadata().getPath());
		}
		
		SimpleTreeNode root = new SimpleTreeNode(self.getMetadata().getName(), self.getMetadata().getLabel(), "icons/folder.gif", null);
		
		for(String str : list){
			String strs[] = str.split(splitRegex);
			String path = null;
			SimpleTreeNode parent = root;
			for(int i=0; i<strs.length; i++){
				String s= strs[i];
				if(path == null){
					path = s;
				}else{
					path = path + "___" + s;
				}
				
				SimpleTreeNode node = cache.get(path); 
				if(node == null){
					node = new SimpleTreeNode();
					node.setText(s);
					node.setId(str);
					node.setData(str);
					String icon = (String) self.doAction("getIcon", actionContext, UtilMap.toMap(new Object[]{
							"str", str, "isLeaf", i == strs.length - 1									
					}));
					node.setIcon(icon);
					
					parent.addChild(node);
					cache.put(path, node);
				}
				parent = node;
			}
		}
		
		return root;
	}
}
