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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

public class CheckBoxEditorCreator {
    public static Object create(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
		//创建Control
		Thing thing = world.getThing("xworker.swt.custom.tableEditors.CheckBoxEditor/@combo");
		Control control = (Control) thing.doAction("create", actionContext);;
		
		control.setData(self);
		
		return control;     
	}

    public static void setValue(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Button button = (Button) actionContext.get("button");
    	
    	String value = String.valueOf(actionContext.get("value"));
		if(UtilString.eq(self, "trueText", value)){
		    button.setSelection(true);
		}else if(UtilString.eq(self, "falseText", value)){
		    button.setSelection(false);
		}else{
		    button.setSelection(self.getBoolean("default"));
		}
		
		button.setFocus();    
	}

    public static Object getValue(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Button button = (Button) actionContext.get("button");
    	
		if(button.getSelection()){
		    return self.getString("trueText");
		}else{
		    return self.getString("falseText");
		}      
	}
    
    public static void keyDown(ActionContext actionContext){
    	Event event = (Event) actionContext.get("event");
    	Button button = (Button) actionContext.get("button");

    	Control control = (Control) event.widget;
    	//Composite cursor = (Composite) button.getData("cursor");
    	//Thing cursorThing = (Thing) cursor.getData("thing");
    	Item tableItem = (Item) button.getData("item");
    	Integer column = (Integer) button.getData("column");

    	switch(event.character){
    	    case SWT.CR:
    	        Thing thing = (Thing) control.getData();
    	        Object value = null;
    	        if(button.getSelection()){
    	            value = thing.get("trueText");
    	        }else{
    	            value = thing.get("falseText");
    	        }
    	        
    	        ItemEditorUtils.saveValue(tableItem, column, value, actionContext);
    	        //cursorThing.doAction("setValue", (ActionContext) actionContext.get("parentContext"), 
    	        //    UtilMap.toParams(new Object[]{"item", tableItem, "column", column, "value", value}));
    	        button.dispose();
    	        break;
    	    case SWT.ESC:
    	        button.dispose();
    	        break;    
    	}
    }
}