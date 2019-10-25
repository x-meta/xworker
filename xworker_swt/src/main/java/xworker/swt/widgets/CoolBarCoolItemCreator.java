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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.swt.design.Designer;
import xworker.swt.util.ItemIndex;
import xworker.swt.util.SwtUtils;

public class CoolBarCoolItemCreator {
    public static Object create(ActionContext actionContext){
    	CoolBar coolBar = (CoolBar) actionContext.get("parent");
    	Integer index= ItemIndex.getRemove();
    	return create(actionContext, index == null ? coolBar.getItemCount() : index);
    }
    
    public static Object create(ActionContext actionContext, int index){
    	Thing self = (Thing) actionContext.get("self");
        
        int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
        if(self.getBoolean("DROP_DOWN"))
            style |= SWT.DROP_DOWN;
            
        CoolItem coolItem = new CoolItem((CoolBar) actionContext.get("parent"), style, index);
        
        //保存变量和创建子事物
        actionContext.getScope(0).put(self.getString("name"), coolItem);
        actionContext.peek().put("parent", actionContext.get("parent"));
        for(Thing child : self.getAllChilds()){
            Control control = (Control) child.doAction("create", actionContext);
            if(control != null) {
            	//如果CoolBar初始没有Item，后期加入Item好像都显示不出来，以下是尝试，未生效
            	control.pack();
            	if(control instanceof Composite){
                    ((Composite) control).layout();
                }
            }
            coolItem.setControl(control);            
        }
        actionContext.peek().remove("parent");
        
        //应用模板，如果存在
        Thing StyleManager = (Thing) actionContext.get("StyleManager");
        if(StyleManager != null){
            StyleManager.doAction("apply", actionContext, UtilMap.toParams(new Object[]{"widget", coolItem, "widgetThing", self}));
        }
        Designer.attach(coolItem, self.getMetadata().getPath(), actionContext);
        return coolItem;
    }
}