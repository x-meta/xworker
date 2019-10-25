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
package xworker.swt.custom;

import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.swt.util.UtilEvent;

public class TableEditorCreator {
    public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//创建editor
		Table parent = (Table) actionContext.get("table");
		TableEditor tableEditor = new TableEditor(parent);
		self.doAction("init", actionContext, UtilMap.toParams(new Object[]{"editor", tableEditor}));
		parent.setData("_tableEditor", tableEditor);
		actionContext.getScope(0).put(self.getString("name"), tableEditor);
		return tableEditor;      
	}

    public static void triggerHandler(ActionContext actionContext){ //这个方法干什么呢？
    	Widget widget = (Widget) UtilEvent.getEventWidget(actionContext);
		Object editor = widget.getData("_tableEditor");
		if(editor == null){
		    return;
		}
		
		Object item = UtilEvent.getEventItem(actionContext);
		if(item == null){
		    return;
		}      
	}
}