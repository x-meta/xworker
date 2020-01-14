package xworker.ide.worldexplorer.editor.thingEditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import org.xmeta.cache.ThingEntry;
import org.xmeta.codes.XmlCoder;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.ThingUtil;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;
import org.xml.sax.SAXException;

import ognl.Ognl;
import ognl.OgnlException;
import xworker.command.interactive.InteractiveUI;
import xworker.ide.assistor.Assistor;
import xworker.ide.utils.ThingGuide;
import xworker.ide.worldexplorer.editor.OutlineTreeMoveListener;
import xworker.ide.xmleditor.ThingXmlDocument;
import xworker.ide.xmleditor.ThingXmlEditorActions;
import xworker.ide.xmleditor.XmlThingLocation;
import xworker.listeners.SwtMenuListener;
import xworker.swt.editor.EditorModifyListener;
import xworker.swt.editor.LabelToolTipListener;
import xworker.swt.events.SwtListener;
import xworker.swt.form.ThingDescriptorForm;
import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtDialogCallback;
import xworker.swt.util.SwtTextUtils;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.XWorkerTreeUtil;
import xworker.swt.xworker.DataTable;
import xworker.util.XWorkerUtils;
import xworker.utils.ThingBackupUtil;

@ActionClass(creator="create")
public class ThingEditorActions {
	private static Logger log = LoggerFactory.getLogger(ThingEditorActions.class);
	
	public static ThingEditorActions create(ActionContext actionContext) {
		String key = "__ThingEditorActions__key__";
		ThingEditorActions ac = actionContext.getObject(key);
		if(ac == null) {
			ac = new ThingEditorActions();
			actionContext.g().put(key, ac);
		}
		return ac;
	}
	
	@ActionField
	ThingEntry thingEntry;
	
	@ActionField
	Tree innerOutline;
	
	@ActionField
	ActionContainer actions;
	
	@ActionField
	Button addChildButton;

	@ActionField
	Composite childContentComposite;
	
	@ActionField
	Composite menuBarComposite;
	
	@ActionField
	Composite guideComposite;
	
	@ActionField
	Combo descriptorsCombo;
	
	@ActionField
	Combo structCombo;
	
	@ActionField
	Combo extendsCombo;
	
	@ActionField
	Combo methodsCombo;
	
	@ActionField
	SashForm addChildSashForm;
	
	@ActionField
	Composite contentEditComposite;
	
	@ActionField
	Composite editPartComposite;
	
	@ActionField
	StackLayout editPartCompositeStackLayout;
	
	@ActionField
	ActionContext currentModelContext;
	
	@ActionField
	ActionContext editorThingContext;
	
	@ActionField
	Thing currentModel;
	
	@ActionField
	Thing editorThing;
	
	@ActionField
	Map<String, Map<String, Object>> dataCache;
	
	@ActionField
	Thing thing;
	
	World world = World.getInstance();
	
	public Object checkThing(ActionContext actionContext) {		

		/*
		暂时删除检测，有bug存在，2013-04-01
		if(thing.metadata.isRemoved()){
		    MessageBox box = new MessageBox(innerOutline.getShell(), SWT.OK | SWT.CANCEL | SWT.ICON_WARNING);
		    box.setMessage("事物文件已更新或者节点已经被删除，是否要重新读取？");
		    box.setText("警告");
		    if(SWT.OK == box.open()){
		        actionContext.getScope(0).put("rootLastModified", currentThing.getRoot().getMetadata().getLastModified());
		        outlineTree.select(innerOutline.getItems()[0]);
		        refreshMenuSelection.handleEvent(null);
		        dataThingCache.clear();    
		        dataCache.clear();
		        actions.doAction("modify", ["setModified":false]);
		        return "changed";
		    }
		}*/

		String key = "__checking_thing__";
		//查看事物是否在外部被改变了
		if(actionContext.get(key) == null && actionContext.get("thingEntry") != null && thingEntry.isChanged()){    
		    //刷新编辑器的事物时间，避免再次重新刷新
		    try{
		        //log.info("lastmodified=" + thingEntry.getThing().getMetadata().getLastModified());
		        actionContext.push().put(key, true);
		        thingEntry.getThing();
		        		        
		        TreeItem treeItem = null;
		        TreeItem[] selections = innerOutline.getSelection();
		        if(selections != null && selections.length > 0) {
		        	treeItem = selections[0];
		        }else {		        	
		        	treeItem = innerOutline.getItems()[0];
		        }
		        Thing thing = (Thing) treeItem.getData();
		        actionContext.peek().put("disableOpenThingAfterRefresh", true);
		        
		        //事物变了应该从根本开始刷新		        
		        innerOutline.select(innerOutline.getItems()[0]);
		        actions.doAction("refreshOutline", actionContext, "refreshThing", thing);
		        
		        if(World.getInstance().getThing(thing.getMetadata().getPath()) != null) {
		        	actions.doAction("selectThing", actionContext, "thing", thing);
		        }
		        return "changed";
		    }finally{
		        actionContext.pop();
		    }
		}

		thing = world.getThing(thing.getMetadata().getPath());
		if(thing == null){
		    //节点已经被删除
			Action showErr = actionContext.getObject("showErr");
		    showErr.run(actionContext, "message", "节点已经被删除！");
		    
		    return null;
		}else{    
		    return thing;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void openThing(ActionContext actionContext) {
		//long start = System.currentTimeMillis();
		TreeItem treeItem = innerOutline.getSelection()[0];
		Object treeThing = treeItem.getData();
		//检测节点是否存在
		treeThing = actions.doAction("checkThing", actionContext, "thing", treeThing);
		if(treeThing instanceof String){
		    return;
		}
		if(treeThing == null){
		    treeItem.dispose();
		    return;
		}
		treeItem.setData(treeThing);
		Thing currentThing = actionContext.getObject("currentThing");
		if(treeThing == currentThing){
		    //return;
		}else if(currentThing != null && UtilData.isTrue(actions.doAction("isXmlEditor", actionContext))
				&& UtilData.isTrue(actionContext.get("modified"))){
		    if(UtilData.isTrue(actions.doAction("save")) == false){
		         return ;
		    }
		}

		//情况添加子事物的面板和按钮状态
		addChildButton.setEnabled(false);
		for(Control childControl : childContentComposite.getChildren()){
		    childControl.dispose();
		}

		//保存当前事物的编辑缓存
		actions.doAction("saveEditCache");

		//def treeItem = outlineTree.getSelection()[0];
		if(innerOutline.getSelection().length == 0){
		    if(innerOutline.getItems() != null && innerOutline.getItems().length > 0){
		        innerOutline.select(innerOutline.getItems()[0]);
		    }else{
		        return;
		    }
		}

		currentThing = (Thing) treeThing;
		actionContext.getScope(0).put("currentThing", currentThing);
		actionContext.getScope(0).put("currentItem", treeItem);
		actionContext.getScope(0).put("rootLastModified", currentThing.getRoot().getMetadata().getLastModified());

		//初始化描述者
		if(menuBarComposite != null && !menuBarComposite.isDisposed()){
		    descriptorsCombo.removeAll();
		    structCombo.removeAll();
		    List<Thing> structures = ((Thing) treeThing).getDescriptors();
		    List<Thing> childComboDatas = new ArrayList<Thing>();
		    for(Thing str : structures){
		        childComboDatas.add(str);
		    }
		    descriptorsCombo.setData(structures);
		    structCombo.setData(childComboDatas);
		    for(Thing struct : structures){
		        descriptorsCombo.add(struct.getMetadata().getLabel());
		    }
		    for(Thing struct : childComboDatas){
		        structCombo.add(struct.getMetadata().getLabel());
		    }
		    childComboDatas.add(world.getThing("xworker.lang.util.ThingTemplate"));
		    //初始化继承
		    List<Thing> exts = ((Thing) treeThing).getExtends();
		    extendsCombo.setData(exts);
		    extendsCombo.removeAll();
		    for(Thing ext : exts){
		        extendsCombo.add(ext.getMetadata().getLabel());
		    }
		    
		    //初始化方法列表
		    methodsCombo.removeAll();
		    List<Thing> scriptObjects = ((Thing) treeThing).getActionsThings();
		    List<String> actionList = new ArrayList<String>();
		    for(Thing sobj : scriptObjects){        
		        actionList.add(sobj.getMetadata().getName());
		    }
		    Collections.sort(actionList);
		    for(String action : actionList){
		        methodsCombo.add(action);
		    }
		    methodsCombo.setData(scriptObjects);
		    
		    //选择默认的节点
		    descriptorsCombo.select(0);
		    structCombo.select(0);
		}

		//log.info("初始化：" + (System.currentTimeMillis() - start));
		//start = System.currentTimeMillis();

		//初始化编辑表单
		if(editPartCompositeStackLayout.topControl == guideComposite){
		    //不能设置为取消，否则会递归，向导现在默认取消，以后可能会有其他动作
		    //thingGuide.cancel();
		    //editPartCompositeStackLayout.topControl = contentEditComposite;
		   // editPartComposite.layout();
		}else if(!UtilData.isTrue(actions.doAction("isXmlEditor", actionContext))){
		    //表单编辑
		    ((Listener) actionContext.get("descriptSelection")).handleEvent(null);  
		}else{    
		    //xml编辑器
		    actions.doAction("xmlSetCurrentThing", actionContext);    
		}
		//log.info("打开编辑表单：" + (System.currentTimeMillis() - start));
		//start = System.currentTimeMillis();

		//初始化添加子节点
		//由于子节点需要通过点击按钮才能看见，因此默认不初始化
		actionContext.getScope(0).put("doInitChildTree", false);
		((Listener) actionContext.get("descriptsComboSelection")).handleEvent(null);

		//切换到编辑界面
		if(editPartCompositeStackLayout.topControl == addChildSashForm){
			editPartCompositeStackLayout.topControl = contentEditComposite;
			editPartComposite.layout();
		}
		//log.info("打开编辑表单2：" + (System.currentTimeMillis() - start));
		//start = System.currentTimeMillis();
		//编辑缓存
		if(dataCache == null) {
			dataCache = new HashMap<String, Map<String, Object>>();
			actionContext.g().put("dataCache", dataCache);
		}
		Map<String, Object> data = dataCache.get(currentThing.getMetadata().getPath());
		if(data != null){
		    if((Long) data.get("time") < currentThing.getMetadata().getLastModified()){
		    	Action clearCache = actionContext.getObject("clearCache");
		         clearCache.run(actionContext, "dataCache", dataCache);
		         /*
		         MessageBox box = new MessageBox(structCombo.getShell(), SWT.YES | SWT.NO | SWT.ICON_QUESTION);
		         box.setText("操作信息");
		         box.setMessage("当前事物比已修改的内容要新，是否放弃修改？");
		         if(SWT.YES == box.open()){
		             //刷新当前节点
		             dataCache.clear();
		             return;
		         }*/
		    }
		    Map<String, Object> attrs = new HashMap<String, Object>();
		    attrs.putAll(currentThing.getAttributes());
		    attrs.putAll((Map<String, Object>) data.get("data"));
		    currentModelContext.put("thingAttributes", attrs);
		    currentModel.doAction("setValue", currentModelContext);
		}

		//通知辅助者
		//Assistor.thingEditorThingSelected(null, treeItem, thing, actionContext, actions);
		InteractiveUI innerOutlineInteractiveUI = actionContext.getObject("innerOutlineInteractiveUI");
		innerOutlineInteractiveUI.fire(currentThing);

		//onNodeSelected事件
		if(actionContext.get("editorThing") != null && actionContext.get("editorThingContext") != null){
		    //println("xxxxxxxxxxxxxxxxxx=" + editorThing.getMetadata().getPath());
		    editorThing.doAction("onNodeSelected", editorThingContext, "thing", currentThing); 
		}else{
		    //println "editorThing=" + actionContext.get("editorThing");
		    //println "editorThingContext=" + actionContext.get("editorThingContext");
		}
		//log.info("处理其他：" + (System.currentTimeMillis() - start));
		//start = System.currentTimeMillis();
	}
	
	public Object openThingClearCacheOk(ActionContext actionContext) {
		dataCache.clear();
		return null;
	}
	
	public Object setThing(final ActionContext actionContext) {
		//判断当前编辑的事物是不是同一个事物，如果是同一个则不用刷新的
		if(innerOutline.getItems().length != 0 && innerOutline.getItems()[0].getData() == thing){
		    return null;
		}

		//保存thingEntry，用于判断是否在外部修改了
		ThingEntry thingEntry = new ThingEntry(thing);
		//log.info("lastmodified=" + thing.getMetadata().getLastModified());
		actionContext.g().put("thingEntry", thingEntry);

		//初始化新的概要数
		for(TreeItem item : innerOutline.getItems()){
		    item.dispose();
		}
		actions.doAction("clearCache");
		actionContext.getScope(0).put("innerOutlineExpandCache", new HashMap<String, Object>());

		TreeItem treeItem = new TreeItem(innerOutline, SWT.NONE);
		actions.doAction("initOutlineTreeItem", actionContext, "thing", thing, "treeItem", treeItem);
		//def initOutline = new Thing("xworker.ide.worldExplorer.swt.actions.OutlineTreeAction/@InitOutlineTree");
		//initOutline.doAction("run", actionContext, ["tree": innerOutline, "thing": thing]);

		//是否能够保存
		ThingManager thingManager = thing.getMetadata().getThingManager();
		if(thingManager != null && !thingManager.isSaveable()){
		    ((ToolItem) actionContext.get("okItem")).setEnabled(false);
		    ((ToolItem) actionContext.get("deleteItem")).setEnabled(false);
		    ((ToolItem) actionContext.get("moveUpItem")).setEnabled(false);
		    ((ToolItem) actionContext.get("moveDownItem")).setEnabled(false);
		}else{
			((ToolItem) actionContext.get("okItem")).setEnabled(true);
			((ToolItem) actionContext.get("deleteItem")).setEnabled(true);
			((ToolItem) actionContext.get("moveUpItem")).setEnabled(true);
			((ToolItem) actionContext.get("moveDownItem")).setEnabled(true);    
		}

		if(thing.isTransient() && !((Label) actionContext.get("titleLabel")).isDisposed()){
			((Label) actionContext.get("titleLabel")).setForeground(
					((Composite) actionContext.get("mainComposite")).getDisplay().getSystemColor(SWT.COLOR_RED));
		}

		//下面两个方法加入到asyncExec中，因为不在里面由于未知原因ubuntu下会崩溃
		innerOutline.getDisplay().asyncExec(new Runnable(){
			public void run(){		
			   //默认选择根结点的数据
			   innerOutline.setSelection(innerOutline.getItems()[0]);
			   actions.doAction("selectThing", actionContext, "refresh", true, "thing", thing);
			   //innerOutline.notifyListeners(SWT.Selection, null); //触发选择事件
			}
		});
		/*
		//初始化概要树
		innerOutline.removeAll();
		def refreshAction = world.getAction("xworker.ide.worldExplorer.swt.shareScript.ThingEditor/@scripts/@initOutline");
		refreshAction.run(null, ["tree":outlineTree, "thing":thing, "itemIndex":null]);
		outlineTree.setSelection(innerOutline.getItems()[0]);
		*/
		return null;
	}
	
	public Object selectThing(ActionContext actionContext) {
		if(innerOutline.isDisposed()) {
			return null;
		}
		
		Object thing = actionContext.get("thing");
		//println "select thing=" + thing;
		if(thing == null) return null;

		if(thing instanceof String){
		    Thing root = (Thing) innerOutline.getItems()[0].getData();
		    String thingPath = (String) thing;
		    thing = root.getThing(thingPath);
		    if(thing == null){
		        thing = world.getThing(thingPath);
		    }
		    if(thing == null){
		        //log.info("thing not exists, path=" + thingPath);
		        return null;
		    }
		}

		if(UtilData.isTrue(actionContext.get("refresh")) == true){    
		    innerOutline.setSelection(innerOutline.getItems()[0]);
		    Listener refreshMenuSelection = actionContext.getObject("refreshMenuSelection");
		    refreshMenuSelection.handleEvent(null);
		}

		Thing currentThing = actionContext.getObject("currentThing");
		if(currentThing == thing && innerOutline.getSelection()[0].getData() == thing){
		    return null;
		}else{
		    actionContext.g().put("currentThing", thing);
		}

		TreeItem it = null;
		for(TreeItem item : innerOutline.getItems()){
		    it = getItem((Thing) thing, item);
		    if(it != null) break;
		}

		if(it != null && innerOutline.getSelection().length > 0 
				&& (it != innerOutline.getSelection()[0] || UtilData.isTrue(actionContext.get("refresh")) == true)){
		    it.getParent().setSelection(it);	

		    //如果是xml编辑器且是选择的xml中的子节点，那么不需要触发选择事件
		    boolean notifyListener = true;
		    if(UtilData.isTrue(actions.doAction("isXmlEditor")) && UtilData.isTrue(actionContext.get("xmlEditorShowThing"))){
		        notifyListener = false;
		        /*
		        def xpath = xmlCurrentThing.getMetadata().getPath();
		        def path = xmlRootThing.getMetadata().getPath();
		        if(xpath == path || xpath.startsWith(path)){
		            notifyListener = false;
		        }*/
		    }
		    //println(notifyListener);
		    if(notifyListener){
		        it.getParent().notifyListeners(SWT.Selection, null);
		    }
		}

		if(UtilData.isTrue(actions.doAction("isXmlEditor")) == true){
		}else{
		    //可以从添加子节点的界面切换到编辑界面
			StackLayout contentEditCompositeStackLayout = actionContext.getObject("contentEditCompositeStackLayout");
			Composite contentComposite = actionContext.getObject("contentComposite");
			Composite mainComposite = actionContext.getObject("mainComposite");
			
		    contentEditCompositeStackLayout.topControl = contentComposite;
		    mainComposite.layout();
		}
		return actionContext;
	}
	
	private TreeItem getItem(Thing dataObj, TreeItem item) {
		Thing data = (Thing) item.getData();
		if(data.getMetadata().getPath().equals(dataObj.getMetadata().getPath())){
	        return item;
	    }
	    
	    for(TreeItem subItem : item.getItems()){
	        TreeItem it = getItem(dataObj, subItem);
	        if(it != null) return it;
	    }

	    return null;
	}
	
	@SuppressWarnings({ "unchecked"})
	public Object save(ActionContext actionContext) throws IOException {
		//阻止保存时出现的递归
		if(UtilData.isTrue(actionContext.get("stopSaving")) || actionContext.get("currentThing") == null){
		    return null;
		}

		Composite xmlComposite = actionContext.getObject("xmlComposite");
		Control xmlText = actionContext.getObject("xmlText");
		Thing xmlRootThing = actionContext.getObject("xmlRootThing");
		Action showErr = actionContext.getObject("showErr");
		Thing currentThing = actionContext.getObject("currentThing");
		Map<String, Thing> dataThingCache = actionContext.getObject("dataThingCache");
		
		if(editPartCompositeStackLayout.topControl == xmlComposite){
		    //在XML的编辑状态下
		    try{
		        //从XML中读取事物
		        Thing thing = new Thing();
		        XmlCoder.parse(thing, SwtTextUtils.getText(xmlText));
		        
		        //使用当前事物覆盖当前节点的事物
		        //currentThing.getChilds().clear();//先清空子节点，好像replace也可以清空子节点
		        xmlRootThing.replace(thing);
		        xmlRootThing.save();
		        
		        thingEntry.getThing();
		        //刷新结构树，同时阻止xmlEditor重新设置xml
		        actions.doAction("refreshOutline", actionContext, "refreshThing", xmlRootThing,
		                "xmlEditorNotReset", true, "stopSaving", true);
		        //actions.doAction("selectThing", actionContext, "thing", xmlCurrentThing);
		        
		        //重新选择xmlEditor中光标位置的事物
		        Thing editThing = actions.doAction("xmlEditorGetSelectThing");
		        editThing = world.getThing(editThing.getMetadata().getPath());
		        actionContext.g().put("currentThing", null);
		        actionContext.g().put("xmlCurrentThing", editThing);
		        
		        //actions.doAction("xmlEditorShowThing", actionContext, "thing", editThing, "xmlEditorNotReset", true);
		        actions.doAction("selectThing", actionContext, "thing", editThing, "xmlEditorShowThing", true);
		        xmlText.setData("modified", false);		        
		    }catch(Exception e){
		        showErr.run(actionContext,"message", e.getMessage());
		        
		        return false;
		    }    
		}else{
		    //检测节点是否存在
		    Object checkedThing = actions.doAction("checkThing", actionContext, "thing", currentThing);
		    if(checkedThing == null || checkedThing instanceof String){
		        return false;
		    }
		    //checkedThing = checkedThing;
		    
		    if(currentModel != null){
		        if(!UtilData.isTrue(currentModel.doAction("validate", currentModelContext))) return false;
		        
		        //保存缓存
		        for(String key : dataCache.keySet()){
		            if(key.equals(currentThing.getMetadata().getPath())){
		                continue;
		            }
		        		            
		            Thing keyThing = dataThingCache.get(key);    
		            Map<String, Object> keyData = dataCache.get(key);
		            if(keyThing != null && keyData != null && !keyThing.getMetadata().isRemoved()){
		                keyThing.cognize((Map<String, Object>) keyData.get("data"));
		            }
		        }
		        dataCache.clear();
		        dataThingCache.clear();
		        
		        //设置和保存
		        Map<String, Object> editedData = currentModel.doAction("getValue", currentModelContext);
		        currentThing.cognize(editedData);      
		    }
		    
		    currentThing.save();
		    actionContext.getScope(0).put("rootLastModified", currentThing.getRoot().getMetadata().getLastModified());
		    
  		    //重新读取，更新缓存
			thingEntry.getThing();
			currentThing = world.getThing(currentThing.getMetadata().getPath());
			TreeItem treeItem = innerOutline.getSelection()[0];
			actions.doAction("initOutlineTreeItem", actionContext, "thing", currentThing, "treeItem", treeItem);
		}

		
		//treeItem.setData(currentThing);
		//treeItem.setText(currentThing.metadata.label + " (" + currentThing.thingName + ")");

		actions.doAction("modify", actionContext, "setModified", false);
		for(TreeItem item : innerOutline.getItems()){
		    initItemThing(item);
		}

		//保存备份
		ThingBackupUtil.backup(currentThing);

		return true;
	}
	
	private void initItemThing(TreeItem item) {
		Thing thing = (Thing) item.getData();
	    if(thing == null){
	        item.dispose();        
	    }else{
	        item.setData(world.getThing(thing.getMetadata().getPath()));
	        for(TreeItem childItem : item.getItems()){
	            initItemThing(childItem);
	        }
	    }
	}
	
	public Object delete(ActionContext actionContext) {
		return null;
	}
	
	public Object moveUp(ActionContext actionContext) {		
		return null;
	}
	
	public Object moveDown(ActionContext actionContext) {
		return null;
	}
	
	public Object clearCache(ActionContext actionContext) {
		if(actionContext.get("dataCache") != null){
		    dataCache.remove(thing.getMetadata().getPath());
		    
		    Map<String, Object> dataThingCache = actionContext.getObject("dataThingCache"); 
		    dataThingCache.remove(thing.getMetadata().getPath());
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Object saveEditCache(ActionContext actionContext) {
		//保存当前事物的编辑缓存
		if(actionContext.get("dataCache") == null){
		    Map<String, Map<String, Object>> dataCache = new HashMap<String, Map<String, Object>>();    
		    Map<String, Object> dataThingCache = new HashMap<String, Object>();
		    actionContext.getScope(0).put("dataCache", dataCache);
		    actionContext.getScope(0).put("dataThingCache", dataThingCache);
		}

		if((Boolean) actions.doAction("isXmlEditor")){
		    //xml没有cache缓存
		    return null;
		}

		Thing currentThing = actionContext.getObject("currentThing");
		Map<String, Object> dataThingCache = actionContext.getObject("dataThingCache");
		if(actionContext.get("currentModel") != null && UtilData.isTrue(actionContext.get("modified"))){
		    Map<String, Object> data = currentModel.doAction("getValue", currentModelContext);
		    Map<String, Object> cache = dataCache.get(currentThing.getMetadata().getPath());
		    if(cache == null){
		        cache = new HashMap<String, Object>();
		        cache.put("data", data);
		        cache.put("time", currentThing.getMetadata().getLastModified());
		    }else{
		    	Map<String, Object> cdata = (Map<String, Object>) cache.get("data");
		        cdata.putAll(data);
		    }
		    
		    dataCache.put(currentThing.getMetadata().getPath(), cache);
		    dataThingCache.put(currentThing.getMetadata().getPath(), currentThing);
		    
		    /*
		    //已经在Tb显示已修改的状态，故取消此代码
		    if(actionContext.get("currentItem") != null && !currentItem.isDisposed()){
		        currentItem.setData("modified", true);
		        actions.doAction("displayItemText", ["treeItem":currentItem]);
		    }*/
		}
		return null;
	}
	
	public Object displayItemText(ActionContext actionContext) {
		Object treeItem = actionContext.getObject("treeItem");
		if(actionContext.get("treeItem") != null && treeItem instanceof TreeItem){
		    Thing thing = (Thing) ((TreeItem) treeItem).getData();
		    
		    String text = thing.getMetadata().getLabel() + "(" + thing.getDescriptors().get(0).getMetadata().getName() + ")";
		    if(UtilData.isTrue(((TreeItem) treeItem).getData("modified")) != true){
		        text = text + "*";
		    }
		    
		    ((TreeItem) treeItem).setText(text);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Object modify(ActionContext actionContext) throws OgnlException {
		CTabItem contentTab = actionContext.getObject("contentTab");
		
		if(actionContext.get("setModified") != null && UtilData.isTrue(actionContext.get("setModified")) == false){
		    if(UtilData.isTrue(actionContext.get("modified")) == true){
		        if(actionContext.get("contentTab") != null){		        	
		            contentTab.setText(contentTab.getText().substring(1, contentTab.getText().length()));
		        }
		    }
		    
		    actionContext.put("modified", false);    
		    //eclipse插件的thingEditor变量
		    if(actionContext.get("thingEditor") != null){
		    	Ognl.getValue("thingEditor.setDirty(false)", actionContext);
		    	//Ognl.setValue("dirty", actionContext.get("thingEditor"), false);
		        //thingEditor.setDirty(false);
		    }
		    
		    //作为事物编辑器时的事件触发
		    if(actionContext.get("editorThing") != null && actionContext.get("editorThingContext") != null){
		        editorThing.doAction("modify", editorThingContext, "modified", false); 
		    }            
		}else if(UtilData.isTrue(actionContext.get("modified")) != true){
		    actionContext.put("modified", true);
		    
		    if(actionContext.get("contentTab") != null){
		        contentTab.setText("*" + contentTab.getText());
		    }

		    //eclipse插件的thingEditor变量
		    if(actionContext.get("thingEditor") != null){
		    	Ognl.getValue("thingEditor.setDirty(true)", actionContext);
		    	//Ognl.setValue("dirty", actionContext.get("thingEditor"), true);
		        //thingEditor.setDirty(true);
		    }

		    //作为事物编辑器时的事件触发
		    if(actionContext.get("editorThing") != null && actionContext.get("editorThingContext") != null){
		        editorThing.doAction("modify", editorThingContext, "modified", true); 
		    }
		}

		//设置标题
		if(actionContext.get("titleLabel") != null){    
			Label titleLabel = actionContext.getObject("titleLabel");
		    int index = descriptorsCombo.getSelectionIndex();
		    if(index == -1){
		        return null;
		    }
		    Thing objStruct = ((List<Thing>) descriptorsCombo.getData()).get(index);
		    Thing thing = actionContext.getObject("currentThing");
		    if(UtilData.isTrue(actionContext.get("modified")) == true){
		        titleLabel.setText("*** " + thing.getMetadata().getLabel() + "－" + objStruct.getMetadata().getName() + "（" + objStruct.getMetadata().getLabel() + "）***");
		    }else{
		        titleLabel.setText(thing.getMetadata().getLabel() + "－" + objStruct.getMetadata().getName() + "（" + objStruct.getMetadata().getLabel() + "）");
		    }
		}
		return null;
	}
	
	public Object refreshOutline(ActionContext actionContext) {
		//强制刷新事物的时间，避免稍候被判断是外部改动重新刷新
		thingEntry.getThing();

		Listener refreshMenuSelection = actionContext.getObject("refreshMenuSelection"); 
		refreshMenuSelection.handleEvent(null);
		
		if(actionContext.get("refreshThing") != null){
			Object refreshThing = actionContext.get("refreshThing"); 
		    actions.doAction("selectThing", actionContext, "thing", refreshThing, "refrsh", false);    
		}
		return null;
	}
	
	public Object setMenuVisiable(ActionContext actionContext) {	
		GridData titleTitleCompositeGridData = actionContext.getObject("titleTitleCompositeGridData");
		GridData menuBarCompositeGridData = actionContext.getObject("menuBarCompositeGridData");
		GridData spliteLabel1GridData = actionContext.getObject("spliteLabel1GridData");
		
		if(UtilData.isTrue(actionContext.get("visiable"))){
		    titleTitleCompositeGridData.horizontalSpan = 0;
		    menuBarCompositeGridData.horizontalSpan = 0;
		    spliteLabel1GridData.horizontalSpan = 0;
		}else{
		    titleTitleCompositeGridData.horizontalSpan = 1;
		    menuBarCompositeGridData.horizontalSpan = 1;
		    spliteLabel1GridData.horizontalSpan = 1;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Object getCurrentAttribute(ActionContext actionContext) {
		//先获取编辑器的
		Thing currentThing = actionContext.getObject("currentThing");
		String name = actionContext.getObject("name");
		
		if(currentThing == thing){
		    Map<String, Object> editedData = currentModel.doAction("getValue", currentModelContext);
		    if(editedData.containsKey(name)){
		        return editedData.get(name);
		    }
		}

		//其次获取编辑未保存的
		if(actionContext.get("dataCache") != null){
		    Map<String, Object> cache = dataCache.get(thing.getMetadata().getPath());
		    if(cache != null){
		        //log.info("i1cache=" + cache);
		    	Map<String, Object> data = (Map<String, Object>) cache.get("data");
		        if(data.containsKey(name)){
		            return data.get(name);
		        }        
		    }    
		}

		//最后获取事物本身的
		return thing.get(name);	
	}
	
	public Object initOutlineTreeItem(ActionContext actionContext) {
		//设置内部的outline
		if(thing == null){
		    return null;
		}

		TreeItem treeItem = actionContext.getObject("treeItem");
		XWorkerTreeUtil.initItem(treeItem, thing, actionContext);
		//initItemColor(treeItem, thing);
		treeItem.setText(thing.getMetadata().getLabel());// + "      (" + thing.thingName + ")");
		treeItem.setData(thing);
		return null;
	}
	
	public Object initQuickEditTable(ActionContext actionContext) {
		Thing currentThing = actionContext.getObject("currentThing");
		Table quickTable = actionContext.getObject("quickTable");
		Button quickEditbutton = actionContext.getObject("quickEditbutton");
		Button parentCategoryButton = actionContext.getObject("parentCategoryButton");
		Button childCategoryButton = actionContext.getObject("childCategoryButton");
		
		//先清除表格
		quickTable.removeAll();
		//for(TableItem item : quickTable.getItems()){
		//    item.dispose();
		//}

		//按钮失效
		quickEditbutton.setEnabled(false);
		parentCategoryButton.setEnabled(false);
		childCategoryButton.setEnabled(false);

		//表格数据
		int selection = 0;
		Thing selectThing = null;
		Thing parent = currentThing.getParent();
		if(parent == null){
		    TableItem item = new TableItem(quickTable, SWT.NONE);
		    item.setText(new String[] {currentThing.getMetadata().getName(), currentThing.getMetadata().getLabel(), 
		    		currentThing.getThingName()});
		    item.setData(currentThing);
		    selectThing = currentThing;
		}else{
		    int index = 0;
		    for(Thing child : parent.getChilds()){
		        TableItem item = new TableItem(quickTable, SWT.NONE);
		        item.setText(new String[] {child.getMetadata().getName(), child.getMetadata().getLabel(), child.getThingName()});
		        item.setData(child);
		        
		        if(child == currentThing){
		            selection = index;
		            selectThing = child;
		        }
		        
		        index++;
		    }
		}

		if(quickTable.getItems().length > 0){
		    quickTable.setSelection(selection);
		    quickEditbutton.setEnabled(true);
		    if(parent != null){
		        parentCategoryButton.setEnabled(true);    
		    }
		    if(selectThing != null && selectThing.getChilds().size() > 0){
		        childCategoryButton.setEnabled(true);
		    }
		}
		return null;
	}
	
	public Object innerOutlineExpand(ActionContext actionContext) {
		if(actionContext.get("innerOutlineExpandCache") != null){
			Map<String, Object> innerOutlineExpandCache = actionContext.getObject("innerOutlineExpandCache");
			Event event = actionContext.getObject("event");
		    innerOutlineExpandCache.put(((Thing) event.item.getData()).getMetadata().getPath(), event.item.getData());
		}
		return null;
	}
	
	public Object innerOutlineCollapse(ActionContext actionContext) {
		Map<String, Object> innerOutlineExpandCache = actionContext.getObject("innerOutlineExpandCache");
		Event event = actionContext.getObject("event");
		
		innerOutlineExpandCache.remove(((Thing) event.item.getData()).getMetadata().getPath());
		return null;
	}
	
	public Object initOutlineBrowser(ActionContext actionContext) {
		//println("actions:initOutlineBrowser");
		//初始化outlineBrowser和相关内容
		Browser outlineBrowser = actionContext.getObject("outlineBrowser");
		Thing descriptor = actionContext.getObject("descriptor");
		Thing thing = actionContext.getObject("thing");
		
		if(actionContext.get("outlineBrowser") == null || outlineBrowser.isDisposed()){
		    return null;
		}

		//标题的提示和帮助

		//def webUrl = XWorkerUtils.getThingDescUrl(descriptor);
		if(actionContext.get("outlineBrowser") != null &&  !outlineBrowser.isDisposed()){
		    SwtUtils.setThingDesc(descriptor, outlineBrowser);
		    //outlineBrowser.setUrl(webUrl);
		}

		//属性列表
		actions.doAction("initOutlineAttributes", actionContext, "thing",  thing);

		//类和描述者
		Map<String, Thing> dess = new HashMap<String, Thing>();
		DataTable descriptorsTable = actionContext.getObject("descriptorsTable");
		descriptorsTable.setDatas(Collections.emptyList());
		for(Thing desc : thing.getAllDescriptors()){
		    if(dess.get(desc.getMetadata().getPath()) != null){
		        continue;
		    }
		    dess.put(desc.getMetadata().getPath(), descriptor);
		    
		    Map<String, Object> params = new HashMap<String, Object>();
		    params.put("name", desc.getMetadata().getName());
		    params.put("path", desc.getMetadata().getPath());
		    descriptorsTable.updateRow(null, params);    
		}

		//继承
		Map<String, Object> exss = new HashMap<String, Object>();
		DataTable extendsTable = actionContext.getObject("extendsTable");
		extendsTable.setDatas(Collections.emptyList());
		for(Thing exend : thing.getAllExtends()){
		    if(exss.get(exend.getMetadata().getPath()) != null){
		        continue;
		    }
		    exss.put(exend.getMetadata().getPath(), exend);

		    Map<String, Object> params = new HashMap<String, Object>();
		    params.put("name", exend.getMetadata().getName());
		    params.put("path", exend.getMetadata().getPath());
		    extendsTable.updateRow(null, params);    
		}

		//行为
		actions.doAction("initOutlineThingActions", actionContext, "thing",  thing);
		return null;
	}
	
	public Object initOutlineThingActions(ActionContext actionContext) {
		//动作文档
		Thing thing = actionContext.getObject("thing");
		List<Thing> actionThings = thing.getActionsThings();
		Table actionsTable = actionContext.getObject("actionsTable");
		
		actionsTable.removeAll();
		List<String> acs = new ArrayList<String>();
		Map<String, String> acss = new HashMap<String, String>();
		Map<String, String> acds = new HashMap<String, String>();
		for(Thing ac : actionThings){
		    acs.add(ac.getMetadata().getName());
		    acss.put(ac.getMetadata().getName(), ac.getMetadata().getPath());
		    Thing parent = ac.getParent().getParent(); 
		    if(parent != null){
		        String acDescriptor = parent.getMetadata().getLabel();
		        acds.put(ac.getMetadata().getName(), acDescriptor);
		        acds.put("type_" + ac.getMetadata().getName(), ac.getDescriptor().getMetadata().getLabel());
		    }
		}
		Collections.sort(acs);

		for(String ac : acs){
		    TableItem titem = new TableItem(actionsTable, SWT.NONE);
		    titem.setText(new String[] {ac, acds.get("type_" + ac), acds.get(ac)});
		    titem.setData(xworker.util.UtilData.toMap("path", acss.get(ac)));
		    titem.setData("name", ac);
		}
		return null;
	}
	
	public Object initOutlineAttributes(ActionContext actionContext) {
		//动作文档
		//if(thing.getThingName() == "Shell"){
//		    actionContext.printStackTrace();
		//}
		
		Thing thing = actionContext.getObject("thing");
		List<Thing> actionThings = thing.getAllAttributesDescriptors();
		Table attributesTable = actionContext.getObject("attributesTable");
		attributesTable.removeAll();
		List<String> acs = new ArrayList<String>();
		Map<String, String> acss = new HashMap<String, String>();
		Map<String, String> acds = new HashMap<String, String>();
		
		for(Thing ac : actionThings){
		    String name = ac.getMetadata().getName();
		    String label = ac.getMetadata().getLabel();
		    if(label != name){
		        label = name + "(" + label + ")";
		    }
		    acs.add(name);
		    acss.put(name, ac.getMetadata().getPath());
		    String acDescriptor = ac.getParent().getMetadata().getLabel();
		    acds.put(name, acDescriptor);
		    //值
		    String value = thing.getStringBlankAsNull(name);
		    if(value == null){
		        value = "";
		    }
		    acds.put("type_" + name, value);
		    
		    acds.put("label_" + name, label);
		}
		Collections.sort(acs);

		//按描述者分组
		List<String> dess = new ArrayList<String>();
		Map<String, String> desc = new HashMap<String, String>();
		for(Thing descrptor : thing.getDescriptors()){
		    String name =  descrptor.getMetadata().getName();
		    dess.add(name);
		    desc.put(name, name);
		}
		for(String ac : acs){
		    String name = acds.get(ac);
		    if(desc.get(name) != null){
		        continue;
		    }
		    
		    dess.add(name);
		    desc.put(name,name);
		}

	    for(String ac : acs){
	            TableItem titem = new TableItem(attributesTable, SWT.NONE);
	            titem.setText(new String[] {ac, acds.get("type_" + ac), acds.get(ac)});
	            titem.setData(xworker.util.UtilData.toMap("path", acss.get(ac)));
	            titem.setData("name", ac);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Object initToobarMenu(ActionContext actionContext) {
		//初始化描述者
		if(menuBarComposite != null && !menuBarComposite.isDisposed()){
		    descriptorsCombo.removeAll();
		    structCombo.removeAll();
		    List<Thing> structures = thing.getDescriptors();
		    List<Thing> childComboDatas = new ArrayList<Thing>();
		    for(Thing str : structures){
		        childComboDatas.add(str);
		    }
		    descriptorsCombo.setData(structures);
		    structCombo.setData(childComboDatas);
		    for(Thing struct : structures){
		        descriptorsCombo.add(struct.getMetadata().getLabel());
		    }
		    for(Thing struct : childComboDatas){
		        structCombo.add(struct.getMetadata().getLabel());
		    }
		    childComboDatas.add(world.getThing("xworker.lang.util.ThingTemplate"));
		    //初始化继承
		    List<Thing> exts = thing.getExtends();
		    extendsCombo.setData(exts);
		    extendsCombo.removeAll();
		    for(Thing ext : exts){
		        extendsCombo.add(ext.getMetadata().getLabel());
		    }
		    
		    //初始化方法列表
		    methodsCombo.removeAll();
		    List<Thing> scriptObjects = thing.getActionsThings();
		    List<String> actionList = new ArrayList<String>();
		    for(Thing sobj : scriptObjects){        
		        actionList.add(sobj.getMetadata().getName());
		    }
		    Collections.sort(actionList);
		    for(String action : actionList){
		        methodsCombo.add(action);
		    }
		    methodsCombo.setData(scriptObjects);
		    
		    //选择默认的节点
		    descriptorsCombo.select(0);
		    structCombo.select(0);
		}

		//初始化菜单
		//取当前选择的描述
		int index = descriptorsCombo.getSelectionIndex();
		Thing objStruct = ((List<Thing>) descriptorsCombo.getData()).get(index);
		//Thing structureThing = objStruct;

		//标题的提示和帮助
		String webUrl = XWorkerUtils.getThingDescUrl(objStruct);
		/*

		if(actionContext.get("outlineBrowser") != null &&  !outlineBrowser.isDisposed()){
		    outlineBrowser.setUrl(webUrl);
		}
		actions.doAction("initOutlineBrowser", actionContext, ["descriptor": objStruct, "thing": currentThing]);
		*/

		Composite titleComposite = actionContext.getObject("titleComposite");
		Label titleLabel = actionContext.getObject("titleLabel");
		if(titleComposite != null && !titleComposite.isDisposed()){
		    titleLabel.setText(UtilString.getString("res:res.w_exp:editLabel:编辑：", actionContext) 
		    		+ thing.getMetadata().getLabel() + "－" + objStruct.getMetadata().getName() 
		    		+ "（" + objStruct.getMetadata().getLabel() + "）");
		    
		    String toolTipText = "<b><u><a href=\"" + webUrl + 
		        "do?sc=xworker.ide.worldExplorer.swt.http.ThingDoc/@desc&thing=" + objStruct.getMetadata().getPath() + "\">" +
		        objStruct.getMetadata().getName() + "</a></u></b><br/><br/>";    			    
		        
		    //log.info(    toolTipText);
		    if(objStruct.getMetadata().getDescription() != null){
		        String ttText = objStruct.getMetadata().getDescription();
		        titleLabel.setData("toolTip", toolTipText + ttText);
		    }else{
		        titleLabel.setData("toolTip", toolTipText);
		    }
		}

		if(menuBarComposite != null && !menuBarComposite.isDisposed()){
		    //初始化菜单
		    List<Thing> menus = null;
		    SwtMenuListener swtMenu = SwtMenuListener.getInstance();
		    if(objStruct != null){
		        String[] paths = thing.getDescriptors().get(0).getMetadata().getPaths();
		        menus = swtMenu.getMenu(thing, paths, "data");
		    }else{        	
		    	String structurePath = actionContext.getObject("structurePath");
		    	String descriptors = actionContext.getObject("descriptors");
		        String strFullPath = (structurePath == null || "".equals(structurePath.trim())) ? descriptors : (descriptors + ":" + structurePath);
		        menus = swtMenu.getMenu(thing, new String[] {strFullPath}, "data");
		    }
		    
		    Thing toolItemListenerObject = world.getThing("xworker.ide.worldExplorer.swt.dataExplorerParts.ThingEditor/@tempShell/@listenersPrepared/@toolBarItemSelection");
		    Thing disposeListenerObject = world.getThing("xworker.ide.worldExplorer.swt.dataExplorerParts.ThingEditor/@tempShell/@listenersPrepared/@dispose");
		    Thing menuListenerObject = world.getThing("xworker.ide.worldExplorer.swt.dataExplorerParts.ThingEditor/@tempShell/@listenersPrepared/@menuSelection");
		    
		    ToolBar toolBar = actionContext.getObject("toolBar");
		    for(ToolItem item : toolBar.getItems()){
		        item.dispose();
		    }
		    
		    for(Thing menu : menus){
		        ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		        //println item;
		        item.setText(menu.getMetadata().getLabel());
		        item.addListener(SWT.Selection, new SwtListener(toolItemListenerObject, actionContext));
		        item.addListener(SWT.Dispose, new SwtListener(disposeListenerObject, actionContext));
		        
		        String attachUrl = "&thingName=" + thing.getMetadata().getPath() + "&descriptors=";
		        //创建菜单
		        Menu amenu = new Menu(toolBar.getShell(), SWT.POP_UP);
		        //item.setMenu(amenu);
		        for(Thing child : menu.getChilds()){
		            initMenuItem(amenu, child, attachUrl, menuListenerObject, actionContext);
		        }    
		        item.setData("menu", amenu);
		    }
		}
	
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void initMenuItem(Menu parent, Thing menuData, String attachUrl,Thing  menuListenerObject, ActionContext swtActionContext) {
		int style = SWT.PUSH;
	    if(menuData.getChilds().size() > 0){
	        style = SWT.CASCADE;
	    }else if(menuData.getBoolean("isSplit")){
	        style = SWT.SEPARATOR;
	    }
	        
	    MenuItem mitem = new MenuItem(parent, style);;
	    
	    mitem.setData("menu", menuData);
	    String accelerator = menuData.getStringBlankAsNull("accelerator");
	    if(accelerator != null){
	        mitem.setText(menuData.getMetadata().getLabel() + "\t" + accelerator);        
	        mitem.setAccelerator(SwtUtils.getAccelerator(accelerator));
	    }else{
	        mitem.setText(menuData.getMetadata().getLabel());
	    }
	    mitem.addListener(SWT.Selection, new SwtListener(menuListenerObject, swtActionContext));
	    
	    String url = menuData.getString("url");
	    if(url != null && url != ""){
	        //if(menuData.getBoolean("attachParam")){
	        //    mitem.setData("url", menuData.getString("url") + attachUrl);
	        //}else{
	        mitem.setData("url", menuData.getString("url"));
	        //}
	    }else{
	        Thing menuActions = menuData.getThing("actions@0");
	        if(menuActions != null && menuActions.getChilds().size() > 0){
	            mitem.setData("url", menuActions.getChilds().get(0).getMetadata().getPath());
	        }
	    }
	    
	    List<Thing> childMenus = (List<Thing>) menuData.get("Menu@");
	    if(childMenus.size() > 0){
	        Menu subMenu = new Menu(mitem); 
	        mitem.setMenu(subMenu);   
	        for(Thing child : childMenus){                     
	            initMenuItem(subMenu, child, attachUrl, menuListenerObject, swtActionContext);        
	        }
	    }
	}
	public Object showAddChild(ActionContext actionContext) {
		if(UtilData.isTrue(actionContext.get("doInitChildTree")) == false){
		    //如果子节点树没有初始化，那么初始化
		    actionContext.getScope(0).put("doInitChildTree", true);
		    
		    Listener descriptsComboSelection = actionContext.getObject("descriptsComboSelection");
		    descriptsComboSelection.handleEvent(null);
		}

		editPartCompositeStackLayout.topControl = addChildSashForm;
		editPartComposite.layout();
		return null;
	}
	
	public Object setAddChildDescriptor(ActionContext actionContext) {
		//先清空原有的添加面板
		Composite childContentComposite = actionContext.getObject("childContentComposite");
		
		for(Control child : childContentComposite.getChildren()){
		    child.dispose();
		}

		Thing descriptor = actionContext.getObject("descriptor");
		Browser childDescBrowser = actionContext.getObject("childDescBrowser");
		Thing objStruct = actionContext.getObject("objStruct");
		Thing newThingDescriptor = descriptor;
		Listener addChildSelectionListener = actionContext.getObject("addChildSelectionListener");

		Thing globalConfig = world.getThing("_local.xworker.config.GlobalConfig");
		String webUrl = globalConfig.getString("webUrl");
		webUrl = webUrl + "do?sc=xworker.ide.worldExplorer.swt.http.ThingDoc/@desc&thing=" + objStruct.getMetadata().getPath();
		childDescBrowser.setUrl(webUrl);

		//初始化编辑内容窗体
		Thing structForm = new Thing();
		structForm.set("descriptors", "xworker.swt.xworker.ThingDescritporForm");
		structForm.put("name","contentForm");
		structForm.put("descriptorPath", objStruct.getMetadata().getPath());
		structForm.put("H_SCROLL",  "true");
		structForm.put("V_SCROLL", "true");

		Thing thing = new Thing("", "", objStruct.getMetadata().getPath(), false);
		ActionContext context = new ActionContext();
		//context.put("parent", addChildComposite);
		context.put("thing", thing);
		context.put("addChildOkButtonSelection", addChildSelectionListener);
		context.put("explorerActions", actionContext.get("explorerActions"));
		context.put("explorerContext", actionContext.get("explorerContext"));
		context.put("utilBrowser", actionContext.get("utilBrowser"));

		Composite structFormObj = ThingDescriptorForm.createThingSingleColumnForm(thing, objStruct, "addChildOkButtonSelection", null, childContentComposite, context);
		//def structFormObj = structForm.doAction("create", context);
		context = (ActionContext) structFormObj.getData();
		context.put("thing", thing);
		context.put("addChildOkButtonSelection", addChildSelectionListener);
		Thing model = (Thing) context.getObject("model");
		model.put("defaultSelection", "addChildOkButtonSelection");
		//context.model.doAction("init", context);
		model.doAction("setValue", context);

		Button addChildButton = actionContext.getObject("addChildButton");
		actionContext.g().put("currentAddModel",  model);
		actionContext.g().put("currentAddModelContext", context);
		addChildButton.setData("currentAddModel",  model);
		addChildButton.setData("currentAddModelContext",  context);

		addChildButton.setEnabled(true);

		/*
		addChildComposite.layout();

		if(contentEditCompositeStackLayout.topControl != childComposite){
		    contentEditComposite.setData("oldTopControl", contentEditCompositeStackLayout.topControl);
		    contentEditCompositeStackLayout.topControl = childComposite;
		    contentEditComposite.layout();
		}
		*/

		if(actionContext.get("helpBrowserAction") != null){
			ActionContainer helpBrowserAction = actionContext.getObject("helpBrowserAction");
		    helpBrowserAction.doAction("setThing", actionContext,  "thing", newThingDescriptor);
		}
		//log.info("helpBrowserAction=" + helpBrowserAction);
		return null;
	}
	
	public Object setAddChildValues(ActionContext actionContext) {
		Button addChildButton = actionContext.getObject("addChildButton");
		Thing currentAddModel = (Thing) addChildButton.getData("currentAddModel");
		ActionContext currentAddModelContext = (ActionContext) addChildButton.getData("currentAddModelContext");

		Map<String, Object> ovalues = currentAddModel.doAction("getValue", actionContext);
		Map<String, Object> values = actionContext.getObject("values");
		ovalues.putAll(values);

		currentAddModel.doAction("setValue", currentAddModelContext, "thingAttributes",  ovalues);
		return null;
	}
	
	public Object doAddChild(ActionContext actionContext) {
		return null;
	}
	
	public Object getAttributeInput(ActionContext actionContext) {
		ActionContext currentModelContext = actionContext.getObject("currentModelContext");
		String name = actionContext.getObject("name");
		return currentModelContext.get(name + "Input");
	}
	
	public Object focusAttributeInput(ActionContext actionContext) {
		String name = actionContext.getObject("name");
		log.info("name=" + name + "Input");
		Object c = actions.doAction("getAttributeInput");
		log.info("forcus c=" + c);
		if(c instanceof Control){
		    ((Control) c).setFocus();
		}
		return null;
	}
	
	public Object setValues(ActionContext actionContext) {
		Thing currentModel = actionContext.getObject("currentModel");
		ActionContext currentModelContext = actionContext.getObject("currentModelContext");
		Map<String, Object> values = actionContext.getObject("values");
		
		Map<String, Object> ovalues = currentModel.doAction("getValue", currentModelContext);
		ovalues.putAll(values);
		currentModel.doAction("setValue", currentModelContext, "thingAttributes", ovalues);
		return null;
	}
	
	public Object showEditor(ActionContext actionContext) {
		editPartCompositeStackLayout.topControl = contentEditComposite;
		editPartComposite.layout();
		return null;
	}
	
	public Object selectChildTreeNode(ActionContext actionContext) {
		Tree childTree = actionContext.getObject("childTree");
		String thingPath = actionContext.getObject("thingPath");
		Listener childTreeSelection = actionContext.getObject("childTreeSelection");
		
		for(TreeItem item : childTree.getItems()){
			selectChildTreeNodeGetItem(childTree, item, thingPath, childTreeSelection);
		}

		return null;
	}
	
	private void selectChildTreeNodeGetItem(Tree tree, TreeItem item, String thingPath, Listener childTreeSelection) {
		if(item.getData() != null && ((Thing) item.getData()).getMetadata().getPath().equals(thingPath)){
	        tree.setSelection(item);
	        childTreeSelection.handleEvent(null);
	        return;
	    }else{
	        for(TreeItem childItem : item.getItems()){
	        	selectChildTreeNodeGetItem(tree, childItem, thingPath, childTreeSelection);
	        }
	    }
	}
	
	public Object isXmlEditor(ActionContext actionContext) {
		//返回当前是否是XML编辑器
		Composite xmlComposite = actionContext.getObject("xmlComposite");
		return editPartCompositeStackLayout.topControl == xmlComposite;
	}
	
	public Object nodeMoveUp(ActionContext actionContext) {
		Thing currentThing = actionContext.getObject("currentThing");
		Thing parent = currentThing.getParent();
		if(parent != null){
		    parent.changeChildIndex(currentThing, -1, -1);
		    parent.save();
		    
		    //刷新缓存
		    thingEntry.getThing();
		    
		    TreeItem treeItem = innerOutline.getSelection()[0];
		    TreeItem parentTreeItem = treeItem.getParentItem();  
		    int index = parentTreeItem.indexOf(treeItem);
		    if(index > 0){
		        index --;        
		        TreeItem item = new TreeItem(parentTreeItem, SWT.NONE, index);
		        item.setData(currentThing);
		        item.setText(currentThing.getMetadata().getLabel()+ " (" + currentThing.getThingName() + ")");
		        XWorkerTreeUtil.initItem(item, currentThing, actionContext);
		        
		        for(Thing child : currentThing.getChilds()){
		        	initOutlineTreeItem(item, child, actionContext);
		        }
		    
		        //item.setExpanded(true);
		        innerOutline.setSelection(item);
		        
		        treeItem.dispose();
		    }
		}
		
		return null;
	}
	
	private void initOutlineTreeItem(TreeItem treeItem, Thing dataObj, ActionContext actionContext){
	    TreeItem childItem = new TreeItem(treeItem, SWT.NONE);
	    childItem.setData(dataObj);
	    childItem.setText(dataObj.getMetadata().getLabel() + " (" + dataObj.getThingName() + ")");
	    //childItem.setImage(Resources.getImage("dataObject.png"));
	    XWorkerTreeUtil.initItem(childItem, dataObj, actionContext);
	    
	    for(Thing child : dataObj.getChilds()){
	    	initOutlineTreeItem(childItem, child, actionContext);
	    }

	}
	
	public Object nodeMoveDown(ActionContext actionContext) {
		Thing currentThing = actionContext.getObject("currentThing");
		Thing parent = currentThing.getParent();

		if(parent != null){
		    parent.changeChildIndex(currentThing, -1, 1);
		    parent.save();
		    //刷新缓存
		    thingEntry.getThing();
		    
		    TreeItem treeItem = innerOutline.getSelection()[0];
		    TreeItem parentTreeItem = treeItem.getParentItem();  
		    int index = parentTreeItem.indexOf(treeItem);
		    if(index < parentTreeItem.getItems().length - 1){
		        index = index + 2;         
		        TreeItem item = new TreeItem(parentTreeItem, SWT.NONE, index);
		        item.setData(currentThing);
		        item.setText(currentThing.getMetadata().getLabel()+ " (" + currentThing.getThingName() + ")");
		        XWorkerTreeUtil.initItem(item, currentThing, actionContext);
		        
		        for(Thing child : currentThing.getChilds()){
		        	initOutlineTreeItem(item, child, actionContext);
		        }
		        
		        //item.setExpanded(true);
		        innerOutline.setSelection(item);
		        
		        treeItem.dispose();
		    }
		}

		return null;
	}
	
	public Object editorChangeType(ActionContext actionContext) {
		Composite xmlComposite = actionContext.getObject("xmlComposite");
		Map<String, Object> dataThingCache = actionContext.getObject("dataThingCache");
		
		//改变编辑器的编辑方法，在表单和XML编辑器之间切换
		if(editPartCompositeStackLayout.topControl == xmlComposite){
		    //检查是否已修改
		    if(UtilData.isTrue(actionContext.get("modified")) == true){
		        if(UtilData.isTrue(actions.doAction("save", actionContext)) == false){
		            //保存失败
		            return null;
		        }
		        /*
		        MessageBox box = new MessageBox(innerOutline.getShell(), SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_WARNING);
		        box.setText("切换编辑方式");
		        box.setMessage("XML已修改，请先保存，否则将丢弃XML的修改结果。");
		        def r = box.open();
		        if(r == SWT.YES){
		            actions.doAction("save", actionContext);
		        }else if(r == SWT.CANCEL){
		            return;
		        }*/
		    }
		    //切换到表单模式
		    editPartCompositeStackLayout.topControl = contentEditComposite;    

		    if(actionContext.get("dataCache") != null){
		        dataCache.clear();
		        dataThingCache.clear();
		    }

		     //命令
		    Thing editorCommandDomain = world.getThing("xworker.ide.worldExplorer.swt.command.ThingEditorCommandDomain");
		    Assistor.setCommandDomain(editorCommandDomain, actionContext);
		    
		    //设置编辑器的表单
		    actions.doAction("openThing", actionContext);
		}else{
		    //切换到XML的编辑模式
		    //先保存
		    if(UtilData.isTrue(actionContext.get("modified")) == true){
		        if(UtilData.isTrue(actions.doAction("save", actionContext)) == false){
		            //保存失败
		            return null;
		        }
		    }
		    
		    //设置xml编辑器的内容
		    actions.doAction("xmlSetCurrentThing", actionContext);
		    
		    //命令
		    Thing editorCommandDomain = world.getThing("xworker.ide.worldExplorer.swt.command.XmlEditorCommandDomain");
		    Assistor.setCommandDomain(editorCommandDomain, actionContext);
		    
		    
		    editPartCompositeStackLayout.topControl = xmlComposite;
		}
		editPartComposite.layout();
		return null;
	}
	
	public Object xmlSetCurrentThing(ActionContext actionContext) {
		//不重新设置，在save时阻止
		if(UtilData.isTrue(actionContext.get("xmlEditorNotReset")) == true){
		    return null;
		}

		//把当前事物转化为XML
		Thing currentThing = actionContext.getObject("currentThing");
		actionContext.g().put("xmlCurrentThing", currentThing);
		actionContext.g().put("xmlRootThing", currentThing);

		String xml = XmlCoder.encodeToString(currentThing);
		Control xmlText = actionContext.getObject("xmlText");
		xmlText.setData("setModified", false);
		xmlText.setData("modified", false);
		try{
			xmlSetCurrentThingSetText(xmlText, "");
			xmlSetCurrentThingSetText(xmlText, xml);
		}catch(Exception e){
		}

		//初始化Toolbar和菜单
		actions.doAction("initOutlineBrowser", actionContext, "descriptor", currentThing.getDescriptor(), "thing", currentThing);
		actions.doAction("initToobarMenu", actionContext,"thing", currentThing);
		
		//设置属性编辑
		ActionContext xmlEditorNodeEditorActionContext= actionContext.getObject("xmlEditorNodeEditorActionContext");
		if(xmlEditorNodeEditorActionContext != null){
			Shell shell = xmlEditorNodeEditorActionContext.getObject("shell");
			if(shell.isDisposed() == false) {
				((ActionContainer) xmlEditorNodeEditorActionContext.get("actions")).doAction("setThing", xmlEditorNodeEditorActionContext, "thing", thing);
			}
		}
		
		return null;
	}
	
	private void xmlSetCurrentThingSetText(Control xmlText, String xml) {
		xmlText.setData("setModified", false);
	    SwtTextUtils.setText(xmlText, xml);
	}
	
	public Object xmlEditorSetXmlThing(ActionContext actionContext) {
		//设置xml并重新定位光标到原来的位置
		String xml = XmlCoder.encodeToString(thing);
		Control xmlText = actionContext.getObject("xmlText");
		int offset = SwtTextUtils.getCaretOffset(xmlText);
		int topIndex = SwtTextUtils.getTopIndex(xmlText);
		SwtTextUtils.setText(xmlText, xml);
		try{
		    SwtTextUtils.setCaretOffset(xmlText, offset);
		}catch(Exception e){
		}
		SwtTextUtils.setTopIndex(xmlText, topIndex);
		return null;
	}
	
	public Object xmlEditorGetSelectThing(ActionContext actionContext) throws ParserConfigurationException, SAXException, IOException {
		if(actionContext.get("xmlRootThing") == null){
		    return null;
		}

		//返回xml编辑器中当前选择处的事物		
		Control xmlText = actionContext.getObject("xmlText");
		Thing xmlRootThing = actionContext.getObject("xmlRootThing");
		ThingXmlDocument doc = ThingXmlEditorActions.getThingXmlDocument(xmlText, actionContext);
		doc.parse(SwtTextUtils.getText(xmlText), xmlRootThing.getMetadata().getPath());
		XmlThingLocation loc = ThingXmlEditorActions.getXmlThingLocation(xmlText, doc);
		if(loc != null){
		    return loc.getThing();
		}else{
		    return null;
		}
	}
	
	public Object xmlEditorShowThing(ActionContext actionContext) throws ParserConfigurationException, SAXException, IOException {
		//未知原因可能会出现递归
		if(UtilData.isTrue(actionContext.get("xmlEditorShowThing"))){
		    return null;
		}

		Control xmlText = actionContext.getObject("xmlText");
		//不重新设置，在save时阻止
		if(UtilData.isTrue(actionContext.get("xmlEditorNotReset")) == true){
		}else{
		    ThingXmlEditorActions.showSelection(xmlText, thing, actionContext);
		}    

		actions.doAction("initOutlineBrowser", actionContext, "descriptor", thing.getDescriptor(), "thing", thing);
		actions.doAction("initToobarMenu", actionContext,"thing", thing);
		actions.doAction("selectThing", actionContext, "thing", thing, "xmlEditorShowThing", true);
		return null;
	}
	
	public Object xmlEditorAddChildSetParent(ActionContext actionContext) {
		ActionContext xmlAddChildActionContext = actionContext.getObject("xmlAddChildActionContext");
		Thing parentThing= actionContext.getObject("parentThing");
		
		if(xmlAddChildActionContext != null){
			Shell shell = xmlAddChildActionContext.getObject("shell");
				if(shell.isDisposed() == false) {
			    //设置父事物				
			    if(actionContext.get("parentThing") != null){
			        ((ActionContainer) xmlAddChildActionContext.get("actions")).doAction("setThing", xmlAddChildActionContext, "thing", parentThing);
			    }
			}
		}
		return null;
	}
	
	public Object getCurrentThing(ActionContext actionContext) {
		if(UtilData.isTrue(actions.doAction("isXmlEditor"))){
		    //xmlCurrentThing是完善xml编辑器后的变量
		    return actionContext.getObject("xmlCurrentThing");
		}else{
		    //currentThing是老的编辑(纯表单）时的变量
		    return actionContext.getObject("currentThing");
		}
	}
	
	public Object editorMainTabFocused(ActionContext actionContext) {
		//在XWorker的事物管理器中，编辑区的tab选中的事物编辑器

	       
		return null;
	}
	
	public Object editorMainTabFocusouted(ActionContext actionContext) {
		//在XWorker的事物管理器中，编辑区的tab选中的了其它的事物编辑器
		ActionContext xmlAddChildActionContext = actionContext.getObject("xmlAddChildActionContext");
		if(xmlAddChildActionContext != null){
			Shell shell = xmlAddChildActionContext.getObject("shell");
			if(shell.isDisposed() == false) {
				shell.dispose();
			}
		}
		
		ActionContext xmlEditorNodeEditorActionContext = actionContext.getObject("xmlEditorNodeEditorActionContext");
		if(xmlAddChildActionContext != null){
			Shell shell = xmlEditorNodeEditorActionContext.getObject("shell");
			if(shell.isDisposed() == false) {
				((ActionContainer) xmlEditorNodeEditorActionContext.get("actions")).doAction("save");
				shell.dispose();
			}
		}
		
		return null;
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public Object setCacheData(ActionContext actionContext) {
		//保存事物节点的属性缓存
		Thing thing = actionContext.getObject("thing");
		
		Map<String, Object> cache = dataCache.get(thing.getMetadata().getPath());
		if(cache == null){
		    cache = xworker.util.UtilData.toMap("data", actionContext.get("attributes"), "time",  thing.getMetadata().getLastModified());
		}else{
			Map<String, Object> data = actionContext.getObject("data");
			Map<String, Object> cdata = (Map<String, Object>) cache.get("data");
		    data.putAll(data);
		}

		Map<String, Object> dataThingCache = actionContext.getObject("dataThingCache");
		dataCache.put(thing.getMetadata().getPath(), cache);
		dataThingCache.put(thing.getMetadata().getPath(), thing);
		return null;
	}
	
	public Object refreshRoot(ActionContext actionContext) {
		//选择根节点
		innerOutline.select(innerOutline.getItems()[0]);

		Listener refreshMenuSelection = actionContext.getObject("refreshMenuSelection");
		refreshMenuSelection.handleEvent(null);
		return null;
	}
	
	public Object showXmlEditor(ActionContext actionContext) {
		//显示XML编辑器
		Listener editModelItemSelection = actionContext.getObject("editModelItemSelection");
		editModelItemSelection.handleEvent(null);
		return null;
	}
	
	public Object showGuideEditor(ActionContext actionContext) {
		//先保存
		actions.doAction("save");

		//第一个默认向导界面是选择向导
		Thing currentThing = actionContext.getObject("currentThing");
		new ThingGuide(currentThing, world.getThing("xworker.lang.util.ThingGuideSelector"), actionContext);

		editPartCompositeStackLayout.topControl = guideComposite;
		editPartComposite.layout();
		return null;
	}
	
	public Object nodeDeleteOk(ActionContext actionContext) {
		Thing currentThing = actionContext.getObject("currentThing");
		
		if(currentThing.getParent() != null){
		    TreeItem treeItem = innerOutline.getSelection()[0];
		    //TreeItem parentTreeItem = treeItem.getParentItem();
		    treeItem.dispose();
		    
		    Thing parent = currentThing.getParent();
		    parent.removeChild(currentThing);
		    parent.save();
		    
		    //刷新事物缓存
		    thingEntry.getThing();
		}else{
		    //删除根节点
			Action removeRoot = actionContext.getObject("removeRoot");
		    removeRoot.run(actionContext);
		}
		return null;
	}
	
	public Object nodeDeleteOKOK(ActionContext actionContext) {
		Thing currentThing = actionContext.getObject("currentThing");
		CTabItem contentTab = actionContext.getObject("contentTab");
		try{
		    //xworker editor
		    currentThing.remove();
		    contentTab.dispose();
		}catch(Exception e){
		}
		try{
		    //XWorker的Workbench框架的关闭
			Action closeEditor = actionContext.getObject("closeEditor");
		    closeEditor.run(actionContext);
		}catch(Exception e){
		}
		try{
		    //eclipse plugin的关闭
			Ognl.getValue("thingEditor.getEditorSite().getPage().closeEditor(thingEditor, false)", actionContext);
		    //thingEditor.getEditorSite().getPage().closeEditor(thingEditor, false);
		}catch(Exception e){
		}
		return null;
	}
	
	
	public Object hasXWorker(ActionContext actionContext) {
		//返回否是有整个XWorker的环境
		return world.getThing("xworker.ide.worldExplorer.swt.dialogs.MarkTreeDialog") != null;
	}
	
	public Object fastLocateCode(ActionContext actionContext) {
		CoolBar coolBar = actionContext.getObject("coolBar");
		Thing currentThing = actionContext.getObject("currentThing");
		
		ActionContext context = new ActionContext();
		context.put("parent", coolBar.getShell());
		context.put("parentContext", actionContext);
		context.put("thing", currentThing.getRoot());
		context.put("currentThing", currentThing);

		Thing templateShellObj = world.getThing("xworker.ide.worldExplorer.swt.dialogs.FastLocateDialog/@shell");
		Shell templateShell = templateShellObj.doAction("create", context);

		templateShell.open();
		return null;
	}
	
	public Object editDescItemSelection(ActionContext actionContext) {		
		CoolBar coolBar = actionContext.getObject("coolBar");
		Thing currentThing = actionContext.getObject("currentThing");
		
		ActionContext context = new ActionContext();
		context.put("explorerActions", actionContext.get("explorerActions"));
		context.put("thing", currentThing);
		context.put("parent", coolBar.getShell());

		Thing shellThing = world.getThing("xworker.ide.worldExplorer.swt.dialogs.DesAndExtendsDialog/@shell");
		Shell shellObj = shellThing.doAction("create", context);
		((ActionContainer) context.get("actions")).doAction("setThing");

		shellObj.open();
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public Object descriptComboSelection(ActionContext actionContext) {

		//long start = System.currentTimeMillis();
		//取当前选择的描述
		Thing currentThing = actionContext.getObject("currentThing");
		Browser outlineBrowser = actionContext.getObject("outlineBrowser");
		Composite titleComposite = actionContext.getObject("titleComposite");
		Label titleLabel = actionContext.getObject("titleLabel");
		ToolBar toolBar = actionContext.getObject("toolBar");
		Composite structComposite = actionContext.getObject("structComposite");
		
		int index = descriptorsCombo.getSelectionIndex();
		Thing objStruct = ((List<Thing>) descriptorsCombo.getData()).get(index);
		thing = currentThing;
		Thing structureThing = objStruct;

		//标题的提示和帮助
		String webUrl = XWorkerUtils.getThingDescUrl(objStruct);
		if(actionContext.get("outlineBrowser") != null &&  !outlineBrowser.isDisposed()){
		    outlineBrowser.setUrl(webUrl);
		}
		actions.doAction("initOutlineBrowser", actionContext, "descriptor", objStruct, "thing", currentThing);

		if(titleComposite != null && !titleComposite.isDisposed()){
		    titleLabel.setText(thing.getMetadata().getLabel() + "－" + objStruct.getMetadata().getName() + "（" 
		    		+ objStruct.getMetadata().getLabel() + "）");
		    
		    String toolTipText = "<b><u><a href=\"" + webUrl + 
		        "do?sc=xworker.ide.worldExplorer.swt.http.ThingDoc/@desc&thing=" + objStruct.getMetadata().getPath() + "\">" +
		        objStruct.getMetadata().getName() + "</a></u></b><br/><br/>";    			    
		        
		    //log.info(    toolTipText);
		    if(objStruct.getMetadata().getDescription() != null){
		        String ttText = objStruct.getMetadata().getDescription();
		        titleLabel.setData("toolTip", toolTipText + ttText);
		    }else{
		        titleLabel.setData("toolTip", toolTipText);
		    }
		}

		if(menuBarComposite != null && !menuBarComposite.isDisposed()){
		    //初始化菜单
		    List<Thing> menus = new ArrayList<Thing>();
		    SwtMenuListener swtMenu = SwtMenuListener.getInstance();
		    if(objStruct != null){
		        String[] paths = thing.getDescriptors().get(0).getMetadata().getPaths();
		        menus = swtMenu.getMenu(thing, paths, "data");
		    }else{        	
		    	String structurePath = actionContext.getObject("structurePath");
		    	String descriptors = actionContext.getObject("descriptors");
		    	
		        String strFullPath = (structurePath == null || "".equals(structurePath.trim())) ? descriptors : (descriptors + ":" + structurePath);
		        menus = swtMenu.getMenu(thing, new String[] {strFullPath}, "data");
		    }
		    
		    Thing toolItemListenerObject = world.getThing("xworker.ide.worldExplorer.swt.dataExplorerParts.ThingEditor/@tempShell/@listenersPrepared/@toolBarItemSelection");
		    Thing disposeListenerObject = world.getThing("xworker.ide.worldExplorer.swt.dataExplorerParts.ThingEditor/@tempShell/@listenersPrepared/@dispose");
		    Thing menuListenerObject = world.getThing("xworker.ide.worldExplorer.swt.dataExplorerParts.ThingEditor/@tempShell/@listenersPrepared/@menuSelection");
		    
		    for(ToolItem item : toolBar.getItems()){
		        item.dispose();
		    }
		    
		    for(Thing menu : menus){
		        ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		        //println item;
		        item.setText(menu.getMetadata().getLabel());
		        item.addListener(SWT.Selection, new SwtListener(toolItemListenerObject, actionContext));
		        item.addListener(SWT.Dispose, new SwtListener(disposeListenerObject, actionContext));
		        
		        String attachUrl = "&thingName=" + thing.getMetadata().getPath() + "&descriptors=";
		        //创建菜单
		        Menu amenu = new Menu(toolBar.getShell(), SWT.POP_UP);
		        //item.setMenu(amenu);
		        for(Thing child : menu.getChilds()){
		            initMenuItem(amenu, child, attachUrl, menuListenerObject, actionContext);
		        }    
		        item.setData("menu", amenu);
		    }
		}

		//log.info("初始化菜单：" + (System.currentTimeMillis() - start));
		//start = System.currentTimeMillis();
		    
		//初始化编辑内容窗体
		Thing structForm = new Thing();
		structForm.put("descriptors", "xworker.swt.xworker.ThingDescritporForm");
		structForm.put("name", "contentForm");
		structForm.put("descriptorPath", objStruct.getMetadata().getPath());
		structForm.put("H_SCROLL", "true");
		structForm.put("V_SCROLL", "true");

		for(Control child : structComposite.getChildren()){
			
		    child.dispose();
		    if(!SwtUtils.isRWT()){
		        //structComposite.removeControl(child);
		    }
		}
		//log.info("销毁旧控件：" + (System.currentTimeMillis() - start));
		//start = System.currentTimeMillis();
		
		//建立新的上下文
		ActionContext newContext = new ActionContext();
		newContext.setLabel("Thing Editor descriptor");
		newContext.put("parentContext", actionContext);
		newContext.put("explorerActions", actionContext.get("explorerActions"));
		newContext.put("explorerContext", actionContext.get("explorerContext"));
		newContext.put("thingContext", actionContext);
		newContext.put("parent", structComposite);
		newContext.put("thing", currentThing);
		newContext.put("defaultSelection", actionContext.get("okButtonSelection"));
		newContext.put("utilBrowser", actionContext.get("utilBrowser"));
		newContext.put("modifyListener", actionContext.get("modifyListener"));
		newContext.put("editModel", actionContext.get("editModel"));
		newContext.put("editActions", actions);
		
		EditorModifyListener modifyListener = actionContext.getObject("modifyListener");
		if(modifyListener != null){			
		    modifyListener.setEnable(false);
		}    
		newContext.put("dataCache", dataCache);

		Composite formComposite = ThingDescriptorForm.createThingSingleColumnForm(currentThing, objStruct, "okButtonSelection", "modifyListener", (Composite) structComposite, newContext);
		if(modifyListener != null){
		    modifyListener.setEnable(true);
		}    

		//log.info("创建表单：" + (System.currentTimeMillis() - start));
		//start = System.currentTimeMillis();
		
		currentModel = newContext.getObject("model");
		currentModelContext = newContext;
		actionContext.g().put("currentModel", currentModel);
		actionContext.g().put("currentModelContext", currentModelContext);

		structComposite.layout();

		StackLayout contentEditCompositeStackLayout = actionContext.getObject("contentEditCompositeStackLayout");
		Composite contentComposite = actionContext.getObject("contentComposite");
		if(contentEditCompositeStackLayout.topControl != contentComposite){
		    contentEditCompositeStackLayout.topControl = contentComposite;
		    contentEditComposite.layout();
		}

		//切换到默认编辑方式
		String defaultEditor = structureThing.getString("defaultEditor");
		if("xml".equals(defaultEditor)){
		    actions.doAction("showXmlEditor");
		}else if("guide".equals(defaultEditor)){
		    actions.doAction("showGuideEditor");
		}
		//log.info("初始化编辑表单：" + (System.currentTimeMillis() - start));
		//start = System.currentTimeMillis();

		//编辑缓存
		Map<String, Object> data = dataCache.get(currentThing.getMetadata().getPath());
		if(data != null){
		    Map<String, Object> attrs = new HashMap<String, Object>();
		    attrs.putAll(currentThing.getAttributes());
		    attrs.putAll((Map<String, Object>) data.get("data"));
		    currentModelContext.put("thingAttributes", attrs);
		    currentModel.doAction("setValue", currentModelContext);
		}

		//log.info("初始化编辑缓存：" + (System.currentTimeMillis() - start));
		//start = System.currentTimeMillis();
		//log.info("xxxx1");

		//是否显示菜单工具栏
		//if(structureThing.get)
		/*def initMenuItem(parent, menuData, attachUrl, menuListenerObject, swtActionContext){
		    int style = SWT.PUSH;
		    if(menuData.childs.size() > 0){
		        style = SWT.CASCADE;
		    }else if(menuData.getBoolean("isSplit")){
		        style = SWT.SEPARATOR;
		    }
		        
		    MenuItem mitem = new MenuItem(parent, style);
		    mitem.setData("menu", menuData);
		    if(menuData.accelerator != null && menuData.accelerator != ""){
		        mitem.setText(menuData.metadata.label + "\t" + menuData.accelerator);        
		        mitem.setAccelerator(SwtUtil.getAccelerator(menuData.accelerator));
		    }else{
		        mitem.setText(menuData.metadata.label);
		    }
		    mitem.addListener(SWT.Selection, new SwtListener(menuListenerObject, swtActionContext));
		    
		    String url = menuData.getString("url");
		    if(url != null && url != ""){
		        //if(menuData.getBoolean("attachParam")){
		        //    mitem.setData("url", menuData.getString("url") + attachUrl);
		        //}else{
		        mitem.setData("url", menuData.getString("url"));
		        //}
		    }else{
		        def menuActions = menuData.get("actions@0");
		        if(menuActions != null && menuActions.childs.size() > 0){
		            mitem.setData("url", menuActions.childs[0].metadata.path);
		        }
		    }
		    
		    def childMenus = menuData.get("Menu@");
		    if(childMenus.size() > 0){
		        Menu subMenu = new Menu(mitem); 
		        mitem.setMenu(subMenu);   
		        for(child in childMenus){                     
		            initMenuItem(subMenu, child, attachUrl, menuListenerObject, swtActionContext);        
		        }
		    }
		}*/
		return null;
	}
	
	public Object addDesSelection(final ActionContext actionContext) {
		Composite contentComposite = actionContext.getObject("contentComposite");
		Shell shell = contentComposite.getShell();
		Thing dialogObject = world.getThing("xworker.ide.worldExplorer.swt.tools.ThingSelector/@shell");

		ActionContext newContext = new ActionContext();
		newContext.put("parent", shell);
		Shell newShell = dialogObject.doAction("create", newContext);

		final Thing currentThing = actionContext.getObject("currentThing");
		SwtDialog dialog = new SwtDialog(shell, newShell, newContext);
		dialog.open(new SwtDialogCallback() {
			@SuppressWarnings("unchecked")
			@Override
			public void disposed(Object result) {
				if(result != null){
				    //descriptorText.setText(result);
				    Thing descriptor = world.getThing((String) result);
				    if(descriptor != null){
				        currentThing.addDescriptor(0, descriptor);
				        currentThing.save();
				        thingEntry.getThing();
				        
				        int index = descriptorsCombo.getSelectionIndex();
				        Thing objStruct = ((List<Thing>) descriptorsCombo.getData()).get(index);
				        List<Thing> descriptors = currentThing.getDescriptors();
				        descriptorsCombo.removeAll();
				        descriptorsCombo.setData(descriptors);
				        
				        for(Thing descriptort : descriptors){
				            descriptorsCombo.add(descriptort.getMetadata().getLabel());
				        }
				        index = 0;
				        for(Thing descriptort : descriptors){
				            if(objStruct.getMetadata().getPath().equals(descriptort.getMetadata().getPath())){
				                descriptorsCombo.select(index);
				                break;
				            }
				    
				            index ++;
				        }
				    }
				}
			}
			
		});
		
		return null;
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public Object removeDescOK(ActionContext actionContext) {
		int dindex = descriptorsCombo.getSelectionIndex();
		Thing currentThing = actionContext.getObject("currentThing");
		Thing des = ((List<Thing>) descriptorsCombo.getData()).get(dindex);
		
		if(currentThing != null){
		    currentThing.removeDescriptor(des);
		    currentThing.save();
		    thingEntry.getThing();
		    
		    int index = descriptorsCombo.getSelectionIndex();
		    Thing objStruct = ((List<Thing>) descriptorsCombo.getData()).get(index);
		    List<Thing> descriptors = currentThing.getDescriptors();
		    descriptorsCombo.removeAll();
		    descriptorsCombo.setData(descriptors);
		    
		    for(Thing descriptor : descriptors){
		        descriptorsCombo.add(descriptor.getMetadata().getLabel());
		    }
		            
		    descriptorsCombo.select(0);
		    Listener descriptSelection = actionContext.getObject("descriptSelection");
		    descriptSelection.handleEvent(null);
		    //dataCenter.runScript("xworker.ide.config.webactions.editor.swt.editors.ConentEditorHead/@shell/@mainComposite/@desComposite/@descriptorsCombo/@listeners/@descriptSelection/@script", binding);
		}
		return null;
	}
	
	public Object addExtendSelection(ActionContext actionContext) {
		Composite contentComposite = actionContext.getObject("contentComposite");
		Shell shell = contentComposite.getShell();
		Thing dialogObject = world.getThing("xworker.ide.worldExplorer.swt.tools.ThingSelector/@shell");

		ActionContext newContext = new ActionContext();
		newContext.put("parent", shell);
		Shell newShell = dialogObject.doAction("create", newContext);

		final Thing currentThing = actionContext.getObject("currentThing");
		SwtDialog dialog = new SwtDialog(shell, newShell, newContext);
		dialog.open(new SwtDialogCallback() {

			@Override
			public void disposed(Object result) {
				if(result != null){
				    //descriptorText.setText(result);
				    Thing extendd = world.getThing((String) result);
				    if(extendd != null){
				        currentThing.addExtend(-1, extendd);
				        currentThing.save();
				        thingEntry.getThing();

				        extendsCombo.removeAll();   
				        List<Thing> textends = currentThing.getExtends();
				        extendsCombo.setData(textends);
				        for(Thing extend : textends){
				            extendsCombo.add(extend.getMetadata().getLabel());
				        }
				    }
				}
			}
			
		});
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Object removeExtendSelectionOk(ActionContext actionContext) {
		Thing currentThing = actionContext.getObject("currentThing");
		if(currentThing != null){
		    int dindex = extendsCombo.getSelectionIndex();
		    if(dindex == -1) return null;
		    
		    Thing extendObj = ((List<Thing>) extendsCombo.getData()).get(dindex);
		    currentThing.removeExtend(extendObj);
		    currentThing.save();
		    thingEntry.getThing();
		    
		    List<Thing> textends = currentThing.getExtends();
		    extendsCombo.setData(textends);
		    extendsCombo.removeAll();
		    for(Thing extend : textends){
		        extendsCombo.add(extend.getMetadata().getLabel());
		    }
		}
		return null;
	}
	
	public void runButtonSelection(ActionContext actionContext) {
		ActionContext ac = new ActionContext();
		Thing currentThing = actionContext.getObject("currentThing");
		ac.put("parentContext", actionContext);

		if(!methodsCombo.getText().trim().equals("")){
		    String method = methodsCombo.getText();
		    Object obj = currentThing.doAction(method, ac);
		    if(obj instanceof Thing){
		        System.out.println((Object) ((Thing) obj).doAction("toXml", ac));
		    }else{
		        System.out.println(obj);
		    }
		}
	}
	
	public void runBackButtonSelection(ActionContext actionContext) {
		if(!methodsCombo.getText().trim().equals("")){
		    final ActionContext acContext = actionContext;
		    final String method = methodsCombo.getText();
		    final Thing currentThing = actionContext.getObject("currentThing");
		    new Thread(new Runnable() {
		    	public void run() {
		    		ActionContext ac = new ActionContext();
		            ac.put("parentContext", acContext);
		            Object obj = currentThing.doAction(method, ac);
		            if(obj instanceof Thing){
				        System.out.println((Object) ((Thing) obj).doAction("toXml", ac));
				    }else{
				        System.out.println(obj);
				    }
		    	}
		    }).start();    
		    
		}
	}
	
	public void runTraceButtonSelection(ActionContext actionContext) {
		ActionContext ac = new ActionContext();
		Thing currentThing = actionContext.getObject("currentThing");
		ac.put("parentContext", actionContext);

		if(!methodsCombo.getText().trim().equals("")){
		    String method = methodsCombo.getText();
		    actionContext.peek().setContextThing(new Thing("xworker.lang.context.DebugContext"));
		    Object obj = currentThing.doAction(method, ac);
		    if(obj instanceof Thing){
		        System.out.println((Object) ((Thing) obj).doAction("toXml", ac));
		    }else{
		        System.out.println(obj);
		    }
		}
	}
	
	public void executeTraceBackgroundMenuItem(ActionContext actionContext) {
		if(!methodsCombo.getText().trim().equals("")){
		    final ActionContext acContext = actionContext;
		    final String method = methodsCombo.getText();
		    final Thing currentThing = actionContext.getObject("currentThing");
		    new Thread(new Runnable() {
		    	public void run() {
		    		ActionContext ac = new ActionContext();
		            ac.put("parentContext", acContext);
		            ac.peek().setContextThing(new Thing("xworker.lang.context.DebugContext"));
		            Object obj = currentThing.doAction(method, ac);
		            if(obj instanceof Thing){
				        System.out.println((Object) ((Thing) obj).doAction("toXml", ac));
				    }else{
				        System.out.println(obj);
				    }
		    	}
		    }).start();    
		    
		}
	}
	
	public Object methodsComboSelection(ActionContext actionContext) {
		Event event = actionContext.getObject("event");
		System.out.println(((Combo) event.widget).getText());
		return null;
	}
	
	public Object getCommandIndex(ActionContext actionContext) {
		Thing currentThing = actionContext.getObject("currentThing");
		String path = currentThing.getMetadata().getPath();
		String key = "__commandAssistorThing__";
		String keyRegist = "__commandAssistorThing__registThing__";
		Bindings _g = actionContext.g();
		//避免节点重复点击，根节点例外
		if(_g.get(key) == path){
		    //println(path);
		    //println(_g.get(key));
		    //println("thing editor interativeUI is current thng");    
		    Object registThng = _g.get(keyRegist);    
		    if(registThng != null){
		        return registThng;
		    }
		}

		//使用新的临时的事物伪装当前正在编辑的事物
		//def thing = world.getThing("xworker.ide.worldExplorer.swt.dataExplorerParts.ThingCommandIndexRoot");
		//thing = thing.detach();
		Thing thing = new Thing();
		String thingPath = thing.getMetadata().getPath();
		thing.set("descriptors", currentThing.getString("descriptors"));
		//默认不查询描述者，所以放到继承里
		String descs = currentThing.getStringBlankAsNull("descriptors");
		String exts = path + ",xworker.ide.worldExplorer.swt.dataExplorerParts.ThingCommandIndexRoot";
		if(descs != null){
		    exts = exts + "," + descs;
		}
		thing.set("extends", exts);
		thing.set("label", currentThing.getMetadata().getLabel() + ":");
		thing.set("thingPath", thingPath);
		String description = thing.getDescriptor().getMetadata().getDescription();
		//println(description);
		thing.set("description", description);
		//thing.getMetadata().setPath(path);

		_g.put(key, path);
		_g.put(keyRegist, thingPath);
		//println("interactive ok");
		//println(thingPath);
		return thingPath;	
	}
	
	public Object copyPathSelection(ActionContext actionContext) {
		Action copyRWT = actionContext.getObject("copyRWT");
		Action copyNormal = actionContext.getObject("copyNormal");
		if(SwtUtils.isRWT()){
		    TreeItem treeItem = innerOutline.getSelection()[0];
		    String plainText = ((Thing) treeItem.getData()).getMetadata().getPath();
		    copyRWT.run(actionContext, "path", plainText);
		}else{
		    copyNormal.run(actionContext);
		}
		return null;
	}
	
	public Object copyPathSelectionCopyNormal(ActionContext actionContext) {
		TreeItem treeItem = innerOutline.getSelection()[0];
		Clipboard clipboard = new Clipboard(innerOutline.getDisplay());
		String plainText = ((Thing) treeItem.getData()).getMetadata().getPath();
		TextTransfer textTransfer = TextTransfer.getInstance();
		clipboard.setContents(new String[] {plainText}, new Transfer[] {textTransfer});
		clipboard.dispose();
		return null;
	}
	
	public Object cutMenuListener(ActionContext actionContext) {
		Listener copyMenuSelection = actionContext.getObject("copyMenuSelection");
		Listener deleteMenuSelection = actionContext.getObject("deleteMenuSelection");
		
		copyMenuSelection.handleEvent(null);
		deleteMenuSelection.handleEvent(null);
		return null;
	}
	
	public Object copyMenuSelection(ActionContext actionContext) {
		TreeItem treeItem = innerOutline.getSelection()[0];
		Thing thing = (Thing) treeItem.getData();
		if(thing != null){
			xworker.ui.swt.Clipboard.add(thing.detach());
		    thingEntry.getThing();
		}
		return null;
	}
	
	public Object pasteMenuSelection(ActionContext actionContext) {
		TreeItem treeItem = innerOutline.getSelection()[0];
		Thing thing = (Thing) treeItem.getData();
		thing = world.getThing(thing.getMetadata().getPath());

		Thing memObj = xworker.ui.swt.Clipboard.get();
		if(memObj == null) return null;

		memObj = memObj.detach();
		ThingUtil.paste(thing, memObj);
		thing.save();
		thingEntry.getThing();

		Listener refreshMenuSelection = actionContext.getObject("refreshMenuSelection");
		refreshMenuSelection.handleEvent(null);
		return null;
	}
	
	public Object pasteAsChildSelection(ActionContext actionContext) {
		TreeItem treeItem = innerOutline.getSelection()[0];
		Thing thing = (Thing) treeItem.getData();
		thing = world.getThing(thing.getMetadata().getPath());

		Thing memObj = xworker.ui.swt.Clipboard.get();
		if(memObj == null) return null;

		memObj = memObj.detach();
		ThingUtil.pasteAsChild(thing, memObj);
		thing.save();
		thingEntry.getThing();

		Listener refreshMenuSelection = actionContext.getObject("refreshMenuSelection");
		refreshMenuSelection.handleEvent(null);		
		return null;
	}
	
	
	private void refreshMenuSelectioninitChild(TreeItem treeItem, Thing dataObj, ActionContainer actions, ActionContext actionContext) {
		TreeItem item = new TreeItem(treeItem, SWT.NONE);
	    //item.setText(dataObj.metadata.label + " (" + dataObj.thingName + ")");
	    //item.setData(dataObj);
	    actions.doAction("initOutlineTreeItem", actionContext, "thing", dataObj, "treeItem", item);
	    //initItemColor(item, dataObj);
	    //item.setToolTip(dataObj.thingName);
	    //actions.doAction("clearCache", ["thing":dataObj]);    
	    
	    for(Thing child : dataObj.getChilds()){
	    	refreshMenuSelectioninitChild(item, child, actions, actionContext);
	    }
	}
	
	private void refreshMenuSelectioninitExpand(TreeItem treeItem, Map<String, Object> innerOutlineExpandCache ) {
		String path = ((Thing) treeItem.getData()).getMetadata().getPath();
		
	    if(innerOutlineExpandCache.get(path) != null){
	        treeItem.setExpanded(true);
	    }
	    
	    for(TreeItem childItem : treeItem.getItems()){
	    	refreshMenuSelectioninitExpand(childItem, innerOutlineExpandCache);
	    }
	}
	
	@SuppressWarnings("unchecked")
	public Object refreshMenuSelection(ActionContext actionContext) {
		TreeItem treeItem = innerOutline.getSelection()[0];
		Thing thing = (Thing) treeItem.getData();
		thing = world.getThing(thing.getMetadata().getPath());
		//currentThing = thing;

		//initItemColor(treeItem, thing);
		actions.doAction("initOutlineTreeItem", actionContext, "thing", thing, "treeItem", treeItem);

		//treeItem.setText(thing.metadata.label + " (" + thing.thingName + ")");
		//treeItem.setData(thing);
		//treeItem.setToolTip(thing.metadata.objectName);
		treeItem.removeAll();

		if(thing != null){
		    for(Thing child : thing.getChilds()){
		    	refreshMenuSelectioninitChild(treeItem, child, actions, actionContext);
		    }
		}

		treeItem.setExpanded(true);

		if(UtilData.isTrue(actionContext.get("disableOpenThingAfterRefresh")) == true){
		    //checkThing检测到外部更新后，会刷新整个事物，可能会造成递归
			//actions.doAction("selectThing", )
		}else{
		    actions.doAction("openThing");
		}

		refreshMenuSelectioninitExpand(treeItem, (Map<String, Object>) actionContext.get("innerOutlineExpandCache"));

		return null;
	}
	
	public Object dragStrat(ActionContext actionContext) {
		//根节点不能移动
		Event event = actionContext.getObject("event");
		Tree tree = (Tree) ((DragSource) event.widget).getControl();
		if(tree.getSelection()[0] == innerOutline.getItems()[0]){
		    event.doit = false;
		}
		return null;
	}
	
	public Object dragSetData(ActionContext actionContext) {
		//根节点不能移动
		Event event = actionContext.getObject("event");
		Tree tree = (Tree) ((DragSource) event.widget).getControl();
		event.data = ((Thing) tree.getSelection()[0].getData()).getMetadata().getPath();
		return null;
	}
	
	public Object dragTargetDrop(ActionContext actionContext) {
		Event event = actionContext.getObject("event");
		Thing thing = world.getThing((String) event.data);
		if(thing == null){
		    event.doit = false;
		    return null;
		}

		Point point = innerOutline.toControl(event.x, event.y);
		TreeItem item = innerOutline.getItem(point);
		if(item == null){
		    event.doit = false;
		    return null;
		}

		if(item.getData() == thing){
		    event.doit = false;
		}

		ActionContext ac = new ActionContext();
		ac.put("parent", innerOutline);
		ac.put("parentContext", actionContext);
		ac.put("targetItem", item);
		ac.put("targetThing", item.getData());
		ac.put("thing", thing);
		ac.put("thingEntry", thingEntry);
		ac.put("innerOutline", innerOutline);

		Thing menuThing = world.getThing("xworker.ide.worldExplorer.swt.dataExplorerParts.ThingEditor/@tempShell/@innerOutlineDragMenu");
		Menu menu = menuThing.doAction("create", ac);

		if(item == innerOutline.getItems()[0]){
		    ((MenuItem) ac.get("preItem")).setEnabled(false);
		    ((MenuItem) ac.get("nextItem")).setEnabled(false);
		    ((MenuItem) ac.get("copyPreItem")).setEnabled(false);
		    ((MenuItem) ac.get("copyNextItem")).setEnabled(false);
		}
		menu.setLocation(event.x, event.y);
		menu.setVisible(true);
		return null;
	}
	
	public void dropAccept(ActionContext actionContext) {
		Event event = actionContext.getObject("event");
		Thing thing = world.getThing((String) event.data);
		if(thing == null){
		    event.doit = false;
		    return;
		}

		Point point = innerOutline.toControl(event.x, event.y);
		TreeItem item = innerOutline.getItem(point);
		if(item == null){
		    event.doit = false;
		    return;
		}

		if(item.getData() == thing){
		    event.doit = false;
		}
	}
	
	public Object onMenuDetect(ActionContext actionContext) {
		Menu popMenu = actionContext.getObject("popMenu"); 
		innerOutline.setMenu(popMenu);
		return null;
	}
	
	public Object showAddChildSelectionListener(ActionContext actionContext) {
		Listener descriptsComboSelection = actionContext.getObject("descriptsComboSelection");
		if(UtilData.isTrue(actionContext.get("doInitChildTree")) == false){
		    //如果子节点树没有初始化，那么初始化
		    actionContext.getScope(0).put("doInitChildTree", true);
		    
		    descriptsComboSelection.handleEvent(null);
		}

		//每次清空查询
		Text childKeyText = actionContext.getObject("childKeyText");
		if(childKeyText.getText() != ""){   
		    childKeyText.setText(""); 
		    descriptsComboSelection.handleEvent(null);   
		}

		editPartCompositeStackLayout.topControl = addChildSashForm;
		editPartComposite.layout();
		return null;
	}
	
	public Object viewmarkButtonSelection(final ActionContext actionContext) {
		//创建弹出窗口
		Thing dialogThing = world.getThing("xworker.ide.worldExplorer.swt.dialogs.MarkTreeDialog");
		ActionContext ac = new ActionContext();
		Button markButton = actionContext.getObject("markButton");
		ac.put("parent", markButton.getShell());
		Shell dialogShell = dialogThing.doAction("create", ac);

		//设置标记的节点
		Thing currentThing = actionContext.getObject("currentThing");
		initMarkTreItem((Tree) ac.get("markTree"), currentThing.getRoot(), actionContext);

		//显示
		SwtDialog dialog = new SwtDialog(markButton.getShell(), dialogShell, ac);
		dialog.open(new SwtDialogCallback() {

			@Override
			public void disposed(Object result) {
				if(result != null){
				    actions.doAction("selectThing", actionContext, "thing", result, "refresh", false);    
				}
			}
			
		});
	
		return null;
	}
	
	private void initMarkTreItem(Object treeItem, Thing thing, ActionContext actionContext){
	    TreeItem item = null;
	    String th_mark = actions.doAction("getCurrentAttribute", actionContext, "thing", thing, "name", "th_mark");
	    if("true".equals(th_mark)){
	    	if(treeItem instanceof Tree) {
	    		item = new TreeItem((Tree) treeItem, SWT.NONE);
	    	}else {
	    		item = new TreeItem((TreeItem) treeItem, SWT.NONE);
	    	}
	        actions.doAction("initOutlineTreeItem", actionContext, "treeItem", item, "thing", thing);
	    }
	    
	    if(item != null){
	        for(Thing child : thing.getChilds()){
	            initMarkTreItem(item, child, actionContext);
	        }
	        
	        item.setExpanded(true);
	    }else{
	        for(Thing child : thing.getChilds()){
	            initMarkTreItem(treeItem, child, actionContext);
	        }
	    }
	}
	public Object markButtonSelection(ActionContext actionContext) {
		Thing dialogThing = world.getThing("xworker.ide.worldExplorer.swt.dialogs.MarkDialog");
		ActionContext ac = new ActionContext();
		Button markButton = actionContext.getObject("markButton");
		Thing currentThing = actionContext.getObject("currentThing");
		
		ac.put("parent", markButton.getShell());
		ac.put("thing", currentThing);
		ac.put("treeItem", innerOutline.getSelection()[0]);
		ac.put("explorerActions", actionContext.get("explorerActions"));

		Shell dialogShell = dialogThing.doAction("create", ac);
		((Label) ac.get("message")).setText("按返回按钮可以返回到：\n" + currentThing.getMetadata().getLabel());
		dialogShell.open();
		return null;
	}
	
	public Object commandButtonSelection(ActionContext actionContext) {
		Thing editorCommandDomain = world.getThing("xworker.ide.worldExplorer.swt.command.ThingEditorCommandDomain");
		Assistor.runCommandDomain(editorCommandDomain, actionContext);
		return null;
	}
	
	public Object editByXmlButtonSelection(ActionContext actionContext) {
		Listener editModelItemSelection = actionContext.getObject("editModelItemSelection");
		editModelItemSelection.handleEvent(null);
		return null;
	}
	
	public Object guideButtonSelection(ActionContext actionContext) {
		//先保存
		actions.doAction("save");

		//第一个默认向导界面是选择向导
		Thing currentThing = actionContext.getObject("currentThing");
		new ThingGuide(currentThing, world.getThing("xworker.lang.util.ThingGuideSelector"), actionContext);

		editPartCompositeStackLayout.topControl = guideComposite;
		editPartComposite.layout();

		/*
		//设置当前事物的描述者
		if(!currentThing.isThing("xworker.lang.ThingGuide")){
		    def descriptors = currentThing.getStringBlankAsNull("descriptors");
		    if(descriptors != null && descriptors != ""){
		        descriptors = "xworker.lang.ThingGuide,"  + descriptors;
		    }else{
		        descriptors = "xworker.lang.ThingGuide";
		    }
		    currentThing.put("descriptors", descriptors);
		}
		//currentThing.put("descriptors", "xworker.lang.ThingGuide");
		//设置向导
		currentThing.put("tg_thingGuidePath", "xworker.lang.util.ThingGuideSelector");

		//刷新编辑器
		//actions.doAction("setThing", actionContext, "thing", currentThing);
		actions.doAction("openThing", actionContext, "thing", currentThing);
		*/
		return null;
	}
	
	public Object initCode(ActionContext actionContext) {
		CoolItem menuCoolItem = actionContext.getObject("menuCoolItem");
		Label titleLabel = actionContext.getObject("titleLabel");
		Composite xmlComposite = actionContext.getObject("xmlComposite");
		
		Point menuCollItemSize = menuCoolItem.getSize();
		menuCollItemSize.x = 1000;
		menuCoolItem.setMinimumSize(menuCollItemSize);

		if(SwtUtils.isRWT() == false){
		    titleLabel.addMouseTrackListener(LabelToolTipListener.getInstance());
		}
		titleLabel.addMouseListener(LabelToolTipListener.getInstance());

		EditorModifyListener modifyListener = new EditorModifyListener(actions);
		actionContext.g().put("modifyListener", modifyListener);

		//设置默认编辑类型
		Thing globalConfig = world.getThing("_local.xworker.config.GlobalConfig");
		if(globalConfig != null){
		    if(globalConfig.getString("thingEditorDefaultMode") == "xml"){
		        editPartCompositeStackLayout.topControl = xmlComposite;
		        editPartComposite.layout();
		    }
		}

		//树的概要栏的鼠标移动事件的监听
		OutlineTreeMoveListener moveListener = new OutlineTreeMoveListener(innerOutline, actionContext);
		actionContext.g().put("innerOutlineMoveListener", moveListener);

		Thing dialogThing = world.getThing("xworker.ide.worldExplorer.swt.dialogs.MarkTreeDialog");
		if(dialogThing == null){
		    ((Button) actionContext.get("viewMarkButton")).dispose();
		    ((Button) actionContext.get("markButton")).dispose();
		    ((Button) actionContext.get("guideButton")).dispose();
		    ((Button) actionContext.get("assistCoolItem")).dispose();
		}
		return null;
	}
	
}
