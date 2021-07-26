package xworker.swt.app.editors;

import java.io.File;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Category;
import org.xmeta.Index;
import org.xmeta.Thing;
import org.xmeta.ThingCoder;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import org.xmeta.index.FileIndex;
import org.xmeta.index.ParentIndex;
import org.xmeta.thingManagers.FileThingManager;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import xworker.lang.executor.Executor;
import xworker.swt.events.SwtListener;
import xworker.swt.xworker.DataTable;
import xworker.ui.swt.SwtBorder;
import xworker.util.UtilFileIcon;
import xworker.util.XWorkerUtils;

@ActionClass(creator="createInstance")
public class XWorkerPackageViewer {
	private static final String TAG = XWorkerPackageViewer.class.getName();
	
	public void initCode() {
		Composite titleComposite = actionContext.getObject("titleComposite");
		//标题的边框
		SwtBorder.attach(titleComposite);
		dataTable.setColumnNames(new String[] {"name", "label"});

		//初始化表格
		if(actionContext.get("actions") != null){
		    actions.doAction("refreshTable");
		}

		//文件的菜单先不需要了，暂时屏蔽
		//mainCoolbar.dispose();
	}
	
    public void dataTableDefaultSelection(){
	    //xworker.swt.app.editors.XWorkerPackageViewer/@EditorComposite/@packageCompoiste/@packageTable/@listeners/@dataTableDefaultSelection/@GroovyAction
    
	    TableItem item = dataTable.getSelection()[0];
	    Index data = (Index)item.getData();
	    
	    if(dataTable.getSelectionIndex() == 0 && "parent".equals(data.getType())){
	        Index index = data.getParent();
	        actions.doAction("refreshTable", actionContext, "index", index.getParent(), "refresh", false);
	        
	        if(actionContext.get("treeItem") != null){
	            Tree tree = treeItem.getParent();
	            treeItem = treeItem.getParentItem();
	            if(treeItem != null){
	            	actionContext.g().put("treeItem", treeItem);
	            	setProjectTreeSelection(tree, treeItem, true);
	                //tree.setSelection(treeItem);
	            }else{
	                tree.setSelection(new TreeItem[] {});
	            }
	        }
	    }else if(Index.TYPE_FILE.equals(data.getType())){
	    	File f = ((FileIndex) data).getFile();
	        String name = f.getName().toLowerCase();
	        String[] textNames = new String[]{"ftl","java","groovy","c", "cpp", "h","log", "properties", "conf", "txt", "scala"};
	        boolean isText = false;
	        for(String text : textNames){
	            if(name.endsWith(text)){
	                isText = true;
	                break;
	            }
	        }
	        if(isText){
	            //打开文本文件
	            String id = "file:" + f.getAbsolutePath();
	            Action openFileEditor = actionContext.getObject("openFileEditor");
	            openFileEditor.run(actionContext, "params", UtilMap.toMap(
	                "file", f,
	                "id", id
	        ), "id", id);
	            
	            //explorerActions.doAction("openTextFile", actionContext, ["file": f]);
	        }else{
	            Program.launch(f.getAbsolutePath());
	        }
	    }else if(!Index.TYPE_THING.equals(data.getType())){   
	        Index index = data; 
	        //log.info("isthing");
	        actions.doAction("refreshTable", actionContext, "index", index);
	        
	        //设置窗体的树选择
	        if(actionContext.get("explorerActions") != null && actionContext.get("projectTree") != null){
	        	ActionContainer explorerActions = actionContext.getObject("explorerActions");
	            explorerActions.doAction("expandProjectTreeItem");
	            TreeItem[] items;
	            if(treeItem.getParentItem() == null){
	                items = projectTree.getItems();
	            }else{
	                items = treeItem.getParentItem().getItems();
	            }
	            
	            TreeItem selectedItem = null;
	            for(TreeItem itemt : items){
	                Index idata = (Index) itemt.getData();
	                //log.info(idata.type + " " + data.type);
	                if(idata.getType().equals(data.getType()) && idata.getName().equals(data.getName())){
	                    selectedItem = itemt;
	                    break;
	                }
	            }
	                
	            if(selectedItem != null){               
	                //tree.setSelection(selectedItem);
	            	setProjectTreeSelection(selectedItem.getParent(), selectedItem, true);
	                //explorerActions.doAction("expandProjectTreeItem");
	                //selectedItem.setExpanded(true);
	                treeItem = selectedItem;
	            }
	            
	            index = (Index) projectTree.getSelection()[0].getData();
	            String indexType = index.getType();
	            Thing helpThing = null;
	            if(indexType == Index.TYPE_WORLD){
	                helpThing = world.getThing("xworker.ide.worldexplorer.swt.help.ProjectHelps/@World");
	            }else if(indexType == Index.TYPE_WORKING_SET){
	                helpThing = world.getThing("xworker.ide.worldexplorer.swt.help.ProjectHelps/@WorkingSet");
	            }else if(indexType == Index.TYPE_THINGMANAGER){
	                helpThing = world.getThing("xworker.ide.worldexplorer.swt.help.ProjectHelps/@ThingManager");
	            }else if(indexType == Index.TYPE_CATEGORY){
	                helpThing = world.getThing("xworker.ide.worldexplorer.swt.help.ProjectHelps/@Category");
	            }
	            if(helpThing != null){
	                String url = XWorkerUtils.getThingDescUrl(helpThing);
	                Browser packageViewerHelpBrowser = actionContext.getObject("packageViewerHelpBrowser");
	                packageViewerHelpBrowser.setUrl(url);
	            }
	        }
	    }else{    
	        //打开数据对象
	        //println data.path;
	        Thing thing = world.getThing(data.getPath());
	        Action openThingEditor = actionContext.getObject("openThingEditor");
	        openThingEditor.run(actionContext, "params", UtilMap.toMap(Index.TYPE_THING, thing, "id", data.getPath()),
	             "id", data.getPath());
	    }
	}
	
	public void dataTableSelection(){
	    //xworker.swt.app.editors.XWorkerPackageViewer/@EditorComposite/@packageCompoiste/@packageTable/@listeners/@Listener/@dataTableSelection
	    TableItem item = dataTable.getSelection()[0];
	    Object data = item.getData();
	    
	    Index index = (Index) data;
	    
	    actions.doAction("refreshToolbarHelp", actionContext, "index", index);
	    
	    //选中项目导航的相应节点
	    if(actionContext.get("treeItem") != null){
	    	TreeItem treeItem = actionContext.getObject("treeItem");
	    	if("parent".equals(index.getType())) {
	    		if(treeItem.getData() != index.getParent() && treeItem.getParentItem() != null) {
	    			//treeItem.getParent().select(treeItem.getParentItem());
	    			setProjectTreeSelection(treeItem.getParent(), treeItem.getParentItem(), false);
	    		}
	    	}else {
		    	boolean ok = false;
		        for(TreeItem childItem : treeItem.getItems()){
		            if(childItem.getData() == data){
		                //treeItem.getParent().select(childItem);
		            	setProjectTreeSelection(treeItem.getParent(), childItem, false);
		                actionContext.getScope(0).put("treeItem", childItem);
		                ok = true;
		                break;
		            }
		        }
		        
		        if(!ok && treeItem.getParentItem() != null) {
		        	for(TreeItem childItem : treeItem.getParentItem().getItems()){
			            if(childItem.getData() == data){
			                //treeItem.getParent().select(childItem);
			            	setProjectTreeSelection(treeItem.getParent(), childItem, false);
			                actionContext.getScope(0).put("treeItem", childItem);
			                ok = true;
			                break;
			            }
			        }
		        }
		        
		        if(!ok) {
		        	for(TreeItem childItem : treeItem.getParent().getItems()){
			            if(childItem.getData() == data){
			                treeItem.getParent().select(childItem);
			                setProjectTreeSelection(treeItem.getParent(), childItem, false);
			                actionContext.getScope(0).put("treeItem", childItem);
			                ok = true;
			                break;
			            }
			        }
		        }		        
	    	}
	    }
	    return;
	    /*
	    def indexType = index.type;
	    
	    def helpThing = null;
	    if(indexType == Index.TYPE_WORLD){
	        helpThing = world.getThing("xworker.ide.worldexplorer.swt.help.ProjectHelps/@World");
	    }else if(indexType == Index.TYPE_WORKING_SET){
	        helpThing = world.getThing("xworker.ide.worldexplorer.swt.help.ProjectHelps/@WorkingSet");
	    }else if(indexType == Index.TYPE_THINGMANAGER){
	        helpThing = world.getThing("xworker.ide.worldexplorer.swt.help.ProjectHelps/@ThingManager");
	    }else if(indexType == Index.TYPE_CATEGORY){
	        helpThing = world.getThing("xworker.ide.worldexplorer.swt.help.ProjectHelps/@Category");
	    }else if(indexType == "parent"){
	        helpThing = world.getThing("xworker.ide.worldexplorer.swt.help.ProjectHelps/@Parent");
	    }else if(indexType == Index.TYPE_THING){
	        helpThing = world.getThing("xworker.ide.worldexplorer.swt.help.ProjectHelps/@Thing");
	    }
	    
	    if(helpThing != null){
	        def url = XWorkerUtils.getThingDescUrl(helpThing);
	        packageViewerHelpBrowser.setUrl(url);
	    }else{
	        packageViewerHelpBrowser.setText("未知的类型：" + indexType);
	    }*/
	}
	
	public void GroovyAction(){
	    //xworker.swt.app.editors.XWorkerPackageViewer/@EditorComposite/@packageCompoiste/@packageTable/@listeners/@dataTableMenuDetect/@GroovyAction
	    TableItem item = dataTable.getSelection()[0];
	    Index data = (Index) item.getData();
	    
	    if(dataTable.getSelectionIndex() == 0 && "parent".equals(data.getType())){
	    	Event event = actionContext.getObject("event");
	        event.doit = false;
	    }
	}
	
	public void tableDragStrat(){
	    //xworker.swt.app.editors.XWorkerPackageViewer/@EditorComposite/@packageCompoiste/@packageTable/@projectTreeDragSource/@prjTreeDragStrat/@run
	    TableItem item = dataTable.getSelection()[0];
	    Event event = actionContext.getObject("event");
	    Index data = (Index) item.getData();
	    
	    if(dataTable.getSelectionIndex() == 0 && "parent".equals(data.getType())){
	        event.doit = false;
	    }else{
	        event.doit = true;
	    }
	}
	
	public void tableSetData(){
	    //xworker.swt.app.editors.XWorkerPackageViewer/@EditorComposite/@packageCompoiste/@packageTable/@projectTreeDragSource/@prjTreeSetData/@run
	    TableItem item = dataTable.getSelection()[0];
	    Index data = (Index) item.getData();
	    Event event = actionContext.getObject("event");
	    if(data.getIndexObject() instanceof Category){
	        event.data = ((Category) data.getIndexObject()).getThingManager().getName() + ":" + data.getPath();
	    }else{
	        event.data = "knownd: tableSetData : 212";//(data.indexObject.thingManager.getName() + ":" + data.getPath();
	    }
	}
	
	public void renameMenuItemSelection(){
	    //xworker.swt.app.editors.XWorkerPackageViewer/@EditorComposite/@packageCompoiste/@packageTable/@dataTableMenu/@renameMenuItem/@listeners/@Listener/@GroovyAction
	    TableItem item = dataTable.getSelection()[0];
	    Index data = (Index) item.getData();
	    ActionContainer explorerActions = XWorkerUtils.getIdeActionContainer();
	    ActionContext explorerActionContext = XWorkerUtils.getIdeActionContainer().getActionContext();
	    
	    if(dataTable.getSelectionIndex() == 0 && "parent".equals(data.getType())){
	    }else if(Index.TYPE_FILE.equals(data.getType())){
	    	FileIndex fileIndex = (FileIndex) data;
	        File f = fileIndex.getFile();
	        ActionContext ac = new ActionContext();
	        ac.put("parent", dataTable.getShell());
	        ac.put("explorerActions", explorerActions);
	        ac.put("explorerActionContext", explorerActionContext);
	        ac.put("file", f);
	        
	        Shell shell = world.getThing("xworker.ide.worldexplorer.swt.dialogs.RenameFileDialog").doAction("create", ac);
	        shell.open();
	    }else if(Index.TYPE_THING.equals(data.getType())){   
	        Executor.info(TAG, "data=" + data.getIndexObject());
	    }else{    
	        //打开数据对象
	        //println data.path;
	        Thing thing = world.getThing(data.getPath());
	        ActionContext ac = new ActionContext();
	        ac.put("parent", dataTable.getShell());
	        ac.put("explorerActions", explorerActions);
	        ac.put("explorerActionContext", explorerActionContext);
	        ac.put(Index.TYPE_THING, thing);
	        
	        Shell shell = world.getThing("xworker.ide.worldexplorer.swt.dialogs.RenameThingDialog").doAction("create", ac);
	        shell.open();
	    }
	}
	
	public Object ok(){
	    //xworker.swt.app.editors.XWorkerPackageViewer/@EditorComposite/@packageCompoiste/@packageTable/@dataTableMenu/@deleteMenuItem/@listeners/@deleteMenuItemSelection/@confirm/@actions/@GroovyAction
	    
	    TableItem item = dataTable.getSelection()[0];
	    Index data = (Index) item.getData();
	    
	    
	    if(dataTable.getSelectionIndex() == 0 && "parent".equals(data.getType())){
	    }else if(Index.TYPE_FILE.equals(data.getType())){
	    	FileIndex fileIndex = (FileIndex) data;
	        File f = fileIndex.getFile();    
	        explorerActions.doAction("closeTab", explorerActions.getActionContext(), "path",  "textFile" + f.getAbsolutePath());
	        f.delete();
	    }else if(!Index.TYPE_THING.equals(data.getType())){   
	        Executor.info(TAG,"data=" + data.getIndexObject());
	    }else{    
	        //打开数据对象
	        //println data.path;
	        Thing thing = world.getThing(data.getPath());
	        if(thing != null){
	            explorerActions.doAction("closeTab", explorerActions.getActionContext(), "path", thing.getMetadata().getPath());
	            thing.remove();
	        }
	            
	    }
	    
	    Tree projectTree = actionContext.getObject("projectTree");
	    if(projectTree != null) {
	    	actions.doAction("refreshTable", actionContext, "index", projectTree.getSelection()[0].getData());
	    }
	    
	    return null;
	}
	
	public void setProjectTreeSelection(Tree tree, TreeItem treeItem, boolean fireSelection) {
		tree.setSelection(treeItem);
		if(fireSelection) {
			SwtListener projectTreeSelection = actionContext.getObject("projectTreeSelection");
			if(projectTreeSelection != null) {
				projectTreeSelection.handleEvent(null);
			}
		}
	}
	
	public void setContent(){
	    //xworker.swt.app.editors.XWorkerPackageViewer/@actions/@setContent
		Map<String, Object> params = actionContext.getObject("params");
	    if(params == null || params.size() == 0){
	        return;
	    }
	    
	    actionContext.g().put("treeItem", params.get("treeItem"));
	    actionContext.g().put("projectTree", params.get("projectTree"));
	    actionContext.g().put("projectTreeSelection", params.get("projectTreeSelection"));
	    
	    //由于使用了XWorker的工具栏等，伪装变量
	    actionContext.g().put("explorerContxt", actionContext);
	    actionContext.g().put("explorerActions", actions);
	    
	    actions.doAction("refreshTable", actionContext, "index", params.get("index"));
	}
	
	public void doDispose(){
	    //xworker.swt.app.editors.XWorkerPackageViewer/@actions/@doDispose
	}
	
	@SuppressWarnings({"unchecked" })
	public void refreshTable(){
	    //xworker.swt.app.editors.XWorkerPackageViewer/@actions/@refreshTable
		if(dataTable.getColumnNames() == null) {
			initCode();
		}
		
	    //取索引
		Index index = null;
	    if(actionContext.get("index") == null){
	        index = Index.getInstance();
	    }else {
	    	index = actionContext.getObject("index");
	    }
	    
	    //println UtilGroovy.getProperty(binding, "refresh");
	    if(index == dataTable.getData("oldIndex") && !UtilData.isTrue(actionContext.get("refresh"))){
	        //return;
	    }
	    
	    //更新表格的数据
	    dataTable.removeAll();
	    
	    Composite packageCompoiste = actionContext.getObject("packageCompoiste");
	    Map<String, Image> res = (Map<String, Image>) packageCompoiste.getData("resources");
	    if(!Index.TYPE_WORLD.equals(index.getType())){
	        Index parent = new ParentIndex(index);
	        TableItem item = dataTable.updateRow(null, -1, parent);
	        if(res != null && res.get("parentImage") != null)
	            item.setImage(res.get("parentImage"));
	    }
	    
	    Label titleLabel = actionContext.getObject("titleLabel");
	    titleLabel.setText(index.getPath());
	    if(actionContext.get("refresh") != null && !UtilData.isTrue(actionContext.get("refresh"))) {
	    }else{
	    	index.refresh();
		}
	
	    for(Index child : index.getChilds()){    
	        TableItem item = dataTable.updateRow(null, -1, child);
	        if(res != null){
	            setImage(item, child, res);
	        }
	    }
	    
	    dataTable.setData("oldIndex", index);
	    
	    actions.doAction("refreshToolbarHelp", actionContext);
	    
	    
	    //刷新表格的文件，取消了Project，此功能目前不正常暂时屏蔽
	    File fdir = null;
	    if(index.getType() == Index.TYPE_CATEGORY){
	        String path = index.getPath();
	        ThingManager thingManager = ((Category) index.getIndexObject()).getThingManager();
	        if(thingManager instanceof FileThingManager){
	            fdir = new File(((FileThingManager) thingManager).getFilePath(), path.replace('.', '/'));
	        }
	    }else if(index.getType() == Index.TYPE_THINGMANAGER){
	        ThingManager thingManager = (ThingManager) index.getIndexObject();
	        if(thingManager instanceof FileThingManager){
	            fdir = new File(((FileThingManager) thingManager).getFilePath());
	        }
	    }
	    if(fdir != null && fdir.exists()){
	        for(File f : fdir.listFiles()){
	            if(f.isDirectory() || isThingFile(f.getName())) continue;    
	            FileIndex fileIndex = new FileIndex(index, f);
	            TableItem item = dataTable.updateRow(null, -1, fileIndex);
	            int lastDot = f.getName().lastIndexOf('.');
	            if(lastDot >= 0 && lastDot < f.getName().length()) {
	                Object icon = getIcon(f.getName().substring(lastDot+1));
	                if(icon != null && icon instanceof Image){
	                    item.setImage(0, (Image) icon);
	                }
	            }    
	        }
	    }
	}
	
	public boolean isThingFile(String fileName){
       for(ThingCoder coder : world.getThingCoders()){
           if(fileName.endsWith(coder.getType())){
               return true;
           }
       }
       
       return false;
    }
    
	
    public void setImage(TableItem item, Index data, Map<String, Image> ress){
        if(Index.TYPE_PROJECTS.equals(data.getType()) || Index.TYPE_PLUGINS.equals(data.getType()) || Index.TYPE_CHILDWORLDS.equals(data.getType())){        	
            item.setImage(ress.get("projectImage"));
        }else if(Index.TYPE_PROJECT.equals(data.getType())){
            //println item;
            item.setImage(ress.get("project1Image"));
        }else if((Index.TYPE_CATEGORY.equals(data.getType()) && "".equals(data.getName())) || Index.TYPE_THINGMANAGER.equals(data.getType())){
            item.setImage(ress.get("factoryImage"));
        }else if(Index.TYPE_CATEGORY.equals(data.getType())){
            item.setImage(ress.get("packageImage"));
        }else if(Index.TYPE_THING.equals(data.getType())){
            item.setImage(ress.get("dataObjectImage"));
        }else if(Index.TYPE_WORKING_SET.equals(data.getType())){
            item.setImage(ress.get("worksetImage"));
        }
    }
    
    public String getIcon(String extension) {
        return UtilFileIcon.getFileIcon(extension, false);
        /*
        if(actionContext.get("imageRegistry") == null){
            def imageRegistry = new ImageRegistry();
            actionContext.put("imageRegistry", imageRegistry);
        }
        //if(imageRegistry == null)
        //    imageRegistry = new ImageRegistry();
        Image image = imageRegistry.get(extension);
        if(image != null)
            return image;
        
        Program program = Program.findProgram(extension);
        if(program != null) {
            image = new Image(dataTable.getDisplay(), program.getImageData());
            imageRegistry.put(extension, image);
        }else{
            imageRegistry.put(extension, normalFileImage);
        }
        
        return image;*/
    }
	
	public void refreshToolbarHelp(){
	    //xworker.swt.app.editors.XWorkerPackageViewer/@actions/@refreshToolbarHelp
	    //根据索引显示帮助和ToolBar
		Index index = actionContext.getObject("index");
		if(index == null) {
			return;
		}
		
	    String indexType = index.getType();
	    Label titleLabel = actionContext.getObject("titleLabel");
	    
	    Thing helpThing = null;
	    Thing toolbarThing = null;
	    if(Index.TYPE_WORLD.equals(indexType)){
	        helpThing = world.getThing("xworker.ide.worldexplorer.swt.help.ProjectHelps/@World");
	        toolbarThing = world.getThing("xworker.ide.worldexplorer.swt.dataExplorerParts.PackageViewerToolbars/@mainCoolBar/@mainCollItem/@mainToolBar");
	        //titleLabel.setText("
	    }else if(Index.TYPE_WORKING_SET.equals(indexType)){
	        helpThing = world.getThing("xworker.ide.worldexplorer.swt.help.ProjectHelps/@WorkingSet");
	        toolbarThing = world.getThing("xworker.ide.worldexplorer.swt.dataExplorerParts.PackageViewerToolbars/@mainCoolBar/@mainCollItem/@mainToolBar");
	        titleLabel.setText(UtilString.getString("lang:d=工作组：&en=WorkSet:", actionContext) + index.getLabel());
	    }else if(Index.TYPE_THINGMANAGER.equals(indexType)){
	        helpThing = world.getThing("xworker.ide.worldexplorer.swt.help.ProjectHelps/@ThingManager");
	        toolbarThing = world.getThing("xworker.ide.worldexplorer.swt.dataExplorerParts.PackageViewerToolbars/@mainCoolBar/@mainCollItem/@thingManagerToolBar");
	        titleLabel.setText(UtilString.getString("lang:d=事物管理器：&en=ThingManager:", actionContext) + index.getLabel());
	    }else if(Index.TYPE_CATEGORY.equals(indexType)){
	        helpThing = world.getThing("xworker.ide.worldexplorer.swt.help.ProjectHelps/@Category");
	        toolbarThing = world.getThing("xworker.ide.worldexplorer.swt.dataExplorerParts.PackageViewerToolbars/@mainCoolBar/@mainCollItem/@categoryToolBar");
	        titleLabel.setText(UtilString.getString("lang:d=包：&en=Package:", actionContext) + index.getPath());
	    }else if("parent".equals(indexType)){
	        helpThing = world.getThing("xworker.ide.worldexplorer.swt.help.ProjectHelps/@Parent");
	    }else if(Index.TYPE_THING.equals(indexType)){
	        helpThing = world.getThing("xworker.ide.worldexplorer.swt.help.ProjectHelps/@Thing");
	        toolbarThing = world.getThing("xworker.ide.worldexplorer.swt.dataExplorerParts.PackageViewerToolbars/@mainCoolBar/@mainCollItem/@thingToolBAr");
	        titleLabel.setText(UtilString.getString("lang:d=事物：&en=Thing", actionContext) + index.getLabel() 
	             + UtilString.getString("lang:d=，路径：&en=,path:", actionContext) + index.getPath());
	    }
	    
	    if(actionContext.get("packageViewerHelpBrowser") != null){
	    	Browser packageViewerHelpBrowser = actionContext.getObject("packageViewerHelpBrowser");
	    			
	        if(helpThing != null){
	            String url = XWorkerUtils.getThingDescUrl(helpThing);
	            packageViewerHelpBrowser.setUrl(url);
	        }else{
	            packageViewerHelpBrowser.setText("未知的类型：" + indexType);
	        }
	    }
	    
	    ToolBar buttonToolBar = actionContext.getObject("buttonToolBar");
	    Composite menuBarCompoiste = actionContext.getObject("menuBarCompoiste"); 
	    for(ToolItem item : buttonToolBar.getItems()){
	        item.dispose();
	    }
	    
	    if(toolbarThing != null){
	        actionContext.peek().put("parent", buttonToolBar);
	        for(Thing child : toolbarThing.getChilds()){
	            child.doAction("create", actionContext);
	        }
	        
	        menuBarCompoiste.layout();
	        buttonToolBar.update();
	    }
	}
	
	
	public static XWorkerPackageViewer createInstance(ActionContext actionContext){
	    //return new MyClass();    
	    String key = XWorkerPackageViewer.class.getName();
	    XWorkerPackageViewer obj = actionContext.getObject(key);
	    if(obj == null){
	        obj = new XWorkerPackageViewer();
	        actionContext.g().put(key, obj);
	    }
	    
	    return obj;
	}    
	    
	@ActionField
	public ActionContext actionContext;
	
	@ActionField
	public DataTable dataTable;
	
	@ActionField
	public TreeItem treeItem;
	
	@ActionField
	public Tree projectTree;
	
	@ActionField
	public ActionContainer actions;
	
	@ActionField
	public ActionContainer explorerActions;
	
	public World world = World.getInstance();
	
	
}
