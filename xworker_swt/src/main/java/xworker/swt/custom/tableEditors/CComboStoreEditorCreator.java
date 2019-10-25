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

import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

public class CComboStoreEditorCreator {
	private static Logger log = LoggerFactory.getLogger(CComboStoreEditorCreator.class);
	
	public static Object create(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
		Thing thing = world.getThing("xworker.swt.custom.tableEditors.CComboStoreEditor/@combo");
		CCombo combo = (CCombo) thing.doAction("create", actionContext);
		combo.setData("thing", self);
		
		Thing store = self.doAction("getDataStore", actionContext);
		if(store != null) {
			try{
			    Bindings bindings = actionContext.push(null);
			    bindings.put("parent", combo);
			    store.doAction("create", actionContext);
			}finally{
			    actionContext.pop();
			}
		}
		if(self.getBoolean("readOnly")){
		    combo.setEditable(false);
		}
		
		return combo;        
	}

    @SuppressWarnings("unchecked")
	public static Object getValue(ActionContext actionContext){
		CCombo combo = (CCombo) actionContext.get("combo");
		
		int index = combo.getSelectionIndex();
		if(index == -1){
		    if(combo instanceof CCombo && combo.getEditable()){
		        return combo.getText();
		    }else{
		        return null;
		    }
		}else{
		    Thing store = (Thing) combo.getData("store");
		    List<Object> records = (List<Object>) store.get("records");
		    if(records.size() > index){
		        Object record = records.get(index);
		        try{
		        	//DataObject是app包的东西，x-swt不直接引用
			        Method method = record.getClass().getMethod("getKeyAndDatas", new Class<?>[]{});
			        Object[][] keys = (Object[][]) method.invoke(record, new Object[]{});
			        if(keys != null && keys.length > 0){
			            return keys[0][1];
			        }
		        }catch(Exception e){
		        	log.warn("CComboStoreEditor get value error", e);
		        }
		    }
		    
		    return null;
		}        
	}

    public static void setValue(ActionContext actionContext){
		CCombo combo = (CCombo) actionContext.get("combo");
		Thing sotreListener = (Thing) combo.getData("storeListener");
		sotreListener.doAction("setValue", actionContext, UtilMap.toParams(new Object[]{"id", actionContext.get("value")}));
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
    	        Thing comboThing = (Thing) combo.getData("thing");
    	        Object value = comboThing.doAction("getValue", actionContext);
    	        //log.info("value=" + value);
    	        
    	        ItemEditorUtils.saveValue(tableItem, column, value, actionContext);
    	        //cursorThing.doAction("setValue", (ActionContext) actionContext.get("parentContext"), 
    	        //    UtilMap.toParams(new Object[]{"item", tableItem, "column", column, "value", value}));
    	        combo.dispose();
    	        break;
    	    case SWT.ESC:
    	        combo.dispose();
    	        break;    
    	}
    }

}