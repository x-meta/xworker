package xworker.ide.worldexplorer.dialogs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
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
import org.xmeta.cache.ThingCache;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtDialogCallback;
import xworker.swt.util.SwtUtils;
import xworker.swt.xworker.DataTable;
import xworker.util.ThingUtils;
import xworker.util.XWorkerUtils;

@ActionClass(creator="create")
public class NewThingDialog {
	/** 实例 */
	public static NewThingDialog instance = null;
	
	World world = World.getInstance();
	
	@ActionField
	public Shell shell;
	
	@ActionField
	public Button selectButton;
	
	@ActionField
	public Button pathSelectButton;
	
	@ActionField
	public Button okButton;
	
	@ActionField
	public Button cancelButton;
	
	@ActionField
	public Text descriptorText;
	
	@ActionField
	public Text pathText;
	
	@ActionField
	public Text searchText;
	
	@ActionField
	public DataTable historyTable;
	
	@ActionField
	public Tree categoryTree;
	
	@ActionField
	public ActionContainer actions;
	
	@ActionField
	public Browser historyBorwser;
	
	public static NewThingDialog create(ActionContext actionContext) {
		String key = "__NewThingDialog__key__";
		NewThingDialog ac = actionContext.getObject(key);
		if(ac == null) {
			ac = new NewThingDialog();
			actionContext.g().put(key, ac);
		}
		
		instance = ac;
		return ac;
	}
	
	public void selectionButtonSelection(ActionContext actionContext) {
		Event event = actionContext.getObject("event");
		Thing dialogObject = world.getThing("xworker.ide.worldexplorer.swt.tools.ThingSelector/@shell");
		TreeItem treeItem = (TreeItem) ((Control) event.widget).getParent().getData();
		
		Shell shell = selectButton.getShell();
		ActionContext context = new ActionContext();
		context.put("treeItem", treeItem);
		context.put("parent", shell);
		context.put("text", descriptorText);
		
		Shell newShell = dialogObject.doAction("create", context);
		SwtDialog dialog = new SwtDialog(shell, newShell, context);
		
		dialog.open(new SwtDialogCallback() {

			@Override
			public void disposed(Object result) {
				if(result != null){
				    descriptorText.setText((String) result);
				}
			}
			
		});		
	}
	
	public void pathSelectionButtonSelection(final ActionContext actionContext) {
		Thing dialogObject = world.getThing("xworker.ide.worldexplorer.swt.tools.CategoryIndexSelector");
		//def treeItem = event.widget.getParent().getData();
		Shell shell = pathSelectButton.getShell();

		ActionContext context = new ActionContext();
		//context.put("treeItem", treeItem);
		context.put("parent", shell);
		context.put("selectType", "category");
		//context.put("text", pathText);

		Shell newShell = dialogObject.doAction("create", context);
		SwtDialog dialog = new SwtDialog(shell, newShell, context);
		dialog.open(new SwtDialogCallback() {

			@Override
			public void disposed(Object result) {
				if(result != null){
					actionContext.getScope(0).put("index", result);
				    pathText.setText(((Index) result).getPath());
				}
			}
			
		});			
	}
	
	public void seaachButtonSelection(ActionContext actionContext) {
		List<Map<String, Object>> alist = new ArrayList<Map<String, Object>>();
		String q = searchText.getText().toLowerCase();
		List<Thing> allThings = actionContext.getObject("allThings");
		
		for(Thing child : allThings){
		    String name = child.getMetadata().getName().toLowerCase();
		    String label = child.getMetadata().getLabel().toLowerCase();
		    
		    if("".equals(q) || name.contains(q) || label.contains(q)){
		        if(child.getThingName().equals("Thing")){
		            Map<String, Object> t = new HashMap<String, Object>();
		            t.put("name",  child.getMetadata().getLabel());
		            t.put("path", child.getString("descriptor"));
		            t.put("lastVisit", "");
		            t.put("visitCount", "");;
		            t.put("thingType", child.getString("thingType"));
		            t.put("thingPath", child.getString("thingPath"));
		            alist.add(t);
		        }
		    }
		}

		historyTable.removeAll();
		historyTable.setData(alist);
		for(Map<String, Object> m : alist){    
		    historyTable.updateRow(null, -1, m);
		}
	}
	
	public void categoryTreeSelection(ActionContext actionContext) {
		TreeItem item = categoryTree.getSelection()[0];

		if(item.getData() == null){
		    //历史记录
		    actions.doAction("initHistory", actionContext);
		}else{
		    //历史记录
		    actions.doAction("initCategoryThing", actionContext);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void nameItemSelection(ActionContext actionContext) {
		Event event = actionContext.getObject("event");
		DataTable historyTable = actionContext.getObject("historyTable");
		
		TableColumn sortColumn = historyTable.getSortColumn();
		TableColumn currentColumn = (TableColumn) event.widget;
		int dir = historyTable.getSortDirection();
		if (sortColumn == currentColumn) {
			dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
		} else {
			historyTable.setSortColumn(currentColumn);
			if(currentColumn == actionContext.get("lastVisitItem") || currentColumn == actionContext.get("nameItem") 
					|| currentColumn == actionContext.get("visitCountItem")) {
				dir = SWT.DOWN;
			}else {
				dir = SWT.UP;
			}
			
		}

		historyTable.setSortDirection(dir);
		List<Map<String, Object>> alist = (List<Map<String, Object>>) historyTable.getData();
		if(currentColumn == actionContext.get("nameItem")){
		    if(dir == SWT.DOWN){
		    	alist.sort(new Comparator<Map<String, Object>>(){
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						String n1 = (String) o1.get("name");
						String n2 = (String) o2.get("name");
						
						return n2.compareTo(n1);
					}
		    	});
		        
		    }else{
		    	alist.sort(new Comparator<Map<String, Object>>(){
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						String n1 = (String) o1.get("name");
						String n2 = (String) o2.get("name");
						
						return n1.compareTo(n2);
					}
		    	});
		    }
		}
		/*
		if(currentColumn == lastVisitItem){
		    if(dir == SWT.DOWN){
		        alist = alist.sort{l,r -> return r.lastVisit <=> l.lastVisit};
		    }else{
		        alist = alist.sort{l,r -> return l.lastVisit <=> r.lastVisit};
		    }
		}
		if(currentColumn == visitCountItem){
		    if(dir == SWT.DOWN){
		        alist = alist.sort{l,r -> return r.visitCount <=> l.visitCount};
		    }else{
		        alist = alist.sort{l,r -> return l.visitCount <=> r.visitCount};
		    }
		}
		*/
		historyTable.removeAll();
		for(Map<String, Object> m : alist){
		    historyTable.updateRow(null, -1, m);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void historyTableSelection(ActionContext actionContext) {
		Object data = historyTable.getSelection()[0].getData();
		Map<String, Object> itemData = data instanceof Thing ? ((Thing) data).getAttributes() : (Map<String, Object>) data;

		String path = (String) itemData.get("path");
		String docPath = path;
		//Thing pathObj = world.getThing(path);

		String thingPath = (String) itemData.get("thingPath");
		if("template".equals(itemData.get("thingType"))  && thingPath != null){
		    okButton.setText(world.getThing("xworker.ide.worldexplorer.swt.i18n.I18nResource/@newThingDialog/@useTemplateButton").getMetadata().getLabel());
		    okButton.setData("action", "useTemplate");    
		    docPath = thingPath;
		}else{
			
		    //默认创建事物
		    okButton.setText(world.getThing("xworker.ide.worldexplorer.swt.i18n.I18nResource/@buttons/@okButton").getMetadata().getLabel());
		    okButton.setData("action", "createThing");
		    cancelButton.setText(world.getThing("xworker.ide.worldexplorer.swt.i18n.I18nResource/@buttons/@cancelButton").getMetadata().getLabel());
		    cancelButton.setData("action", "dispose");
		}

		if(path != null){
		    descriptorText.setText(path);
		    //def cfg = world.getThing("xworker.ide.config.globalConfig/@html");
		    //String html = cfg.getString("head") + pathObj.getString("description") + cfg.getString("bottom"); 
		    
		    //def globalConfig = world.getThing("_local.xworker.config.GlobalConfig");
		    //def webUrl = globalConfig.getString("webUrl");
		    String webUrl = XWorkerUtils.getThingDescUrl(docPath);
		    historyBorwser.setUrl(webUrl);// + "do?sc=xworker.ide.worldexplorer.swt.http.ThingDoc/@desc&thing=" + docPath);
		    /*String html = pathObj.getString("description");
		    if(html == null){
		        html = "";
		    }
		    historyBorwser.setText(html);*/
		}else{
		    descriptorText.setText("");
		    historyBorwser.setText("");
		}
	}
	
	public Object okButtonSelection(ActionContext actionContext) {
		String action = (String) okButton.getData("action");
		if(action == null){
		    action = "createThing";
		}
	    //okButton被赋予了其他操作，如应用模板等
	    return actions.doAction(action, actionContext);		    
	}
	
	public Object cancelButtonSelection(ActionContext actionContext) {
		String action = (String) cancelButton.getData("action");
		if(action != null){
		    return actions.doAction(action, actionContext);
		}

		Shell shell = actionContext.getObject("shell");
		shell.dispose();

		//回调，如果存在
		if(actionContext.get("callback") != null){
			Action callback = actionContext.getObject("callback");
		    callback.call(actionContext, "thing", null);
		}
		
		return null;
	}
	
	public void initHistory(ActionContext actionContext) {
		historyTable.removeAll();

		Thing memory = world.getThing("_local.xworker.worldExplorer.CreateThingMemory");
		if(memory == null){
			if(world.getThing("xworker.ide.worldexplorer.swt.memory.CreateThingMemory/@struct") == null) {
				return;
			}
			
		    memory = new Thing("xworker.ide.worldexplorer.swt.memory.CreateThingMemory/@struct");
		    
		    memory.saveAs("_local", "_local.xworker.worldExplorer.CreateThingMemory");

		    memory = world.getThing("_local.xworker.worldExplorer.CreateThingMemory");
		}
		List<Thing> ms = memory.getChilds();

		List<Thing> alist = new ArrayList<Thing>();
		for(Thing m : ms){
		    alist.add(m);
		}

		alist.sort(new Comparator<Thing>() {

			@Override
			public int compare(Thing o1, Thing o2) {
				Date d1 = o1.getDate("lastVisit");
				Date d2 = o2.getDate("lastVisit");
				return d2.compareTo(d1);
				/*
				String s1 = o1.getString("lastVisit");
				String s2 = o2.getString("lastVisit");
				System.out.println(s1 + ":" + s2);
				return s2.compareTo(s1);*/
			}
			
		});
		
		historyTable.setData(alist);
		for(Thing m : alist){    
		    historyTable.updateRow(null, -1, m);
		}
	}
	
	public void initCategoryThing(ActionContext actionContext) {
		historyTable.removeAll();

		TreeItem item = categoryTree.getSelection()[0];

		List<Map<String, Object>> alist = new ArrayList<Map<String, Object>>();
		ThingsGroup thingsGroup = (ThingsGroup) item.getData();
		SwtUtils.setThingDesc(thingsGroup.getThing(), historyBorwser);
		List<Thing> things = thingsGroup.getThings();
		for(Thing child : things){//((Thing) item.getData()).getAllChilds()){
		    if("Thing".equals(child.getThingName())){
		        Map<String, Object> t = new HashMap<String, Object>();
	            t.put("name",  child.getMetadata().getLabel());
	            t.put("path", child.getString("descriptor"));
	            t.put("lastVisit", "");
	            t.put("visitCount", "");;
	            t.put("thingType", child.getString("thingType"));
	            t.put("thingPath", child.getString("thingPath"));
	            t.put("configThing", child.getMetadata().getPath());
	            alist.add(t);
		    }
		}

		historyTable.setData(alist);
		for(Map<String, Object>m : alist){    
		    historyTable.updateRow(null, -1, m);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void useTemplate(ActionContext actionContext) {
		//编辑模板数据
		Map<String, Object> itemData = (Map<String, Object>) historyTable.getSelection()[0].getData();
		Thing templateThing = world.getThing((String) itemData.get("thingPath"));
		Thing descriptor = (Thing) templateThing.get("InputThing@0");
		Thing templateThingData = new Thing(descriptor.getMetadata().getPath());
		actionContext.g().put("templateThingData", templateThingData);
		
		ActionContainer editorActions = actionContext.getObject("editorActions"); 
		editorActions.doAction("setThing", actionContext, "thing", templateThingData);

		okButton.setText("确定");
		okButton.setData("action", "applyTemplate");
		cancelButton.setText("取消模板");
		cancelButton.setData("action", "cancelUseTemplate");
		    
		//显示模板
		StackLayout historyCompositeStackLayotu = actionContext.getObject("historyCompositeStackLayotu");
		Composite templateComposite = actionContext.getObject("templateComposite");
		Composite historyThingsComposite = actionContext.getObject("historyThingsComposite");
		historyCompositeStackLayotu.topControl =templateComposite;
		historyThingsComposite.layout();
	}
	
	public void cancelUseTemplate(ActionContext actionContext) {
		//显示模板
		StackLayout historyCompositeStackLayotu = actionContext.getObject("historyCompositeStackLayotu");
		Composite thingsCompostie = actionContext.getObject("thingsCompostie");
		Composite historyThingsComposite = actionContext.getObject("historyThingsComposite");
		
		historyCompositeStackLayotu.topControl = thingsCompostie;
		historyThingsComposite.layout();

		okButton.setText(UtilString.getString("lang:d=确定&en=Ok", actionContext));
		okButton.setData("action", "createThing");
		cancelButton.setText(UtilString.getString("lang:d=取消&en=Cancel", actionContext));
		cancelButton.setData("action", "dispose");
	}
	
	@SuppressWarnings("unchecked")
	public void applyTemplate(ActionContext actionContext) {
		//校验数据输入
		Thing model = actionContext.getObject("model");		
		if(!UtilData.isTrue(model.doAction("validate", actionContext))) return;

		Object category = world.get(pathText.getText());
		if(category != null && (category instanceof Category || category instanceof ThingManager)){
		     if(category instanceof ThingManager){
		         category = ((ThingManager) category).getCategory("");
		     }
		}else{
			Action pathMsg = actionContext.getObject("pathMsg");
		    pathMsg.run(actionContext);
		    return;
		}

		Map<String, Object> values = model.doAction("getValue", actionContext);
		//判断数据对象是否存在
		if(((Category) category).getThing((String) values.get("name")) != null){
			Action existsMsg = actionContext.getObject("existsMsg");
			existsMsg.run(actionContext);
		    return;
		}else{
		    //创建新的对象    
			String name = (String) values.get("name");
			String descriptors = (String) values.get("descriptors");
		    Thing object = new Thing(name, name, descriptors, false);
		    object.getMetadata().setCategory((Category) category);
		    object.getMetadata().initPath();
		    ((Category) category).getThingManager().save(object);
		    
		    Thing thing = world.getThing(object.getMetadata().getPath());
		    if(actionContext.get("treeItem") != null){
		        //如果是从事物浏览器创建的，会带一个treeItem过来
		    	TreeItem treeItem = actionContext.getObject("treeItem");
		        ((Index) treeItem.getData()).refresh();
		    }
		        
		    Map<String, Object> itemData = (Map<String, Object>) historyTable.getSelection()[0].getData();
		    Thing templateThing = world.getThing((String) itemData.get("thingPath"));
		    ActionContainer editorActions = actionContext.getObject("editorActions");
		    Thing data = editorActions.doAction("getThing", actionContext);
		    Thing proccessedThing = templateThing.doAction("process", actionContext, "data", data);
		    if(proccessedThing != null){
		        thing.paste(proccessedThing);
		        thing.save();
		    }
		    
		    //explorerActions.doAction("openPackageViewer", ["index":treeItem.getData(), "refresh":true]);
		    //开打事物
		    ActionContainer explorerActions = actionContext.getObject("explorerActions");
		    explorerActions.doAction("openThing", actionContext, "thing", thing);
		    
		    //保存对描述的历史记录
		    String descPath = (String) values.get("descriptors");
		    Thing descObj = world.getThing(descPath);
		    if(descObj == null) return;
		    
		    saveToHistory(descObj, descPath, null, null);

		    Shell shell = actionContext.getObject("shell");
		    shell.dispose();
		}
	}
	
	/**
	 * 保存创建事物的历史。
	 * 
	 * @param descObj
	 * @param descPath
	 * @param thingType
	 * @param configThing
	 */
	private void saveToHistory(Thing descObj, String descPath, String thingType, String configThing) {
		Thing memory = world.getThing("_local.xworker.worldExplorer.CreateThingMemory");
	    boolean have = false;
	    if(memory == null) {
	    	return;
	    }
	    
	    for(Thing dataObj : memory.getChilds()){
	        if(dataObj.getString("path").equals(descPath)){
	            dataObj.put("name", descObj.getMetadata().getName());
	            dataObj.put("lastVisit", new Date());
	            dataObj.put("visitCount", dataObj.getInt("visitCount") + 1);
	            dataObj.put("thingType", thingType);
	            dataObj.put("configThing", configThing);
	            dataObj.save();
	            have = true;
	        }
	    }
	    
	    if(!have){
	    	String name = descObj.getMetadata().getName();
	        Thing vd = new Thing(name, name, "xworker.ide.worldexplorer.swt.memory.CreateThingMemory/@struct", false);        
	        
	        vd.put("path", descPath);
	        vd.put("lastVisit", new Date());
	        vd.put("visitCount", 1);
	        vd.put("thingType", thingType);
	        vd.put("configThing", configThing);
	        memory.addChild(vd);
	        memory.save();
	    }
	}
	
	@SuppressWarnings("unchecked")
	public Object createThing(ActionContext actionContext) {
		Thing model = actionContext.getObject("model");
		Shell shell = actionContext.getObject("shell");
		
		String hThingType = null;
		String hConfigThing = null;
		String hPath = null;
		if(historyTable.getSelection().length > 0) {				
			Object data = historyTable.getSelection()[0].getData();
			Map<String, Object> itemData = data instanceof Thing ? ((Thing) data).getAttributes() : (Map<String, Object>) data;
	
			hPath = (String) itemData.get("path");
			hConfigThing = (String) itemData.get("configThing");
			hThingType = (String) itemData.get("thingType");
		}
		
		//校验数据输入
		if(!UtilData.isTrue(model.doAction("validate", actionContext))) return null;

		if(actionContext.get("index") == null){
		    //RWT下confirmNoPath无效
			Action confirmNoPath = actionContext.getObject("confirmNoPath");
		    if(SwtUtils.isRWT() || "OK".equals(confirmNoPath.run(actionContext))){        
		        Map<String, Object> values = model.doAction("getValue", actionContext);
		        String name = (String) values.get("name");
		        String descriptors = (String) values.get("descriptors");
		        Thing thing = new Thing(name, name, descriptors, true);
		        //初始化事物
			    if(actionContext.get("thingInitValues") != null){
			    	thing.putAll((Map<String, Object>) actionContext.get("thingInitValues"));
			    }
			    if(actionContext.get("thingInitThing") != null){
			    	thing.paste((Thing) actionContext.get("thingInitThing"));		        
			    }
		        XWorkerUtils.ideOpenThing(thing);
		        
		        if(actionContext.get("shell") != null){
		            shell.dispose();    
		            actionContext.getScope(0).put("result", thing);
		        }
		        return thing;
		    }
		    return null;
		}
		//def index = treeItem.getData();
		Category category = null;
		Object indexx = actionContext.get("index");
		if(indexx instanceof Category){
		    category = (Category) indexx;
		}else{
		    //默认是Index
			Index index = actionContext.getObject("index");
		    if("thingManager".equals(index.getType())){
		        category = ((ThingManager) index.getIndexObject()).getCategory(null);;
		    }else{
		        category = (Category) index.getIndexObject();
		    }
		}

		Map<String, Object> values = model.doAction("getValue", actionContext);
		String name = (String) values.get("name");
        String descriptors = (String) values.get("descriptors");
		//判断数据对象是否存在
		if(category.getThing(name) != null){
		    //MessageBox box = new MessageBox(okButton.getShell(), SWT.OK | SWT.ICON_ERROR);
		    //box.setText(UtilString.getString("res:res.w_exp:commonBoxTitle:操作信息", actionContext));
		    //box.setMessage(UtilString.getString("res:res.w_exp:alertThingExists:事物已经已经存在！", actionContext));
		    //box.open();
			Action thingAlreadyExists = actionContext.getObject("thingAlreadyExists");
		    thingAlreadyExists.run(actionContext);
		    return null;
		}else if(name == null || name.trim().equals("") || name.indexOf(".") != -1 || name.indexOf(":") != -1){
		    //MessageBox box = new MessageBox(okButton.getShell(), SWT.OK | SWT.ICON_ERROR);
		    //box.setText(UtilString.getString("res:res.w_exp:commonBoxTitle|操作信息", actionContext));
		    //box.setMessage(UtilString.getString("请输入正确的事物名！事物名不能为空和包含符号'.'和':'。", actionContext));
		    //box.open();
		    //nameNotLegal(actionContext);
		    return null;
		}else{
		    //创建新的对象    
		    if(descriptors == null || descriptors.equals("")){
		        descriptors = "xworker.lang.MetaDescriptor3";
		    }
		    Thing object = new Thing(name, name, descriptors, false);
		    object.getMetadata().setCategory(category);
		    object.getMetadata().initPath();
		    object.getMetadata().setCoderType((String) values.get("codecType"));
		    
		    //初始化事物
		    if(hConfigThing != null && "descriptor".equals(hThingType) && descriptors.equals(hPath)) {
	        	//在界面里选中了一个预先定义的描述者时，配置改描述者的模型设置的默认属性
        		Thing configThing = world.getThing(hConfigThing);
        		if(configThing != null) {
	        		Map<String, Object> attrs = configThing.doAction("getAttributes", actionContext);
					if(attrs != null) {
						object.getAttributes().putAll(attrs);				
					}
        		}
	        }
        		
		    if(actionContext.get("thingInitValues") != null){
		        object.putAll((Map<String, Object>) actionContext.get("thingInitValues"));
		    }
		    if(actionContext.get("thingInitThing") != null){
		        object.paste((Thing) actionContext.get("thingInitThing"));		        
		    }
		    category.getThingManager().save(object);
		    
		    //放入缓存
		    ThingCache.put(object.getMetadata().getPath(), object);
		            
		    if(actionContext.get("treeItem") != null){
		        //如果是从事物浏览器创建的，会带一个treeItem过来
		    	TreeItem treeItem = actionContext.getObject("treeItem");
		        ((Index) treeItem.getData()).refresh();
		    }
		        
		    //explorerActions.doAction("openPackageViewer", ["index":treeItem.getData(), "refresh":true]);
		    //开打事物
		    Thing thing = object;//world.getThing(object.metadata.path);
		    //explorerActions.doAction("openThing", ["thing":thing]);
		    
		    //保存对描述的历史记录
		    String descPath = descriptors;
		    Thing descObj = world.getThing(descPath);
		    if(descObj != null && "thingTab".equals(descObj.getString("createOpenAction"))){
		        XWorkerUtils.ideOpenThingTab(thing);
		    }else{
		        XWorkerUtils.ideOpenThing(thing);
		    }    
		    
		    if(descObj == null) return null;
		    
		    saveToHistory(descObj, descPath, hThingType, hConfigThing);
		  
		    if(actionContext.get("shell") != null){
		        shell.dispose();
		        actionContext.getScope(0).put("result", thing);
		    }

		    if(actionContext.get("callback") != null){
		    	Action callback = actionContext.getObject("callback");
			    callback.call(actionContext, "thing", null);
		        //callback.call("thing", thing);
		    }
		    return thing;
		}
	}
	
	public void dispose(ActionContext actionContext) {
		Shell shell = actionContext.getObject("shell");
		if(shell != null) {
			shell.dispose();
		}
	}
	
	public void initCode(ActionContext actionContext) {
		//目录
		String categoryPath = actionContext.getObject("categoryPath");
		if(actionContext.get("categoryPath") != null){
		    pathText.setText(categoryPath);
		}

		Image folderImage = (Image) actionContext.get("folderImage");
		if(XWorkerUtils.hasXWorker()){
		    //历史记录
		    actions.doAction("initHistory", actionContext);
		    
		    //目录树
		    TreeItem item = new TreeItem(categoryTree, SWT.NONE);
		    item.setText(world.getThing("xworker.ide.worldexplorer.swt.i18n.I18nResource/@newThingDialog/@historyRecord").getMetadata().getLabel());
		    item.setImage(folderImage);
		}

		List<Thing> allThings = new ArrayList<Thing>();
		ThingsGroup thingsGroup = new ThingsGroup(actionContext);
		initThings(categoryTree, thingsGroup, allThings, folderImage);

		//用于查询
		actionContext.getScope(0).put("allThings", allThings);

		/*

		Thing coreThings = world.getThing("xworker.lang.LangThings");
		if(coreThings != null){
		    initThings(categoryTree, coreThings, allThings, folderImage);
		}

		Thing registorThing = world.getThing("xworker.lang.ThingsIndex");
		List<Thing> thingsList = ThingUtils.searchRegistThings(registorThing, "child", new ArrayList<String>(), actionContext);
		for(Thing things : thingsList){
		    if(!things.getMetadata().getPath().equals("xworker.lang.LangThings")){
		        initThings(categoryTree, things, allThings, folderImage);
		    }
		}*/

		/*
		for(thingManager in world.getThingManagers()){
		    def rootCategory = thingManager.getCategory(null);
		    for(lv1Category in rootCategory.getCategorys()){
		        for(lv2Category in lv1Category.getCategorys()){
		            String project = lv1Category.getSimpleName() + "." + lv2Category.getSimpleName();
		            def things = world.getThing(project + ".config.Things");
		            if(things != null){
		                 if(project == "xworker.ide"){                    
		                 }else{
		                     initThings(categoryTree, things, allThings);
		                 }
		            }
		        }
		    }
		}
		*/
		
		Combo codecTypeCombo = actionContext.getObject("codecTypeCombo");
		for(ThingCoder thingCoder : world.getThingCoders()){
		    for(String type : thingCoder.getCodeTypes()){
		        codecTypeCombo.add(type);
		    }
		}
		codecTypeCombo.select(0);
		/*
		for(project in world.getProjects()){
		    for(thingManager in project.getThingManagers()){
		        def things = world.getThing(project.getName() + ":" + thingManager.getName() + ":config.Things");
		        if(things != null){
		             if(project.name == "xworker" && thingManager.name == "core"){                    
		             }else{
		                 initThings(categoryTree, things);
		             }
		        }
		    }
		}
		*/

		//为解决模板bug，事物定义是先显示模板，正常启动后切换到事物选择
		StackLayout historyCompositeStackLayotu = actionContext.getObject("historyCompositeStackLayotu");
		Composite thingsCompostie = actionContext.getObject("thingsCompostie");
		Composite historyThingsComposite = actionContext.getObject("historyThingsComposite");
		historyCompositeStackLayotu.topControl = thingsCompostie;
		historyThingsComposite.layout();

		
	}
	
	private void initThings(Object treeItem, ThingsGroup thingsGroup, List<Thing> allThings, Image folderImage){
		for(ThingsGroup group : thingsGroup.getChilds()){
			TreeItem item = null;
			if(treeItem instanceof Tree) {
				item = new TreeItem((Tree) treeItem, SWT.NONE);
			}else {
				item = new TreeItem((TreeItem) treeItem, SWT.NONE);
			}
			item.setImage(folderImage);
			item.setData(group);
			item.setText(group.getThing().getMetadata().getLabel());
			allThings.addAll(group.getThings());

			initThings(item, group, allThings, folderImage);
		}
		/*
	    for(Thing child : things.getAllChilds()){
	        String thingName = child.getThingName();
	        if("Category".equals(thingName)){
	            TreeItem item = null;

	            boolean exists = false;
	            if(treeItem instanceof Tree) {
	            	for(TreeItem childItem : ((Tree) treeItem).getItems()){
	            		if(child.getMetadata().getLabel().equals(childItem.getText())){
	            			item = childItem;
							exists = true;
	            			break;
						}
					}
	            	if(item == null){
						item = new TreeItem((Tree) treeItem, SWT.NONE);
					}
	            }else {
					for(TreeItem childItem : ((TreeItem) treeItem).getItems()){
						if(child.getMetadata().getLabel().equals(childItem.getText())){
							item = childItem;
							exists = true;
							break;
						}
					}
					if(item == null){
						item = new TreeItem((TreeItem) treeItem, SWT.NONE);
					}
	            }
	            if(!exists) {
					item.setText(child.getMetadata().getLabel());
					item.setData(child);
					item.setImage(folderImage);
				}
	            
	            initThings(item, child, allThings, folderImage);
	        }else if("Thing".equals(thingName)){
	        	Widget parent = (Widget) treeItem;
	        	List<Thing> treeThings = (List<Thing>) parent.getData("things");
	        	if(treeThings == null){
					treeThings = new ArrayList<Thing>();
					parent.setData("things", treeThings);
				}
	        	treeThings.add(child);
	            allThings.add(child);
	        }
	    }
	    
	    if(treeItem instanceof TreeItem){
	        //treeItem.setExpanded(true);
	    }*/
	}
}

