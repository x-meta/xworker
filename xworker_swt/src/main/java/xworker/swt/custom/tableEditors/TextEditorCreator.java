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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class TextEditorCreator {
    public static Object create(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = actionContext.getObject("self");
    	
		//创建Control
		Thing textThing = null;
		if(self.getBoolean("PASSWORD")) {
			textThing = world.getThing("xworker.swt.custom.tableEditors.TextEditor/@TextEditor1");
		}else {
			textThing = world.getThing("xworker.swt.custom.tableEditors.TextEditor/@TextEditor");
		}
		Control control = (Control) textThing.doAction("create", actionContext);;
		
		return control;       
	}

    public static void setValue(ActionContext actionContext){
    	Text textEditor = (Text) actionContext.get("textEditor");
    	Object value = actionContext.get("value");
    	if(value == null) {
    		textEditor.setText("");
    	}else {
    		textEditor.setText(String.valueOf(value));
    	}
		textEditor.selectAll();
		textEditor.setFocus();        
	}

    public static Object getValue(ActionContext actionContext){
    	Text textEditor = (Text) actionContext.get("textEditor");
		return textEditor.getText();       
	}

    public static void keyDown(ActionContext actionContext){
    	Event event = (Event) actionContext.get("event");
    	
    	Text text = (Text) event.widget;
    	/*
    	Composite cursor = (Composite) text.getData("cursor");
    	if(cursor == null){
    		return;
    	}*/
    	
    	//Thing cursorThing = actionContext.getObject("cursorThing");//(Thing) cursor.getData("thing");
    	Item item = (Item) text.getData("item");
    	Integer column = (Integer) text.getData("column");

    	if (event.keyCode == SWT.CR || event.keyCode == SWT.TAB) {
    		ItemEditorUtils.saveValue(item, column, text.getText(), actionContext);
    	    //cursorThing.doAction("setValue", (ActionContext) actionContext.get("parentContext"), 
    	    //        UtilMap.toParams(new Object[]{"item", item, "column", column, "value", text.getText()}));
    	    //tableItem.setText(column, text.getText());
    		if(!ItemEditorUtils.isNotDisposeOnSaveValue(actionContext)) {
    			text.dispose();
    		}
    	}
    	// close the text editor when the user hits "ESC"
    	if (event.keyCode == SWT.ESC) {
    		if(!ItemEditorUtils.isNotDisposeOnSaveValue(actionContext)) {
    			text.dispose();
    		}
    	}    	
    }
}