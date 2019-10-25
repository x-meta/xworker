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
package xworker.swt.xworker.attributeEditor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import xworker.swt.ActionContainer;
import xworker.swt.util.UtilBrowser;

public class OpenWindowsCreator {
	private static Logger log = LoggerFactory.getLogger(OpenWindowsCreator.class);
	
    @SuppressWarnings("unchecked")
	public static Object create(ActionContext actionContext){
    	World world = World.getInstance();
        Thing self = (Thing) actionContext.get("self");
        ActionContext parentContext = (ActionContext) actionContext.get("parentContext");
        
        //先获取输入类型
        String inputType = "";
        CCombo combo = (CCombo) parentContext.get("inputtypeInput");
        if(combo == null){
            return null;
        }
        int index = combo.getSelectionIndex();
        if(index != -1){
        	Object data = combo.getData();
        	if(!(data instanceof List)){
        		inputType = null;
        	}else{
        		List datas = (List) data;
        		if(index < datas.size()){
        			inputType = String.valueOf(datas.get(index));
        		}else{
        			inputType = null;
        		}
        	}
        }else{
        	inputType = combo.getText();
        }
        
        List<Thing> desChilds = new ArrayList<Thing>();
        for(Thing child : self.getChilds()){
        	desChilds.add(child);
        }
        //查找并添加注册的子事物
        try{
            Thing registThing = world.getThing("xworker.ide.db.dbindex.app.dataObject.RegistsByThing");
            Thing openWindowSets = world.getThing("xworker.swt.xworker.attributeEditor.OpenWindowSets");
            List<Map<String, Object>> rchilds = (List<Map<String, Object>>) registThing.doAction("query", actionContext, UtilMap.toParams(new Object[]{"thing", openWindowSets, "noDescriptor",true, "registType","child"}));
            for(Map<String, Object> rc : rchilds){
                Thing child = world.getThing(String.valueOf(rc.get("path")));
                if(child != null){
                    desChilds.add(child);
                }
            }
        }catch(Exception e){
            log.info("add regist child error", e);
        }
        
        Thing winThing = null;
        for(Thing des : desChilds){
            if(inputType != null && inputType.equals(des.getString("name"))){
                winThing = des;
                break;
            }
        }
        
        if(winThing == null){
            winThing = world.getThing("xworker.swt.xworker.attributeEditor.openWins.NoneOpenWindow");
        }
        
        return winThing.doAction("create", actionContext, UtilMap.toMap(new Object[]{"inputType", inputType}));
    }

    public static void httpDo(ActionContext actionContext){
    	World world = World.getInstance();
        Thing thing = world.getThing("xworker.swt.xworker.attributeEditor.openWinsExtjs.Openwindow/@openWindow");
        thing.doAction("httpDo", actionContext);
    }
    
    @SuppressWarnings("unchecked")
	public static void okSeleciton(ActionContext actionContext){
    	Thing codeForm = (Thing) actionContext.get("codeForm");
    	Map<String, Object> values = (Map<String, Object>) codeForm.doAction("getValues", actionContext);
    	actionContext.getScope(0).put("result", 
    	      "codeName=" + values.get("codeName") + " codeType=" + values.get("codeType") + 
    	      " wrap=" +  values.get("wrap") + " fillBoth=" + values.get("fillBoth") + 
    	      " cols=" + values.get("cols") + " rows=" + values.get("rows"));
    	((Shell) actionContext.get("codeEditor")).dispose();
    }
    
    public static void childTreeSelection(ActionContext actionContext){
    	//Tree childTree = (Tree) actionContext.get("childTree");

    	//TreeItem treeItem = childTree.getSelection()[0];
    	//Thing objStruct = (Thing) treeItem.getData();
    	Thing objStruct = actionContext.getObject("thing");
    	if(objStruct == null) return;

    	actionContext.g().put("selectedThing", objStruct);
    	Thing dataObjectForm = (Thing) actionContext.get("dataObjectForm");
    	Map<String, Object> valueCache = (Map<String, Object>) actionContext.get("valueCache");
    	//保存先前编辑的数据
    	if(actionContext.get("currentSet") != null){
    	    Object values = dataObjectForm.doAction("getValues", actionContext);
    	    valueCache.put((String) actionContext.get("currentSet"), values);
    	}

    	Thing dataObject = null;
    	if(objStruct.getChilds().size() > 0) {
    		dataObject = objStruct.getChilds().get(0);
    	}else {
    		dataObject = World.getInstance().getThing("xworker.swt.xworker.attributeEditor.openWins.NoParamsEditor");
    	}
    	//log.info("descBrowser=" + descBrowserAction);
    	ActionContainer descBrowserAction = (ActionContainer) actionContext.get("descBrowserAction");
    	descBrowserAction.doAction("setThing", UtilMap.toParams(new Object[]{"thing",objStruct}));

    	//log.info("get cahche path=" + objStruct.metadata.path);
    	Object values = valueCache.get(objStruct.getMetadata().getPath());
    	//log.info("cached values=" + values);
    	dataObjectForm.doAction("setDataObject", actionContext, UtilMap.toParams(new Object[]{"dataObject", dataObject}));
    	if(values != null){
    	    dataObjectForm.doAction("setValues", actionContext, UtilMap.toParams(new Object[]{"values", values}));
    	}

    	Button okButton = (Button) actionContext.get("okButton");
    	if(objStruct.getMetadata().getPath().equals("xworker.swt.xworker.attributeEditor.OpenWindowSets/@thing")){
    	    okButton.setEnabled(false);
    	}else{
    	    okButton.setEnabled(true);
    	}
    }
    
    @SuppressWarnings("unchecked")
	public static void okSeleciton1(ActionContext actionContext) throws UnsupportedEncodingException{
    	//打开窗口的定义，分隔符和编码
    	//Tree childTree = (Tree) actionContext.get("childTree");
    	//TreeItem treeItem = childTree.getSelection()[0];
    	Thing objStruct = actionContext.getObject("selectedThing");//(Thing) treeItem.getData();
    	String encoding = "utf-8";
    	String separateChar = ",";
    	if(objStruct != null){
    	    if(objStruct.getString("separateChar") != null && !"".equals(objStruct.getString("separateChar"))){
    	        separateChar = objStruct.getString("separateChar");
    	    }
    	    if(objStruct.getString("encoding") != null && !"".equals(objStruct.getString("encoding"))){
    	        encoding = objStruct.getString("encoding");
    	    }
    	}

    	//编码数据
    	String result = "";
    	Thing dataObjectForm = (Thing) actionContext.get("dataObjectForm");
    	Map<String, Object> values = (Map<String, Object>) dataObjectForm.doAction("getValues", actionContext);
    	for(String key : values.keySet()){
    	    String value = String.valueOf(values.get(key));
    	    if(value != null && !"".equals(value)){
    	        if(!"".equals(result)){
    	            result = result + separateChar;
    	        }
    	        result = result + key + "=" + URLEncoder.encode(String.valueOf(value), encoding);
    	    }
    	}
    	if(objStruct != null){
    	    result = objStruct.get("winPath") + "|" + result;
    	}

    	actionContext.getScope(0).put("result", result);
    	Shell openWindow = (Shell) actionContext.get("openWindow");
    	openWindow.dispose();
    }

    @SuppressWarnings("unchecked")
	public static void openwindowInit(ActionContext actionContext){
    	Map<String, Object> valueCache = new HashMap<String, Object>();
    	actionContext.getScope(0).put("valueCache", valueCache);
    	World world = World.getInstance();
    	/*
    	
    	Thing descriptor = world.getThing("xworker.swt.xworker.attributeEditor.OpenWindowSets");
    	List<Thing> desChilds = new ArrayList<Thing>();
    	for(Thing child : descriptor.getChilds()){
    	    desChilds.add(child);
    	}

    	//查找并添加注册的子事物
    	try{
    	    Thing registThing = world.getThing("xworker.ide.db.dbindex.app.dataObject.RegistsByThing");
    	    List<Map<String, Object>> rchilds = (List<Map<String, Object>>) registThing.doAction("query", actionContext, UtilMap.toParams(new Object[]{"thing",descriptor, "noDescriptor",true, "registType","child"}));
    	    for(Map<String, Object> rc : rchilds){
    	        Thing child = world.getThing(String.valueOf(rc.get("path")));
    	        if(child != null){
    	            desChilds.add(child);
    	        }
    	    }
    	}catch(Exception e){
    	    log.info("add regist child error", e);
    	}

    	List<Thing> dess = new ArrayList<Thing>();
    	Thing currentThing = (Thing) actionContext.get("currentThing");
    	for(Thing des : desChilds){
    	    if("false".equals(des.getString("many"))){
    	        if(currentThing.getChilds(des.getMetadata().getPath()).size() != 0){
    	            continue;
    	        }
    	    }
    	    //log.info(des.getMetadata().getPath());
    	    dess.add(des);
    	}

    	//分组
    	Map<String, Object> root = new HashMap<String, Object>();
    	root.put("childs", new ArrayList<Object>());    	
    	for(Thing des : dess){
    	    String group = des.getString("group");
    	    if(group == null || "".equals(group)){
    	        ((List<Object>) root.get("childs")).add(des);
    	    }else{
    	        String[] gs = group.split("[.]");
    	        Map<String, Object> subRoot = null;
    	        Map<String, Object> currentRoot = root;
    	        for(String g : gs){
    	            subRoot = (Map<String, Object>) currentRoot.get(g);
    	            if(subRoot == null){ 
    	            	subRoot = new HashMap<String, Object>();
    	            	subRoot.put("childs", new ArrayList<Object>());   
    	                subRoot.put("__name__", g);
    	                currentRoot.put(g, subRoot);
    	            }
    	            currentRoot = subRoot;
    	        }
    	        ((List<Object>) subRoot.get("childs")).add(des);
    	    }
    	}
    	//构造子节点树
    	Tree childTree = (Tree) actionContext.get("childTree");
    	childTree.removeAll();
    	createTree(childTree, root);
	*/
    	
    	ActionContainer actions = actionContext.getObject("thingRegist");
    	actions.doAction("refresh");
    	
    	//初始化表单
    	Map<String, String> params = new HashMap<String, String>();
    	//Map<String, Object> valueCache = new HashMap<String, Object>();
    	//actionContext.getScope(0).put("valueCache", valueCache);
    	String value = (String) actionContext.get("value");
    	//Object currentSet = null;
    	if(value != null && !"".equals(value.trim())){
    		actionContext.g().put("value", value);    	    
    	}else{
    	    //默认打开帮助
    		Thing help = world.getThing("xworker.swt.xworker.attributeEditor.OpenWindowSets/@thing");
    		actionContext.peek().put("thing", help);
 	        childTreeSelection(actionContext);
    	    //Map<String, Object> hparams = UtilMap.toParams(new Object[]{"description", world.getThing("xworker.swt.xworker.attributeEditor.OpenWindows/@NoneOpenWindow1/@sashForm/@dataObjectForm/@dataObjects/@ThingDataObject/@description").getMetadata().getDescription()});
    	    //((Thing) actionContext.get("dataObjectForm")).doAction("setValues", actionContext, UtilMap.toParams(new Object[]{"values", hparams}));
    	}
    }
    
    @SuppressWarnings("unchecked")
	public static void createTree(Object item, Map<String, Object> aroot){
	    List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
	    for(String key : aroot.keySet()){
	        Object v = aroot.get(key);
	        if(v instanceof Map){
	            groups.add((Map<String, Object>) v);
	        }
	    }
	    
	    //为group排序
	    Collections.sort(groups, new Comparator<Map<String, Object>>(){
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				String t1 = ((String) o1.get("__name__")).toLowerCase();
				String t2 = ((String) o2.get("__name__")).toLowerCase();
				return t1.compareTo(t2);
			}
	    	
	    });
	    	   
	    for(Map<String, Object> g : groups){
	        TreeItem subItem = null;
	        if(item instanceof Tree){
	        	subItem = new TreeItem((Tree) item, SWT.NONE);
	        }else{
	        	subItem = new TreeItem((TreeItem) item, SWT.NONE);
	        }
	        subItem.setText(String.valueOf(g.get("__name__")));
	        createTree(subItem, g);
	    }

	    for(Object child : (List<Object>) aroot.get("childs")){
	    	TreeItem subItem = null;
	        if(item instanceof Tree){
	        	subItem = new TreeItem((Tree) item, SWT.NONE);
	        }else{
	        	subItem = new TreeItem((TreeItem) item, SWT.NONE);
	        }
	        
	        subItem.setText(((Thing) child).getMetadata().getLabel());
	        subItem.setData(child);
		    
	    }
	    
	    if(item instanceof TreeItem){
	        ((TreeItem) item).setExpanded(true);
	    }
	}
    
    public static Object getTreeItem(Object item, String winPath){
	    if(item instanceof TreeItem){
	    	return getTreeItem((TreeItem) item, winPath);
	    }else {
	    	return getTreeItem((Tree) item, winPath);
	    }
	}
    
    public static Object getTreeItem(TreeItem item, String winPath){
    	Object obj = item.getData();
    	if(obj instanceof Thing){
    		Thing data = (Thing) obj;//item.getData();
    	    if(data != null && UtilString.eq(data, "winPath", winPath)){
    	        return item;
    	    }
    	}
	    
	    for(TreeItem childItem : item.getItems()){
	        Object itm = getTreeItem(childItem, winPath);
	        if(itm != null){
	            return itm;
	        }
	    }
	    
	    return null;
	}
    
    public static Object getTreeItem(Tree item, String winPath){
    	Thing data = (Thing) item.getData();
	    if(data != null && UtilString.eq(data, "winPath", winPath)){
	        return item;
	    }
	    
	    for(TreeItem childItem : item.getItems()){
	        Object itm = getTreeItem(childItem, winPath);
	        if(itm != null){
	            return itm;
	        }
	    }
	    
	    return null;
	}
    
    public static void openwindowInitLoaded(ActionContext actionContext) {
    	String value = actionContext.getObject("value");
    	if(value == null || "".equals(value)) {
    		return;
    	}
    	
    	Map<String, String> params = null;
    	ActionContainer actions = actionContext.getObject("thingRegist");
    	Map<String, Object> valueCache = actionContext.getObject("valueCache");
    	String[] vs = value.split("[|]");
	    String winPath = vs[0];
	    if(vs.length > 1){
	        params = UtilString.getParams(vs[1], ",");
	    }
	        
	    Tree childTree = actions.getActionContext().getObject("thingTree");
	    TreeItem treeItem = (TreeItem) getTreeItem(actions.getActionContext().getObject("thingTree"), winPath);
	    //log.info("params=" + params + ",treeItem=" + treeItem);
	    if(treeItem != null){
	        Object obj = treeItem.getData();
	        
	        valueCache.put(((Thing) obj).getMetadata().getPath(), params);
	        //log.info("cache=" + obj.metadata.path);
	        childTree.select(treeItem);
	        actionContext.peek().put("thing", obj);
	        childTreeSelection(actionContext);
	        //((SwtListener) actionContext.get("childTreeSelection")).handleEvent(null);
	        //def obj = treeItem.getData();
	        //dataObjectForm.doAction("setDataObject", actionContext, ["dataObject": obj]);
	        //dataObjectForm.doAction("setValues", actionContext, ["values": params]);        
	        //currentSet = obj.metadata.path;
	    }else{
	        //value = ["description".world.getThing("xxworker.swt.xworker.attributeEditor.OpenWindows/@NoneOpenWindow1/@sashForm/@dataObjectForm/@dataObjects/@ThingDataObject/@description").metadata.description];
	        //dataObjectForm.doAction("setValues", actionContext, ["values": value]);
	    }
    }
    
    @SuppressWarnings("unchecked")
	public static void selectokSeleciton(ActionContext actionContext){
    	Thing form = (Thing) actionContext.get("form");
    	Map<String, Object> values = (Map<String, Object>) form.doAction("getValues", actionContext);
    	String result = "";
    	if(values.get("storePath") != null && !"".equals(values.get("storePath"))){
    	    result = values.get("storePath") + "," + values.get("displayCols") + "," + values.get("dataStoreLocal");
    	}
    	
		actionContext.getScope(0).put("result", result);
		((Shell) actionContext.get("select")).dispose();
    }
    
    @SuppressWarnings("unchecked")
	public static void multiselectokSeleciton(ActionContext actionContext){
    	Thing form = (Thing) actionContext.get("form");
    	Map<String, Object> values = (Map<String, Object>) form.doAction("getValues", actionContext);
    	String result = "";
    	if(values.get("storePath") != null && !"".equals(values.get("storePath"))){
    	    result = values.get("storePath") + "," + values.get("displayCols") + "," + values.get("dataStoreLocal");
    	}
    	
		actionContext.getScope(0).put("result", result);
		((Shell) actionContext.get("multSelect")).dispose();
    }
    
    public static void selectinit(ActionContext actionContext){
    	if(actionContext.get("value") != null){
    	    String[] vs = ((String) actionContext.get("value")).split("[,]");
    	    if(!"".equals(vs[0].trim())){
	    	    Map<String, Object> params = UtilMap.toMap(new Object[]{"storePath", vs[0], "displayCols", vs.length > 1 ? vs[1]:"", "dataStoreLocal", vs.length >2 ? vs[2] : ""});
	    	    Thing form = (Thing) actionContext.get("form");
	    	    form.doAction("setValues", actionContext, UtilMap.toMap(new Object[]{"values", params}));
    	    }
    	}

    	((UtilBrowser) actionContext.get("utilBrowser")).attach((Browser) actionContext.get("descBrowser"));
    }
    
    public static void multiselectinit(ActionContext actionContext){
    	if(actionContext.get("value") != null){
    	    String[] vs = ((String) actionContext.get("value")).split("[,]");
    	    if(!"".equals(vs[0].trim())){
	    	    Map<String, Object> params = UtilMap.toMap(new Object[]{"storePath", vs[0], "displayCols", vs.length > 1 ? vs[1]:"", "dataStoreLocal", vs.length >2 ? vs[2] : ""});
	    	    Thing form = (Thing) actionContext.get("form");
	    	    form.doAction("setValues", actionContext, UtilMap.toMap(new Object[]{"values", params}));
    	    }
    	}

    	((UtilBrowser) actionContext.get("utilBrowser")).attach((Browser) actionContext.get("descBrowser"));
    }
    
    @SuppressWarnings("unchecked")
	public static void htmlokSeleciton(ActionContext actionContext){
    	Thing codeForm = (Thing) actionContext.get("codeForm");
    	Map<String, Object> values = (Map<String, Object>) codeForm.doAction("getValues", actionContext);
    	actionContext.getScope(0).put("result", 
    	      "Width=" + values.get("Width" + ";Height=" + values.get("Height") + 
    	      ";ToolbarSet=" +  values.get("ToolbarSet") + ";fillBoth=" + values.get("fillBoth")));
    	((Shell) actionContext.get("html")).dispose();
    }
    
    public static void htmlinit(ActionContext actionContext){
    	String value = (String) actionContext.get("value");
    	Thing codeForm = (Thing) actionContext.get("codeForm");
    	if(actionContext.get("value") != null){
    	    Map<String, String> params = UtilString.getParams(value, " ");
    	    if(params != null){
    	        codeForm.doAction("setValues", actionContext, UtilMap.toMap(new Object[]{"values", params}));
    	    }
    	}
    }
}