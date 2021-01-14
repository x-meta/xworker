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
package xworker.swt.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Category;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.util.ThingGroup;
import xworker.util.UtilData;
import xworker.util.XWorkerUtils;

public class XWorkerTreeUtil {
	private static Logger logger = LoggerFactory.getLogger(XWorkerTreeUtil.class);
	private static Map<String, Map<String, Boolean>> expandCache = new HashMap<String, Map<String, Boolean>>();
	
	/**
	 * 针对showThingsOnTreeByGroup方法显示的树节点。
	 * 
	 * @param cachePath
	 * @param item
	 * @return
	 */
	public static boolean isExpaned(String cachePath, TreeItem item){
		if(cachePath == null){
			return false;
		}
		
		String data = String.valueOf(item.getData());
		Map<String, Boolean> cache = expandCache.get(cachePath);
		if(cache != null){
			Boolean exp = cache.get(data);
			if(exp != null){
				return exp;
			}
		}
		
		return false;
	}
	
	public static void expandAll(TreeItem item) {
		for(TreeItem childItem : item.getItems()) {
			expandAll(childItem);
		}
		
		item.setExpanded(true);
	}
	
	public static void refresh(TreeItem item, Thing thing, ActionContext actionContext) {
		initItem(item, thing, actionContext);
				
		for(TreeItem childItem : item.getItems()) {
			childItem.dispose();
		}
		
		for(Thing child : thing.getChilds()) {
			showThingOnTreeItem(item, child, actionContext);
		}
	}
	
	/**
	 * 记录一个TreeItem是否是打开的状态。
	 * 
	 * @param item
	 * @param caches
	 */
	public static void initExpandCaches(TreeItem item, Map<String, Boolean> caches) {
		Thing thing = (Thing) item.getData();
		if(thing != null) {
			caches.put(thing.getMetadata().getPath(), item.getExpanded());			
		}
		
		for(TreeItem childItem : item.getItems()) {
			initExpandCaches(childItem, caches);
		}
	}
	
	public static void initExpandCaches(Tree tree, Map<String, Boolean> caches) {
		for(TreeItem childItem : tree.getItems()) {
			initExpandCaches(childItem, caches);
		}
	}
	
	/**
	 * 刷新一个显示事物模型的树，其中parentThing是父节点，即它的子节点显示在Tree的第一层，如果没有设置为null。
	 * 
	 * @param tree
	 * @param parentThing
	 * @param actionContext
	 */
	public static void refresh(Tree tree, Thing parentThing, ActionContext actionContext) {
		//先记录以前
		Map<String, Boolean> caches = new HashMap<String, Boolean>();		
		initExpandCaches(tree, caches);
		
		//记录当前选中的Tree的选择
		String selectedThing = null;
		if(tree.getSelectionCount() > 0) {
			Thing thing = (Thing) tree.getSelection()[0].getData();
			selectedThing = thing.getMetadata().getPath();
		}
		
		if(parentThing != null) {
			tree.removeAll();
			for(Thing child : parentThing.getChilds()) {
				XWorkerTreeUtil.showThingOnTree(tree, child, actionContext);
			}
		}else {
			for(TreeItem item : tree.getItems()) {
				Thing thing = (Thing) item.getData();
				if(thing != null && thing.getMetadata().isRemoved()) {
					item.dispose();
				}else {
					refresh(item, thing, actionContext);
				}
			}
		}
		
		restoreStatus(tree, caches, selectedThing);
	}
	
	public static void restoreStatus(Tree tree, Map<String, Boolean> caches, String selectedThing) {
		for(TreeItem item : tree.getItems()) {
			restoreStatus(item, caches, selectedThing);
		}
	}
	
	public static void restoreStatus(TreeItem treeItem, Map<String, Boolean> caches, String selectedThing) {
		Thing thing = (Thing) treeItem.getData();
		if(thing == null) {
			return;
		}
		
		String path = thing.getMetadata().getPath();
		if(path.equals(selectedThing)) {
			treeItem.getParent().setSelection(treeItem);
		}
		
		if(UtilData.isTrue(caches.get(path))) {
			treeItem.setExpanded(true);			
			
			for(TreeItem item : treeItem.getItems()) {
				restoreStatus(item, caches, selectedThing);
			}
		}
		
		
	}
		
	public static void setExpaned(String cachePath, String treeData, boolean expanded){
		if(cachePath == null || treeData == null || "".equals(treeData)){
			return;
		}
		
		Map<String, Boolean> cache = expandCache.get(cachePath);
		if(cache == null){
			cache = new HashMap<String, Boolean>();
			expandCache.put(cachePath, cache);
		}
		
		cache.put(treeData, expanded);
	}
	
	public static void initTree(Tree tree, List<String> paths, String rootPath, ActionContext actionContext){
		for(TreeItem item : tree.getItems()){
			item.dispose();
		}
			
		Collections.sort(paths);
		Map<String, Path> pathCache = new HashMap<String, Path>();
		for(String path : paths){
			Object obj = World.getInstance().get(path);
			if(obj instanceof Thing){
				initPathCache(rootPath, (Thing) obj, null, pathCache);
			}else if(obj instanceof Category){
				initPathCache(rootPath, (Category) obj, null, pathCache);
			}
		}
		List<Path> rootPathList = new ArrayList<Path>();
		for(String key : pathCache.keySet()){
			Path p = pathCache.get(key);
			if(p.parent == null){
				rootPathList.add(p);
			}
		}
		Collections.sort(rootPathList);
		
		for(Path p : rootPathList){
			initTreeItem(p, tree, null, actionContext);
		}
	}
	
	private static void initPathCache(String rootPath, Thing thing, Path child, Map<String, Path> pathCache){		
		String thingPath = thing.getMetadata().getPath();
		if(!thingPath.startsWith(rootPath) || thingPath.equals(rootPath)){
			return;
		}
		
		Path path = pathCache.get(thingPath);
		if(path != null){
			if(child != null){
				addChildThingPath(path, child);
				child.parent = path;
			}
			return;
		}else{
			path = new Path();
			path.thing = thing;
			path.path = thingPath;
			pathCache.put(thingPath, path);
			
			if(child != null){				
				addChildThingPath(path, child);
				child.parent = path;
			}
			
			Thing parent = thing.getParent();
			if(parent != null){
				initPathCache(rootPath, parent, path, pathCache);
			}else{
				Category category = thing.getMetadata().getCategory();
				initPathCache(rootPath, category, path, pathCache);				
			}
		}		
	}
	
	private static void addChildThingPath(Path parent, Path child){
		if(parent.thing == null){
			parent.childs.add(child);
			return;
		}
		
		Thing parentThing = parent.thing;
		List<Thing> parentChilds = parentThing.getChilds();
		Thing childThing = child.thing;
		int childIndex = parentChilds.indexOf(childThing);
		
		for(Path childPath : parent.childs){
			if(childIndex < parentChilds.indexOf(childPath.thing)){
				parent.childs.add(parent.childs.indexOf(childPath), child);
				return;
			}
		}
		
		parent.childs.add(child);
	}
	
	public static void showThingOnTree(Tree tree, Thing thing, ActionContext actionContext){
		TreeItem item = new TreeItem(tree, SWT.None);
		XWorkerTreeUtil.initItem(item, thing, actionContext);
		item.setData(thing);
		
		for(Thing child : thing.getChilds()){
			showThingOnTreeItem(item, child, actionContext);
		}
	}
	
	public static void showThingOnTreeItem(TreeItem pitem, Thing thing, ActionContext actionContext){
		TreeItem item = new TreeItem(pitem, SWT.None);
		XWorkerTreeUtil.initItem(item, thing, actionContext);
		item.setData(thing);
		
		for(Thing child : thing.getChilds()){
			showThingOnTreeItem(item, child, actionContext);
		}
	}
	
	private static void initPathCache(String rootPath, Category category, Path child, Map<String, Path> pathCache){		
		String categoryPath = category.getName();
		if(!categoryPath.startsWith(rootPath) || categoryPath.equals(rootPath)){
			return;
		}
		
		Path path = pathCache.get(categoryPath);
		if(path != null){
			if(child != null){
				path.childs.add(child);
				child.parent = path;
			}
			
			return;
		}else{
			path = new Path();
			path.category = category;
			path.path = categoryPath;
			pathCache.put(categoryPath, path);
			
			if(child != null){
				path.childs.add(child);
			}
			
			Category parent = category.getParent();
			initPathCache(rootPath, parent, path, pathCache);
		}		
	}
	
	/**
	 * 返回目录的图标。
	 * 
	 * @param tree
	 * @param actionContext
	 * @return
	 */
	public static Image getIconFolder(Tree tree, ActionContext actionContext){
		actionContext.push().put("parent", tree);
    	try{
	        Image img = (Image) ResourceManager.createIamge("/icons/folder.png", actionContext);
	        return img;
    	}catch(Exception e){
    		logger.error("Create image error", e);
    	}finally{
    		actionContext.pop();
    	}
    	
    	return null;
	}
	
	/**
	 * 返回事物对应的图标。
	 * 
	 * @param thing
	 * @param tree
	 * @param actionContext
	 * @return
	 */
	public static Image getIcon(Thing thing, Control tree, ActionContext actionContext){
		//现在改为默认情况下获取自己的icon，以前是false，应该是在事物编辑器中为false
		return getIcon(thing, tree, actionContext, true);
	}
	
	/**
	 * 返回事物对应的图标。
	 * 
	 * @param thing
	 * @param tree
	 * @param actionContext
	 * @param findSelfIcon 是否先从thing上寻找icon属性
	 * @return
	 */
	public static Image getIcon(Thing thing, Control tree, ActionContext actionContext, boolean findSelfIcon){ 
		String icon = null;
		if(findSelfIcon){
			icon = thing.getStringBlankAsNull("icon");
		}
		if(icon == null){
			for(Thing descriptor : thing.getAllDescriptors()){
			    //println descriptor.getMetadata().getPath();
			    icon = descriptor.getString("icon");
			    //println icon;
			    if(icon != null && !"".equals(icon)){
			        break;
			    }
			}
		}
		
		if(icon != null && !"".equals(icon)){
			actionContext.push().put("parent", tree);
	    	try{
		        Image img = (Image) ResourceManager.createIamge(icon, actionContext);
		        return img;
	    	}catch(Exception e){
	    		logger.error("Create image error", e);
	    	}finally{
	    		actionContext.pop();
	    	}
		}
		
		return null;
	}
	
	/**
	 * 返回事物对应的字体。
	 * 
	 * @param thing
	 * @param tree
	 * @param actionContext
	 * @return
	 */
	public static Font getFont(Thing thing, Control tree, ActionContext actionContext){
		String th_font = thing.getStringBlankAsNull("th_font");
		if(th_font == null){
			for(Thing descriptor : thing.getAllDescriptors()){
			    //println descriptor.getMetadata().getPath();
				th_font = descriptor.getStringBlankAsNull("font");
			    //println icon;
			    if(th_font != null){
			        break;
			    }
			}			
		}
		
		if(th_font != null && !"".equals(th_font)){
	         Font font = ResourceManager.createFont(tree, th_font, actionContext);
	         return font;
	    }
		
		return null;
	}
	
	/**
	 * 返回事物对应的字体颜色。
	 * 
	 * @param thing
	 * @param tree
	 * @param actionContext
	 * @return
	 */
	public static Color getColor(Thing thing, Control tree, ActionContext actionContext){
		String th_nodeColor = thing.getStringBlankAsNull("th_nodeColor");
		if(th_nodeColor == null){
			for(Thing descriptor : thing.getAllDescriptors()){
			    //println descriptor.getMetadata().getPath();
				th_nodeColor = descriptor.getStringBlankAsNull("color");
			    //println icon;
			    if(th_nodeColor != null){
			        break;
			    }
			}
		}
		
		return getColor(th_nodeColor);
	}
	
	public static Color getColor(String th_nodeColor){
		if(th_nodeColor != null && !"".equals(th_nodeColor)){
			th_nodeColor = th_nodeColor.toUpperCase();
	          Color color = null;
	          int style = SwtUtils.getSWT(th_nodeColor);
	          if(style != SWT.NONE) {
	        	  color = Display.getCurrent().getSystemColor(style);
	          }
	          /**
	          if("BLUE".equals(th_nodeColor)){
	        	  color = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
	          }else if("CYAN".equals(th_nodeColor)){
              color = Display.getCurrent().getSystemColor(SWT.COLOR_CYAN);
	          }else if("DARK_BLUE".equals(th_nodeColor)){
              color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
	          }else if("DARK_CYAN".equals(th_nodeColor)){
              color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN);
	          }else if("DARK_GRAY".equals(th_nodeColor)){
              color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
	          }else if("DARK_GREEN".equals(th_nodeColor)){
              color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
	          }else if("DARK_MAGENTA".equals(th_nodeColor)){
              color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA);
	          }else if("DARK_RED".equals(th_nodeColor)){
              color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED);
	          }else if("DARK_YELLOW".equals(th_nodeColor)){
              color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW);
	          }else if("GRAY".equals(th_nodeColor)){
              color = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
	          }else if("GREEN".equals(th_nodeColor)){
              color = Display.getCurrent().getSystemColor(SWT.COLOR_GREEN);
	          }else if("MAGENTA".equals(th_nodeColor)){
              color = Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA);
	          }else if("RED".equals(th_nodeColor)){
              color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	          }else if("YELLOW".equals(th_nodeColor)){
              color = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);	                  
	          }          */
	          
	          return color;        
	     }
		
		return null;
	}
	
	private static void initTreeItem(Path path, Tree tree, TreeItem parentItem, ActionContext actionContext){
		TreeItem item = null;
		if(parentItem == null){
			item = new TreeItem(tree, SWT.NONE);			
		}else{
			item = new TreeItem(parentItem, SWT.NONE);
		}
		
		if(path.thing != null){
			item.setData(path.thing);
			
			Thing thing = path.thing;
			item.setText(path.thing.getMetadata().getLabel() + " (" + path.thing.getThingName() + ")");
			
			
			String th_font = thing.getString("th_font");
			String th_nodeColor = thing.getString("th_nodeColor");
			String icon = null;
			for(Thing descriptor : thing.getAllDescriptors()){
			    //println descriptor.getMetadata().getPath();
			    icon = descriptor.getString("icon");
			    //println icon;
			    if(icon != null && !"".equals(icon)){
			        break;
			    }
			}
			XWorkerTreeUtil.initItem(item, th_font, th_nodeColor, icon, actionContext);
		}else{
			item.setData(path.category);
			
			item.setText(path.category.getSimpleName());
			XWorkerTreeUtil.initItem(item, null, null, "/xworker/ide/images/package.gif", actionContext);
		}
		
		for(Path childPath : path.childs){
			initTreeItem(childPath, tree, item, actionContext);
		}
		
		item.setExpanded(true);
	}
	
	static class Path implements Comparable<Path>{
		String path;
		Thing thing;
		Category category;
		Path parent = null;
		List<Path> childs = new ArrayList<Path>();
		
		@Override
		public int compareTo(Path o) {
			return path.compareTo(o.path);
		}
	}
	
	/**
	 * 根据事物显示一个树节点的文字和图片等。
	 * 
	 * @param treeItem
	 * @param thing
	 * @param actionContext
	 */
	public static void initItem(TreeItem treeItem, Thing thing, ActionContext actionContext){
		treeItem.setText(thing.getMetadata().getLabel());// + " (" + thing.getThingName() + ")");
		
		Font font = getFont(thing, treeItem.getParent(), actionContext);
		if(font != null){
			treeItem.setFont(font);
		}
		
		Color color = getColor(thing, treeItem.getParent(), actionContext);
		if(color != null){
			treeItem.setForeground(color);
		}
		
		Image icon = getIcon(thing, treeItem.getParent(), actionContext);
		if(icon != null){
			treeItem.setImage(icon);
		}
	}
	
	public static void initItem(TreeItem treeItem, String th_font, String th_nodeColor, String icon, ActionContext actionContext){
	     if(th_font != null && !"".equals(th_font)){
	         Font font = ResourceManager.createFont(treeItem.getParent(), th_font, actionContext);
	         if(font != null){
	             treeItem.setFont(font);
	         }
	     }
	     if(th_nodeColor != null && !"".equals(th_nodeColor)){
	          Color color = getColor(th_nodeColor);
	          
	          if(color != null){
	              treeItem.setForeground(color);
	          }            
	    }
	    
	    //println "icon=" + icon;
	    if(icon != null && !"".equals(icon)){	    	
	    	actionContext.push().put("parent", treeItem.getParent());
	    	try{
		        Image img = (Image) ResourceManager.createIamge(icon, actionContext);
		        if(img != null){
		            treeItem.setImage(img);
		        }
	    	}catch(Exception e){
	    		logger.error("Create image error", e);
	    	}finally{
	    		actionContext.pop();
	    	}
	    }else{
	    	System.out.println();
	    }
	}
	
	public static void showGroupsOnTree(String cachePath, List<xworker.util.ThingGroup> groups, Object treeOrTreeItem, ActionContext actionContext){
		Collections.sort(groups);
		/*
		Map<String, ThingGroup> folderGroupCache = new HashMap<String, ThingGroup>();
		ThingGroup rootGroup = new ThingGroup(null, null);
		folderGroupCache.put("", rootGroup);
		for(xworker.util.ThingGroup group : groups){
			if(group.getThing() != null && "xworker.example.action.ActionExamples/@IteratorFileLine".equals(group.getThing().getMetadata().getPath())) {
				System.out.println("have ");
			}
			if("".equals(group.getGroup())){
				rootGroup.getChilds().add(group);
				continue;
			}
						
			String groupPaths[] = group.getGroup().split("[,]");
			
			for(int n=0; n<groupPaths.length; n++){
				String path = "";
				ThingGroup parentGroup = rootGroup;
				String gs[] = groupPaths[n].split("[.]");
				for(int i=0; i<gs.length; i++){
					if(i == 0){
						path = gs[i];
					}else{
						path = path + "." + gs[i];
					}
					String pathKey = path;
					int index = pathKey.indexOf("|"); 
					if(index != -1) {
						pathKey = pathKey.substring(index + 1, pathKey.length());
					}
					
					ThingGroup cacheGroup = folderGroupCache.get(pathKey);
					if(cacheGroup == null){
						cacheGroup = new ThingGroup(null, gs[i]);
						parentGroup.getChilds().add(cacheGroup);
						folderGroupCache.put(pathKey, cacheGroup);
					}
					
					parentGroup = cacheGroup;
					if(i == gs.length - 1){
						//最后一个节点加上事物
						cacheGroup.getChilds().add(group);
					}
				}
			}
		}
		
		rootGroup.sort();
		*/
		//Collections.sort(rootGroup.getChilds());
		for( xworker.util.ThingGroup group : groups){
			showThingGroupOnTreeOrItem(cachePath, group, treeOrTreeItem, "", actionContext);
		}
	}
	
	/**
	 * <p>根据事物的分组显示到树或树节点上，注意该方法并不清空树的子节点以及子节点的子节点，需要时请自己清空。</p>
	 * 
	 * 注意此时TreeItem的data是字符串分组或事物，其中getData("path")如果是分组则是分组字符串，如果是事物，那么是thing:加事物的路径。
	 * 
	 * @param cachePath
	 * @param things
	 * @param treeOrTreeItem
	 */
	public static void showThingsOnTreeByGroup(String cachePath, List<Thing> things, Object treeOrTreeItem, ActionContext actionContext){
		//获取分组并排序
		List<ThingGroup> groups = XWorkerUtils.getThingGroups(things);
		/*
		List<xworker.util.ThingGroup> groups = new ArrayList<xworker.util.ThingGroup>();
		for(Thing thing : things){
			String group = thing.getMetadata().getGroup();//thing.getStringBlankAsNull("group");
			if(group == null){
				group = "";
			}
			
			if("xworker.example.action.ActionExamples/@IteratorFileLine".equals(thing.getMetadata().getPath())) {
				System.out.println("have ");
			}
			
			//一个分组字符串可以是多个分组，使用,号隔开
			for(String g : group.split("[,]")){
				groups.add(new ThingGroup(thing, g));
			}
			
			
		}		
		*/
		showGroupsOnTree(cachePath, groups, treeOrTreeItem, actionContext);
	}
	
	/**
	 * <p>根据事物的分组显示到树或树节点上，注意该方法并不清空树的子节点以及子节点的子节点，需要时请自己清空。</p>
	 * 
	 * 注意此时TreeItem的data是字符串分组或事物，其中getData("path")如果是分组则是分组字符串，如果是事物，那么是thing:加事物的路径。
	 * 
	 * @param things
	 * @param treeOrTreeItem
	 */
	public static void showThingsOnTreeByGroup(List<Thing> things, Object treeOrTreeItem, ActionContext actionContext){
		showThingsOnTreeByGroup(null, things, treeOrTreeItem, actionContext);
	}
			
	public static void showThingGroupOnTreeOrItem(String cachePath, xworker.util.ThingGroup group, Object treeOrItem, String parentGroup, ActionContext actionContext){
		TreeItem item = null;
		if(group.getThing() != null) {
			item = showThingOnTreeOrItem(cachePath, group.getThing(), treeOrItem, actionContext);
		}else {
			item = showGroupOnTreeOrITem(cachePath, group, parentGroup, treeOrItem, actionContext);
		}
		
		String path = parentGroup.equals("") ? group.getOldGroup() : parentGroup + "." + group.getOldGroup();
		Collections.sort(group.getChilds());
		for(xworker.util.ThingGroup childGroup : group.getChilds()){
			showThingGroupOnTreeOrItem(cachePath, childGroup, item, path, actionContext);
		}
		
		if(isExpaned(cachePath, item)){
			item.setExpanded(true);
		}
	}
	
	private static TreeItem showGroupOnTreeOrITem(String cachePath, xworker.util.ThingGroup group, String parentGroup, Object treeOrItem, ActionContext actionContext){
		TreeItem item = null;
		if(treeOrItem instanceof Tree){
			item = new TreeItem((Tree) treeOrItem, SWT.NONE);
			item.setImage(getIconFolder((Tree) treeOrItem, actionContext));
		}else{
			item = new TreeItem((TreeItem) treeOrItem, SWT.NONE);
			item.setImage(getIconFolder(((TreeItem) treeOrItem).getParent(), actionContext));
		}
		
		item.setText(group.getGroup());
		if(parentGroup.equals("")){
			item.setData(group.getOldGroup());
		}else{
			item.setData(parentGroup + "." + group.getOldGroup());
		}
		
		return item;
	}
	
	private static TreeItem showThingOnTreeOrItem(String cachePath, Thing thing, Object treeOrItem, ActionContext actionContext){
		TreeItem item = null;
		if(treeOrItem instanceof Tree){
			item = new TreeItem((Tree) treeOrItem, SWT.NONE);			
		}else{
			item = new TreeItem((TreeItem) treeOrItem, SWT.NONE);
		}
		
		initItem(item, thing, actionContext);
		item.setData(thing);
		item.setData("path", "thing:" + thing.getMetadata().getPath());
		return item;
	}

}