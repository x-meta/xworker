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
package xworker.app.model.tree.swt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.swt.events.SwtListener;

public class TreeModelTreeListenerCreator {
	private static Logger log = LoggerFactory.getLogger(TreeModelTreeListenerCreator.class);
	
	private static Map<String, Map<String, Boolean>> expandStatusCache = new HashMap<String, Map<String, Boolean>>();
	
	private static TreeListener treeListener = new TreeListener(){
		public void treeCollapsed(TreeEvent event) {
			Thing treeModel = (Thing) event.item.getData("_treeModel");
			treeModel = (Thing) treeModel.get("modelThing");
			if(treeModel != null){
				String cachedId = treeModel.getStringBlankAsNull("cacheExpandStatusId");
				String treeNodeId = (String) event.item.getData("_treeNodeId");
				if(cachedId != null){
					Map<String, Boolean> cache = expandStatusCache.get(cachedId);
					if(cache != null){
						cache.remove(treeNodeId);
					}
				}
			}
		}

		public void treeExpanded(TreeEvent event) {
			Thing treeModel = (Thing) event.item.getData("_treeModel");
			treeModel = (Thing) treeModel.get("modelThing");
			if(treeModel != null){
				String cachedId = treeModel.getStringBlankAsNull("cacheExpandStatusId");
				String treeNodeId = (String) event.item.getData("_treeNodeId");
				if(cachedId != null){					
					Map<String, Boolean> cache = expandStatusCache.get(cachedId);
					if(cache == null){
						cache = new HashMap<String, Boolean>();
						expandStatusCache.put(cachedId, cache);
					}
					
					cache.put(String.valueOf(treeNodeId), true);
				}
			}
		}		
	};
	
	public static void setExpandedCache(Thing treeModel, Object treeNodeId){
		treeModel = (Thing) treeModel.get("modelThing");
		if(treeModel != null){
			String cachedId = treeModel.getStringBlankAsNull("cacheExpandStatusId");
			if(cachedId != null){					
				Map<String, Boolean> cache = expandStatusCache.get(cachedId);
				if(cache == null){
					cache = new HashMap<String, Boolean>();
					expandStatusCache.put(cachedId, cache);
				}
				
				cache.put(String.valueOf(treeNodeId), true);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void checkExpand(TreeItem treeItem, Thing treeModel_, Object parentNode, String id, boolean hasChilds,  Map<String, Object> treeNodes, ActionContext actionContext) throws OgnlException{
		Thing treeModel = (Thing) treeModel_.get("modelThing");
		String cachedId = treeModel.getStringBlankAsNull("cacheExpandStatusId");
		if(cachedId != null){					
			Map<String, Boolean> cache = expandStatusCache.get(cachedId);
			if(cache != null){
				if(cache.get(id) != null){
					if(!hasChilds){
						//可能是动态树，需要动态获取下级节点						
						Object childs = treeModel.doAction("getChilds", actionContext, UtilMap.toMap(new Object[]{"id", id, "parentNode", parentNode, "parentTreeItem", treeItem}));
						if(childs != null){
							 List<Object> rs = new ArrayList<Object>();
					         for(Object root : ((Iterable<Object>) childs)){
					              Map<String, Object> nodeMap = UtilMap.toMap(new Object[]{"id", "" + OgnlUtil.getValue("id", root), "node", root});
					              treeModel.doAction("initNodeMap", actionContext, UtilMap.toMap(new Object[]{"nodeMap", nodeMap, "node",root}));
					              rs.add(nodeMap);
					          }
					            					           
							 initTreeItem(treeModel_, null, treeItem, treeItem.getItems().length, actionContext, treeNodes, rs);          
						}
					}
					treeItem.setExpanded(true);
				}
			}
		}
	}
	
    @SuppressWarnings("unchecked")
	public static void init(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
        Tree parent = (Tree) actionContext.get("parent");
    	
        //节点实例缓存，当模型变更或者更新时，可以找到对应的SWT树节点
        Map<String, TreeItem> treeNodes = (Map<String, TreeItem>) self.get("treeNodes");
        if(treeNodes == null){
            treeNodes = new HashMap<String, TreeItem>();
            self.put("treeNodes", treeNodes);
        }else{
            //清空树节点
            treeNodes.clear();
            for(TreeItem item : parent.getItems()){
                item.dispose();
            }
        }
        
        Thing treeModel = (Thing) actionContext.get("treeModel");
        self.put("treeModel", treeModel);
        self.put("tree", parent);
        self.put("parent", parent);
        parent.setData("_treeModel", treeModel);
        parent.setData("treeModel", treeModel);
        
        //创建树节点
        //log.info(treeModel.modelThing.name + ",rootVisibale=" + treeModel.modelThing.rootVisiable);
        if(!((Thing) treeModel.get("modelThing")).getBoolean("rootVisiable")){
            //如果不显示根节点
            Object roots = treeModel.doAction("getRoots", actionContext);
            self.doAction("initItems", actionContext, 
                        UtilMap.toMap(new Object[]{"parent",parent, "treeNodes",roots, "treeNode",null, "treeModel",treeModel, "index",-1}));
        }else{
            Object root = treeModel.doAction("getRoot", actionContext);
            self.doAction("initItems", actionContext, 
                UtilMap.toMap(new Object[]{"parent",parent, "treeNode",root, "treeModel",treeModel, "index",-1, "treeNodes", null}));
        }
        
        //为SWT控件添加销毁事件
        if(parent instanceof Tree){
            //添加树销毁的监听，从而释放监听到该Tree
        	SwtListener listener = new SwtListener(world.getThing("xworker.app.model.tree.swt.TreeDisposeListener"), actionContext, true);
            parent.addListener(SWT.Dispose, listener);
            
            //添加树的选择事件，以便根据Model的设定做出相应的操作
            SwtListener selectionListener = new SwtListener(world.getThing("xworker.app.model.tree.swt.TreeModelSelection/@run"), actionContext, true);
            parent.addListener(SWT.Selection, selectionListener);
            
            //缓存节点
           if(((Thing) treeModel.get("modelThing")).getStringBlankAsNull("cacheExpandStatusId") != null){
        	   parent.addTreeListener(treeListener);
           }
        }else{
        	SwtListener listener = new SwtListener(world.getThing("xworker.app.model.tree.swt.TreeItemDisposeListener"), actionContext, true);
            parent.addListener(SWT.Dispose, listener);
        }
    }

    @SuppressWarnings("unchecked")
	public static void initItems(final ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        Object parent = actionContext.get("parent");
    	
        //节点缓存和索引
        Map<String, Object> treeNodes = (Map<String, Object>) self.get("treeNodes");
        int index = -1;
        try{
        	index = (Integer) actionContext.get("index");
        }catch(Exception e){
        	
        }
        if(index == -1){
        	if(parent instanceof Tree){
        		index = ((Tree) parent).getItems().length;
        	}else{
        		index = ((TreeItem) parent).getItems().length;
        	}
        }
        
        //要加入的节点
        Object treeNode = actionContext.get("treeNode");
        final Iterable<Object> treeNodeList = (Iterable<Object>) actionContext.get("treeNodes");
        //log.info("init item treeNode=" + treeNode);
        if(treeNode == null && treeNodeList == null ){
            log.info("TreeModelTreeListener initItems: treeNode is null, treeItem not inited");
        }else{
        	Thing treeModel = (Thing) actionContext.get("treeModel");
            if(treeModel.getBoolean("loadBackground")){
                final Thing _treeModel = (Thing) actionContext.get("treeModel");
                final Tree _parent = (Tree) actionContext.get("parent");
                final ActionContext acContext = new ActionContext();
                final int index_ = index;
                acContext.put("parent", _parent);
                Runnable runnable = new Runnable(){
                	public void run(){
                		try {
							initTreeItem(_treeModel, actionContext.get("_treeNode"), _parent, index_, acContext, (Map<String, Object>) actionContext.get("_treeNodes"), treeNodeList);
						} catch (OgnlException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                };                
                
                new Thread(runnable).start();
                
            }else{
                initTreeItem(treeModel, treeNode, parent, index, actionContext, treeNodes, treeNodeList);
            }
        }
    }
    
    public static Object createResource(String value, String descriptor, String attributeName, ActionContext actionContext){
        if(value == null || "".equals(value)){
            return null;
        }
        
        Thing resThing = new Thing(descriptor);
        resThing.set(attributeName, value);
        return resThing.doAction("create", actionContext);
    }
    
    //初始化树节点
    @SuppressWarnings("unchecked")
	public static void  initTreeItem(Thing treeModel, Object treeNodeForAdd, Object treeItem, int index, ActionContext actionContext, Map<String, Object> treeNodes, Iterable<Object> treeNodeList) throws OgnlException{   
        List<Object> nodeList = new ArrayList<Object>();
        if(treeNodeForAdd != null){
            nodeList.add(treeNodeForAdd);
        } 
        if(treeNodeList != null){
        	for(Object node : treeNodeList){
        		nodeList.add(node);
        	}
        }
        //创建树节点    
        for(Object treeNodeMap : nodeList){
            Object treeNode = OgnlUtil.getValue("node", treeNodeMap);
            Object id = OgnlUtil.getValue("id", treeNodeMap);
            TreeItem item = null;
            if(treeItem instanceof Tree){
            	item = new TreeItem((Tree) treeItem, SWT.NONE, index);
            }else{
            	item = new TreeItem((TreeItem) treeItem, SWT.NONE, index);
            }
            
            String text = String.valueOf(OgnlUtil.getValue("text", treeNode));
            if(text == null || "".equals(text)){
                if(treeNode instanceof Thing){
                    item.setText(((Thing) treeNode).getMetadata().getLabel());
                }
            }else{
                item.setText(text);
            }
            item.setData("_treeNode", treeNode);
            item.setData("treeNode", treeNode);
            item.setData("treeModel", treeModel);
            item.setData("_treeModel", treeModel);
            item.setData("_treeNodeId", id);
            item.setData("_modelId", OgnlUtil.getValue("modelId", treeNodeMap));
            item.setData(treeNode);
    
            Object nodeid = getValue("id", treeNode);
            if(nodeid != null){
                treeNodes.put(nodeid.toString(), item);
            }
            
            //如果有静态的子节点，初始化子节点
            Iterable<Object> childs = (Iterable<Object>) OgnlUtil.getValue("childs", treeNodeMap);
            if(childs != null){            
                initTreeItem(treeModel, null, item, item.getItems().length, actionContext, treeNodes, childs);            
            }        
            
            //应用样式
            String cls = (String) getValue("cls", treeNode);
            if(cls != null && !"".equals(cls)){
                Thing StyleManager = (Thing) actionContext.get("StyleManager");
                if(StyleManager != null){
                    StyleManager.doAction("apply", actionContext, UtilMap.toMap(new Object[]{"widget",item, "widgetThing",treeNode}));
                }
            }
            
            //设置其他属性
            Object expanded = getValue("expanded", treeNode);
            if("true".equals(expanded) || (expanded != null && new Boolean(true).equals(expanded))){
                item.setExpanded(true);
            }
            
            Object checked = getValue("expanded", treeNode);
            if("true".equals(checked) || (checked != null && new Boolean(true).equals(checked))){
                item.setChecked(true);
            }
            //图标
            String icon = String.valueOf(getValue("icon", treeNode));
            if(icon != null && !"".equals(icon)){
            	try{
            		actionContext.push().put("parent", item.getParent());
	                Image image = (Image) createResource(icon, 
	                        "xworker.swt.graphics.Image", "imageFile", actionContext);
	                if(image != null && !image.isDisposed()){
	                    item.setImage(image);
	                }
            	}finally{
            		actionContext.pop();
            	}
            }
            index++;
            
            //检查节点打开状态
            checkExpand(item, treeModel, treeNode,id.toString(), childs != null, treeNodes, actionContext);
        }
    }
    
    public static Object getValue(String path, Object object){
    	try{
    		return  OgnlUtil.getValue(path, object);
    	}catch(Exception e){
    		return null;
    	}
    }

    @SuppressWarnings("unchecked")
	public static void onNodeUpdate(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        
        //log.info("on tree node updated");
        //树节点缓存列表
    	Map<String, Object> treeNodes = (Map<String, Object>) self.get("treeNodes");
    	boolean updateChilds = false;
    	if((Boolean) actionContext.get("includeChilds")){
    		updateChilds = true;
    	}        
        
        //单个节点
        Object treeNode = actionContext.get("node");
        if(treeNode != null){
            updateTreeNode((Thing) self.get("treeModel"), treeNode, treeNodes, actionContext, updateChilds);
        }
        
        //节点列表
        Iterable<Object> nodes = (Iterable<Object>) actionContext.get("nodes");
        if(nodes != null){
            for(Object node : nodes){
                updateTreeNode((Thing) self.get("treeModel"), node, treeNodes, actionContext, updateChilds);
            }
        }
        
        //整棵树
        Thing treeModel = (Thing) actionContext.get("treeModel");
        if(treeNode == null && nodes == null){
            if(!((Thing) treeModel.get("modelThing")).getBoolean("rootVisiable")){
                //如果不显示根节点，刷新第一级列表
                Iterable<Object> roots = (Iterable<Object>) treeModel.doAction("getRoots", actionContext);
                updateChildNodes(self.get("tree"), roots, (Thing) self.get("treeModel"), treeNodes, updateChilds, actionContext);
            }else{
                Object root = treeModel.doAction("getRoot", actionContext);
                if(treeNodes.get(OgnlUtil.getValue("id", root)) == null){
                    //根节点都没有，重新初始化
                	Tree tree = (Tree) self.get("tree");
                    for(TreeItem item : tree.getItems()){
                        item.dispose();
                    }
                    self.doAction("initItems", actionContext, 
                        UtilMap.toMap(new Object[]{"parent", actionContext.get("parent"), "treeNode",root, "treeModel",treeModel, "index",-1, "treeNodes", null}));
                }else{
                    updateTreeNode((Thing) self.get("treeModel"), root, treeNodes, actionContext, updateChilds);
                }
            }
        }
    }

    public static TreeItem[] getTreeItems(Object item){
    	if(item instanceof Tree){
    		return ((Tree) item).getItems();
    	}else {
    		return ((TreeItem) item).getItems();
    	}
    }
    
    //更新子节点列表
    public static void updateChildNodes(Object treeItem, Iterable<Object> childNodes, Thing treeModel, Map<String, Object> treeNodes , boolean updateChilds, ActionContext actionContext) throws OgnlException{
       //把子节点做删除的标记，在更新中如果有那么移除删除标记，最后剩下的就是需要删除的了
        for(TreeItem childItem : getTreeItems(treeItem)){
            childItem.setData("delete", "true");
        }
        
        //更新有的子节点
        for(Object childNode : childNodes){
            updateTreeNode(treeModel, childNode, treeNodes, actionContext, updateChilds);
        }
        
        //删除已删除的子节点
        for(TreeItem childItem : getTreeItems(treeItem)){
            if(childItem.getData("delete") == "true"){
                Object node = childItem.getData();
                Object nodeId = OgnlUtil.getValue("id", node);
                if(nodeId != null){
                    treeNodes.remove(String.valueOf(nodeId));
                }
                childItem.dispose();
            }
        }
        
        //添加新增的子节点
        int index = 0;
        for(Object childNode : childNodes){
            boolean have = false;
            for(TreeItem childItem : getTreeItems(treeItem)){
                Object node = childItem.getData();
                Object nodeId = OgnlUtil.getValue("id", node);
                if(nodeId.equals(OgnlUtil.getValue("id", childNode))){
                    have = true;
                    break;
                }
            }
            
            if(!have){
                //是新增的子节点，创建
                Thing self = (Thing) actionContext.get("self");
                self.doAction("initItems", actionContext, UtilMap.toMap(new Object[]{"parent", treeItem, "treeModel",treeModel,
                        "treeNode", childNode, "index", index}));                      
            }
            
            index++;
        }
    }

    //更新树节点，只更新已创建树节点的节点，同时也更新节点的子节点
    @SuppressWarnings("unchecked")
	public static void  updateTreeNode(Thing treeModel, Object treeNodeMap, Map<String, Object> treeNodes, ActionContext actionContext, boolean updateChilds) throws OgnlException{
        Object treeNode = OgnlUtil.getValue("node", treeNodeMap);
        Object id = OgnlUtil.getValue("id", treeNodeMap);
        if(id == null){
            return;
        }
        
        TreeItem treeItem = (TreeItem) treeNodes.get(id.toString());
        if(treeItem != null && !treeItem.isDisposed()){ //树节点的实例不存在不用更新，因为可能还没有创建，如果动态子节点时
            treeItem.setText(String.valueOf(OgnlUtil.getValue("text",treeNode)));
            treeItem.setData("_treeNode", treeNode);
            treeItem.setData(treeNode);
            treeItem.setData("delete", null);
    
            Object childNodes = null;
            try{
                if(treeNode instanceof Thing){
                    childNodes = ((Thing) treeNode).getChilds();
                }else{
                    childNodes = treeModel.doAction("getChilds", actionContext, "id", id);// OgnlUtil.getValue("childs",treeNode);
                }
            }catch(Exception e){
            }
            
            if(childNodes != null && updateChilds){
                updateChildNodes(treeItem, (Iterable<Object>) childNodes, treeModel, treeNodes ,updateChilds, actionContext);
            }
            
            //应用样式
            String cls = String.valueOf(OgnlUtil.getValue("cls", treeNode));
            if(cls != null && !"".equals(cls)){
                Thing StyleManager = (Thing) actionContext.get("StyleManager");
                if(StyleManager != null){
                    StyleManager.doAction("apply", actionContext, UtilMap.toMap(new Object[]{"widget",treeItem, "widgetThing",treeNode}));
                }
            }
            
            //设置其他属性
            Object expanded = OgnlUtil.getValue("expanded", treeNode);
            if("true".equals(expanded) || (expanded != null && new Boolean(true).equals(expanded))){
            	treeItem.setExpanded(true);
            }
            
            Object checked = OgnlUtil.getValue("expanded", treeNode);
            if("true".equals(checked) || (checked != null && new Boolean(true).equals(checked))){
            	treeItem.setChecked(true);
            }
            //图标
            String icon = String.valueOf(OgnlUtil.getValue("icon", treeNode));
            if(icon != null && !"".equals(icon)){
            	try{
            		actionContext.push().put("parent", treeItem.getParent());
	                Image image = (Image) createResource(icon, 
	                        "xworker.swt.graphics.Image", "imageFile", actionContext);
	                if(image != null){
	                	treeItem.setImage(image);
	                }
            	}finally{
            		actionContext.pop();
            	}
            }
        }
    }
    
    @SuppressWarnings("unchecked")
	public static void onNodeRemoved(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
    	
    	Map<String, Object> treeNodes = (Map<String, Object>) self.get("treeNodes");
        
        Object treeNode = actionContext.get("node");
        if(treeNode != null){
            deleteNode(treeNode, treeNodes);
        }
        
        Iterable<Object> nodes = (Iterable<Object>) actionContext.get("nodes");
        if(nodes != null){
            for(Object node : nodes){
                deleteNode(node, treeNodes);
            }
        }
    }
    
    public static void deleteNode(Object node, Map<String, Object> treeNodes) throws OgnlException{
        Object nodeId = OgnlUtil.getValue("id", node);
        if(nodeId != null){
            TreeItem treeItem = (TreeItem) treeNodes.get(nodeId.toString());
            treeNodes.remove(nodeId.toString());
            treeItem.dispose();
        }   
    }

    @SuppressWarnings("unchecked")
	public static void onNodeInserted(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        
        //查找要插入的父节点
        Object treeItemParent = self.get("parent");
        Object parentId = actionContext.get("parentId");
        //log.info("parentId=" + parentId);
        if(parentId != null){
            if(self.get("treeNodes") == null){
                return;
            }
            
            Map<String, Object> treeNodes = (Map<String, Object>) self.get("treeNodes");
            Object p = treeNodes.get(parentId.toString());
            if(p != null){
                treeItemParent = p;
            }else{
                treeItemParent = null;
            }
        }
        
        //log.info("self.treeNodes=" + self.treeNodes);
        //log.info("parentId=" + parentId + ",treeItemParent=" + treeItemParent);
        //如果父节点位null，不需要插入树的节点
        //log.info("treeItemParent=" + treeItemParent);
        if(treeItemParent == null){
            return;
        }
        
        //插入树节点
        Object treeNode = actionContext.get("node");
        //log.info("treeNode=" + treeNode);
        if(treeNode != null){
            self.doAction("initItems", actionContext, 
            		UtilMap.toMap(new Object[]{"parent",treeItemParent, "treeModel", ((TreeItem) treeItemParent).getData("_treeModel"),
                    "treeNode", treeNode, "treeNodes", null}));
        }
        
        Object nodes = actionContext.get("nodes");
        //log.info("treeNodes=" + nodes);
        if(nodes != null){
            self.doAction("initItems", actionContext, 
                        UtilMap.toMap(new Object[]{"parent",treeItemParent, "treeNodes",nodes, "treeNode",null, "treeModel", ((TreeItem) treeItemParent).getData("_treeModel")}));
        }
        
        if(treeItemParent instanceof TreeItem){
            ((TreeItem) treeItemParent).setExpanded(true);
        }
    }

}