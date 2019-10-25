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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class DatePickerEditorCreator {
    public static Object create(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
		//创建Control
		Thing textThing = world.getThing("xworker.swt.custom.tableEditors.DatePickerEditor/@dateText").detach();
		if(self.getBoolean("showTime")) {
			textThing.set("showTime", "true");
		}
		textThing.set("pattern", self.getString("pattern"));
		Composite control = (Composite) textThing.doAction("create", actionContext);;
		
		Text dateText = (Text) actionContext.get("dateText");
		TableItem tableItem = (TableItem) actionContext.get("item");
		int column = (Integer) actionContext.get("column");
		dateText.setText(tableItem.getText(column));
		dateText.setData("cursor", actionContext.get("cursor"));
		dateText.selectAll();
		dateText.setFocus();
		
		
		return control;//.getData("composite");
    }

    public static void dispose(ActionContext actionContext){
    	Text dateText = (Text) actionContext.get("dateText");
		Composite composite = (Composite) dateText.getData("composite");
		composite.dispose();
	}

    public static void setValue(ActionContext actionContext){
    	String value = String.valueOf(actionContext.get("value"));
    	Text dateText = (Text) actionContext.get("dateText");
    	dateText.setText(value);        
    }

    public static Object getValue(ActionContext actionContext){
    	Text dateText = (Text) actionContext.get("dateText");
		return dateText.getText();        
	}
    
    public static void keyDown(ActionContext actionContext){
    	Event event = (Event) actionContext.get("event");

    	Text text = (Text) event.widget;
    	Composite composite = (Composite) text.getData("composite");
    	//Composite cursor = (Composite) text.getData("cursor");
    	//Thing cursorThing = (Thing) cursor.getData("thing");
    	Item tableItem = (Item) composite.getData("item");
    	Integer column = (Integer) composite.getData("column");

    	if (event.character == SWT.CR) {
    		ItemEditorUtils.saveValue(tableItem, column, text.getText(), actionContext);
    		
    	    //cursorThing.doAction("setValue", (ActionContext) actionContext.get("parentContext"),
    	     //       UtilMap.toParams(new Object[]{"item", tableItem, "column", column, "value", text.getText()}));
    	    //tableItem.setText(column, text.getText());
    	    composite.dispose();
    	}
    	// close the text editor when the user hits "ESC"
    	if (event.character == SWT.ESC) {
    	    composite.dispose();
    	}
    }

}