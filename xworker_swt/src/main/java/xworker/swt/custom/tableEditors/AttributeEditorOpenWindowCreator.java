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
package xworker.swt.custom.tableEditors;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class AttributeEditorOpenWindowCreator {
	public static void dispose(ActionContext actionContext){
		((Shell) actionContext.get("AttributeEditorOpenWindow")).dispose();
		((Text) actionContext.get("text")).dispose();
	}
	
	public static void exitOnEscape(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		switch (event.detail) {
	        case SWT.TRAVERSE_ESCAPE:
	            ((Shell) actionContext.get("AttributeEditorOpenWindow")).dispose();
	            event.detail = SWT.TRAVERSE_NONE;
	            event.doit = false;
	            break;
	    }
	}
	
	@SuppressWarnings("unchecked")
	public static void okButtonSelection(ActionContext actionContext){
		Text text = (Text) actionContext.get("text");
		if(text.isDisposed()){
		    return; //当在下拉列表中按回车键，会出现disposed的情况，貌似执行了两次？
		}

		//Object cursor = text.getData("cursor");
		//Thing cursorThing = actionContext.getObject("cursorThing");//(Thing) cursor.getData("thing");
		Item tableItem = (Item) text.getData("item");
		Integer column = (Integer) text.getData("column");

		Map<String, Object> values = (Map<String, Object>) ((Thing) actionContext.get("form")).doAction("getValues", actionContext);
		ItemEditorUtils.saveValue(tableItem, column, values.get("attr"), actionContext);
		//cursorThing.doAction("setValue", (ActionContext) actionContext.get("parentContext"), 
		//        UtilMap.toParams(new Object[]{"item", tableItem, "column", column, "value", values.get("attr")}));

		((Shell) actionContext.get("AttributeEditorOpenWindow")).dispose();
		text.dispose();
	}
}