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
package xworker.swt.xworker;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.swt.design.Designer;


public class TreeComboCreator {
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		//创建弹出下拉框
		Thing combo = new Thing("xworker.swt.xworker.PopCombo");
		combo.put("name", self.getString("name"));
		combo.put("READ_ONLY", self.get("READ_ONLY"));
		combo.put("BORDER", self.get("BORDER"));
		combo.put("popWinWidth", self.get("popWinWidth"));
		combo.put("popWinHeight", self.get("popWinHeight"));
		combo.put("popWinFocusControl", "var:tree");
		combo.put("compositePath", "xworker.swt.xworker.TreeCombo/@tableComposite");
		for(Thing child : self.getAllChilds()){
		    combo.addChild(child, false);
		}
		
		//创建下拉框的变量上下文，下拉框使用独立的动作上下文
		ActionContext ac = new ActionContext();
		ac.put("parentContext", actionContext);
		ac.put("parent", actionContext.get("parent"));
		ac.put("comboThing", self);
		Composite composite =  null;
		Designer.pushCreator(self);
		try{
			composite = combo.doAction("create", ac);
		}finally{
			Designer.popCreator();
		}
		Text text = (Text) composite.getData(AttributeEditor.INPUT_CONTROL);
		ac.put("composite", text.getData("composite"));
		ac.put("text", text);
		
		//设置值的动作
		Thing actions = World.getInstance().getThing("xworker.swt.xworker.TreeCombo/@actions1");
		ActionContainer actionContainer = actions.doAction("create", ac);
		composite.setData(AttributeEditor.ACTIONCONTAINER, actionContainer);
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		return composite;        
	}

    @SuppressWarnings("unchecked")
	public static Object getDatas(ActionContext actionContext) throws OgnlException{
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
        Thing comboThing = (Thing) actionContext.get("comboThing");
		
        if(comboThing == null){
        	return null;
        }
		String dataSource = comboThing.getString("dataSource");
		List<Object> datas = new ArrayList<Object>();
		if("selfValues".equals(dataSource)){
		    //自定义的数据
		    List<Thing> rootDatas = (List<Thing>) comboThing.get("value@");    
		    for(Thing rootData : rootDatas){
		        valueToList(rootData, datas);
		    }
		}else if(dataSource == "treeModel"){
		    //树模型
		    Object treeModel = world.getThing(self.getString("dataName"));
		    if(treeModel != null){
		        Thing rootModel = (Thing) ((Thing) treeModel).doAction("getRoot", actionContext);
		        boolean isList = rootModel instanceof List;
		        
		        if(!isList){
		            treeModelToList((Thing) treeModel, rootModel, datas, actionContext);
		        }else{
		            for(Thing treeM : (List<Thing>) rootModel){
		                treeModelToList((Thing) treeModel, treeM, datas, actionContext);
		            }
		        }
		    }
		}else if(dataSource == "varList"){
		    //变量列表
		    Object rootTreeDatas = OgnlUtil.getValue(self.getString("dataName"), actionContext.get("parentContext"));  
		    boolean isList = rootTreeDatas instanceof List;
		    
		    if(!isList){
		        treeDataToDatas(rootTreeDatas, datas, self.getString("childName"));
		    }else{
		        for(Object rootTreeData : (List<Object>) rootTreeDatas){
		            treeDataToDatas(rootTreeData, datas, self.getString("childName"));
		        }
		    }
		}
		return datas;     
	}
    
    public static void valueToList(Thing values, List<Object> datas){
	    datas.add(values);
	    for(Thing child : values.getChilds()){
	        valueToList(child, datas);
	    }
	} 

    @SuppressWarnings("unchecked")
	public static void treeModelToList(Thing treeModel, Thing nodeModel, List<Object> datas, ActionContext actionContext) throws OgnlException{
	    if(nodeModel != null){
	        datas.add(nodeModel);
	        
	        if(nodeModel.getBoolean("leaf") == false){
	            List<Thing> childs = (List<Thing>) treeModel.doAction("getChilds", actionContext, UtilMap.toParams(new Object[]{"id", OgnlUtil.getValue("id", nodeModel)}));
	            if(childs != null){
	                for(Thing child : childs){
	                    treeModelToList(treeModel, child, datas, actionContext);
	                }
	            }
	        }
	    }
	}
    
    @SuppressWarnings("unchecked")
	public static void treeDataToDatas(Object treeData, List<Object> datas, String childName){
	    if(treeData == null){
	        return;
	    }
	    try{
	        for(Object data : (List<Object>) treeData){
	            datas.add(data);
	            
	            try{
	                List<Object> childDatas = (List<Object>) OgnlUtil.getValue(childName, data);
	                for(Object child : childDatas){
	                    treeDataToDatas(child, datas, childName);
	                }
	            }catch(Exception e){
	            }
	        }
	    }catch(Exception e){
	        datas.add(treeData);
	        try{
	        	List<Object>  childDatas = (List<Object>) OgnlUtil.getValue(childName, treeData);
	            for(Object child : childDatas){
	                treeDataToDatas(child, datas, childName);
	            }
	        }catch(Exception ee){
	        }
	    }
	}
    
    @SuppressWarnings("unchecked")
	public static void setValue(ActionContext actionContext) throws OgnlException{
    	Text text = (Text) actionContext.get("text");
    	Thing comboThing = (Thing) actionContext.get("comboThing");
    	
    	//保存值，编辑器返回的值
    	Object value = actionContext.get("value");
    	text.setData(value);

    	//值通常是id号，显示的时候要显示标签值
    	if(value == null || "".equals(value)){
    	    text.setText("");
    	}else{
    	    String dataSource = comboThing.getString("dataSource");
    	    List<Object> datas = (List<Object>) comboThing.doAction("getDatas", (ActionContext) actionContext.get("parentContent"));

    	    String idName = comboThing.getString("idName");
    	    String labelName = comboThing.getString("labelName");
    	    if(dataSource == "selfValues"){
    	        idName = "value";
    	        labelName = "getMetadata().getLabel()";
    	    }else if(dataSource == "treeModel"){
    	        idName = "id";
    	        labelName = "text";
    	    }

    	    String textStr = "";
    	    for(String v : value.toString().split(",")){
    	    	if(datas == null){
    	    		break;
    	    	}
    	        for(Object data : datas){
    	            Object id = OgnlUtil.getValue(idName, data);
    	            if(id != null && id.toString() == v){
    	                Object label = OgnlUtil.getValue(labelName, data);
    	                if(textStr != ""){
    	                    textStr = textStr + ",";
    	                }
    	                
    	                textStr = textStr + label;
    	            }
    	        }
    	    }
    	    text.setText(textStr);
    	}
    }
    
    public static Object getValue(ActionContext actionContext){
    	//log.info("multi select combo get value");
    	Text text = (Text) actionContext.get("text");
    	return text.getData();
    }
    
    public static void treeSelection(ActionContext actionContext) throws OgnlException{
    	ActionContext parentContext = (ActionContext) actionContext.get("parentContext");
    	Tree tree = (Tree) actionContext.get("tree");
    	Text text = (Text) actionContext.get("text");

    	Thing comboThing = (Thing) parentContext.get("comboThing");
    	String idName = comboThing.getString("idName");
    	String labelName = comboThing.getString("labelName");
    	String dataSource = comboThing.getString("dataSource");
    	if("selfValues".equals(dataSource)){
    	    idName = "value";
    	    labelName = "getMetadata().getLabel()";
    	}else if("treeModel".equals(dataSource)){
    	    idName = "id";
    	    labelName = "text";
    	}
    	String textStr = "";
    	String idStr = "";
    	List<TreeItem> items = new ArrayList<TreeItem>();
    	for(TreeItem item : tree.getItems()){
    	    getSelectItems(item, items);
    	}
    	for(TreeItem item : items){
    	    if(item.getChecked() == true){
    	        if(textStr != ""){
    	            textStr = textStr + ",";
    	            idStr = idStr + ",";
    	        }
    	        
    	        textStr = textStr + OgnlUtil.getValue(labelName, item.getData());
    	        idStr = idStr + OgnlUtil.getValue(idName, item.getData());
    	    }
    	}

    	text.setText(textStr);
    	text.setData(idStr);
    }
    
    public static void getSelectItems(TreeItem treeItem, List<TreeItem> itemList){
	    if(treeItem.getChecked()){
	        itemList.add(treeItem);
	    }
	    
	    for(TreeItem childItem : treeItem.getItems()){
	        getSelectItems(childItem, itemList);
	    }
	}
    
    @SuppressWarnings("unchecked")
	public static void init(ActionContext actionContext) throws OgnlException{
    	ActionContext parentContext = (ActionContext) actionContext.get("parentContext");
    	World world = World.getInstance();
    	Tree tree = (Tree) actionContext.get("tree");
    	
    	Thing comboThing = (Thing) parentContext.get("comboThing");

    	//-----------初始化表格中的待选数据-------
    	String dataSource = comboThing.getString("dataSource");
    	if("selfValues".equals(dataSource)){
    	    //自定义的数据
    	    List<Thing> rootDatas = (List<Thing>) comboThing.get("value@");    
    	    for(Thing rootData : rootDatas){
    	        valueToTree(rootData, tree);
    	    }
    	}else if("treeModel".equals(dataSource)){
    	    //树模型
    	    Thing treeModel = world.getThing(comboThing.getString("dataName"));
    	    if(treeModel != null){
    	        boolean isList = false;
    	        Object rootModel = treeModel.doAction("getRoot", actionContext);
    	        try{
    	            isList = rootModel instanceof List;
    	        }catch(Exception e){        
    	        }
    	        if(!isList){
    	            treeModelToTree(treeModel, rootModel, tree, actionContext);
    	        }else{
    	            for(Object rootM : (List<Object>) rootModel){
    	                treeModelToTree(treeModel, rootM, tree, actionContext);
    	            }
    	        }
    	    }
    	}else if("var".equals(dataSource)){
    	    //变量列表
    	    Object rootTreeDatas = OgnlUtil.getValue(comboThing.getString("dataName"), ((ActionContext) parentContext.get("parentContext")).get("parentContext"));  
    	   
    	    boolean isList = false;
    	    try{
    	        isList = rootTreeDatas instanceof List;
    	    }catch(Exception e){
    	    }
    	    
    	    if(!isList){
    	        treeDataToTree(rootTreeDatas, tree, comboThing.getString("childName"), comboThing.getString("labelName"));
    	    }else{
    	        for(Object rootData : (List<Object>) rootTreeDatas){
    	            treeDataToTree(rootData, tree, comboThing.getString("childName"), comboThing.getString("labelName"));
    	        }
    	    }
    	}

    	//选择已经选择的数据
    	String idStr = (String) ((Text) actionContext.get("text")).getData();
    	if(idStr != null){
    	    String[] ids = idStr.split("[,]");
    	    String idName = comboThing.getString("idName");
    	    if("selfValues".equals(dataSource)){
    	        idName = "value";
    	    }else if("treeModel".equals(dataSource)){
    	        idName = "id";
    	    }
    	    List<TreeItem> items = new ArrayList<TreeItem>();
    	    for(TreeItem item : tree.getItems()){
    	        getItems(item, items);
    	    }
    	    for(TreeItem item : items){
    	        Object idValue = OgnlUtil.getValue(idName, item.getData());
    	        if(idValue != null){
    	            for(String id : ids){
    	                if(id.equals(idValue.toString())){
    	                    item.setChecked(true);
    	                    break;
    	                }
    	            }
    	        }
    	    }
    	}
    }
    
    @SuppressWarnings("unchecked")
	public static void treeDataToTree(Object treeData, Object item, String childName, String labelName) throws OgnlException{
	    if(treeData == null){
	        return;
	    }      
	    TreeItem treeItem = null;	        
        if(item instanceof TreeItem){
	    	treeItem = new TreeItem((TreeItem) item, SWT.NONE);
	    }else{
	    	treeItem = new TreeItem((Tree) item, SWT.NONE);
	    }
	    treeItem.setText((String) OgnlUtil.getValue(labelName, treeData));
	    treeItem.setData(treeData);
	    
	    List<Object> childDatas = (List<Object>) OgnlUtil.getValue(childName, treeData);
	    if(childDatas != null){
		    for(Object child : childDatas){
		        treeDataToTree(child, treeItem, childName, labelName);
		    }
	    }
	    
	    treeItem.setExpanded(true);
	}
    
    public static void  getItems(TreeItem treeItem, List<TreeItem> itemList){
	    itemList.add(treeItem);
	    
	    for(TreeItem childItem : treeItem.getItems()){
	        getItems(childItem, itemList);
	    }
	}
    
    @SuppressWarnings("unchecked")
	public static void treeModelToTree(Thing treeModel, Object nodeModel, Object item, ActionContext actionContext) throws OgnlException{
	    if(nodeModel != null){
	        TreeItem treeItem = null;	        
	        if(item instanceof TreeItem){
		    	treeItem = new TreeItem((TreeItem) item, SWT.NONE);
		    }else{
		    	treeItem = new TreeItem((Tree) item, SWT.NONE);
		    }
	        treeItem.setText(String.valueOf(OgnlUtil.getValue("text", nodeModel)));
	        treeItem.setData(nodeModel);
	        
	        String leaf = String.valueOf(OgnlUtil.getValue("leaf", nodeModel));
	        if("false".equals(leaf)){
	            List<Object> childs = (List<Object>) treeModel.doAction("getChilds", actionContext, UtilMap.toParams(new Object[]{"id", OgnlUtil.getValue("id", nodeModel)}));
	            if(childs != null){
	                for(Object child : childs){
	                    treeModelToTree(treeModel, child, treeItem, actionContext);
	                }
	            }
	        }
	        
	        treeItem.setExpanded(true);
	    }
	}
    
    public static void valueToTree(Thing values, Object item){
	    TreeItem treeItem = null;
	    if(item instanceof TreeItem){
	    	treeItem = new TreeItem((TreeItem) item, SWT.NONE);
	    }else{
	    	treeItem = new TreeItem((Tree) item, SWT.NONE);
	    }
	  
	    treeItem.setText(values.getMetadata().getLabel());
	    treeItem.setData(values);
	    
	    for(Thing child : values.getChilds()){
	        valueToTree(child, treeItem);
	    }
	    
	    treeItem.setExpanded(true);
	}
}