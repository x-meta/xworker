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

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;

public class TableTableItemCreator {
    public static void toJavaCode(ActionContext actionContext){
    	/*
        String code = "TableItem ${self.name} = new TableItem(${self.parent.name}, SWT.NONE);";
        
        if(self.text != null)
            code = code + "\n${self.name}.setText(${self.text});";
        if(self.checked != null)
            code = code + "\n${self.name}.setChecked(true);";
            
        return code;*/
    }

    @SuppressWarnings("unchecked")
	public static Object create(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        
        TableItem item = new TableItem((Table) actionContext.get("parent"), SWT.NONE);
        
        Object text = self.doAction("getText", actionContext);
        if(text instanceof String[]) {
        	item.setText((String[]) text);
        }else if(text instanceof String){
        	item.setText((String) text);
        }else if(text instanceof Collection) {
        	Collection<Object> cs = ((Collection<Object>) text);
        	String[] ca = new String[cs.size()];
        	cs.toArray(ca);
        	item.setText(ca);
        }
        if(UtilData.isTrue(self.doAction("isChecked", actionContext))) {
        	item.setChecked(true);
        }
            
        //应用模板，如果存在
        Thing StyleManager = (Thing) actionContext.get("StyleManager");
        if(StyleManager != null){
            StyleManager.doAction("apply", actionContext, UtilMap.toParams(new Object[]{"widget", item, "widgetThing", self}));
        }
        return item;
    }

}