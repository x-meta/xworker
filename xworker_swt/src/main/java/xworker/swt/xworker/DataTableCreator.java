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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;

public class DataTableCreator {
    public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int style = SWT.NONE;
		String selectStyle = self.getString("selectStyle");
		if("SINGLE".equals(selectStyle)){
			style |= SWT.SINGLE;
		}else if("MULTI".equals(selectStyle)){
			style |= SWT.MULTI;
		}		
		
		if(self.getBoolean("V_SCROLL"))
		    style |= SWT.V_SCROLL;
		if(self.getBoolean("H_SCROLL"))
		    style |= SWT.H_SCROLL;
		if(self.getBoolean("CHECK"))
		    style |= SWT.CHECK;
		if(self.getBoolean("FULL_SELECTION"))
		    style |= SWT.FULL_SELECTION;
		if(self.getBoolean("HIDE_SELECTION"))
		    style |= SWT.HIDE_SELECTION;
		if(self.getBoolean("BORDER"))
		    style |= SWT.BORDER;
		if(self.getBoolean("VIRTUAL"))
		    style |= SWT.VIRTUAL;    
		    
		Composite parent = (Composite) actionContext.get("parent");
		DataTable table = new DataTable(parent, style);
		table.setBinding(actionContext);
		table.setThingPath(self.getMetadata().getPath());
		
		if(self.getBoolean("lineVisible"))
		    table.setLinesVisible(true);
		if(self.getBoolean("headerVisible"))
		    table.setHeaderVisible(true);
		actionContext.getScope(0).put(self.getString("name"), table);
		table.setData("binding", actionContext);
		
		String columnNames = self.getString("columnNames");
		if(columnNames != null && !"".equals(columnNames)){			
		    table.setColumnNames(UtilString.getGroovyStringArray(columnNames));
		}
		
		//创建子节点
		actionContext.getScope(0).put(self.getString("name"), table);
		actionContext.peek().put("parent", table);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		//boolean haveEditor = false;
		for(Thing child : self.getChilds("TableColumn")){
		    if(child.getChilds("TableEditor").size() != 0){
		        //haveEditor = true;
		    }
		    
		    if(self.getBoolean("moveableColumns")){
		        TableColumn c = (TableColumn) actionContext.get(child.getString("name"));
		        c.setMoveable(true);
		    }
		        
		    Thing editor = child.getThing("TableEditor@0");
		    
		    if(editor == null || editor.getChilds().size() == 0){
		        continue;
		    }
		    
		    Thing editComponent = editor.getChilds().get(0);
		    actionContext.getScope(0).put(editComponent.getString("name"), null);
		}
		
		Designer.attach(table, self.getMetadata().getPath(), actionContext);
		return table;        
	}

}