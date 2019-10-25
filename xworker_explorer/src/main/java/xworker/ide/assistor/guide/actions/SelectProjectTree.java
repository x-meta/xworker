package xworker.ide.assistor.guide.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Index;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.index.WorldIndex;

import xworker.ide.worldexplorer.actions.ProjectTreeActions;
import xworker.swt.design.Designer;
import xworker.swt.events.SwtListener;

public class SelectProjectTree {
	private static Logger logger = LoggerFactory.getLogger(SelectProjectTree.class);
	
	public static void selectProjectTree(ActionContext actionContext){
		//选择事物管理器项目浏览上的一个条目
		
	}
	
	/**
	 * 选择项目树上的一个节点。
	 * 
	 * @param type thingManager、category、thing
	 * @param path
	 * @return
	 */
	public static boolean selectProject(String type, String path){
		ActionContext ac = new ActionContext();
		
		Thing data = new Thing();
		
		data.put("type", type);
		data.put("path", path);
		ac.put("data", data);
		
		return run(ac);
	}
	
	public static boolean run(ActionContext actionContext){
		World world = World.getInstance();
		ActionContext explorerContext = Designer.getExplorerActions().getActionContext();// (ActionContext) actionContext.get("explorerContext");
		Thing data = (Thing) actionContext.get("data");
		SwtListener projectTreeSelection = (SwtListener) explorerContext.get("projectTreeSelection");
		
		//项目树
		Tree projectTree = (Tree) explorerContext.get("projectTree");

		//查找的类型
		String type = data.getString("type");
		String path = data.getString("path");
		Shell shell = (Shell) explorerContext.get("shell");
		List<Index> indexs = null;
		if("thingManager".equals(type)){
		    indexs = getIndexPaths(world.getThingManager(path));
		    if(indexs == null){
		        showMessageBox(shell, "事物管理器" + path + "不存在！");
		        return false;
		    }
		}else if("category".equals(type)){
			indexs = getIndexPaths(data.getString("thingManager"), path);
		    if(indexs == null){
		        showMessageBox(shell, "事物管理器" + data.getString("thingManager") + "下目录" + path + "不存在！");
		        return false;
		    }
		}else if("thing".equals(type)){
			Thing thing = world.getThing(path);
			if(thing != null){
				String thingManager = thing.getMetadata().getThingManager().getName();
				String category = thing.getMetadata().getCategory().getName();
				indexs = getIndexPaths(thingManager, category);
			}
		    if(indexs == null){
		        showMessageBox(shell, "事物所在目录" + path + "不存在！");
		        return false;
		    }
		}else if("workset".equals(type)){
			indexs = getIndexPaths(world.getThing(path));
		    if(indexs == null){
		        showMessageBox(shell, "工作组" + path + "不存在！");
		        return false;
		    }		    
		}else{
		    for(TreeItem item : projectTree.getItems()){
		        Index index = (Index) item.getData();
		        if("world".equals(type) && Index.TYPE_WORLD.equals(index.getType())){
		            projectTree.select(item);
		            projectTreeSelection.handleEvent(null);
		            return true;
		        }
		    }	
		    
		    showMessageBox(shell, "项目节点不存在，应该不会发生此事，除非项目树的'项目'根节点已取消！");
		    return false;
		}
		
		//第一个是事物管理器，先选择事物管理器节点
		indexs.remove(0); //第一个是worldIndex
		//Index index = indexs.remove(1);
		if(projectTree != null){
			for(TreeItem item : projectTree.getItems()){
				Index childIndex  = (Index) item.getData();
				if(childIndex.getType().equals(indexs.get(0).getType()) && childIndex.getPath().equals(indexs.get(0).getPath())){
					selectTreeItem(indexs, item, explorerContext);
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static TreeItem selectThingManagerTreeItem(Index thingManagerIndex, Tree projectTree, SwtListener projectTreeSelection){
		String thingManager = thingManagerIndex.getName();
		Thing workingSet = getThingManagerAtWorkingSet(thingManager);		
		if(workingSet != null && ((WorldIndex) Index.getInstance()).showWorkingSet){
			List<String> workingSetPath = new ArrayList<String>();
			Thing parent = workingSet.getParent();
			while(parent != null){
				workingSetPath.add(0, parent.getMetadata().getName());
				parent = parent.getParent();				
			}
			
			TreeItem treeItem = null;
			for(int i=1; i<workingSetPath.size(); i++){
				String name = workingSetPath.get(i);
				if(i == 1){
					for(TreeItem item : projectTree.getItems()){
						Index index = (Index) item.getData();
						if(index.getType().equals(Index.TYPE_WORKING_SET) && index.getName().equals(name)){
							treeItem = item;
							
							treeItem.getParent().setSelection(treeItem);
							projectTreeSelection.handleEvent(null);
							break;
						}
					}
				}else{
					for(TreeItem item : treeItem.getItems()){
						Index index = (Index) item.getData();
						if(index.getType().equals(Index.TYPE_WORKING_SET) && index.getName().equals(name)){
							treeItem = item;
							
							treeItem.getParent().setSelection(treeItem);
							projectTreeSelection.handleEvent(null);
							break;
						}
					}
				}				
			}
			
			if(treeItem != null){
				for(TreeItem item : treeItem.getItems()){
					Index index = (Index) item.getData();
					if(index.getType().equals(Index.TYPE_THINGMANAGER) && index.getName().equals(thingManager)){
						treeItem = item;
						
						treeItem.getParent().setSelection(treeItem);
						projectTreeSelection.handleEvent(null);
						return treeItem;
					}
				}
				
				return null;
			}else{
				return null;
			}
		}else{
			for(TreeItem item : projectTree.getItems()){
				Index index = (Index) item.getData();
				if(index.getType().equals(Index.TYPE_THINGMANAGER) && index.getName().equals(thingManager)){
					projectTree.setSelection(item);
					projectTreeSelection.handleEvent(null);
					return item;
				}
			}
			
			return null;
		}
	}
		
	private static Thing getThingManagerAtWorkingSet(String thingManager){
		Thing workingSet = World.getInstance().getThing("_local.xworker.worldExplorer.WorkingSet");
		if(workingSet == null){
			return null;
		}else{
			return getThingManagerAtWorkingSet(thingManager, workingSet);
		}		
	}
	
	private static Thing getThingManagerAtWorkingSet(String thingManager, Thing workingSet){
		for(Thing child : workingSet.getChilds()){
			if(child.getThingName().equals("ThingManager") && child.getMetadata().getName().equals(thingManager)){
				return child;
			}else{
				Thing thingM = getThingManagerAtWorkingSet(thingManager, child);
				if(thingM != null){
					return thingM;
				}
			}
		}
		
		return null;
	}
	
	public static void selectTreeItem(List<Index> indexs, TreeItem treeItem, ActionContext explorerActionContext){
		Index index = indexs.remove(0);
		//如果树节点未展开先展开
		if(treeItem.getItems() == null || treeItem.getItems().length == 0){
			treeItem.getParent().setSelection(treeItem);
			ProjectTreeActions.expandProjectTree(explorerActionContext);
			//projectTreeSelection.handleEvent(null);
		}
		
		Index itemIndex = (Index) treeItem.getData();
		if(itemIndex.getType().equals(index.getType()) && itemIndex.getPath().equals(index.getPath())){
			treeItem.getParent().setSelection(treeItem);
			if(indexs.size() > 0){
				for(TreeItem item : treeItem.getItems()){
					Index childIndex  = (Index) item.getData();
					if(childIndex.getType().equals(indexs.get(0).getType()) && childIndex.getPath().equals(indexs.get(0).getPath())){
						selectTreeItem(indexs, item, explorerActionContext);
						break;
					}
				}
			}
		}		
	}
	
	public static List<Index> getIndexPaths(String thingManagers, String category){
		Index thingManagerIndex = findIndex(Index.TYPE_THINGMANAGER, thingManagers, Index.getInstance());
		if(thingManagerIndex == null){
			return null;
		}
		
		Index index = null;
		for(Index childIndex : thingManagerIndex.getChilds()){
			if(category != null && childIndex != null && category.startsWith(childIndex.getPath())){
				index = findCategoryIndex(childIndex, category);
				break;
			}
		}
		
		if(index == null){
			return null;
		}
		
		return getIndexPaths(index);
	}
	
	public static Index findCategoryIndex(Index index, String category){
		if(index.getType().equals(Index.TYPE_CATEGORY) && index.getPath().equals(category)){
			return index;
		}else{
			for(Index childIndex : index.getChilds()){
				String childPath = childIndex.getPath();
				if(category.startsWith(childPath + ".") || category.equals(childPath)){
					return findCategoryIndex(childIndex, category);
				}
			}
			
			return null;
		}
	}
	
	public static List<Index> getIndexPaths(Thing workingSet){
		if(workingSet == null){
			return null;
		}
		
		Index index = findIndex(Index.TYPE_WORKING_SET, workingSet.getMetadata().getPath(), Index.getInstance());
		return getIndexPaths(index);
		
	}
	
	public static List<Index> getIndexPaths(ThingManager thingManager){
		if(thingManager == null){
			return null;
		}
		
		Index index = findIndex(Index.TYPE_THINGMANAGER, thingManager.getName(), Index.getInstance());
		return getIndexPaths(index);
	}
	
	public static List<Index> getIndexPaths(Index index){
		if(index == null){
			return null;
		}
		
		List<Index> indexs = new ArrayList<Index>();
		indexs.add(index);
		Index parent = index.getParent();
		while(parent != null){
			indexs.add(0, parent);
			parent = parent.getParent();
		}
		
		return indexs;
	}
	
	/**
	 * 查找工作组或事物管理器的索引。
	 * 
	 * @param type
	 * @param path
	 * @param index
	 * @return
	 */
	public static Index findIndex(String type, String path, Index index){
		String indexTyp = index.getType();
		if(indexTyp.equals(type) && index.getPath().equals(path)){
			return index;
		}else if(indexTyp.equals(Index.TYPE_WORLD) || indexTyp.equals(Index.TYPE_WORKING_SET)){
			for(Index childIndex : index.getChilds()){
				Index tindex = findIndex(type, path, childIndex);
				if(tindex != null){
					return tindex;
				}
			}
		}
		
		return null;
	}

	public static void showMessageBox(Shell shell, String msg){
		logger.warn(msg);
		return;
		/*
		MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
	    box.setText("选择项目树");
	    box.setMessage(msg);
	    box.open();
	    */
	}
}
