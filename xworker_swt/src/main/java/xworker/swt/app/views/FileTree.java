package xworker.swt.app.views;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilFile;

public class FileTree {
	@SuppressWarnings("unchecked")
	public static Object treeDefaultSelectionCondition(ActionContext actionContext) {
		Event event = actionContext.getObject("event");
		
		Map<String, Object> data = (Map<String, Object>) event.item.getData();
		String dataId = (String) data.get("dataId");
		File file = new File(dataId);
		if(file.isFile()){
		    actionContext.l().put("file", file);
		    return true;
		}else{
		    return false;
		}
	}
	
	public static void openFile(ActionContext actionContext) throws IOException {
		File file = actionContext.getObject("file");
		String thingPath = UtilFile.getThingPathByFile(file);
		World world = World.getInstance();
		Action openThingEditor = actionContext.getObject("openThingEditor");
		Action openFileEditor = actionContext.getObject("openFileEditor");
		
		if(thingPath != null){
		    //文件是事物，编辑事物
		    Thing thing = world.getThing(thingPath);
		    openThingEditor.run(actionContext, "thing", thing);
		}else{
		    //是普通文件，用文件编辑器打开
		    openFileEditor.run(actionContext, "file", file);
		}
	}
	
	public static Object getThingManager(ActionContext actionContext) {
		//通常第一个事物管理器就是工作目录所在的事物管理器
		return World.getInstance().getThingManagers().get(0).getName();
	}
	
	@SuppressWarnings("unchecked")
	public static Object getRootDir(ActionContext actionContext) {
		Tree menuTree = actionContext.getObject("menuTree");
		File dir = null;
		TreeItem[] s = menuTree.getSelection();
		if(s != null && s.length > 0){
			Map<String, Object> data = (Map<String, Object>) s[0].getData();
			String dataId = (String) data.get("dataId");
		    dir = new File(dataId);
		    if(dir.isFile()){
		        dir = dir.getParentFile();
		    }
		}else{
		    dir = new File(".");
		}

		return dir;
	}
	
	@SuppressWarnings("unchecked")
	public static void deleteFile(ActionContext actionContext) {
		File dir = null;
		Tree menuTree = actionContext.getObject("menuTree");
		TreeItem[] s = menuTree.getSelection();
		TreeItem treeItem = null;
		if(s != null && s.length > 0){
		    Map<String, Object> data = (Map<String, Object>) s[0].getData();
			String dataId = (String) data.get("dataId");
		    dir = new File(dataId);
		    treeItem = s[0];
		}

		//没有选择文件
		if(dir == null){
			Action noFileMsg = actionContext.getObject("noFileMsg");
		    noFileMsg.run(actionContext);
		    return;
		}

		//确认删除
		String msg = null;
		if(dir.isDirectory()){
		    msg = "确定要删除目录" + dir.getPath() + "及目录下的所有文件和子目录么！";
		}else{
		    msg = "确定要删除文件" + dir.getPath() + "么！";
		}
		
		Action delete = actionContext.getObject("delete");
		delete.run(actionContext, "msg", msg, "treeItem", treeItem, "dir", dir);
	}
	
	public static Object deleteFileOk(ActionContext actionContext) throws IOException {
		TreeItem treeItem = actionContext.getObject("treeItem");
		File dir = actionContext.getObject("dir");
		if(dir.isDirectory()){
		    FileUtils.deleteDirectory(dir);
		}else{
		    dir.delete();
		}

		//closeTabItem(treeItem);
		treeItem.dispose();    

		return dir;
	}
	
	/*
	private void closeTabItem(TreeItem treeItem){
	    String s = ((Map<String, Object>) treeItem.getData()).get("dataId");
	    File dir = new File(s);
	    
	    for(TreeItem item : mainTabFolder.getItems()){
	        def file = item.getData("__file__");
	        if(dir.equals(file)){
	            item.dispose();
	            break;
	        }
	    }
	    
	    for(item in treeItem.getItems()){
	        closeTabItem(item);
	    }
	}*/
	
	public static void openThing(ActionContext actionContext) throws IOException {
		File file = actionContext.getObject("file");
		String thingPath = UtilFile.getThingPathByFile(file);
		Thing thing = World.getInstance().getThing(thingPath);

		Action openThingEditor = actionContext.getObject("openThingEditor");
		openThingEditor.run(actionContext, "thing", thing);
	}
	
	public static Object isRefreshParent(ActionContext actionContext) {
		Tree menuTree = actionContext.getObject("menuTree");
		TreeItem[] items = menuTree.getSelection();
		if(items.length > 0){
		    File file = getFile(items[0]);
		    if(file.isFile()){
		        return true;
		    }else{
		        return false;
		    }
		}else{
		    return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static File getFile(TreeItem item) {
		Map<String, Object> data = (Map<String, Object>) item.getData();
		String dataId = (String) data.get("dataId");
		return new File(dataId);
	}
}
