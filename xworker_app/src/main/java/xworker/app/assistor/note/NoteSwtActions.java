package xworker.app.assistor.note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;

public class NoteSwtActions {
	public static void init(ActionContext actionContext){
		//获取所有笔记
		List<DataObject> notes = DataObjectUtil.query("xworker.app.assistor.note.dataobjects.Note", new HashMap<String, Object>(), actionContext);
		
		for(DataObject note : notes){
			updateItem(note, actionContext);
		}
		
		CTabFolder tabFolder = (CTabFolder) actionContext.get("tabFolder");
		if(tabFolder.getItemCount() > 0){
			tabFolder.setSelection(0);
		}
	}
	
	/**
	 * 在树上显示或替换节点。
	 * 
	 * @param note
	 * @param actionContext
	 */
	@SuppressWarnings("unchecked")
	public static void updateItem(DataObject note, ActionContext actionContext){
		String label = note.getString("label");
		if(label == null || "".equals(label)){
			label = "默认";
		}
		
		String agroup = note.getString("agroup");
		if(agroup == null){
			agroup = "";
		}
		
		String[] labels = label.split("[,]");
		String[] groups = agroup.split("[,]");
		
		List<TreeItem> items = (List<TreeItem>) note.get("__treeItems__");
		for(String lb : labels){
			for(String gr : groups){
				boolean have = false;
				
				if(items != null){
					for(TreeItem item : items){
						String l = (String) item.getData("label");
						String g = (String) item.getData("group");
						
						if(lb.equals(l) && g.equals(gr)){
							have = true;
							item.setText(note.getString("name"));
							break;
						}									
					}
				}
				
				if(!have){
					createItem(note, lb, gr, actionContext);
				}					
			}
		}			
		
		//移除没有的条目
		if(items != null){
			for(TreeItem item : items){
				String l = (String) item.getData("label");
				String g = (String) item.getData("group");
			
				boolean have = false;
				for(String lb : labels){
					for(String gr : groups){
						if(lb.equals(l) && g.equals(gr)){
							have = true;
							break;
						}
					}
					
					if(have){
						break;
					}
				}
				
				if(!have){
					disposeItem(item);
				}
			}
			
			for(int i=0;i<items.size(); i++){
				if(items.get(i).isDisposed()){
					items.remove(i);
					i--;
				}
			}
		}
	}
	
	public static void disposeItem(TreeItem item){
		TreeItem parentItem = item.getParentItem();
		
		if(parentItem == null && item.getParent().getItemCount() == 1){
			CTabItem tabItem = (CTabItem) item.getParent().getData("tabItem");
			tabItem.dispose();
			return;
		}
		
		item.dispose();
		if(parentItem != null && parentItem.getItemCount() == 0){
			disposeItem(parentItem);
		}
	}
	
	public static void createItem(DataObject note, String label, String agroup, ActionContext actionContext){
		CTabFolder tab = (CTabFolder) actionContext.get("tabFolder");
		for(CTabItem tabItem : tab.getItems()){
			String l = (String) tabItem.getText();
			if(l != null && l.equals(label)){
				createItemAtTab(note, agroup, tabItem, actionContext);
				return;
			}
		}
		
		ActionContext ac = new ActionContext();
		ac.put("parent", tab);
		ac.put("parentContext", actionContext);
		Thing tabItemThing = World.getInstance().getThing("xworker.app.assistor.note.swt.Note/@composite/@tabFolder/@PreparedWidgets/@defaultTabItem");
		CTabItem tabItem = (CTabItem) tabItemThing.doAction("create", ac);
		if(label != null && !"".equals(label)){
			tabItem.setText(label);
		}
		Image image = (Image) actionContext.get("tabImage");
		if(image != null){
			tabItem.setImage(image);
		}
		tabItem.setData("actionContext", ac);
		createItemAtTab(note, agroup, tabItem, actionContext);
	}
	
	public static void createItemAtTab(DataObject note, String agroup, CTabItem tabItem, ActionContext actionContext){
		ActionContext ac = (ActionContext) tabItem.getData("actionContext");
		Tree tree = (Tree) ac.get("tree");
		String[] group = agroup.split("[.]");
		for(TreeItem item : tree.getItems()){
			if(createToTreeItem(item, note, tabItem.getText(), agroup, group, 0, actionContext)){
				return;
			}
		}
		
		if(!"".equals(agroup)){
			TreeItem nodeItem = new TreeItem(tree, SWT.None);
			nodeItem.setData("type", "node");
			nodeItem.setText(group[0]);			
			nodeItem.setData("group", agroup);
			nodeItem.setData("label", tabItem.getText());
			setImage(nodeItem, "nodeImage", actionContext);
			createToTreeItem(nodeItem, note, tabItem.getText(), agroup, group, 0, actionContext);
		}else{
			TreeItem newItem = new TreeItem(tree, SWT.None);
			newItem.setData("type", "note");
			newItem.setData(note);
			newItem.setText(note.getString("name"));
			newItem.setData("label", tabItem.getText());
			newItem.setData("group", agroup);
			setImage(newItem, "noteImage", actionContext);
			addTreeItemToNode(newItem, note);
		}
	}
	
	public static void setImage(TreeItem item, String imageName, ActionContext actionContext){
		Image image = (Image) actionContext.get(imageName);
		if(image != null){
			item.setImage(image);
		}
	}
	
	public static boolean createToTreeItem(TreeItem item, DataObject note, String label, String agroup, String[] group, int groupIndex, ActionContext actionContext){
		if("node".equals(item.getData("type")) && group[groupIndex].equals(item.getText())){
			if(groupIndex == group.length - 1){
				TreeItem newItem = new TreeItem(item, SWT.None);
				newItem.setData("type", "note");
				newItem.setData(note);
				newItem.setText(note.getString("name"));
				newItem.setData("label", label);
				newItem.setData("group", agroup);
				setImage(newItem, "noteImage", actionContext);
				addTreeItemToNode(newItem, note);
				return true;
			}else if(groupIndex < group.length - 1){
				boolean created = false;
				for(TreeItem childItem : item.getItems()){
					if(createToTreeItem(childItem, note, label, agroup, group, groupIndex + 1, actionContext)){
						created = true;
						return true;
					}
				}
				
				if(!created){
					TreeItem nodeItem = new TreeItem(item, SWT.None);
					nodeItem.setData("type", "node");
					nodeItem.setText(group[groupIndex + 1]);
					setImage(nodeItem, "nodeImage", actionContext);
					nodeItem.setData("group", agroup);
					nodeItem.setData("label", label);
					return createToTreeItem(nodeItem, note, label, agroup, group, groupIndex + 1, actionContext);
				}								
			}
			
			return false;
		}else{
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void addTreeItemToNode(TreeItem item, DataObject note){
		List<TreeItem> items = (List<TreeItem>) note.get("__treeItems__");
		if(items == null){
			items = new ArrayList<TreeItem>();
			note.put("__treeItems__", items);
		}
		
		items.add(item);
	}
	
	@SuppressWarnings("unchecked")
	public static void removeNote(DataObject note, ActionContext actionContext){
		List<TreeItem> items = (List<TreeItem>) note.get("__treeItems__");
		if(items != null){
			for(TreeItem item : items){
				disposeItem(item);
			}
		}
	}
}
