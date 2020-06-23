package xworker.lang.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Category;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;

import xworker.swt.util.SwtUtils;
import xworker.swt.util.XWorkerTreeUtil;

public class FuntionsUtil  {
	public static List<FunctionNode> initFunctionNodes(ActionContext actionContext){
		List<FunctionNode> nodeList = new ArrayList<FunctionNode>();
		Map<String, FunctionNode> context = new HashMap<String, FunctionNode>();
		
		//获取所有的功能设置
		World world = World.getInstance();
		for(ThingManager manager : world.getThingManagers()){
		    Category rootCategory = manager.getCategory(null);
		    if(rootCategory != null){
		        for(Category lv1Category : rootCategory.getCategorys()){
		            for(Category lv2Category : lv1Category.getCategorys()){
		                //激活事物菜单
		                String projectPath = lv1Category.getSimpleName() + "." + lv2Category.getSimpleName();
		                Thing p = world.getThing(projectPath + ".config.functions");                     
		                if(p != null){
		                	for(Thing node : p.getChilds("FunctionsRelation")){
		                		FunctionNode fnode = getOrCreateRootNode(node, context);
		                		if(!node.getBoolean("onlyRegist")){
		                			//添加到根目录
		                			if(!nodeList.contains(fnode)){
		                				nodeList.add(fnode);
		                			}
		                		}
		                	}
		                }
		            }
		        }
		    }
		}
		
		//初始化关系
		for(String key : context.keySet()){
			FunctionNode fnode = context.get(key);
			if(fnode.inited == false && fnode.parentNodeIds != null){
				for(String parentId : fnode.parentNodeIds.split("[,]")){
					parentId = parentId.trim();
					FunctionNode parent = context.get(parentId);
					if(parent != null){
						parent.addChildNode(fnode, context);
					}					
				}
				
				fnode.inited = true;
			}
		}
		
		//排序
		sort(nodeList, new ActionContext());
		return nodeList;
	}
	
	public static void sort(List<FunctionNode> nodeList, ActionContext context){
		//移除重复的会引起递归的节点
		for(int i=0; i<nodeList.size(); i++){
			FunctionNode node = nodeList.get(i);
			if(context.get(node.node.getMetadata().getPath()) != null){
				nodeList.remove(i);
				i--;
			}
		}
		for(int i=0; i<nodeList.size(); i++){
			FunctionNode node = nodeList.get(i);
			context.peek().put(node.node.getMetadata().getPath(), node);
		}
		
		Collections.sort(nodeList);
		/*
		//排前和排后
		setAllInited(nodeList, false);		
		while(!isAllInited(nodeList)){
			for(int i=0; i<nodeList.size(); i++){
				FunctionNode node = nodeList.get(i);
				if(node.inited == false && node.beforeNodeId != null){
					for(String id : node.beforeNodeId.split("[,]")){
						for(int n=0; n<nodeList.size(); n++){
							FunctionNode nnode = nodeList.get(n);
							if(n != i && id.equals(nnode.nodeId)){
								nodeList.add(n, node);
								if(i < n){								
									nodeList.remove(i);
								}else{
									nodeList.remove(i + 1);
								}
								node.inited = true;
								break;
							}
						}
						
						if(node.inited){
							break;
						}
					}
				}
				node.inited = true;
			}
		}
		
		setAllInited(nodeList, false);		
		while(!isAllInited(nodeList)){
			for(int i=0; i<nodeList.size(); i++){
				FunctionNode node = nodeList.get(i);
				if(node.inited == false && node.afterNodeId != null){
					for(String id : node.afterNodeId.split("[,]")){
						for(int n=0; n<nodeList.size(); n++){
							FunctionNode nnode = nodeList.get(n);
							if(n != i && id.equals(nnode.nodeId)){
								nodeList.add(n+1, node);
								if(i < n){								
									nodeList.remove(i);
								}else{
									nodeList.remove(i + 1);
								}
								node.inited = true;
								break;
							}
						}
						
						if(node.inited){
							break;
						}
					}
				}
				node.inited = true;
			}
		}
		*/
		
		for(FunctionNode node : nodeList){
			sort(node.childs, context);
		}
	}
	
	public static boolean isAllInited(List<FunctionNode> nodeList){
		for(int i=0; i<nodeList.size(); i++){
			FunctionNode node = nodeList.get(i);
			if(!node.inited){
				return false;
			}
		}
		
		return true;
	}
	
	public static void setAllInited(List<FunctionNode> nodeList, boolean inited){
		for(int i=0; i<nodeList.size(); i++){
			FunctionNode node = nodeList.get(i);
			node.inited = inited;
		}
	}
	
	public static void attachToMenu(final Menu menu, final ActionContext actionContext){
		new Thread(new Runnable() {
			public void run() {
				final List<FunctionNode> nodeList = initFunctionNodes(actionContext);
				menu.getDisplay().asyncExec(new Runnable() {
					public void run() {
						try {
							for(FunctionNode node : nodeList){
								init(menu, node, actionContext, true);
							}
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
				});
				
			}
		}).start();
		
		
	}
	
	public static void attachToMenuItem(MenuItem item, ActionContext actionContext){
		List<FunctionNode> nodeList = initFunctionNodes(actionContext);
		Menu cmenu = new Menu(item);
		for(FunctionNode cnode : nodeList){
			init(cmenu, cnode, actionContext, false);
		}
	}
	
	public static void init(Menu menu, FunctionNode node, ActionContext actionContext, boolean root){
		int style = node.childs.size() > 0 ? SWT.CASCADE : SWT.PUSH;
		if("Seperator".equals(node.node.getThingName())){
			style = SWT.SEPARATOR;
		}
		MenuItem item = new MenuItem(menu, style);
		item.setData(node.node);
		String accelerator = node.node.getStringBlankAsNull("accelerator");
		item.setText(node.node.getMetadata().getLabel() + (accelerator != null ? "\t" + accelerator : ""));
		item.addListener(SWT.Selection, new MenuListener(node.node, actionContext));
		if(accelerator != null){
            item.setAccelerator(SwtUtils.getAccelerator(accelerator));
        }
		if(!root){
			Image icon = XWorkerTreeUtil.getIcon(node.node, menu.getParent(), actionContext, true);
			if(icon != null){
				item.setImage(icon);
			}
		}
		
		if(node.childs.size() > 0){
			Menu cmenu = new Menu(item);
			item.setMenu(cmenu);
			for(FunctionNode cnode : node.childs){
				init(cmenu, cnode, actionContext, false);
			}
		}
	}
	
	public static void attachToTree(Tree tree, ActionContext actionContext){
		List<FunctionNode> nodeList = initFunctionNodes(actionContext);
		for(FunctionNode node : nodeList){
			if("Seperator".equals(node.node.getThingName())){
				continue;
			}
			TreeItem item = new TreeItem(tree, SWT.NONE);
			item.setData(node.node);
			item.setText(node.node.getMetadata().getLabel());
			Image icon = XWorkerTreeUtil.getIcon(node.node, tree, actionContext, true);
			if(icon != null){
				item.setImage(icon);
			}
			
			for(FunctionNode cnode : node.childs){
				init(item, cnode, actionContext);
			}
		}
	}
	
	public static void attachToTreeItem(TreeItem item, ActionContext actionContext){
		List<FunctionNode> nodeList = initFunctionNodes(actionContext);
		for(FunctionNode node : nodeList){
			init(item, node, actionContext);
		}
	}
	
	public static void init(TreeItem parentItem, FunctionNode node, ActionContext actionContext){
		if("Seperator".equals(node.node.getThingName())){
			return;
		}
		
		TreeItem item = new TreeItem(parentItem, SWT.NONE);
		item.setData(node.node);
		item.setText(node.node.getMetadata().getLabel());
		Image icon = XWorkerTreeUtil.getIcon(node.node, parentItem.getParent(), actionContext, true);
		if(icon != null){
			item.setImage(icon);
		}
		
		for(FunctionNode cnode : node.childs){
			init(item, cnode, actionContext);
		}
	}
	
	public static FunctionNode getOrCreateRootNode(Thing node, Map<String, FunctionNode> context){
		FunctionNode fnode = context.get(node.getMetadata().getPath());
		if(fnode == null){
			String nodeId = node.getStringBlankAsNull("nodeId");
			if(nodeId != null){
				fnode = context.get(nodeId);
			}
			if(fnode == null){
				fnode = new FunctionNode(node);
				context.put(node.getMetadata().getPath(), fnode);
				
			
				if(fnode.nodeId != null){
					context.put(fnode.nodeId, fnode);
				}
			}
			
			for(Thing child : node.getChilds("FunctionsRelation")){
				fnode.addChildNode(child, context);
			}
		}
		
		return fnode;
	}
	
	public static FunctionNode getOrCreateNode(Thing node, Map<String, FunctionNode> context){
		FunctionNode fnode = context.get(node.getMetadata().getPath());
		if(fnode == null){
			fnode = new FunctionNode(node);
			context.put(node.getMetadata().getPath(), fnode);
			
			if(fnode.nodeId != null){
				context.put(fnode.nodeId, fnode);
			}
			
			for(Thing child : node.getChilds("FunctionsRelation")){
				fnode.addChildNode(child, context);
			}
		}
		
		return fnode;
	}
	
	static class MenuListener implements Listener{
		Thing thing;
		ActionContext actionContext;
		
		public MenuListener(Thing thing, ActionContext actionContext){
			this.thing = thing;
			this.actionContext = actionContext;
		}
		
		@Override
		public void handleEvent(Event event) {
			thing.doAction("runAction", new ActionContext(actionContext));
		}
		
	}
	
	static class FunctionNode implements Comparable<FunctionNode>{
		Thing node;
		String nodeId;
		String parentNodeIds;
		double sortWeight;
		boolean inited = false;
		List<FunctionNode> childs = new ArrayList<FunctionNode>();
		
		public FunctionNode(Thing node){
			this.node = node;
			this.nodeId = node.getStringBlankAsNull("nodeId");
			this.parentNodeIds = node.getStringBlankAsNull("parentNodeIds");
			sortWeight = node.getDouble("sortWeight", 0);
		}
		
		public void addChildNode(FunctionNode node, Map<String, FunctionNode> context){
			for(FunctionNode child : childs){
				if(child.node == node.node){
					return;
				}
			}
			
			childs.add(node);
		}		
		
		public void addChildNode(Thing node, Map<String, FunctionNode> context){
			for(FunctionNode child : childs){
				if(child.node == node){
					return;
				}
			}
			
			FunctionNode childNode = getOrCreateNode(node, context);
			if(node.getBoolean("onlyRegist")){
				return;
			}else{
				childs.add(childNode);
			}
			
			for(Thing cnodes : node.getChilds("FunctionsRelation")){
				childNode.addChildNode(cnodes, context);
			}	
		}

		@Override
		public int compareTo(FunctionNode o) {
			if(this.sortWeight < o.sortWeight){
				return -1;
			}else if(this.sortWeight == o.sortWeight){
				return 0;
			}else{
				return 1;
			}
		}		
	}
}
