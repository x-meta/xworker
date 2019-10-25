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
package xworker.swt.model.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class TableDataStoreListenerCreator {
    @SuppressWarnings("unchecked")
	public static void onReconfig(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		Table table = (Table) self.get("table");
		
		//删除所有数据
		table.removeAll();
		//删除所有列
		for(TableColumn column : table.getColumns()){
		    column.dispose();
		}
		
		//重新组建表格的列
		Thing store = (Thing) actionContext.get("store");
		Thing dataObject = (Thing) store.get("dataObject");
		if(dataObject == null){
		    return;
		}
		
		List<Thing> columns = new ArrayList<Thing>();
		for(Thing attr : (List<Thing>) dataObject.get("attribute@")){
		    if(attr.getBoolean("viewField") && attr.getBoolean("showInTable") && attr.getBoolean("gridEditor")){
		        int style = SWT.NONE;
		        String gridAlign = attr.getString("gridAlign");
		        if("left".equals(gridAlign)){
		        	style = style | SWT.LEFT;
		        }else if("right".equals(gridAlign)){
		        	style = style | SWT.RIGHT;
		        }else if("center".equals(gridAlign)){
		        	style = style | SWT.CENTER;
		        }
		        
		        TableColumn column = new TableColumn(table, style);
		        column.setText(attr.getMetadata().getLabel());
		        int width = attr.getInt("gridWidth");
		        if(width > 0){
		            column.setWidth(width);
		        }
		        column.setResizable(attr.getBoolean("gridResizable"));
		        column.setMoveable(true);
		        columns.add(attr);
		    }
		}
		self.set("columns", columns);
		
		//初始化数据，如果存在
		self.doAction("onLoaded", actionContext);        
	}

    @SuppressWarnings("unchecked")
	public static void onLoaded(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		Table table = (Table) self.get("table");
		
		//先清空数据
		table.removeAll();
		
		Thing store = (Thing) actionContext.get("store");
		List<Map<String, Object>> records = (List<Map<String, Object>>) store.get("records");
		if(records != null){
		    for(Map<String, Object> record : records){
		        List<String> texts = new ArrayList<String>();
		        for(Thing column : (List<Thing>) self.get("columns")){
		            Object text = record.get((String) column.get("name"));
		            if(text == null){
		                text = "";
		            }else{
		                text = String.valueOf(text);
		            }
		            texts.add((String) text);
		        }
		        
		        TableItem tableItem = new TableItem(table, SWT.NONE);
		        tableItem.setText(texts.toArray(new String[texts.size()]));
		    }
		}        
	}

    public static void onDataChanged(ActionContext actionContext){
    }

    public static void beforeLoad(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		Table table = (Table) self.get("table");
		//先清空数据
		table.removeAll();
		
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText("loading...");      
	}

}