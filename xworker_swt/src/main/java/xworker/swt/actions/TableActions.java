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
package xworker.swt.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilAction;

public class TableActions {
	/**
	 * 选择一个表格的被选中的记录。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object getSelection(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//表格变量
		String tableName = self.getString("tableName");
		Table table = (Table) actionContext.get(tableName);
		TableItem[] items = null;
		if(table != null){
			items = table.getSelection();			
		}
		
		//返回值
		Object rvalue = null;
		String returnType = self.getString("returnType");
		if("one".equals(returnType)){
			if(items != null && items.length > 0){
				rvalue = items[0];
			}
		}else if("list".equals(returnType)){
			List<TableItem> list = new ArrayList<TableItem>();
			if(items != null){
				for(TableItem item : items){
					list.add(item);
				}
			}
			rvalue = list;
		}
		
		//保存变量
		UtilAction.putVarByActioScope(self, self.getString("varName"), rvalue, actionContext);
		
		return rvalue;
	}
	
	/**
	 * 选择一个表格的被选中的记录数据。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object getSelectionData(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//表格变量
		String tableName = self.getString("tableName");
		Table table = (Table) actionContext.get(tableName);
		TableItem[] items = null;
		if(table != null){
			items = table.getSelection();			
		}
		
		//返回值
		Object rvalue = null;
		String returnType = self.getString("returnType");
		if("one".equals(returnType)){
			if(items != null && items.length > 0){
				rvalue = items[0].getData();
			}
		}else {
			List<Object> list = new ArrayList<Object>();
			if(items != null){
				for(TableItem item : items){
					list.add(item.getData());
				}
			}
			rvalue = list;
		}
		
		//保存变量
		UtilAction.putVarByActioScope(self, self.getString("varName"), rvalue, actionContext);
		
		return rvalue;
	}
}