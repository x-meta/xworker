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

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class ComboEditorCreator {
    @SuppressWarnings("unchecked")
	public static Object create(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
    	Thing thing = world.getThing("xworker.swt.custom.tableEditors.ComboEditor/@combo");
		CCombo combo = (CCombo) thing.doAction("create", actionContext);;
		
		//为combo赋值，值在TableCursorEditor的columnEditor的子事物value中定义
		List<Thing> values = (List<Thing>) self.get("value@");
		combo.setData(values);
		
		for(Thing value : values){
		    combo.add(value.getMetadata().getLabel());
		}
		
		if(self.getBoolean("readOnly")){
		    combo.setEditable(false);
		}
		
		return combo;        
	}

    public static Object getValue(ActionContext actionContext){
    	CCombo combo = (CCombo) actionContext.get("combo");
    	
    	return combo.getText();        
	}

    public static void setValue(ActionContext actionContext){
    	CCombo combo = (CCombo) actionContext.get("combo");
    	
		combo.setText(String.valueOf(actionContext.get("value")));
		combo.setFocus();        
	}
    
    public static void keyDown(ActionContext actionContext){
    	Event event = (Event) actionContext.get("event");
    	
    	CCombo combo = (CCombo) event.widget;
    	//Composite cursor = (Composite) combo.getData("cursor");
    	//Thing cursorThing = (Thing) cursor.getData("thing");
    	Item tableItem = (Item) combo.getData("item");
    	Integer column = (Integer) combo.getData("column");

    	switch(event.character){
    	    case SWT.CR:
    	    	ItemEditorUtils.saveValue(tableItem, column, combo.getText(), actionContext);
    	        //cursorThing.doAction("setValue", (ActionContext) actionContext.get("parentContext"), 
    	        //    UtilMap.toParams(new Object[]{"item", tableItem, "column", column, "value", combo.getText()}));
    	        combo.dispose();
    	        break;
    	    case SWT.ESC:
    	        combo.dispose();
    	        break;    
    	}
    }

}