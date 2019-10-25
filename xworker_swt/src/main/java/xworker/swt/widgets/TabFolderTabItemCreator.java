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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;

public class TabFolderTabItemCreator {
    public static Object create(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        
        return create(actionContext, self, -1);
    }
    
    public static Object create(ActionContext actionContext, Thing self,  int index){        
        TabFolder parent = (TabFolder) actionContext.get("parent");
        TabItem item = null;
        if(index != -1){
        	item = new TabItem(parent, SWT.NONE, index);
        }else{
        	item = new TabItem(parent, SWT.NONE);
        }
        if(self.getString("text") != null)
            item.setText(UtilString.getString(self.getString("text"), actionContext));
        if(self.getString("toolTipText") != null)
            item.setToolTipText(UtilString.getString(self.getString("oolTipText"), actionContext));
        
        //保存变量和创建子事物
        actionContext.getScope(0).put(self.getString("name"), item);        
        for(Thing child : self.getAllChilds()){
        	actionContext.peek().put("parent", parent);
            Control control = (Control) child.doAction("create", actionContext);
            item.setControl(control);
        }
        item.setData(Designer.DATA_THING, self.getMetadata().getPath());
        
        return item;
    }

}