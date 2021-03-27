package xworker.ide.worldexplorer.editor.thingEditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import org.xmeta.cache.ThingEntry;
import org.xmeta.util.ActionContainer;

import xworker.command.interactive.InteractiveUI;
import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;
import xworker.swt.form.ThingDescriptorForm;
import xworker.swt.util.ResourceManager;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.XWorkerTreeUtil;
import xworker.util.ThingUtils;
import xworker.util.UtilData;

@ActionClass(creator="create")
public class ThingEditorChildActions {
	private static final String TAG = ThingEditorChildActions.class.getName();
	
	public static ThingEditorChildActions create(ActionContext actionContext) {
		String key = "__ThingEditorChildActions__key__";
		ThingEditorChildActions ac = actionContext.getObject(key);
		if(ac == null) {
			ac = new ThingEditorChildActions();
			actionContext.g().put(key, ac);
		}
		return ac;
	}
	
	@ActionField
	Combo structCombo;
	
	@ActionField
	ActionContainer childScripts;
	
	@ActionField
	Tree childTree;
	
	@ActionField
	Composite childContentComposite;
	
	@ActionField
	Browser childDescBrowser;
	
	@ActionField
	Thing currentAddModel;
	
	@ActionField
	ActionContext currentAddModelContext;
	
	@ActionField
	Text childKeyText;
	
	World world = World.getInstance();
	
	@SuppressWarnings("unchecked")
	public void descriptsComboSelection(ActionContext actionContext) {
		int index = structCombo.getSelectionIndex();
		if(index == -1){
		    return;
		}

		List<Thing> descriptors = (List<Thing>) structCombo.getData();
		Thing descriptor = descriptors.get(index);

		childScripts.doAction("selectDescriptor", actionContext,  "descriptor", descriptor, "thing",structCombo.getData("thing"));
	}
	
	public void childTreeSelection(ActionContext actionContext) {
		TreeItem treeItem = childTree.getSelection()[0];
		Thing objStruct = (Thing) treeItem.getData();
		if(objStruct == null) return;


		//先清空原有的添加面板
		for(Control child : childContentComposite.getChildren()){
		    child.dispose();
		}

		Thing newThingDescriptor = objStruct;
		actionContext.g().put("newThingDescriptor", newThingDescriptor);

		SwtUtils.setThingDesc(objStruct, childDescBrowser);

		/*
		if(actions.doAction("hasXWorker", actionContext)){
		    def webUrl = XWorkerUtils.getThingDescUrl(objStruct);
		    childDescBrowser.setUrl(webUrl);
		}else{
		    def desc = objStruct.getMetadata().getDescription();
		    if(desc == null){
		        desc = "";
		    }
		    childDescBrowser.setText(desc);
		}

		childTitleLabel.setText(UtilString.getString("res:res.w_exp:addLabel添加：", actionContext) + objStruct.metadata.name + "（" + objStruct.metadata.label + "）");
		String toolTipText = "<b><u><a href=\"" + webUrl + "do?sc=xworker.ide.worldexplorer.swt.http.ThingDoc/@desc&thing=" + objStruct.getMetadata().getPath() + "\">" + objStruct.getMetadata().getName() + "</a></u></b><br/><br/>";    			    
		if(objStruct.metadata.description != null){
		    String ttText = objStruct.metadata.description;
		    childTitleLabel.setData("toolTip", toolTipText + ttText);
		}else{
		    childTitleLabel.setData("toolTip", toolTipText);
		}

		for(child in addChildComposite.getChildren()){
		    child.dispose();
		    addChildComposite.removeControl(child);
		}
		*/
		//初始化编辑内容窗体
		Thing structForm = new Thing();
		structForm.set("descriptors", "xworker.swt.xworker.ThingDescritporForm");
		structForm.put("name",  "contentForm");
		structForm.put("descriptorPath",  objStruct.getMetadata().getPath());
		structForm.put("H_SCROLL", "true");
		structForm.put("V_SCROLL", "true");

		Listener addChildSelectionListener = actionContext.getObject("addChildSelectionListener");
		Thing thing = new Thing("", "", objStruct.getMetadata().getPath(), false);
		ActionContext context = new ActionContext();
		//context.put("parent", addChildComposite);
		context.put("thing", thing);
		context.put("addChildOkButtonSelection", addChildSelectionListener);
		context.put("explorerActions", actionContext.get("explorerActions"));
		context.put("explorerContext", actionContext.get("explorerContext"));
		context.put("utilBrowser", actionContext.get("utilBrowser"));
		context.put("defaultSelection", addChildSelectionListener);

		Composite structFormObj = ThingDescriptorForm.createThingSingleColumnForm(thing, objStruct, "addChildOkButtonSelection", null, childContentComposite, context);
		//def structFormObj = structForm.doAction("create", context);
		context = (ActionContext) structFormObj.getData();
		context.put("thing", thing);
		context.put("addChildOkButtonSelection", addChildSelectionListener);
		Thing model = context.getObject("model");
		model.put("defaultSelection", "addChildOkButtonSelection");
		//context.model.doAction("init", context);
		model.doAction("setValue", context);

		currentAddModel = model;
		actionContext.g().put("currentAddModel", currentAddModel);
		currentAddModelContext =  context;
		actionContext.g().put("currentAddModelContext", currentAddModelContext);
		
		Button addChildButton = actionContext.getObject("addChildButton");
		addChildButton.setData("currentAddModelContext", context);
		addChildButton.setData("currentAddModel", currentAddModel);
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
		    helpBrowserAction.doAction("setThing", actionContext, "thing", newThingDescriptor);
		}
		//log.info("helpBrowserAction=" + helpBrowserAction);

		((InteractiveUI) actionContext.get("childTreeInteractiveUI")).fire(objStruct);
	}
	
	public Object getCommandIndex(ActionContext actionContext) {
		if(childTree.getSelection().length == 0){
		    return null;
		}

		TreeItem treeItem = childTree.getSelection()[0];
		Thing objStruct = (Thing) treeItem.getData();
		if(objStruct == null) return null;

		Thing currentThing = objStruct;
		String path = currentThing.getMetadata().getPath();
		String key = "__commandAssistorThing__";
		Bindings _g = actionContext.g(); 
		if(_g.get(key) == path){
		    //println(path);
		    //println(_g.get(key));
		    //println("thing editor interativeUI is current thng");
		    return null;    
		}

		//使用新的临时的事物伪装当前正在编辑的事物
		//def thing = world.getThing("xworker.ide.worldexplorer.swt.dataExplorerParts.ThingCommandIndexRoot");
		//thing = thing.detach();
		Thing thing = new Thing();
		String thingPath = thing.getMetadata().getPath();
		thing.set("descriptors", currentThing.getMetadata().getPath());
		//默认不查询描述者，所以放到继承里
		String descs = currentThing.getMetadata().getPath();
		String exts = path + ",xworker.ide.worldexplorer.swt.dataExplorerParts.ThingCommandIndexRoot";
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
		//println("interactive ok");
		//println(thingPath);
		return thingPath;
	}
	
	public void initChilds(ActionContext actionContext) {
		/*
		 应该是没用了
		def dataObject = _request.get("dataObject");

		def descriptors = dataObject.getMetadata().getDescriptors();

		structCombo.removeAll();
		structCombo.setData("descriptors", descriptors);
		for(des in descriptors){
		    structCombo.add(des.metadata.label);
		}

		log.info("ini childs");
		structCombo.setData("dataObject", dataObject);
		structCombo.select(0);

		childScripts.exec("selectDescriptor", ["descriptor":descriptors[0], "dataObject":dataObject]);*/
	}
	
	private void addThing(List<Thing> list, Thing thing, Map<String, Thing >context){
	    String path = thing.getMetadata().getPath();
	    if(context.get(path) != null){
	        return;
	    }
	    
	    context.put(path, thing);
	    list.add(thing);
	}
	
	@SuppressWarnings("unchecked")
	private void createTree(Object item, Map<String, Object> aroot, boolean expand, ActionContext actionContext){
	    List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
	    for(String key : aroot.keySet()){
	        Object v = aroot.get(key);
	        if(v instanceof Map){
	            groups.add((Map<String, Object>) v);
	        }
	    }
	    
	    //为group排序
	    Collections.sort(groups, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> t1, Map<String, Object> t2) {
				String name1 = (String) t1.get("__name__");
				String name2 = (String) t2.get("__name__");
				return name1.compareTo(name2);
			}                                       
	    });
	    for(Map<String, Object> g : groups){
	        Collections.sort((List<Thing>) g.get("childs"), new Comparator<Thing>() {
				@Override
				public int compare(Thing o1, Thing o2) {
					String name1 = o1.getMetadata().getLabel();
					String name2 = o2.getMetadata().getLabel();
					return name1.compareTo(name2);
				}                                       
		    });
	       
	        TreeItem subItem = null;
	        if(item instanceof Tree) {
	        	subItem = new TreeItem((Tree) item, SWT.NONE); 
	        }else {
	        	subItem = new TreeItem((TreeItem) item, SWT.NONE);
	        }
	        
	        subItem.setText((String) g.get("__name__"));
	        actionContext.peek().put("parent", subItem.getParent());
	        Image img = (Image) ResourceManager.createIamge("icons/folder.png", actionContext);
			if(img != null){
			    subItem.setImage(img);
			}
	        
	        createTree(subItem, g, expand, actionContext);
	    }
	
	    Collections.sort((List<Thing>) aroot.get("childs"), new Comparator<Thing>() {
			@Override
			public int compare(Thing o1, Thing o2) {
				String name1 = o1.getMetadata().getLabel();
				String name2 = o2.getMetadata().getLabel();
				return name1.compareTo(name2);
			}                                       
	    });
	    for(Thing child : (List<Thing>) aroot.get("childs")){       
	        TreeItem subItem = null;
	        if(item instanceof Tree) {
	        	subItem = new TreeItem((Tree) item, SWT.NONE);
	        }else {
	        	subItem = new TreeItem((TreeItem) item, SWT.NONE);
	        }
	        XWorkerTreeUtil.initItem(subItem, child, actionContext);
	        /*
	        String icon = child.getStringBlankAsNull("icon");
	        if(icon == null || "".equals(icon)){
	            XWorkerTreeUtil.initItem(subItem, child, actionContext);
	        }else{
	            XWorkerTreeUtil.initItem(subItem, child.getString("th_font"), child.getString("th_nodeColor"),
	            		child.getString("icon"), actionContext);
	        }
	        subItem.setText(child.getMetadata().getLabel());
	        */
	        subItem.setData(child);	        
	    }
	    
	    if(expand && item instanceof TreeItem){
	        ((TreeItem) item).setExpanded(true);
	    }
	}
	
	public void createTemplate(Tree item, ActionContext actionContext){
	    TreeItem tItem = new TreeItem(item, SWT.NONE);
	    tItem.setText("#ThingTemplate");    
	    Image img = (Image) ResourceManager.createIamge("icons/folder.png", actionContext);
		if(img != null){
		    tItem.setImage(img);
		}
	        
	    //添加事物模板的子节点
	    Thing template = world.getThing("xworker.lang.util.ThingTemplate");
	    for(Thing child : template.getChilds("thing")){
	        TreeItem subItem = new TreeItem(tItem, SWT.NONE);
	        XWorkerTreeUtil.initItem(subItem, child, actionContext);
	        subItem.setText(child.getMetadata().getLabel());
	        subItem.setData(child);
	    }
	}
	
	@SuppressWarnings("unchecked")
	public void selectDescriptor(ActionContext actionContext) {
		if(UtilData.isTrue(actionContext.get("doInitChildTree")) == false){
		    return;
		}
		
		//long start = System.currentTimeMillis();
		Thing descriptor = actionContext.getObject("descriptor");
		//在这里也过滤了excludeDescriptorsForChilds的事物
		List<Thing> desChilds = descriptor.getAllChilds("thing");
		//for(child in desChilds){
		//    log.info("child=" + child);
		//}
		//过滤不需要的，即excludeDescriptorsForChilds执行的描述者下的子节点
		Map<String, String> excludes = new HashMap<String, String>();
		String excludeDescriptorsForChilds = descriptor.getStringBlankAsNull("excludeDescriptorsForChilds");
		if(excludeDescriptorsForChilds != null) {
			for(String ext : excludeDescriptorsForChilds.split("[,]")) {
				excludes.put(ext, ext);
			}
			
			for(int i=0; i<desChilds.size(); i++) {
				Thing child = desChilds.get(i);
				if(excludes.get(child.getParent().getMetadata().getPath()) != null) {
					//父节点，即描述者在excludeDescriptorsForChilds中排除了
					desChilds.remove(i);
					i--;
				}
						
			}
		}
		//查找并添加注册的子事物
		try{
			Map<String, Thing> context = new HashMap<String, Thing>();
		    Thing registThing = world.getThing("xworker.ide.db.dbindex.app.dataObject.RegistsByThing");
		    if(registThing != null){
		        List<DataObject> rchilds = registThing.doAction("query", actionContext, "thing", descriptor, "noDescriptor", true, "registType", "child");
		        for(DataObject rc : rchilds){
		            //log.info("child=" + rc);
		            Thing child = world.getThing((String) rc.get("path"));        
		            if(excludes.get(child.getMetadata().getPath()) != null) {
		            	continue;
		            }
		            
		            if(child != null){
		            	//作用域是private的也不添加
                    	if("private".equals(child.getString("modifier"))) {
                    		continue;
                    	}
                    	
		                if(child.getBoolean("th_registMyChilds")){
		                    for(Thing cchild : child.getChilds()){
		                    	//屏蔽注册的不添加
		                    	if(cchild.getBoolean("th_registDisabled")){
		            				continue;
		            			}
		                    	
		                    	//作用域是private的也不添加
		                    	if("private".equals(cchild.getString("modifier"))) {
		                    		continue;
		                    	}
		                    	
		                        addThing(desChilds, cchild, context);
		                    }
		                }else{
		                    addThing(desChilds, child, context);
		                }
		            }
		        }
		    }
			ThingUtils.initFromRegistCache(desChilds, context, descriptor, "child");
		}catch(Exception e){
		    Executor.info(TAG, "add regist child error", e);
		}
		//log.info("Select DB Time: " + (System.currentTimeMillis() - start));
		
		String keyword = childKeyText.getText().toLowerCase();
		List<Thing> dess = new ArrayList<Thing>();
		boolean expand = keyword != "";
		Thing currentThing = actionContext.getObject("currentThing");
		for(Thing des : desChilds){
		    if(des.getString("many") == "false"){
		        //只找首要描述者相同的子节点，其他情况不再过滤了
		        boolean have = false;
		        for(Thing child : currentThing.getChilds()){
		            if(child.getDescriptor().getMetadata().getPath().equals(des.getMetadata().getPath())){
		                have = true;
		                break;
		            }
		        }
		        if(have){
		            continue;
		        }
		    }
		    
		    if("private".equals(des.getStringBlankAsNull("modifier"))){
		    	//private的不显示
		    	continue;
		    }
		    
		    //过滤关键字
		    String[] keys = keyword.split("[ ]");
		    boolean addok = true;
		    for(String key : keys){
		        String desKeys = des.getString("th_keywords");
		        boolean ok = false;
		        if(desKeys != null && desKeys.toLowerCase().indexOf(key) != -1){
		            ok = true;
		        }
		        if(!ok && des.getMetadata().getPath().toLowerCase().indexOf(key) != -1){
		            ok = true;
		        }
		        if(!ok){
		            String group = des.getString("group");
		            if(group != null && group.toLowerCase().indexOf(key) != -1){
		                ok = true;
		            }
		        }
		        if(!ok && des.getMetadata().getLabel().toLowerCase().indexOf(key) != -1){
		            ok = true;
		        }
		        if(!ok){
		            addok = false;
		            break;
		        }
		    }
		
		    if(addok){
		        dess.add(des);
		    }
		}
		//log.info("Filter Many Time: " + (System.currentTimeMillis() - start));
		
		//分组
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("childs", new ArrayList<Thing>());
	
		for(Thing des : dess){
		    String group = des.getString("group");
		    if(group == null || "".equals(group)){
		        ((List<Thing>) root.get("childs")).add(des);
		    }else{
		        for(String grs : group.split("[,]")){
		            String[] gs = grs.split("[.]");
		            Map<String, Object> subRoot = null;
		            Map<String, Object> currentRoot = root;
		            for(String g : gs){
		                subRoot = (Map<String, Object>) currentRoot.get(g);
		                if(subRoot == null){ 
		                    subRoot = new HashMap<String, Object>();
		                    subRoot.put("childs", new ArrayList<Thing>());
		                    subRoot.put("__name__", g);
		                    currentRoot.put(g, subRoot);
		                }
		                currentRoot = subRoot;
		            }
		            ((List<Thing>) subRoot.get("childs")).add(des);
		        }
		    }
		}
		
		//log.info("Group Time: " + (System.currentTimeMillis() - start));
		//构造子节点树
		childTree.removeAll();
		createTree(childTree, root, expand, actionContext);
		createTemplate(childTree, actionContext);
		//log.info("Create Tree Time: " + (System.currentTimeMillis() - start));
	}
	
	public void selectChildList(ActionContext actionContext) {
		/*def selectIndex = childList.getSelectionIndex();
		if(selectIndex == -1) return;
		def objStruct = childList.getData().get(selectIndex);

		childTitleLabel.setText("添加：" + objStruct.metadata.name + "（" + objStruct.metadata.label + "）");
		String toolTipText = "<b><u>" + objStruct.getMetadata().getName() + "</u></b><br/><br/>";    			    
		if(objStruct.getString("description") != null){
		    String ttText = objStruct.getString("description");
		    childTitleLabel.setData("toolTip", toolTipText + ttText);
		}else{
		    childTitleLabel.setData("toolTip", toolTipText);
		}

		//初始化编辑内容窗体
		DataObject structForm = new DataObject();
		structForm.setAttribute("descriptors", "view.objects.swt.xworker.StructureForm");
		structForm.name = "contentForm";
		structForm.structurePath = objStruct.metadata.path;
		structForm.H_SCROLL = "true";
		structForm.V_SCROLL = "true";

		for(child in addChildComposite.getChildren()){
		    child.dispose();
		    addChildComposite.removeControl(child);
		}

		def dataObject = new DataObject();
		Binding cbin = new Binding();
		cbin.setVariable("binding", cbin);
		cbin.setVariable("parent", addChildComposite);
		cbin.setVariable("dataObject", dataObject);
		cbin.setVariable("addChildOkButtonSelection", addChildOkButtonSelection);
		def structFormObj = structForm.exec("create", cbin);

		cbin.contentFormModel.defaultSelection = "addChildOkButtonSelection";
		cbin.contentFormModel.exec("init", cbin);
		cbin.contentFormModel.exec("setValue", cbin);
		binding.setVariable("currentAddModel", cbin.contentFormModel);
		binding.setVariable("currentAddModelBin", cbin);
		addChildComposite.layout();

		contentEditCompositeStackLayout.topControl = childComposite;
		contentEditComposite.layout();*/
	}
	
	@SuppressWarnings("unchecked")
	public Object searchChilds(ActionContext actionContext) {
		if(UtilData.isTrue(actionContext.get("doInitChildTree")) == false){
		    return null;
		}
		
		//long start = System.currentTimeMillis();
		int index = structCombo.getSelectionIndex();
		List<Thing> descriptors = (List<Thing>) structCombo.getData();
		Thing descriptor = descriptors.get(index);
		List<Thing> desChilds = descriptor.getAllChilds("thing");
		//查找并添加注册的子事物
		try{
		    Thing registThing = world.getThing("xworker.ide.db.dbindex.app.dataObject.RegistsByThing");
		    List<DataObject> rchilds = registThing.doAction("query", actionContext, "thing", descriptor, "noDescriptor", true, "registType", "child");
		    Map<String, Thing> context = new HashMap<String, Thing>();
		    for(DataObject rc : rchilds){
		        //log.info("child=" + rchilds);
		        Thing child = world.getThing(rc.getString("path"));        
		        if(child != null){
		            if(child.getBoolean("th_registMyChilds")){
		                for(Thing cchild : child.getChilds()){
		                    addThing(desChilds, cchild, context);
		                }
		            }else{
		                addThing(desChilds, child, context);
		            }
		        }
		    }
		    
		    ThingUtils.initFromRegistCache(desChilds, context, descriptor, "child");
		}catch(Exception e){
		    Executor.info(TAG, "add regist child error", e);
		}
		//log.info("Select DB Time: " + (System.currentTimeMillis() - start));
		
		String keyword = actionContext.getObject("keyword");
		keyword = keyword.toLowerCase();
		List<Thing> dess = new ArrayList<Thing>();
		Thing currentThing = actionContext.getObject("currentThing");
		for(Thing des : desChilds){
		    if(des.getString("many") == "false"){
		        //只找首要描述者相同的子节点，其他情况不再过滤了
		        boolean have = false;		        
		        for(Thing child : currentThing.getChilds()){
		            if(child.getDescriptor().getMetadata().getPath().equals(des.getMetadata().getPath())){
		                have = true;
		                break;
		            }
		        }
		        if(have){
		            continue;
		        }
		    }
		    
		    //过滤关键字
		    if(keyword != ""){
		        String desKeys = des.getString("th_keywords");
		        boolean ok = false;
		        if(desKeys != null && desKeys.toLowerCase().indexOf(keyword) != -1){
		            ok = true;
		        }
		        if(!ok && des.getMetadata().getPath().toLowerCase().indexOf(keyword) != -1){
		            ok = true;
		        }
		        if(!ok){
		            String group = des.getString("group");
		            if(group != null && group.toLowerCase().indexOf(keyword) != -1){
		                ok = true;
		            }
		        }
		        if(!ok && des.getMetadata().getLabel().toLowerCase().indexOf(keyword) != -1){
		            ok = true;
		        }
		        if(!ok){
		            continue;
		        }
		    }
		
		    dess.add(des);
		}
		//log.info("Filter Many Time: " + (System.currentTimeMillis() - start));
		
		return dess;
	}
	
	public void addChildSelection(ActionContext actionContext) {
		//检查事物是否已经变更
		ActionContainer actions = actionContext.getObject("actions");
		Thing currentThing = actionContext.getObject("currentThing");
		
		Object checkedThing = actions.doAction("checkThing", actionContext, "thing", currentThing);
		if(checkedThing == null || checkedThing instanceof String){
		    System.out.println("check thing false: " + checkedThing);
		    return;
		}
		currentThing = (Thing) checkedThing;

		//def selectIndex = childList.getSelectionIndex();
		Thing childStructure = actionContext.getObject("newThingDescriptor");
		if(childStructure == null){
		    childStructure = (Thing) childTree.getSelection()[0].getData();
		}
		Map<String, Object> childData = currentAddModel.doAction("getValue", currentAddModelContext);

		//创建和添加子节点
		Thing childObj = new Thing(null, null, childStructure.getMetadata().getPath(), false);
		childObj.cognize(childData);
		childObj.set("descriptors", childStructure.getMetadata().getPath());
		        
		//name属性默认为空
		if(childObj.getStringBlankAsNull("name") == null){
		    Thing desc = world.getThing(childStructure.getMetadata().getPath());
		    if(desc != null){
		        childObj.put("name", desc.get("name"));
		    }
		}

		Tree innerOutline = actionContext.getObject("innerOutline");
		TreeItem treeItem = innerOutline.getSelection()[0];

		currentThing.addChild(childObj);
		currentThing.save();
		actionContext.getScope(0).put("rootLastModified", currentThing.getRoot().getMetadata().getLastModified());
		currentThing = world.getThing(currentThing.getMetadata().getPath());
		childObj = currentThing.getChilds().get(currentThing.getChilds().size() - 1);

		TreeItem newItem = new TreeItem(treeItem, SWT.NONE);
		actions.doAction("initOutlineTreeItem", actionContext, "treeItem", newItem, "thing", childObj);
		currentAddModel.doAction("setValue", currentAddModelContext);

		//添加自动创建的子节点
		List<String> autoAddChilds = childStructure.doAction("ideGetAutoAddChilds", actionContext);
		if(autoAddChilds != null){
		    boolean added = false;
		    for(String childDesc : autoAddChilds){
		        added = true;
		        Thing childThing = new Thing(null, null, childDesc, false);
		        childThing.initDefaultValue();
		        childObj.addChild(childThing);
		        TreeItem childItem = new TreeItem(newItem, SWT.NONE);
		        actions.doAction("initOutlineTreeItem", actionContext, "treeItem", childItem, "thing", childThing);
		    }
		    
		    if(added){
		        currentThing.save();
		        
		        actionContext.getScope(0).put("rootLastModified", currentThing.getRoot().getMetadata().getLastModified());
		    }
		}

		//刷新缓存
		ThingEntry thingEntry = actionContext.getObject("thingEntry");
		thingEntry.getThing();

		treeItem.setExpanded(true);
		newItem.setExpanded(true);

		for(TreeItem item : innerOutline.getItems()){
		    initItemThing(item);
		}
	}
	
	private void  initItemThing(TreeItem item){
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
	
	public void cancelAddChildSelection(ActionContext actionContext) {
		StackLayout editPartCompositeStackLayout = actionContext.getObject("editPartCompositeStackLayout");
		Composite contentEditComposite = actionContext.getObject("contentEditComposite");
		Composite editPartComposite = actionContext.getObject("editPartComposite");
		
		editPartCompositeStackLayout.topControl = contentEditComposite;
		editPartComposite.layout();
	}
}
