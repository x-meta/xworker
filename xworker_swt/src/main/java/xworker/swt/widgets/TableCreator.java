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
package xworker.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class TableCreator {
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
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
		Table table = new Table(parent, style);
		
		if(self.getBoolean("lineVisible"))
		    table.setLinesVisible(true);
		if(self.getBoolean("headerVisible"))
		    table.setHeaderVisible(true);
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), table);
		actionContext.peek().put("parent", table);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		@SuppressWarnings("unused")
		boolean haveEditor = false;
		for(Thing child : self.getChilds("TableColumn")){
		    if(child.getChilds("TableEditor").size() != 0){
		        haveEditor = true;
		    }
		    
		    if(self.getBoolean("moveableColumns")){
		    	TableColumn c = (TableColumn) actionContext.get(child.getString("name"));
		        if(c != null){
		        	c.setMoveable(true);
		        }
		    }
		    /*
		        
		    Thing editor = child.getThing("TableEditor@0");
		    
		    if(editor == null || editor.getChilds().size() == 0){
		        continue;
		    }
		    
		    Thing editComponent = editor.getChilds().get(0);
		    actionContext.put(editComponent.getString("name"), null);
		    */
		}
		
		Designer.attach(table, self.getMetadata().getPath(), actionContext);
		return table;        
	}
}