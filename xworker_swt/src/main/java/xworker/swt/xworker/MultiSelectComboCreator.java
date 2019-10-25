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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;

public class MultiSelectComboCreator {
    public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//创建弹出下拉框
		Thing combo = new Thing("xworker.swt.xworker.PopCombo");
		combo.put("name", self.getString("name"));
		combo.put("READ_ONLY", self.getString("READONLY"));
		combo.put("BORDER", self.getString("BORDER"));
		combo.put("dynamicWinSize", "true");
		combo.put("compositePath", "xworker.swt.xworker.MultiSelectCombo/@tableComposite");
		for(Thing child : self.getAllChilds()){
		    combo.addChild(child, false);
		}
		
		//创建多选下拉框的变量上下文，多选下拉框使用独立的动作上下文
		ActionContext ac = new ActionContext();
		ac.put("parentContext", actionContext);
		ac.put("parent", actionContext.get("parent"));
		ac.put("comboThing", self);
		Composite composite = null;
		Designer.pushCreator(self);		
		try{
			composite = combo.doAction("create", ac);
		}finally{
			Designer.popCreator();
		}
		Text text = (Text) composite.getData(AttributeEditor.INPUT_CONTROL);
		ac.put("text", text);
		ac.put("composite", text.getData("composite"));
		ac.put("self", self);
		ac.put("attribute", actionContext.get("attribute"));
		
		//设置值的动作
		Thing actions = World.getInstance().getThing("xworker.swt.xworker.MultiSelectCombo/@actions1");
		ActionContainer action = (ActionContainer) actions.doAction("create", ac);
		ac.put("action", action);
		
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		composite.setData(AttributeEditor.ACTIONCONTAINER, action);
		return composite;        
	}

    @SuppressWarnings("unchecked")
	public static void setValue(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");    	
    	Text text = (Text) actionContext.get("text");
    	Thing comboThing = (Thing) actionContext.get("comboThing");
    	
    	//保存值，编辑器返回的值
    	String value = (String) actionContext.get("value");
    	text.setData(value);
    	//log.info("set value=" + value);

    	//值通常是id号，显示的时候要显示标签值
    	if(value == null || "".equals(value)){
    	    text.setText("");
    	}else{
    	    String dataSource = comboThing.getString("dataSource");
    	    List<Object> datas = null;
    	    if("selfValues".equals(dataSource)){
    	        //自定义的数据
    	        datas = (List<Object>) comboThing.get("value@");    
    	    }else if("dataStore".equals(dataSource)){
    	        //数据仓库的数据
    	        Thing dataStore = (Thing) OgnlUtil.getValue(self.getString("dataName"), actionContext.get("parentContext"));
    	        if(dataStore != null){
    	            datas = (List<Object>) dataStore.get("records");
    	        }    
    	    }else if("selfDataStore".equals(dataSource)){
    	        Thing dataStore = (Thing) OgnlUtil.getValue(comboThing.getString("dataName"),  actionContext);
    	        if(dataStore != null){
    	            datas = (List<Object>) dataStore.get("records");
    	        }  
    	    }else if("varList".equals(dataSource)){
    	        //变量列表
    	        datas = (List<Object>) OgnlUtil.getValue(self.getString("dataName"), actionContext.get("parentContext"));  
    	    }

    	    String idName = comboThing.getString("idName");
    	    if(idName == null || "".equals(idName)){
    	        idName = "name";
    	    }
    	    
    	    String labelName = comboThing.getString("labelName");
    	    if(labelName == null || "".equals(labelName)){
    	        labelName = "label";
    	    }

    	    if(!(value instanceof String)){
    	        value = value.toString();
    	    }
    	    if(datas == null){
    	    	text.setText(value);
    	    	return;
    	    }
    	    String textStr = "";
    	  
    	    
    	    for(String v : value.split(",")){
    	        for(Object data : datas){
    	            Object id = null;
					try {
						id = OgnlUtil.getValue(idName, data);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	            if(id != null && id.toString().equals(v)){
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
    
    @SuppressWarnings("unchecked")
	public static Object getValue(ActionContext actionContext) throws OgnlException{
    	Text text = (Text) actionContext.get("text");
    	Thing self = (Thing) actionContext.get("self");
    	
    	Thing comboThing = (Thing) actionContext.get("comboThing");
    	if("false".equals(comboThing.getString("READONLY"))){
    	    String textStr = text.getText().trim();
    	    if("".equals(textStr)){
    	        return null;
    	    }
    	    
    	    List<Object> datas = null;
    	    String ids = "";
    	    String idName = comboThing.getString("idName");
    	    String labelName = comboThing.getString("labelName");
    	    String dataSource = comboThing.getString("dataSource");
    	    if("selfValues".equals(dataSource)){
    	        //自定义的数据
    	        datas = (List<Object>) comboThing.get("value@");    
    	        idName = "value";
    	        labelName = "getMetadata().getLabel()";
    	    }else if("dataStore".equals(dataSource)){
    	        //数据仓库的数据
    	        Thing dataStore = (Thing) OgnlUtil.getValue(self.getString("dataName"), actionContext.get("parentContext"));
    	        if(dataStore != null){
    	            datas = (List<Object>) dataStore.get("records");
    	        }    
    	    }else if("selfDataStore".equals(dataSource)){
    	        Thing dataStore = (Thing) OgnlUtil.getValue(comboThing.getString("dataName"),  actionContext);
    	        if(dataStore != null){
    	            datas = (List<Object>) dataStore.get("records");
    	        }  
    	    }else if("varList".equals(dataSource)){
    	        //变量列表
    	        datas = (List<Object>) OgnlUtil.getValue(self.getString("dataName"), actionContext.get("parentContext"));  
    	    }
    	    
    	    for(String txtStr : textStr.split("[,]")){
    	        boolean have = false;
    	        Object idData = null;
    	        for(Object data : datas){
    	            Object label = OgnlUtil.getValue(labelName, data);
    	            if(label != null && label.toString().equals(txtStr)){
    	                have = true;
    	                idData = data;
    	                break;
    	            }
    	        }
    	        if(ids != ""){
    	            ids = ids + ",";
    	        }
    	        if(have){
    	            ids = ids + OgnlUtil.getValue(idName, idData);
    	        }else{
    	            ids = ids + txtStr;
    	        }
    	    }
    	    return ids;
    	}else{
    	    return text.getData();
    	}
    }
    
    @SuppressWarnings("unchecked")
	public static void searchTextAction(ActionContext actionContext) throws OgnlException{
    	Table table = (Table) actionContext.get("table");

    	//删除Table中原有的数据
    	for(TableItem item : table.getItems()){
    	    item.dispose();
    	}

    	ActionContext parentContext = (ActionContext) actionContext.get("parentContext");
    	Thing comboThing = (Thing) parentContext.get("comboThing");

    	//-----------初始化表格中的待选数据-------
    	String dataSource = comboThing.getString("dataSource");
    	List<Object> datas = null;
    	String idName = comboThing.getString("idName");
    	String labelName = comboThing.getString("labelName");
    	if("selfValues".equals(dataSource)){
    	    //自定义的数据
    	    datas = (List<Object>) comboThing.get("value@");    
    	    idName = "name";
    	    labelName = "getMetadata().getLabel()";
    	}else if("dataStore".equals(dataSource)){
    	    //数据仓库的数据
    	    Thing dataStore = (Thing) OgnlUtil.getValue(comboThing.getString("dataName"), ((ActionContext) parentContext.get("parentContext")).get("parentContext"));
    	    if(dataStore != null){
    	        datas = (List<Object>) dataStore.get("records");
    	    }    
    	}else if("varList".equals(dataSource)){
    	    //变量列表
    	    datas = (List<Object>) OgnlUtil.getValue(comboThing.getString("dataName"), ((ActionContext) parentContext.get("parentContext")).get("parentContext"));  
    	}
    	int rowHeight = 0;

    	Text searchText = (Text) actionContext.get("searchText"); 
    	String filterText = searchText.getText().trim();
    	if(datas != null){
    	    for(Object data : datas){
    	        Object dataObj = data;
    	        if(data instanceof String){               
    	            dataObj = new HashMap<String, Object>();
    	            ((HashMap<String, Object>) dataObj).put(labelName, data);
    	            ((HashMap<String, Object>) dataObj).put(idName, data);
    	        }
    	        
    	        String label = (String) OgnlUtil.getValue(labelName, dataObj);
    	        if(label != null && label.startsWith(filterText)){
    	            TableItem tableItem = new TableItem(table, SWT.NONE);
    	            tableItem.setData(dataObj);        
    	            tableItem.setText(new String[]{label});
    	            rowHeight += table.getItemHeight();
    	        }
    	    }
    	}

    	String value = (String) ((ActionContainer) parentContext.get("action")).doAction("getValue");
    	if(value == null){
    	    value = "";
    	}
    	for(String v : value.split(",")){
    	    for(TableItem row : table.getItems()){
    	        Object id = OgnlUtil.getValue(idName, row.getData());
    	        if(id != null && id.toString().equals(v)){
    	            row.setChecked(true);
    	        }
    	    }
    	}
    }
    
    public static void tableSelection(ActionContext actionContext) throws OgnlException{
    	Event event = (Event) actionContext.get("event");
    	ActionContext parentContext = (ActionContext) actionContext.get("parentContext");
    	Text text = (Text) actionContext.get("text");
    	Table table = (Table) actionContext.get("table");

    	Thing comboThing = (Thing) parentContext.get("comboThing");

    	String idName = comboThing.getString("idName");
    	String labelName = comboThing.getString("labelName");
    	if("selfValues".equals(comboThing.getString("dataSource"))){
    	    idName = "name";
    	    labelName = "getMetadata().getLabel()";
    	}

    	if(comboThing.getBoolean("filter") == true){
    	    //如果是过滤的，那么采用附加的方式
    	    String labelStr = (String) OgnlUtil.getValue(labelName, event.item.getData());
    	    String idStr = (String) OgnlUtil.getValue(idName, event.item.getData());
    	    String textLabelStr = text.getText();
    	    String textIdStr = (String) text.getData();
    	    if(((TableItem) event.item).getChecked()){
    	        //附加
    	        if(textIdStr == null || "".equals(textIdStr)){
    	            textIdStr = idStr;
    	            textLabelStr = labelStr;
    	        }else{
    	            boolean have = false;
    	            for(String tIdStr : textIdStr.split("[,]")){
    	                if(tIdStr == idStr){
    	                    have = true;
    	                    break;
    	                }
    	            }
    	            if(!have){
    	                textIdStr = textIdStr + "," + idStr;
    	                textLabelStr = textLabelStr + "," + labelStr;
    	            }
    	        }
    	    }else{
    	        //移除
    	        if(textIdStr == null || textIdStr == ""){
    	        }else{
    	            int index = textIdStr.indexOf(idStr);
    	            if(index != -1){
    	                if(index > 0 && textIdStr.substring(index-1,index) == ","){
    	                    textIdStr = textIdStr.substring(0, index-1) + textIdStr.substring(index + idStr.length(), textIdStr.length());
    	                }else{
    	                    textIdStr = textIdStr.substring(0, index) + textIdStr.substring(index + idStr.length(), textIdStr.length());
    	                }
    	            }
    	            index = textLabelStr.indexOf(labelStr);
    	            if(index != -1){
    	                if(index > 0 && textLabelStr.substring(index-1,index) == ","){
    	                    textLabelStr = textLabelStr.substring(0, index-1) + textLabelStr.substring(index + labelStr.length(), textLabelStr.length());
    	                }else{
    	                    textLabelStr = textLabelStr.substring(0, index) + textLabelStr.substring(index + labelStr.length(), textLabelStr.length());
    	                }
    	            }
    	        }
    	    }
    	    
    	    if(textLabelStr != null && textLabelStr.startsWith(",")){
    	        textLabelStr = textLabelStr.substring(1, textLabelStr.length());
    	    }
    	    if(textIdStr != null && textIdStr.startsWith(",")){
    	        textIdStr = textIdStr.substring(1, textIdStr.length());
    	    }
    	    text.setText(textLabelStr);
    	    text.setData(textIdStr);
    	}else{
    	    String textStr = "";
    	    String idStr = "";
    	    for(TableItem item : table.getItems()){
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
    }
    
    @SuppressWarnings("unchecked")
	public static void init(ActionContext actionContext) throws OgnlException{
    	ActionContext parentContext = (ActionContext) actionContext.get("parentContext");
    	final Table table = (Table) actionContext.get("table");
    	
    	Thing comboThing = (Thing) parentContext.get("comboThing");
    	Text searchText = (Text) actionContext.get("searchText");
    	if(comboThing.getBoolean("filter") == false){
    	    searchText.dispose();
    	}

    	//-----------初始化表格中的待选数据-------
    	String dataSource = comboThing.getString("dataSource");
    	List<Object> datas = null;
    	String idName = comboThing.getString("idName");
    	String labelName = comboThing.getString("labelName");
    	if("selfValues".equals(dataSource)){
    	    //自定义的数据
    	    datas = (List<Object>) comboThing.get("value@");    
    	    idName = "value";
    	    labelName = "getMetadata().getLabel()";
    	}else if("dataStore".equals(dataSource)){
    	    //log.info("get data store，dataStore=" + comboThing.dataName);
    	    //log.info("dataStore=" + parentContext.parentContext.parentContext.get("dataStore"));
    	    //数据仓库的数据
    	    Thing dataStore = (Thing) OgnlUtil.getValue(comboThing.getString("dataName"), ((ActionContext) parentContext.get("parentContext")).get("parentContext"));
    	    //log.info("dataStore=" + dataStore);
    	    if(dataStore != null){
    	        datas = (List<Object>) dataStore.get("records");
    	    }    
    	    if(datas == null){
    	        datas = Collections.emptyList();
    	    }
    	}else if("selfDataStore".equals(dataSource)){
    	    //log.info(comboThing.dataName);
    	    Thing dataStore = (Thing) OgnlUtil.getValue(comboThing.getString("dataName"),  parentContext.get("parentContext"));
    	    if(dataStore != null){
    	        datas = (List<Object>) dataStore.get("records");
    	    }  
    	}else if("varList".equals(dataSource)){
    	    //变量列表
    	    datas = (List<Object>) OgnlUtil.getValue(comboThing.getString("dataName"), ((ActionContext) parentContext.get("parentContext")).get("parentContext"));  
    	}
    	int rowHeight = 0;

    	if(datas != null){
    	    for(Object data : datas){
    	        Object dataObj = data;
    	        if(data instanceof String){               
    	            dataObj = new HashMap<String, Object>();
    	            ((HashMap<String, Object>) dataObj).put(labelName, data);
    	            ((HashMap<String, Object>) dataObj).put(idName, data);
    	        }
    	        
    	        TableItem tableItem = new TableItem(table, SWT.NONE);
    	        tableItem.setData(dataObj);
    	        if(labelName == null){
    	            continue;
    	        }
    	        String label = (String) OgnlUtil.getValue(labelName, dataObj);
    	        tableItem.setText(new String[]{label});
    	        rowHeight += table.getItemHeight();
    	    }
    	}

    	String value = (String) ((ActionContainer) parentContext.get("action")).doAction("getValue");
    	if(value == null){
    	    value = "";
    	}
    	for(String v : value.split(",")){
    	    for(TableItem row : table.getItems()){
    	        Object id = OgnlUtil.getValue(idName, row.getData());
    	        if(id != null && id.toString().equals(v)){
    	            row.setChecked(true);
    	        }
    	    }
    	}

    	//设置表格的列宽至少和表格一样长
    	int width = 0;
    	for(TableColumn column : table.getColumns()){
    	    width += column.getWidth();
    	}

    	Text text = (Text) parentContext.get("text");
    	
    	int shellWidth = ((Composite) text.getParent()).getSize().x;
    	if(shellWidth <= 0){
    	    shellWidth = 100;
    	}

    	Shell shell = (Shell) actionContext.get("shell");
    	if(width < shellWidth){
    	    if(rowHeight > shell.getSize().y){
    	        table.getColumn(0).setWidth(table.getColumn(0).getWidth() + shellWidth - width - 23);
    	    }else{
    	        table.getColumn(0).setWidth(table.getColumn(0).getWidth() + shellWidth - width - 7);
    	    }
    	}

    	int itemsCount = table.getItems().length;
    	if(itemsCount > comboThing.getInt("popWinMaxRows")){
    	    itemsCount = comboThing.getInt("popWinMaxRows");
    	}
    	int height = 9 + table.getItemHeight() * table.getItems().length;
    	if(searchText != null && !searchText.isDisposed()){
    	    height = searchText.getSize().y + height + 20;
    	}

    	if(height > 200){
    	    height = 200;
    	}
    	shell.setSize(shellWidth, height);
    	shell.addListener(SWT.Activate, new Listener() {
			@Override
			public void handleEvent(Event event) {
				table.forceFocus();
			}    		
    	});
    	
    }
}