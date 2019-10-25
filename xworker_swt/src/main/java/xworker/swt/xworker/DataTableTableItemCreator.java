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
package xworker.swt.xworker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

public class DataTableTableItemCreator {
    public static void toJavaCode(ActionContext actionContext){
    	/*
        String code = "TableItem ${self.name} = new TableItem(${self.parent.name}, SWT.NONE);";
        
        if(self.text != null)
            code = code + "\n${self.name}.setText(${self.text});";
        if(self.checked != null)
            code = code + "\n${self.name}.setChecked(true);";
            
        return code; */
    }

    public static Object create(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        TableItem item = new TableItem((Table) actionContext.get("parent"), SWT.NONE);
        
        String text = UtilString.getString(self, "text", actionContext);
        if(text != null)
            item.setText(text);
        
        if(self.getBoolean("checked"))
            item.setChecked(true);
            
        return item;
    }

}